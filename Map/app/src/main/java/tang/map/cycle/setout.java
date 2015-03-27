package tang.map.cycle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import tang.map.R;


public class setout extends Activity {

    private int TIME = 1000;
    private int i = 0;
    boolean isFirstLoc = true;// 是否首次定位
    boolean isFirstadd=true;
    int addtime = 0;// 是否第一次添加数据点

    private MapView mMapView = null;
    private TextView distanceShow;
    private TextView timeShow;
    private TextView averspeedShow;
    private TextView speedShow;
    private BaiduMap mBaiduMap;
    private ImageButton focusbutton;
    private Button close = null;
    private Button back = null;

    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private DecimalFormat df = new DecimalFormat("0.00");
    //  private SharedPreferences sp = null;


    private double current_latitude;
    private double current_longitude;
    private double old_latitude;
    private double old_longitude;
    private double increase_dis;
    private float totle_dis;
    private double averspeed;
    private double speed;
    private List<LatLng> points = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setout);

        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();

        //获取地图控件引用
        timeShow = (TextView) findViewById(R.id.time_show);
        distanceShow=(TextView) findViewById(R.id.dis_show);
        averspeedShow=(TextView) findViewById(R.id.averspeed_show);
        speedShow=(TextView) findViewById(R.id.speed_show);
        focusbutton = (ImageButton)findViewById(R.id.focus);
        close = (Button)findViewById(R.id.close);
        close.setOnClickListener(new MyClickListener());
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new MyClickListener());

        //  sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        // totle_dis = sp.getFloat("totalDis",0);

        focusbutton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        focusbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.setout_focus_on));
                        break;

                    case MotionEvent.ACTION_UP:
                        if (!isFirstLoc)
                        {
                            LatLng ll = new LatLng(current_latitude,current_longitude);
                            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                            mBaiduMap.animateMapStatus(u);
                        }
                        focusbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.setout_focus));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        handler.postDelayed(runnable, TIME); //每隔1s执行
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

//        MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.NORMAL;
//        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode,true,null));
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(2000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Override
    protected void onDestroy()
    {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }


    Handler handler = new Handler();
    Runnable runnable = new Runnable()
    {
        @Override
        public void run() {
            // handler自带方法实现定时器
            try
            {
                handler.postDelayed(this, TIME);
                i++;
                averspeed = totle_dis/i;//计算平均速度
                averspeedShow.setText(df.format(averspeed)+"m/s");
                timeShow.setText(convertTime(i));//计算总时间
                System.out.println("do...");
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("exception...");
            }
        }
    };





    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location)
        {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    //   .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            if (!isFirstLoc)
            {
                //mMapView.getMap().clear();
                old_latitude = current_latitude;
                old_longitude = current_longitude;
                current_latitude = location.getLatitude();
                current_longitude = location.getLongitude();

                LatLng old_point= new LatLng (old_latitude,old_longitude);
                LatLng current_point= new LatLng(current_latitude, current_longitude);
                increase_dis = DistanceUtil.getDistance(old_point,current_point);

                //判断是否产生数据颠簸

                if(increase_dis < 40&&increase_dis!=0)
                {
                    //判断是否是第一次添加数据
                    addtime++;
                    if(addtime==2) {
                        if(isFirstadd)
                        {
                            points.add(old_point);
                            isFirstadd=false;
                        }
                        speed = increase_dis / 2.0;//计算即时速度
                        speedShow.setText(df.format(speed) + "m/s");
                        totle_dis += increase_dis;
                        distanceShow.setText(df.format(totle_dis) + 'm');
                        mMapView.getMap().clear();
                        points.add(current_point);
                        OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
                        mBaiduMap.addOverlay(ooPolyline);
                        addtime--;
                    }


                }
                else
                {
                    addtime=0;
                }

            }

            else
            {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
                current_latitude = ll.latitude;
                current_longitude = ll.longitude;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }



        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    public String convertTime(int seconds)
    {

        int hour=seconds/3600;
        String hourtext=Integer.toString(hour);
        if(hour<10)
        {
            hourtext='0'+hourtext;
        }
        seconds-=hour*3600;
        int min=seconds/60;

        String mintext=Integer.toString(min);
        if(min<10)
        {
            mintext='0'+mintext;
        }
        seconds-=min*60;

        String secondtext=Integer.toString(seconds);
        if(seconds<10)
        {
            secondtext='0'+secondtext;
        }


        return hourtext+':'+mintext+':'+secondtext+'s';

    }

    private class MyClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            //   SharedPreferences.Editor editor = sp.edit();
            //  editor.putFloat("totalDis",totle_dis);
            //    editor.commit();
            finish();
        }
    }

}
