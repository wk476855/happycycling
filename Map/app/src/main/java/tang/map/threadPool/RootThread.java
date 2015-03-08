package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by wk on 2015/1/10.
 */
public class RootThread extends Thread{
    protected Handler handler;
    protected Bundle bundle;

    public RootThread(Bundle bundle){
        this.bundle = bundle;
    }

    public RootThread(Handler handler, Bundle bundle)
    {
        this.handler = handler;
        this.bundle = bundle;
    }

    public void sendMsg(String result, int what) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("result", result);
        msg.setData(data);
        msg.what = what;
        handler.sendMessage(msg);
    }
}
