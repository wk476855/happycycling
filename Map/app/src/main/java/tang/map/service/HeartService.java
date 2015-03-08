package tang.map.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import tang.map.data.ClientSocket;

import org.json.JSONException;
import org.json.JSONObject;

public class HeartService extends Service {

    private ClientSocket clientSocket = null;
    private JSONObject json  = new JSONObject();
    private SharedPreferences sp;

    private final int HEART_SEND_CMD = 105;


    public HeartService() {
    }

    @Override
    public void onCreate(){
//        new Thread(){
//            @Override
//            public void run(){
//                sp = getSharedPreferences("user", Context.MODE_PRIVATE);
//                while(true){
//                    clientSocket = ClientSocket.getInstance();
//                    long session = sp.getLong("session", 0);
//                    try{
//                        json.put("session", session);
//                    }catch (JSONException e){
//                        e.printStackTrace();
//                    }
//                    clientSocket.send(json.toString(), HEART_SEND_CMD);
//                    try{
//                        Thread.sleep(5000);
//                    }catch (InterruptedException e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
