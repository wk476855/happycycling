package tang.map.data;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by wk on 2014/11/7.
 */
public class ReceivePool {

    private volatile static ReceivePool receivePool = new ReceivePool();
    private Map<Integer, String> data;   //cmd 对应 包体


    public static ReceivePool getInstance(){
        return receivePool;
    }

    private ReceivePool(){
        data = new IdentityHashMap<Integer, String>();

    }

    public synchronized void put(Integer cmd, String content){
        data.put(cmd, content);
    }

    public synchronized boolean hasCmd(Integer cmd){
        return data.containsKey(cmd);
    }

    public synchronized String get(Integer cmd){
        String content = data.get(cmd);
        data.remove(cmd);
        return content;
    }

    public synchronized void remove(Integer cmd){
        data.remove(cmd);
    }
}
