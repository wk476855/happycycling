package tang.map.module;

import tang.map.data.ReceivePool;
import tang.map.data.SendPool;
import tang.map.data.SocketTerm;

import org.json.JSONObject;

/**
 * Created by wk on 2014/11/11.
 */
public class RootModule {

    JSONObject json = null;
    volatile ReceivePool receivePool = null;
    volatile SendPool sendPool = null;

    public RootModule(){
        json = new JSONObject();
        receivePool = ReceivePool.getInstance();
        sendPool = SendPool.getInstance();
    }

    public String getPackContent(Integer recCmd){
        int cnt = 0;
        while(cnt++ < SocketTerm.MAX_CONNECT_TIMES){
            if(receivePool.hasCmd(recCmd)){
                return receivePool.get(recCmd);
            }
            else{
                try {
                    Thread.sleep(200);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
