package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import tang.map.module.LocationModule;
import tang.map.module.TeamModule;

/**
 * Created by ximing on 2014/12/12.
 */
public class GetUsersThread extends RootThread
{

    public GetUsersThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }

    @Override
    public void run()
    {
        int type = this.bundle.getInt("type");
        String result = "";
        switch (type)
        {
            case 0:
                LocationModule lm = new LocationModule();
                long session = this.bundle.getLong("session");
                int distance = this.bundle.getInt("distance");
                int sex = this.bundle.getInt("sex");
                if(distance != 0 && sex != -1)
                {
                    result = lm.askForOthersLocation(session, 1, distance,2,sex);
                }
                else if(distance != 0)
                {
                    result = lm.askForOthersLocation(session,1,distance);
                }
                else
                {
                    result = lm.askForOthersLocation(session,2,sex);
                }
                break;
            case 1:
                //get teammates
                TeamModule tm = new TeamModule();
                result = tm.showTeam();
                break;
        }
        Bundle data = new Bundle();
        data.putString("result", result);
        Message msg = new Message();
        msg.what = 0x003;
        msg.setData(data);
        this.handler.sendMessage(msg);
    }
}
