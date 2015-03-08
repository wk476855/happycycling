package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;

import tang.map.module.TeamModule;

/**
 * Created by wk on 2015/1/10.
 */
public class ExitTeamThread extends RootThread{

    public ExitTeamThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run() {
        TeamModule tm = new TeamModule();
        String result = tm.quit();
        sendMsg(result, 0x012);
    }
}
