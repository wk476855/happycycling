package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import tang.map.module.CommunicationModule;

/**
 * Created by ximing on 2014/12/25.
 */
public class ReceiveMsgThread extends RootThread
{

    public ReceiveMsgThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run()
    {
        CommunicationModule cm = new CommunicationModule();
        String result = cm.recMessage();
        Message msg = new Message();
        msg.what = 0x007;
        Bundle bundle = new Bundle();
        bundle.putString("result", result);
        msg.setData(bundle);
        this.handler.sendMessage(msg);
    }
}
