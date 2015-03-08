package tang.map.threadPool;

import android.os.Bundle;

import tang.map.data.User;
import tang.map.module.LocationModule;

/**
 * Created by ximing on 2014/12/12.
 */
public class UpdateLocThread extends RootThread
{

    public UpdateLocThread(Bundle bundle)
    {
        super(bundle);
    }

    @Override
    public void run()
    {
        User user = (User)this.bundle.getSerializable("user");
        LocationModule lm = new LocationModule();
        lm.updateLocation(user);
    }
}
