package tang.map.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

import tang.map.data.Protocol;

/**
 * Created by ximing on 2014/11/16.
 */
public class CommunicationModule extends RootModule{

    public String sendMessage(String message, int type,int userid, int groupid, String datetime){
        try {
            json.put("content", message);
            json.put("type", type);
            json.put("userid", userid);
            json.put("groupid", groupid);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            json.put("datetime", datetime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendPool.push(json.toString(), Protocol.MESSAGE_SEND_CMD);
        return getPackContent(Protocol.MESSAGE_SENDR_CMD);
    }

    public String recMessage(){
        String str = getPackContent(Protocol.MESSAGE_REC_CMD);
        try{
            if(str != null){
                json = new JSONObject(str);
                if(json.has("datetime")){
                    JSONObject json2 = new JSONObject();
                    json2.put("datetime", json.get("datetime"));
                    json2.put("code", 0);
                    sendPool.push(json2.toString(), Protocol.MESSAGE_RECR_CMD);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return str;
    }
}
