package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;

import tang.map.module.TeamModule;

/**
 * Created by ximing on 2015/1/10.
 */
public class JoinTeamThread extends RootThread {
    public JoinTeamThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run() {
        int teamId = this.bundle.getInt("teamId");
        String verify = this.bundle.getString("verify");
        TeamModule tm = new TeamModule();
        String result = tm.joinTeam(teamId, verify);
        sendMsg(result,0x102);
    }
}
