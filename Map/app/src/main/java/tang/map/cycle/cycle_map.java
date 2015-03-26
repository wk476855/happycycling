package tang.map.cycle;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import tang.map.R;
import tang.map.data.DataToolkit;

/**
 * A simple {@link Fragment} subclass.
 */
public class cycle_map extends Fragment {

    private View root = null;
    private MapView cycleMap = null;
    private BaiduMap baiduMap = null;
    private LocationClient cycleLoc = null;
    private ArrayList<HashMap<String, Object>> photolist = null;
    private Context context = null;
    private boolean isFirstLoc = true;// 是否首次定位




    public cycle_map() {
        // Required empty public constructor
    }

    public void setPhotolist(ArrayList<HashMap<String, Object>> photolist) {
        this.photolist = photolist;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.cycle_map, container, false);
        setMap();
        return root;
    }

    private void setMap()
    {
        cycleMap = (MapView)root.findViewById(R.id.cycleMap);
        baiduMap = cycleMap.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setMyLocationEnabled(true);
   //     MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
  //      baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode,true,null));
        cycleLoc = new LocationClient(context);
        cycleLoc.registerLocationListener(new MyLocationLister());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd0911");
        cycleLoc.setLocOption(option);
        cycleLoc.start();

        MapLoadedListener mapLoadedListener = new MapLoadedListener(this.photolist);
        baiduMap.setOnMapLoadedCallback(mapLoadedListener);
    }

    private void showMap(ArrayList<HashMap<String,Object>> photolist)
    {
        for(int i = 0;i < photolist.size(); i++)
        {
            HashMap<String,Object> tmp = photolist.get(i);
            double longitude = (Double)tmp.get("longitude");
            double latitude = (Double)tmp.get("latitude");
            LatLng point = new LatLng(31.025395,121.442027);
            Bitmap photo = DataToolkit.StringToBitmap(tmp.get("photo").toString());
            BitmapDescriptor photoDescriptor = BitmapDescriptorFactory.fromBitmap(photo);
            OverlayOptions photoOptions = new MarkerOptions().icon(photoDescriptor).position(point);
            baiduMap.addOverlay(photoOptions);

            BitmapDescriptor pinkDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.cycle_pink);
            OverlayOptions pinkOptions = new MarkerOptions().icon(pinkDescriptor).position(point);
            baiduMap.addOverlay(pinkOptions);
        }
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    private class MapLoadedListener implements BaiduMap.OnMapLoadedCallback
    {
        private ArrayList<HashMap<String, Object>> photolist = null;
        public MapLoadedListener(ArrayList<HashMap<String,Object>> photolist)
        {
            this.photolist = photolist;
        }
        @Override
        public void onMapLoaded() {
            showMap(this.photolist);
        }
    }
    private class MyLocationLister implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location) {

            if(isFirstLoc) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(u);
                isFirstLoc=false;
            }
            if(location == null || cycleMap == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                 //   .accuracy(location.getRadius())
                    .direction(0).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
        }


    }


    @Override
    public void onDestroy() {
        cycleLoc.stop();
        super.onDestroy();
    }
}
