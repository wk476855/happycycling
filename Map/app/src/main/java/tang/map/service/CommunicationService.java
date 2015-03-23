package tang.map.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import tang.map.R;
import tang.map.cycle.home;
import tang.map.data.Application;
import tang.map.data.ApplicationTable;
import tang.map.data.CommunicationItem;
import tang.map.data.CommunicationTable;
import tang.map.data.DataToolkit;
import tang.map.module.LoginModule;
import tang.map.start.start;
import tang.map.start.welcome;
import tang.map.team.applytoteam;
import tang.map.cycle.chat;
import tang.map.data.ClientSocket;
import tang.map.data.Protocol;
import tang.map.data.ReceivePool;
import tang.map.data.SendPool;
import tang.map.data.User;

public class CommunicationService extends Service {

    private ClientSocket clientSocket;
    private SendPool sendPool = null;
    private ReceivePool receivePool = null;
    private SharedPreferences sp = null;
    private SharedPreferences sp1 = null;
    private ApplicationTable at = null;
    private CommunicationTable ct = null;
    private AssetManager am = null;


    public CommunicationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createConn(){
        String host = null;
        int port = 0;
        String state = null;
        try {
            ServiceInfo info = getPackageManager().getServiceInfo(new ComponentName(this, CommunicationService.class), PackageManager.GET_META_DATA);
            state = info.metaData.getString("state");
            if (state.equals("test")) {
                host = info.metaData.getString("testServer");
                port = info.metaData.getInt("testPort");
            }else {
                host = info.metaData.getString("releaseServer");
                port = info.metaData.getInt("releasePort");
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            clientSocket = new ClientSocket(host, port, state);
        }catch (SocketTimeoutException ste){
              myHandler.sendEmptyMessage(0x100);
              ste.printStackTrace();
        }catch (IOException e) {
            myHandler.sendEmptyMessage(0x100);
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        sp1 = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        at = new ApplicationTable();
        ct = new CommunicationTable();

        //创建和服务器的连接
        new CreateConnect().start();

        //获取发送池和接受池
        sendPool = SendPool.getInstance();
        receivePool = ReceivePool.getInstance();

        //创建发送数据包的线程
        new SendThread().start();

        //创建接受数据包的线程
        new ReceiveThread().start();

        am = getAssets();

//        new HeartThread().start();

    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x100){
                Toast.makeText(CommunicationService.this, "网络未连接", Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 0x101){
                String result = msg.getData().getString("result");
//                System.out.println(result.substring(result.length() - 50));
                JSONObject json = null;
                try {
                    json = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int gid = -1;
                if(sp.contains("gid"))
                    gid = sp.getInt("gid", -1);
                try {
                    if(json.has("groupid") && gid == json.getInt("groupid")) {
                        Intent intent = new Intent();
                        intent.setAction("shout");
                        sendBroadcast(intent);
//                        if(sp1.contains("currentActivity") && sp1.getString("currentActivity", "").equals("map")) {
//                            receivePool.remove(Protocol.MESSAGE_REC_CMD);
//                            JSONObject json2 = new JSONObject();
//                            json2.put("code", 0);
//                            json2.put("datetime", json.getString("datetime"));
//                            sendPool.push(json2.toString(), Protocol.MESSAGE_RECR_CMD);
//                        }
                    }else {
                        boolean flag = false;
                        CommunicationItem item = new CommunicationItem();
                        if(json.has("userid")){
                            item.setUserid(json.getInt("userid"));
                            item.setSendOrRec(1);
                            if(json.has("type")) {
                                int type = json.getInt("type");
                                item.setType(type);
                                if(json.has("datetime")) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    item.setDate(sdf.format(new Date()));
                                    if(json.has("content")){
                                        if(type == 1){
                                            item.setContent(DataToolkit.stringToVoice(json.getString("content")));
                                        }else {
                                            item.setContent(json.getString("content"));
                                        }
                                        ct.insert(item);
                                        flag = true;
                                    }
                                }
                            }
                        }

                        //判断当前界面

                        if (sp1 != null && sp1.contains("currentActivity") && sp1.getString("currentActivity", "").equals("chat")) {
                            Intent intent = new Intent();
                            intent.setAction("chat");
                            sendBroadcast(intent);
                        } else {

                            if(flag) {
                                receivePool.remove(Protocol.MESSAGE_REC_CMD);
                                JSONObject json2 = new JSONObject();
                                json2.put("code", 0);
                                json2.put("datetime", json.getString("datetime"));
                                sendPool.push(json2.toString(), Protocol.MESSAGE_RECR_CMD);
                            }
                            int id = 1;
                            User user = new User();
                            try {
                                if (json.has("userid"))
                                    user.setUserid(json.getInt("userid"));
                                if(json.has("nickname"))
                                    user.setNickname(json.getString("nickname"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String content = user.getNickname()+ "来消息了";
                            Intent resultIntent = new Intent(CommunicationService.this, chat.class);
                            Bundle data = new Bundle();
                            data.putSerializable("user", user);
                            resultIntent.putExtras(data);
                            MyNotification mn = new MyNotification();
                            mn.notifyActivity(101, resultIntent,  content);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(msg.what == 0x102){
                String result = msg.getData().getString("result");
                Application app = new Application();
                String nickname = "";
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.has("nickname")) {
                        nickname = json.getString("nickname");
                        app.setUserid(json.getInt("userid"));

                        if (json.has("id")) {
                            app.setAid(json.getInt("id"));
                            if(json.has("vertify")){
                                app.setVerify(json.getString("vertify"));
                                if(json.has("head")){
                                    app.setHead(json.getString("head"));
                                    if(json.has("nickname")) {
                                        app.setNickname(json.getString("nickname"));
                                        at.insert(app);
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String content = nickname + "申请加入队伍";
                Intent resultIntent = new Intent(CommunicationService.this, applytoteam.class);
                MyNotification mn = new MyNotification();
                mn.notifyActivity(102, resultIntent, content);

            }

            if(msg.what == 0x103){
                String result = msg.getData().getString("result");
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.has("code")){
                        int code = json.getInt("code");
                        if(code == 0){
                            if(json.has("gid")){
                                int gid = json.getInt("gid");
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("gid", gid);
                                editor.commit();
                                Toast.makeText(CommunicationService.this, "您已成功加入队伍" + gid, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(CommunicationService.this, "您申请的队伍拒绝了您", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(msg.what == 0x104){
                String result = msg.getData().getString("result");
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.has("type")){
                        int type = json.getInt("type");
                        switch (type) {
                            case 2:
                                if(json.has("nickname"))
                                    Toast.makeText(CommunicationService.this, json.getString("nickname")+"退出了队伍!", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                if(json.has("nickname"))
                                    Toast.makeText(CommunicationService.this, json.getString("nickname")+"被踢出了队伍!", Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                Toast.makeText(CommunicationService.this, "你所在队伍解散!", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(msg.what == 0x105) {

                try {
                    AssetFileDescriptor afd = am.openFd("sos.mp3");
                    MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mp.prepare();
                    mp.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(msg.what == 0x106) {
                String result = msg.getData().getString("result");
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.has("nickname"))
                        Toast.makeText(CommunicationService.this, String.valueOf(json.getString("nickname")), Toast.LENGTH_SHORT).show();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(msg.what == 0x111){
                Intent intent = new Intent(CommunicationService.this, home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(CommunicationService.this, "您已登录！", Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 0x112){
//                Intent intent = new Intent(CommunicationService.this, welcome.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            }
        }
    };

    private class MyNotification{

        public void notifyActivity(int notificationId, Intent intent, String content){
            PendingIntent pendingIntent = PendingIntent.getActivity(CommunicationService.this, 0, intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(CommunicationService.this)
                    .setSmallIcon(R.drawable.icon)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setContentText(content)
                    .setContentTitle("骑乐融融")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationId, builder.build());
        }
    }

    private class CreateConnect extends Thread{
        @Override
        public void run() {
            while (clientSocket == null) {
                createConn();
                if(clientSocket != null)
                    checkLogin();
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
        发送线程
     */
    private class SendThread extends Thread{
        @Override
        public void run() {
            final boolean connected = false;
            super.run();
            while (true){
                if(clientSocket != null) {
                    while (!sendPool.isEmpty()) {
                        Map<Integer, String> pair = sendPool.pop();
                        Set<Integer> keySet = pair.keySet();
                        Iterator<Integer> iterator = keySet.iterator();
                        while (iterator.hasNext()) {
                            int cmd = iterator.next();
                            System.err.println("send" + cmd + pair.get(cmd));
                            try {
                                clientSocket.send(pair.get(cmd), cmd);
                            } catch (IOException e) {
//                                clientSocket.close();
                                clientSocket = null;
                                new CreateConnect().start();
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
//                    if (!sendPool.isEmpty()){
//                        createConn();
//                    }
                }
            }
        }
    }

    /*
        接受线程
     */
    private class ReceiveThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (true){
                if(clientSocket != null) {
                    Map<Integer, String> pair = null;
                    try {
                        pair = clientSocket.recMsg();
                    }catch (SocketTimeoutException ste){
                        myHandler.sendEmptyMessage(0x100);
                        ste.printStackTrace();
                    }catch(IOException e) {
                        myHandler.sendEmptyMessage(0x100);
                        e.printStackTrace();
                    }
                    if(pair != null) {
                        Set<Integer> keySet = pair.keySet();
                        Iterator<Integer> iterator = keySet.iterator();
                        while (iterator.hasNext()) {
                            Integer cmd = Protocol.getProtocol(iterator.next());
                            receivePool.put(cmd, pair.get(cmd));
                            System.err.println("receive: CMD:" + cmd +" packLen: "+ pair.get(cmd).length()+"  content: "+pair.get(cmd));

                            /*
                                 判断接受的包，并通知用户
                             */

                            if(cmd == Protocol.HEART_REC_CMD){
                                receivePool.remove(cmd);
                            }

                            if (cmd == Protocol.MESSAGE_REC_CMD) {
                                String result = pair.get(cmd);
                                sendMsg(result, 0x101);
                            }

                            if(cmd == Protocol.REQUEST_REC_CMD){
                                String result = pair.get(cmd);
                                sendMsg(result, 0x102);
                                receivePool.remove(Protocol.REQUEST_REC_CMD);
                            }

                            if(cmd == Protocol.RESPONSE_REC_CMD){
                                String result = pair.get(cmd);
                                sendBack(result, Protocol.RESPONSE_REC_CMD, Protocol.RESPONSE_SEND_CMD);
                                sendMsg(result, 0x103);
                            }
                            if(cmd == Protocol.QUITR_REC_CMD) {
                                String result = pair.get(cmd);
                                sendBack(result, Protocol.QUITR_REC_CMD, Protocol.QUITR_SEND_CMD);
                                sendMsg(result, 0x104);
                            }
                            if(cmd == Protocol.HELP_REC_CMD) {
                                String result = pair.get(cmd);
                                receivePool.remove(cmd);
                                sendMsg(result, 0x105);
                            }
                            if(cmd == Protocol.LOST_REC_CMD) {
                                String result = pair.get(cmd);
                                receivePool.remove(cmd);
                                sendMsg(result, 0x10);
                            }
                        }
                    }
                }
            }
        }

        public void sendMsg(String result, int what){
            Bundle data = new Bundle();
            data.putString("result", result);
            Message msg = new Message();
            msg.what = what;
            msg.setData(data);
            myHandler.sendMessage(msg);
        }

        public void sendBack(String result, Integer recCmd, Integer sendCmd){
            receivePool.remove(recCmd);
            try {
                JSONObject json = new JSONObject(result);
                JSONObject json2 = new JSONObject();
                if(json.has("id")){
                    json2.put("id", json.get("id"));
                }
                sendPool.push(json2.toString(), sendCmd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class HeartThread extends Thread{
        @Override
        public void run() {
//            while (true){
//                if(clientSocket != null) {
//                    try {
//                        clientSocket.sendHeart();
//                        try {
//                            sleep(5000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        myHandler.sendEmptyMessage(0x111);
//                    }
//                }
//            }
        }
    }
//
    private void checkLogin(){
        new Thread(){
            @Override
            public void run() {
                if (!sp.contains("cookie")) {
                    return;
                }else {
                    long cookie = sp.getLong("cookie", 0);
                    long now = new Date().getTime();
                    if (now - cookie > 0 * 60 * 1000) {
                        myHandler.sendEmptyMessage(0x112);
                        return;
                    }
                }
                while (true) {
                    if (clientSocket != null) {
                        LoginModule lm = new LoginModule();
                        User user = new User();
                        if (sp.contains("account")) {
                            user.setAccount(sp.getString("account", ""));
                            if (sp.contains("password")) {
                                user.setPassword(sp.getString("password", ""));
                                lm.login(user);
                                myHandler.sendEmptyMessage(0x111);
                            }
                        }
                        //创建心跳线程
                        break;
                    }
                }
            }
        }.start();
    }
}
