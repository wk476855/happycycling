package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;

import tang.map.module.TeamModule;

/**
 * Created by wk on 2015/3/14.
 */
public class SOSThread extends RootThread{

    public SOSThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run() {
        TeamModule tm = new TeamModule();
        tm.help();
    }
}
