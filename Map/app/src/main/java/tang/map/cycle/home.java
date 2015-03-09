package tang.map.cycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import tang.map.R;
import tang.map.data.DataToolkit;
import tang.map.data.User;
import tang.map.threadPool.UpdateLocThread;

/**
 * tang
 */
public class home extends Activity
{
    //location
    private LocationClient userLoc = null;
    //普通控件
    private Button back = null;
    private ImageButton start = null;
    private ImageButton map = null;
    private ImageButton cycling = null;
    private ImageButton person = null;
    private User user = new User();

    private SharedPreferences sp = null;
    private SharedPreferences.Editor editor = null;
    private GeoCoder translate = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);




        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.home);
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sp.edit();
        //初始化
        init();
        if(sp.contains("account"))
            user.setAccount(sp.getString("account", null));
        if(sp.contains("userid"))
            user.setUserid(sp.getInt("userid", 0));
        if(sp.contains("sex"))
            user.setSex(sp.getInt("sex", 0));
        if(sp.contains("nickname"))
            user.setNickname(sp.getString("nickname", null));
        if(sp.contains("gid"))
            user.setGid(sp.getInt("gid", 0));
        if(sp.contains("head"))
            user.setHead(DataToolkit.StringToBitmap(sp.getString("head", "")));
        userLoc = new LocationClient(this);
        userLoc.registerLocationListener(new MyLocationLister());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        userLoc.setLocOption(option);
        userLoc.start();

        translate = GeoCoder.newInstance();
        translate.setOnGetGeoCodeResultListener(new MyGtoCoderListener());

        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();
    }

    private void init()
    {
        back = (Button)findViewById(R.id.back);
        start = (ImageButton)findViewById(R.id.start);
        map = (ImageButton)findViewById(R.id.map);
        cycling = (ImageButton)findViewById(R.id.cycling);
        person = (ImageButton)findViewById(R.id.person);
        back.setOnClickListener(new MyClickListener());
        start.setOnClickListener(new MyClickListener());
        map.setOnClickListener(new MyClickListener());
        cycling.setOnClickListener(new MyClickListener());
        person.setOnClickListener(new MyClickListener());
    }

    @Override
    protected void onResume()
    {
        userLoc.requestLocation();
        start.setBackgroundResource(R.drawable.home_start);
        map.setBackgroundResource(R.drawable.home_map);
        cycling.setBackgroundResource(R.drawable.home_cycling);
        person.setBackgroundResource(R.drawable.home_person);
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        userLoc.stop();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    private class MyLocationLister implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location) {
//            if(user.getNickname().equals("x1"))
//                location = new BDLocation(121,31,0);
            LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
            translate.reverseGeoCode(new ReverseGeoCodeOption().location(loc));
            user.setLongitude(loc.longitude);
            user.setLatitude(loc.latitude);
            /*editor.putFloat("longitude", (float)loc.longitude);
            editor.putFloat("latitude", (float)loc.latitude);*/
            editor.putString("longitude", new Double(loc.longitude).toString());
            editor.putString("latitude", new Double(loc.latitude).toString());
            editor.commit();
            Bundle updataLoc = new Bundle();
            updataLoc.putSerializable("user",user);
            UpdateLocThread updateLocThread = new UpdateLocThread(updataLoc);
            updateLocThread.start();
        }


    }
    private class MyClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent = null;
            switch (id)
            {
                case R.id.back:
                    finish();
                    break;
                case R.id.start:
                    start.setBackgroundResource(R.drawable.home_start_selected);
                    intent = new Intent(home.this,setout.class);
                    startActivity(intent);
                    break;
                case R.id.map:
                    map.setBackgroundResource(R.drawable.home_map_selected);
                    intent = new Intent(home.this, tang.map.cycle.map.class);
                    Bundle data = new Bundle();
                    data.putSerializable("user",user);
                    intent.putExtras(data);
                    startActivity(intent);
                    break;
                case R.id.cycling:
                    cycling.setBackgroundResource(R.drawable.home_cycling_selected);
                    intent = new Intent(home.this, cycle.class);
                    startActivity(intent);
                    break;
                case R.id.person:
                    person.setBackgroundResource(R.drawable.home_person_selected);
                    intent = new Intent(home.this,person.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    private class MyGtoCoderListener implements OnGetGeoCoderResultListener
    {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            String location = "";
            if(reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR)
            {
                location = "";
            }
            location = reverseGeoCodeResult.getAddress();
            System.out.println(location);
            editor.putString("location",location);
            editor.commit();
        }
    }
}
