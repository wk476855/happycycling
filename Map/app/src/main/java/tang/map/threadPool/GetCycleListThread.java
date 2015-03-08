package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import tang.map.module.ShareModule;

/**
 * Created by ximing on 2014/12/25.
 */
public class GetCycleListThread extends RootThread
{
    public GetCycleListThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run()
    {
        float longitude = 0f, latitude = 0f;
        if(bundle.containsKey("longitude")){
            longitude = bundle.getFloat("longitude");
        }
        if(bundle.containsKey("latitude")){
            latitude = bundle.getFloat("latitude");
        }
        ShareModule sm = new ShareModule();
        String result = sm.getShare(longitude, latitude, 0, 1000);
        Bundle data = new Bundle();
        data.putString("result", result);
        Message msg = new Message();
        msg.what = 0x010;
        msg.setData(data);
        handler.sendMessage(msg);
    }
}
