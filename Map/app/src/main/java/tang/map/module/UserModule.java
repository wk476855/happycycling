package tang.map.module;

import org.json.JSONException;

import tang.map.data.Protocol;

/**
 * Created by wk on 2014/12/3.
 */
public class UserModule extends RootModule{

    public String getUser(int userid){
       try {
           json.put("userid", userid);
       }catch (JSONException e){
           e.printStackTrace();
       }
       sendPool.push(json.toString(), Protocol.USERINFOC_SEND_CMD);
       return getPackContent(Protocol.USERINFOC_REC_CMD);
    }
}
