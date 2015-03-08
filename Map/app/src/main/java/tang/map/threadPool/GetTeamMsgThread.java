package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import tang.map.module.CommunicationModule;

/**
 * Created by ximing on 2014/12/13.
 */
public class GetTeamMsgThread extends RootThread
{

    public GetTeamMsgThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run()
    {
        CommunicationModule cm = new CommunicationModule();
        String result = cm.recMessage();
        Bundle data = new Bundle();
        data.putString("result", result);
        Message msg = new Message();
        msg.what = 0x005;
        msg.setData(data);
        this.handler.sendMessage(msg);
    }
}
