package tang.map.service;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.json.JSONObject;

import tang.map.data.ClientSocket;
import tang.map.data.ReceivePool;

public class SocketService extends Service {

    ClientSocket clientSocket = null;
    ReceivePool receivePool = null;
    private final int MESSAGE_REC_CMD = 113;
    private final int mid = 11111111;
    JSONObject json = null;

    public SocketService() {
    }

    @Override
    public void onCreate() {

//        final Handler myHandler = new Handler() {
//            int userid;
//            int groupid;
//            @Override
//            public void handleMessage(Message msg) {
//                if(msg.what == 0x111){
//                    String content = "";
//                    Bundle data = msg.getData();
//                    if(data != null){
//                       try{
//                           json = new JSONObject(data.getString("result"));
//                           if(json.has("userid")){
//                               userid = json.getInt("userid");
//                               content += userid;
//                               if(json.has("groupid")){
//                                   groupid  = json.getInt("groupid");
//                                   if(groupid != 0){
//                                       content += "在" + groupid + "中";
//                                   }
//                               }
//                               content += "说话了";
//                           }
//                       }catch (JSONException e){
//                           e.printStackTrace();
//                       }
//                    }
//
//                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SocketService.this)
//                                                           .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
//                                                           .setSmallIcon(R.drawable.log)
//                                                           .setContentTitle("通知")
//                                                            .setContentText(content);
//                    Bundle userData = new Bundle();
//                    User user = new User();
//                    user.setUserid(userid);
//                    userData.putSerializable("user",user);
//                    Intent resultIntent = new Intent(SocketService.this, chat.class);
//                    resultIntent.putExtras(userData);
//
//                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(SocketService.this);
//                    stackBuilder.addParentStack(chat.class);
//                    stackBuilder.addNextIntent(resultIntent);
//                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//                    mBuilder.setContentIntent(resultPendingIntent);
//                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    notificationManager.notify(mid, mBuilder.build());
//                }
//            }
//        };
//
//        new Thread(){
//            @Override
//            public void run(){
//                while (true){
//                    clientSocket = ClientSocket.getInstance();
//                    recievePool = RecievePool.getInstance();
//                    Map<Integer, String> pair = null;
//                    pair = clientSocket.recMsg();
//                    if(pair != null) {
//                        Set<Integer> keySet = pair.keySet();
//                        Iterator<Integer> iterator = keySet.iterator();
//                        while (iterator.hasNext()) {
//                            Integer key = Protocol.getProtocol(iterator.next());
//                            recievePool.put(key, pair.get(key));
//                            System.err.println(key);
//                            System.err.println(pair.get(key));
//                            if(key == MESSAGE_REC_CMD) {
//                                String result = pair.get(key);
//                                  Bundle data = new Bundle();
//                                data.putString("result", result);
//                                Message msg = new Message();
//                                msg.what = 0x111;
//                                msg.setData(data);
//                                myHandler.sendMessage(msg);
//                            }
//
//                        }
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

    @Override
    public void onDestroy(){
        if(clientSocket != null)
            clientSocket.close();
    }
}
