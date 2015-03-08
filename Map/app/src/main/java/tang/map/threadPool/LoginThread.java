package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import tang.map.data.User;
import tang.map.module.LoginModule;

/**
 * Created by ximing on 2014/12/12.
 */
public class LoginThread extends RootThread
{

    public LoginThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run()
    {
        User user = (User)this.bundle.getSerializable("user");
        LoginModule lm = new LoginModule();
        String result = lm.login(user);
        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = 0x002;
        bundle.putString("result", result);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
