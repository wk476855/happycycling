package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import tang.map.module.TeamModule;

/**
 * Created by ximing on 2015/1/10.
 */
public class CreateTeamThread extends RootThread {
    public CreateTeamThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run() {
        String name = this.bundle.getString("name");
        String content = this.bundle.getString("content");
        TeamModule tm = new TeamModule();
        String result = tm.createTeam(name, content);
        sendMsg(result,0x101);
    }
}
