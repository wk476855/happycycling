package tang.map.threadPool;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import tang.map.data.Protocol;
import tang.map.data.SendPool;

public class HeartThread extends Thread{

    private SharedPreferences sp;
    private SendPool sendPool;
    private Context context;

    public HeartThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        sendPool = SendPool.getInstance();
        while (true) {
            long session = 0;
            if (sp.contains("session"))
                session = sp.getLong("session", 0);
            JSONObject json = new JSONObject();
            try {
                json.put("session", session);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendPool.push(json.toString(), Protocol.getProtocol(105));
            try {
                TimeUnit.SECONDS.sleep(120);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}