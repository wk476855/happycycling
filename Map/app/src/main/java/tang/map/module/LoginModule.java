package tang.map.module;

import android.content.Context;
import android.content.SharedPreferences;

import tang.map.data.Protocol;
import tang.map.data.SocketTerm;
import tang.map.data.User;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

/**
 * Created by wk on 2014/11/11.
 */
public class LoginModule extends RootModule {

    public String login(User user){

        try {
            sendPool.push(user.toJson().toString(), Protocol.LOGIN_SEND_CMD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPackContent(Protocol.LOGIN_REC_CMD);
    }

    public boolean checkLoginState(Context context){
        //过期时间个小时
        long limit = 60 * 60 * 1000;

        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        if(!sp.contains("lasttime"))
            return false;
        else{
            long lasttime = sp.getLong("lasttime", 0);
            long now = new Date().getTime();
            if(now - lasttime > limit)
                return false;
            else
                return true;
        }
    }
}
