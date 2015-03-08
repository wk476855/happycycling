package tang.map.module;

import tang.map.data.Protocol;
import tang.map.data.SocketTerm;
import tang.map.data.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wk on 2014/11/7.
 */

public class RegisterModule extends RootModule {

    public String register(User user){
        try{
            sendPool.push(user.toJson().toString(), Protocol.REGISTER_SEND_CMD);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return getPackContent(Protocol.REGISTER_REC_CMD);
    }

}
