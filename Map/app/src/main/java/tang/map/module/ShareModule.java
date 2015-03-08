package tang.map.module;

import android.graphics.Bitmap;

import org.json.JSONException;

import tang.map.data.DataToolkit;
import tang.map.data.Protocol;

/**
 * Created by wk on 2014/12/19.
 */
public class ShareModule extends RootModule{

    public String share(double longitude, double latitude, String comment, String pic,String location){
        try {
            json.put("longitude", longitude);
            json.put("latitude", latitude);
            String content = comment + "#" + pic;
            json.put("content", content);
            json.put("location",location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendPool.push(json.toString(), Protocol.SHARE_SEND_CMD);
        return getPackContent(Protocol.SHARE_REC_CMD);
    }

    public String getShare(float longitude, float latitude, int index, int distance){
        try{
            json.put("longitude", longitude);
            json.put("latitude", latitude);
            json.put("index", index);
            json.put("distance", distance);
        }catch (JSONException e){
            e.printStackTrace();
        }
        sendPool.push(json.toString(), Protocol.GETSHARE_SEND_CMD);
        return getPackContent(Protocol.GETSHARE_REC_CMD);
    }
}
