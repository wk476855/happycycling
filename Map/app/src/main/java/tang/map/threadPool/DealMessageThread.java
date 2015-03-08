package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;

import tang.map.module.TeamModule;


/**
 * Created by wk on 2015/1/10.
 */
public class DealMessageThread extends RootThread{
    public DealMessageThread(Handler handler,Bundle bundler){super(handler,bundler);}

    @Override
    public void run() {
        int id = bundle.getInt("id");
        int code = bundle.getInt("code");
        TeamModule tm = new TeamModule();
        tm.accept(id,code);
    }
}
