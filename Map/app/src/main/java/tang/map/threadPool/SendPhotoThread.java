package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import tang.map.module.ShareModule;

/**
 * Created by ximing on 2014/12/26.
 */
public class SendPhotoThread extends RootThread{

    public SendPhotoThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run()
    {
        String description = this.bundle.getString("description");
        String bitmap = this.bundle.getString("photo");
        double longitude = this.bundle.getDouble("longitude");
        double latitude = this.bundle.getDouble("latitude");
        String location = this.bundle.getString("location");
        ShareModule sm = new ShareModule();
        String result = sm.share(longitude, latitude, description, bitmap,location);
        Bundle data = new Bundle();
        data.putString("result", result);
        Message msg = new Message();
        msg.what = 0x011;
        msg.setData(data);
        handler.sendMessage(msg);
    }
}
