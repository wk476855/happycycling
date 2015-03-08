package tang.map.cycle;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.ArrayList;
import java.util.Random;

import tang.map.R;
import tang.map.data.User;
import tang.map.interfaces.IMap;
import tang.map.team.createTeam;
import tang.map.team.joinTeam;
import tang.map.threadPool.GetTeamMsgThread;
import tang.map.threadPool.GetUsersThread;
import tang.map.threadPool.MyHandler;
import tang.map.threadPool.SendMsgThread;
import tang.map.threadPool.UpdateLocThread;
import tang.map.voiceHelper.VoiceRecord;

public class map extends Activity implements IMap
{
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationClient locationClient;
    private MyLocationListener myLocationListener = new MyLocationListener();

    // route
    private int nodeIndex = -1;
    private RouteLine route = null;
    private RoutePlanSearch mSearch = null;

    private Button back = null;
    private Button select = null;
    private ImageButton team = null;
    private ImageButton navigation = null;
    private ImageButton shout = null;
    private ImageButton pre = null;
    private ImageButton next = null;
    private ImageButton start = null;
    private ImageButton end = null;
    private ImageButton go = null;

    private boolean choseStart = false;
    private boolean choseEnd = false;
    private LatLng startP = null;
    private LatLng endP = null;

    private PopupWindow menu = null;
    private int preMenu = -1;

    private User user = new User();
    private SharedPreferences sp = null;
    private int gid = 0;
    private int userid = 0;

    private VoiceRecord voiceRecord;

