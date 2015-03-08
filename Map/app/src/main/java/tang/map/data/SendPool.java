package tang.map.data;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by wk on 2014/11/30.
 */

public class SendPool {

    private static SendPool sendPool = new SendPool();
    private Queue<Map<Integer, String>> queue = null;

    private SendPool(){
        queue = new ArrayDeque<Map<Integer, String>>();
    }

    public static SendPool getInstance(){
        return sendPool;
    }

    public synchronized void push(String content, Integer cmd){
        Map<Integer, String> pair = new HashMap<Integer, String>();
        pair.put(cmd, content);
        queue.add(pair);
    }

    public synchronized Map<Integer, String> pop(){
        return queue.remove();
    }

    public synchronized boolean isEmpty(){
        return queue.isEmpty();
    }
}
