package tang.map.cycle;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import tang.map.R;
import tang.map.adapter.CycleListAdapter;
import tang.map.data.DataToolkit;
import tang.map.threadPool.GetCycleListThread;
import tang.map.threadPool.MyHandler;
import tang.map.threadPool.UpdateLocThread;

public class cycle extends FragmentActivity{

    private ArrayList<HashMap<String, Object>> photolist = new ArrayList<HashMap<String, Object>>();
    private Button switchMap = null;
    private Button add = null;
    private Button back = null;

    private CycleListAdapter adapter = null;
    private GetCycleListThread getlist = null;

    private FragmentManager fragmentManager = null;
    private cycle_list cl = null;
    private cycle_map cm = null;
    private FragmentTransaction fragmentTransaction = null;
    private boolean isMap = false;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cycle);

        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();

        sp = getSharedPreferences("user",Context.MODE_PRIVATE);
        fragmentManager = getFragmentManager();

        init();

        getList();
    }

    private void init()
    {
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new MyClickListener());
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new MyClickListener());
        switchMap = (Button)findViewById(R.id.switchToMap);
        switchMap.setOnClickListener(new MyClickListener());
    }

    private void getList()
    {
        Bundle bundle = new Bundle();
        bundle.putFloat("longitude",sp.getFloat("longitude",0f) );
        bundle.putFloat("latitude", sp.getFloat("latitude",0f));

        MyHandler myHandler = new MyHandler(cycle.this);
        getlist = new GetCycleListThread(myHandler,bundle);
        getlist.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cycle, menu);
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

    private class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.add) {
                Intent intent = new Intent(cycle.this, sendphoto.class);
                startActivityForResult(intent, 0x001);
            }
            else if(id == R.id.switchToMap)
            {
                if(!isMap)
                {
                    switchMap.setBackgroundResource(R.drawable.cycle_switch_list);
                    if(cm == null) {
                        cm = new cycle_map();
                        cm.setPhotolist(photolist);
                        cm.setContext(cycle.this);
                    }
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.cycle_fragment, cm);
                    fragmentTransaction.commit();
                    isMap = true;
                }
                else
                {
                    switchMap.setBackgroundResource(R.drawable.cycle_switch);
                    if(cl != null)
                    {
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.cycle_fragment, cl);
                        fragmentTransaction.commit();
                        isMap = false;
                    }
                }
            }
            else if(id == R.id.back) {
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0x001)
        {
            if(resultCode == 0x001)
            {
                int result = data.getIntExtra("result",0);
                if(result == 0)
                {
                    Toast.makeText(this,"状态发布成功",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(this,"状态发布失败",Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        getList();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showList(ArrayList<HashMap<String,Object>> lists)
    {
        cl = new cycle_list();
        cl.setPhotolist(lists);
        photolist = new ArrayList<HashMap<String, Object>>(lists);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.cycle_fragment,cl);
        fragmentTransaction.commit();
    }
    /*public void addToList(ArrayList<HashMap<String,Object>> lists)
    {
        photolist.clear();
        photolist.addAll(lists);
        adapter.notifyDataSetChanged();
    }

    public void addToMap(ArrayList<HashMap<String,Object>> lists)
    {
        System.out.println("addtomap");
        baiduMap.clear();
        for(int i = 0; i < 1; i++)
        {
            HashMap<String,Object> tmp = lists.get(i);
            Double longitude = (Double)tmp.get("longitude");
            Double latitude = (Double)tmp.get("latitude");
            Bitmap bitmap = DataToolkit.StringToBitmap(tmp.get("photo").toString());
            ImageView photo = new ImageView(this);
            photo.setBackgroundResource(R.drawable.map_head_mask);
            photo.setImageBitmap(bitmap);
            photo.setPadding(20,20,20,20);
            baiduMap.showInfoWindow(new InfoWindow(photo,new LatLng(31.025633,121.44209),null));
        }
    }
    private class MyItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String,Object> item = photolist.get(position);
            Intent intent = new Intent(cycle.this,showphoto.class);
            Bundle data = new Bundle();
            data.putString("latlng","(" + item.get("longitude").toString() + "," +  item.get("latitude") + ")");
            data.putString("photo",item.get("photo").toString());
            data.putString("description",item.get("description").toString());
            intent.putExtra("details",data);
            startActivity(intent);
        }
    }

    private class MyLocationLister implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location == null || cycleMap == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(0).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
        }

        @Override
        public void onReceivePoi(BDLocation location) {

        }
    }*/

}
