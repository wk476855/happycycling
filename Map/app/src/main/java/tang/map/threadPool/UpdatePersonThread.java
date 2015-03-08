package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;

import tang.map.module.PersonModule;

/**
 * Created by wk on 2015/1/10.
 */
public class UpdatePersonThread extends RootThread{

    public UpdatePersonThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run() {
        PersonModule pm = new PersonModule();

        String oldpassword = "";
        if(bundle.containsKey("oldpassword"))
            oldpassword = bundle.getString("oldpassword");
        String password = "";
        if(bundle.containsKey("password"))
            password = bundle.getString("password");
        String nickname = "";
        if(bundle.containsKey("nickname"))
            nickname = bundle.getString("nickname");
        String result = pm.updatePersonInfo(nickname, oldpassword, password);
        sendMsg(result, 0x013);
    }
}