    private ArrayList<Marker> markers = null;
    private SharedPreferences activitysp = null;
    private TeamMessageReceiver tmReseiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*SDKInitializer.initialize(getApplicationContext());*/
        setContentView(R.layout.map);
        /**
         * initiate the components
         */
        init();
        /**
         * baidu map and location
         */
        setMap();


        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();

//        activitysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = activitysp.edit();
//        editor.putString("currentActivity", "map");
//        editor.commit();
    }

    private void init()
    {
        /**
         * 控件监听事件注册
         */
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new MyClickListener());
        select = (Button)findViewById(R.id.select);
        select.setOnClickListener(new MyClickListener());
        team = (ImageButton)findViewById(R.id.team);
        team.setOnClickListener(new MyClickListener());
        navigation = (ImageButton)findViewById(R.id.navigation);
        navigation.setOnClickListener(new MyClickListener());
        shout = (ImageButton)findViewById(R.id.shout);
        shout.setOnClickListener(new MyClickListener());
        pre = (ImageButton)findViewById(R.id.pre);
        pre.setVisibility(View.GONE);
        next = (ImageButton)findViewById(R.id.next);
        next.setVisibility(View.GONE);

        IntentFilter action = new IntentFilter();
        action.addAction("shout");
        tmReseiver = new TeamMessageReceiver();
        registerReceiver(tmReseiver,action);

        user = (User)this.getIntent().getSerializableExtra("user");
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        voiceRecord = new VoiceRecord(map.this);
        if(sp.contains("gid"))
            gid = sp.getInt("gid",0);
        if(sp.contains("userid"))
            userid = sp.getInt("userid",0);
    }

    private void setMap()
    {
        mapView = (MapView)findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setMyLocationEnabled(true);
        MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode,true,null));
        baiduMap.setOnMapClickListener(new MyMapClickListener());
        baiduMap.setOnMarkerClickListener(new MarkClickListener());

        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(myLocationListener);

        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setOpenGps(true);
        locationClientOption.setCoorType("bd09ll");
        locationClient.setLocOption(locationClientOption);
        locationClient.start();

        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new RouteResultListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0x001)
        {
            if(resultCode == RESULT_OK)
            {
                Bundle getUserData = new Bundle();
                getUserData.putSerializable("type",0);
                SharedPreferences sp = getSharedPreferences("user",Context.MODE_PRIVATE);
                long session = sp.getLong("session", 0);
                getUserData.putLong("session", session);
                int dist = data.getIntExtra("distance",0);
                int sex = data.getIntExtra("sex",-1);
                getUserData.putInt("distance",dist);
                getUserData.putInt("sex",sex);
                MyHandler getTeamHandler = new MyHandler(map.this);
                GetUsersThread getTeamThread = new GetUsersThread(getTeamHandler,getUserData);
                getTeamThread.start();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        locationClient.start();
        super.onResume();
//        SharedPreferences.Editor editor = activitysp.edit();
//        editor.putString("currentActivity", "map");
//        editor.commit();
    }

    @Override
    protected void onPause() {
        locationClient.stop();
        super.onPause();
//        SharedPreferences.Editor editor = activitysp.edit();
//        editor.putString("currentActivity", "");
//        editor.commit();
//        if(tmReseiver != null)
//            unregisterReceiver(tmReseiver);
    }

    @Override
    protected void onDestroy() {
        locationClient.stop();
        super.onDestroy();
//        SharedPreferences.Editor editor = activitysp.edit();
//        editor.putString("currentActivity", "");
//        editor.commit();
    }

    @Override
    protected void onStop() {

        super.onStop();
//        SharedPreferences.Editor editor = activitysp.edit();
//        editor.putString("currentActivity", "");
//        editor.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_HEADSETHOOK)
        {
            event.startTracking();
        }
        else if(keyCode == KeyEvent.KEYCODE_MENU)
        {
            if(preMenu != R.id.sos) {
                preMenu = R.id.sos;
                View sosView = getLayoutInflater().inflate(R.layout.sos, null);
                menu = new PopupWindow(sosView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                menu.showAtLocation(shout, Gravity.BOTTOM, 0, 0);
                ImageButton sos = (ImageButton) sosView.findViewById(R.id.sos);
                sos.setOnClickListener(new MyClickListener());
             }
            else {
                menu.dismiss();
                preMenu = -1;
            }
        }
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_HEADSETHOOK)
        {
            String path = "/sdcard/happyCycling/" + new Random().nextInt(100000) + ".3gp";
            voiceRecord = new VoiceRecord(map.this);
            voiceRecord.setPath(path);
            if(!voiceRecord.startRecord())
            {
                Toast.makeText(this,"开启录音有错误",Toast.LENGTH_SHORT).show();
            }
            else
            {
                show("开始喊话");
            }
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_HEADSETHOOK)
        {
            if(!voiceRecord.stopRecord())
            {
                Toast.makeText(map.this, "录音时间不能少于1秒", Toast.LENGTH_SHORT).show();
            }
            else
            {
                MyHandler myHandler = new MyHandler(map.this);
                Bundle data = new Bundle();
                data.putString("path",voiceRecord.getPath());
                data.putInt("type",1);
                data.putInt("gid",gid);
                data.putInt("userid",userid);
                data.putInt("method", 1);
                SendMsgThread sendvoice = new SendMsgThread(myHandler,data);
                sendvoice.setId(0x004);
                sendvoice.start();
            }
        }
        return true;
    }

    @Override
    public void show(String str)
    {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUsers(ArrayList<User> users)
    {
        baiduMap.clear();
        markers = new ArrayList<Marker>();
        for (User user : users) {
            BitmapDescriptor head = null;
            int type = user.getType();
            switch (type) {
                case 0:
                    head = BitmapDescriptorFactory.fromResource(R.drawable.map_me);
                    break;
                case 2:
                    head = BitmapDescriptorFactory.fromResource(R.drawable.map_other);
                    break;
            }
            OverlayOptions options = new MarkerOptions().position(new LatLng(user.getLatitude(),user.getLongitude())).icon(head).zIndex(9);
            Marker marker = (Marker) baiduMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            marker.setExtraInfo(bundle);
            markers.add(marker);
        }
    }

    @Override
    public void setUsersUI(int type,int userid)
    {
        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        if(sp.contains("userid") && markers != null)
        {
            for (Marker userMarker : markers)
            {
                User user = (User) userMarker.getExtraInfo().get("user");
                if(type == 0)
                {
                    if (user.getUserid() == userid) {
                        userMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_other_speaking));
                    }
                }
                else {
                    int myid = sp.getInt("userid",0);
                    if(user.getUserid() != myid)
                        userMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_other));
                }
            }
        }
    }

    @Override
    public void clear() {
        menu.dismiss();
    }

    @Override
    public void jump() {

    }
    /**
     * the click listener for the every components
     */
    private class MyClickListener implements View.OnClickListener
    {
        private View view = null;
        private Bundle data = null;
        public void setBundle(Bundle data)
        {
            this.data = data;
        }
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id)
            {
                //map主界面上的按钮
                case R.id.back:
                    finish();
                    break;
                case R.id.select:
                    Intent selectIntent = new Intent(map.this,select.class);
                    startActivityForResult(selectIntent,0x001);
                    break;
                case R.id.pre:
                    getNodeInfo(pre);
                    break;
                case R.id.next:
                    getNodeInfo(next);
                    break;
                case R.id.team:
                    if(preMenu != R.id.team)
                    {
                        preMenu = R.id.team;
                        view = getLayoutInflater().inflate(R.layout.team, null);
                        menu = new PopupWindow(view, 350, 460, true);
                        menu.setBackgroundDrawable(new PaintDrawable(android.R.color.transparent));
                    }
                    if(!menu.isShowing())
                    {
                        int[] location = new int[2];
                        team.getLocationInWindow(location);
                        menu.showAtLocation(team,Gravity.NO_GRAVITY,location[0],location[1] - menu.getHeight());
                        ImageButton create = (ImageButton)view.findViewById(R.id.createTeam);
                        create.setOnClickListener(new MyClickListener());
                        ImageButton join = (ImageButton)view.findViewById(R.id.joinTeam);
                        join.setOnClickListener(new MyClickListener());
                        ImageButton display = (ImageButton)view.findViewById(R.id.dipTeam);
                        display.setOnClickListener(new MyClickListener());
                    }
                    break;
                case R.id.navigation:
                    if(preMenu != R.id.navigation)
                    {
                        preMenu = R.id.navigation;
                        view = getLayoutInflater().inflate(R.layout.guide,null);
                        menu = new PopupWindow(view,400,460,true);
                        menu.setBackgroundDrawable(new PaintDrawable(android.R.color.transparent));
                    }
                    if(!menu.isShowing())
                    {
                        int[] location = new int[2];
                        navigation.getLocationInWindow(location);
                        menu.showAtLocation(navigation, Gravity.NO_GRAVITY, location[0] - (menu.getWidth() - navigation.getWidth()) / 2, location[1] - menu.getHeight());
                        menu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        start = (ImageButton)view.findViewById(R.id.start);
                        start.setOnClickListener(new MyClickListener());
                        end = (ImageButton)view.findViewById(R.id.end);
                        end.setOnClickListener(new MyClickListener());
                        go = (ImageButton)view.findViewById(R.id.go);
                        go.setOnClickListener(new MyClickListener());
                    }
                    break;
                case R.id.shout:
                    if (sp.contains("gid"))
                        gid = sp.getInt("gid",0);
                    if(gid == 0)
                        show("您现在不在队伍里，无法一键喊话...");
                    else
                    {
                        if(preMenu != R.id.shout)
                        {
                            preMenu = R.id.shout;
                            view = getLayoutInflater().inflate(R.layout.shout, null);
                            menu = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                            menu.setBackgroundDrawable(new PaintDrawable(android.R.color.transparent));
                        }
                        if(!menu.isShowing())
                        {
                            menu.showAtLocation(navigation,Gravity.CENTER,0,0);
                            ImageButton speak = (ImageButton)view.findViewById(R.id.speak);
                            speak.setOnTouchListener(new SpeakTouchListener());
                        }
                    }
                    break;
                //team
                case R.id.createTeam:
                    menu.dismiss();
                    Intent createIntent = new Intent(map.this,createTeam.class);
                    startActivity(createIntent);
                    break;
                case R.id.joinTeam:
                    menu.dismiss();
                    Intent joinIntent = new Intent(map.this,joinTeam.class);
                    startActivity(joinIntent);
                    break;
                case R.id.dipTeam:
                    menu.dismiss();
                    baiduMap.clear();
                    Bundle getTeamData = new Bundle();
                    getTeamData.putSerializable("type",1);
                    MyHandler getTeamHandler = new MyHandler(map.this);
                    GetUsersThread getTeamThread = new GetUsersThread(getTeamHandler,getTeamData);
                    getTeamThread.start();
                    break;
                //guide
                case R.id.start:
                    menu.dismiss();
                    choseStart = true;
                    menu.dismiss();
                    break;
                case R.id.end:
                    menu.dismiss();
                    choseEnd = true;
                    menu.dismiss();
                    break;
                case R.id.go:
                    menu.dismiss();
                    route = null;
                    baiduMap.clear();
                    if(startP == null)
                        Toast.makeText(map.this,"请先选择起点",Toast.LENGTH_SHORT).show();
                    else if(endP == null)
                        Toast.makeText(map.this,"请先选择终点",Toast.LENGTH_SHORT).show();
                    else
                    {
                        PlanNode startNode = PlanNode.withLocation(startP);
                        PlanNode endNode = PlanNode.withLocation(endP);
                        mSearch.drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));
                    }
                    break;
                case R.id.chat:
                    Intent intent = new Intent(map.this, chat.class);
                    intent.putExtras(this.data);
                    startActivity(intent);
                    break;
                case R.id.sos:

                    break;
            }
        }
    }

    /**
     * get the node's information of the navigation
     * @param v
     */
    private void getNodeInfo(View v)
    {
        if(route == null || route.getAllStep() == null)
            return;
        if(v.getId() == R.id.next)
        {
            if(nodeIndex < route.getAllStep().size() - 1)
                nodeIndex++;
            else
                return;
        }
        else if(v.getId() == R.id.pre)
        {
            if(nodeIndex > 0)
                nodeIndex--;
            else
                return;
        }
        LatLng nodeloc = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if(step instanceof DrivingRouteLine.DrivingStep)
        {
            nodeloc = ((DrivingRouteLine.DrivingStep)step).getEntrace().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep)step).getInstructions();
        }
        if(nodeloc == null || nodeTitle == null)
            return;
        StringBuffer title = new StringBuffer();
        while(nodeTitle.length() > 16)
        {
            title.append(nodeTitle.substring(0, 16) + "\n");
            nodeTitle = nodeTitle.substring(16);
        }
        title.append(nodeTitle);
        nodeTitle = title.toString();
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeloc));
        EditText popupText = new EditText(map.this);
        popupText.setBackgroundResource(R.drawable.map_guide_popup);
        popupText.setEnabled(false);
        popupText.setPadding(20,20,20,30);
        popupText.setTextColor(0xff000000);
        popupText.setText(nodeTitle);
        baiduMap.showInfoWindow(new InfoWindow(popupText,nodeloc,null));
    }

    /**
     * for get the route result
     */
    private class RouteResultListener implements OnGetRoutePlanResultListener
    {
        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult)
        {
            if(drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR)
            {
                Toast.makeText(map.this,"抱歉，未找到结果",Toast.LENGTH_SHORT).show();
            }
            if(drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR)
            {
                return ;
            }
            if(drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR)
            {
                pre.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                pre.setOnClickListener(new MyClickListener());
                next.setOnClickListener(new MyClickListener());
                nodeIndex = -1;
                route = drivingRouteResult.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(baiduMap);
                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }

        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

    }

    /**
     * the navigation overlay for cycling
     */
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
                return BitmapDescriptorFactory.fromResource(R.drawable.map_guide_s);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
                return BitmapDescriptorFactory.fromResource(R.drawable.map_guide_f);
        }
    }

    /**
     * for send voice message to the team
     */
    private class SpeakTouchListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            ImageButton speak = (ImageButton) v;

            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                speak.setBackgroundResource(R.drawable.map_shout_speaking);
                String path = "/sdcard/happyCycling/" + new Random().nextInt(100000) + ".3gp";
                voiceRecord = new VoiceRecord(map.this);
                voiceRecord.setPath(path);
                if(!voiceRecord.startRecord())
                {
                    Toast.makeText(map.this,"开启录音有错误",Toast.LENGTH_SHORT).show();
                }

            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                speak.setBackgroundResource(R.drawable.map_shout_speak);
                if(!voiceRecord.stopRecord())
                {
                    Toast.makeText(map.this, "录音时间不能少于1秒", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    MyHandler myHandler = new MyHandler(map.this);
                    Bundle data = new Bundle();
                    data.putString("path",voiceRecord.getPath());
                    data.putInt("type", 1);
                    data.putInt("gid",gid);
                    data.putInt("userid",userid);
                    data.putInt("method", 1);
                    SendMsgThread sendvoice = new SendMsgThread(myHandler,data);
                    sendvoice.setId(0x004);
                    sendvoice.start();
                }
            }

            return false;
        }
    }

    /**
     * for get the message of the people on the map
     */
    private class MarkClickListener implements BaiduMap.OnMarkerClickListener
    {
        @Override
        public boolean onMarkerClick(Marker marker) {
            System.out.println("marker click");
            User user = (User)marker.getExtraInfo().get("user");
            if(user != null)
            {
                Point p = baiduMap.getProjection().toScreenLocation(marker.getPosition());
                View view = getLayoutInflater().inflate(R.layout.head, null);
                TextView account = (TextView) view.findViewById(R.id.account);
                account.setText(user.getNickname());
                ImageView head = (ImageView)view.findViewById(R.id.head);
                head.setImageBitmap(user.getHead());
                menu = new PopupWindow(view,400,300,true);
                menu.setBackgroundDrawable(new PaintDrawable(android.R.color.transparent));
                menu.setOutsideTouchable(true);
                menu.showAtLocation(mapView,Gravity.NO_GRAVITY,p.x - 200,p.y - 180);
                Button chat = (Button) view.findViewById(R.id.chat);
                MyClickListener clickListener = new MyClickListener();
                Bundle data = new Bundle();
                data.putSerializable("user", user);
                clickListener.setBundle(data);
                chat.setOnClickListener(clickListener);
                preMenu = -1;
            }
            /*if (user != null)
                {
                    Intent intent = new Intent(map.this, chat.class);
                    Bundle data = new Bundle();
                    data.putSerializable("user", user);
                    intent.putExtras(data);
                    startActivity(intent);
                }*/
            return false;
        }
    }

    /**
     * for location real-time
     */
    private class MyLocationListener implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location)
        {
            if(user.getNickname().equals("x1"))
                location = new BDLocation(121,31,0);
            if(location == null || mapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(0).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            user.setLongitude(location.getLongitude());
            user.setLatitude(location.getLatitude());
            SharedPreferences sp = getSharedPreferences("user",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("longitude", (float)location.getLongitude());
            editor.putFloat("latitude", (float)location.getLatitude());
            editor.commit();
//
//            Bundle updataLoc = new Bundle();
//            updataLoc.putSerializable("user",user);
//            UpdateLocThread updateLocThread = new UpdateLocThread(updataLoc);
//            updateLocThread.start();
        }
        @Override
        public void onReceivePoi(BDLocation location)
        {

        }
    }

    private class MyMapClickListener implements BaiduMap.OnMapClickListener
    {
        @Override
        public void onMapClick(LatLng latLng)
        {
            if(choseStart == true) {
                startP = latLng;
                BitmapDescriptor start = BitmapDescriptorFactory.fromResource(R.drawable.map_guide_s);
                OverlayOptions startOptions = new MarkerOptions().position(startP).icon(start);
                baiduMap.addOverlay(startOptions);
                choseStart = false;
            }
            else if(choseEnd == true)
            {
                endP = latLng;
                BitmapDescriptor end = BitmapDescriptorFactory.fromResource(R.drawable.map_guide_f);
                OverlayOptions options = new MarkerOptions().position(endP).icon(end);
                baiduMap.addOverlay(options);
                choseEnd = false;
            }
            baiduMap.hideInfoWindow();
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            return false;
        }
    }

    private class TeamMessageReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            MyHandler teamMsgHandler = new MyHandler(map.this);
            GetTeamMsgThread getTeamMsgThread = new GetTeamMsgThread(teamMsgHandler,null);
            getTeamMsgThread.start();
        }
    }

}
