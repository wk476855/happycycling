package tang.map.module;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tang.map.data.Protocol;
import tang.map.data.User;

/**
 * Created by wk on 2014/11/22.
 */
public class LocationModule extends RootModule{

    public String updateLocation(User user){
        try{
            json.put("longitude", user.getLongitude());
            json.put("latitude", user.getLatitude());
        }catch (JSONException e){
            e.printStackTrace();
        }
        sendPool.push(json.toString(), Protocol.UPDATELOC_SEND_CMD);
        return getPackContent(Protocol.UPDATELOC_REC_CMD);
    }

    public String askForOthersLocation(long session, Object... args){

        if(args.length < 1 || args.length % 2 != 0)
            return null;
        JSONArray jsonArray = new JSONArray();
        try{
            json.put("session", session);
            for(int i=0; i<args.length; i+=2){
                JSONObject tmp = new JSONObject();
                tmp.put("type", args[i]);
                tmp.put("para", args[i+1]);
                jsonArray.put(tmp);
            }
            json.put("list", jsonArray);
        }catch (JSONException e){
            e.printStackTrace();
        }
        sendPool.push(json.toString(), Protocol.ASKLOC_SEND_CMD);
        return  getPackContent(Protocol.ASKLOC_REC_CMD);
    }
}
