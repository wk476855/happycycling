package tang.map.threadPool;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import tang.map.data.User;
import tang.map.module.RegisterModule;

/**
 * Created by ximing on 2014/12/12.
 */
public class RegisterThread extends RootThread
{
    public RegisterThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run()
    {
        User user = (User)this.bundle.getSerializable("user");
        RegisterModule rm = new RegisterModule();
        String result = rm.register(user);
        Message msg = new Message();
        msg.what = 0x001;
        Bundle bundle = new Bundle();
        if(result == null)
        {
            bundle.putInt("success",2);
        }
        else
        {
            try
            {
                JSONObject jsonObject = new JSONObject(result);
                int success = jsonObject.getInt("code");
                bundle.putInt("success",success);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        msg.setData(bundle);
        this.handler.sendMessage(msg);
    }
}
