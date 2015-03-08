package tang.map.threadPool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

import tang.map.data.CommunicationItem;
import tang.map.data.CommunicationTable;
import tang.map.data.DataToolkit;
import tang.map.module.CommunicationModule;

/**
 * Created by ximing on 2014/12/12.
 */
public class SendMsgThread extends RootThread
{

    private int id;

    public SendMsgThread(Handler handler, Bundle bundle) {
        super(handler, bundle);
    }


    public void setId(int id)
    {
        this.id = id;
    }

    @Override
    public void run()
    {
        String content = "";
        int type = this.bundle.getInt("type");
        int gid = this.bundle.getInt("gid");
        int userid = this.bundle.getInt("userid");
        int method = this.bundle.getInt("method");
        String path = this.bundle.getString("path");
        if(method == 0)
            gid = 0;
        if(type == 1)
            content = DataToolkit.voiceToString(path);
        else
            content = this.bundle.getString("word");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        CommunicationModule cm = new CommunicationModule();
        String result = cm.sendMessage(content, type, userid, gid, datetime);
        if(method == 0) {
            CommunicationTable ct = new CommunicationTable();
            CommunicationItem item = new CommunicationItem();
            item.setSendOrRec(0);
            item.setType(type);
            if(type == 1)
                item.setContent(path);
            else
                item.setContent(content);
            item.setUserid(userid);
            item.setDate(datetime);
            ct.insert(item);
        }
        Message msg = new Message();
        msg.what = id;
        Bundle bundle = new Bundle();
        bundle.putString("result", result);
        if(type == 1)
            bundle.putString("path", this.bundle.getString("path"));
        else
            bundle.putString("word",this.bundle.getString("word"));
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
