package tang.map.data;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by wk on 2014/11/7.
 */

public class ClientSocket {

    public Socket socket = null;
    private BufferedOutputStream bos = null;
    private BufferedInputStream bis =null;

    public ClientSocket(String host, int port, String state) throws IOException{
//        socket = new Socket();
//        socket.setKeepAlive(false);
//        SocketAddress address = new InetSocketAddress(IP, port);
//        socket.connect(address, 2000);
//        socket.setSoTimeout (100);
//        socket.setSoTimeout(100);
        if (state.equals("test")) {
            // host IP
            socket = new Socket(host, port);
        }
        else {
            InetAddress netAddress = InetAddress.getByName(host);
            socket = new Socket(netAddress,port);
        }
        bis = new BufferedInputStream(socket.getInputStream());
        bos = new BufferedOutputStream(socket.getOutputStream());
    }

    public void sendHeart() throws IOException {
        socket.sendUrgentData(0xff);
    }

    private void sendPackage(byte[] data) throws IOException {
        bos.write(data);
        bos.flush();
    }

    private byte[] generateHeader(int contentLen, int cmd){
        int totalLen = contentLen + 8;
        byte[] data = new byte[totalLen];
        byte[] tmp = DataToolkit.intToByteArray(contentLen, 4);
        System.arraycopy(tmp, 0, data, 0, 4);
        tmp = DataToolkit.intToByteArray(cmd, 2);
        System.arraycopy(tmp, 0, data, 4, 2);
        tmp = DataToolkit.intToByteArray(0, 2);
        System.arraycopy(tmp, 0, data, 6, 2);
        return data;
    }

    /*
        发json字符串
     */
    public void send(String msg, int cmd) throws IOException {
        byte[] content = msg.getBytes();
        byte[] data = generateHeader(content.length, cmd);          //加包头
        System.arraycopy(content, 0, data, 8, content.length);
        sendPackage(data);
    }

    /*
        @method 返回json字符串
     */
    public  Map<Integer, String> recMsg() throws IOException{
        Map<Integer, String> pack = null;
        byte[] header = new byte[8];
        byte[] headerCopy = new byte[8];
        byte[] content = new byte[256];
        int len = 0;
        int cnt = 0;
        int contentLen = -1;
        int cmd = -1;
        int res = -1;
        int once;
        StringBuilder sb = new StringBuilder();
        /*
                 解析一下包头
        */
//        if ((len = bis.read(header, 0, 4)) == 4) {
//            contentLen = DataToolkit.byteArrayToInt(header, 4);
//            if ((len = bis.read(header, 0, 2)) == 2) {
//                cmd = DataToolkit.byteArrayToInt(header, 2);
//                if ((len = bis.read(header, 0, 2)) == 2) {
//                    res = DataToolkit.byteArrayToInt(header, 2);
//                    once = Math.min(contentLen, 256);
//                    while (contentLen > 0 && (len = bis.read(content, 0, once)) > 0) {
//                        contentLen -= len;
//                        sb.append(new String(content, 0, len));
//                    }
//                    pack = new HashMap<Integer, String>();
//                    pack.put(cmd, sb.toString());
//                }
//            }
//        }

        while(true){
            len = bis.read(header, cnt, 4-cnt);
            if(len <=  0)
                break;
//            System.err.println(Arrays.toString(header));
            Log.v("header", Arrays.toString(header));
            System.arraycopy(header, cnt, headerCopy, cnt, len);
            cnt += len;
            if(cnt == 4) {
                contentLen = DataToolkit.byteArrayToInt(headerCopy, 4);
//                System.err.println("contentLen   " + contentLen);
                Log.v("packByteLen", ""+contentLen);
                break;
            }
        }

        if(contentLen != -1){
            cnt = 0;
            while (true){
                len = bis.read(header, cnt, 2-cnt);
                System.arraycopy(header, cnt, headerCopy, cnt, len);
                cnt += len;
                if(cnt == 2){
                    cmd = DataToolkit.byteArrayToInt(headerCopy, 2);
//                    System.out.println("cmd   " + cmd);
                    break;
                }
            }
        }

        if(cmd != -1){
            cnt = 0;
            while (true){
                len = bis.read(header, cnt, 2-cnt);
                System.arraycopy(header, cnt, headerCopy, cnt, len);
                cnt += len;
                if(cnt == 2){
                    res = DataToolkit.byteArrayToInt(headerCopy, 2);
                    break;
                }
            }
        }

        if(res != -1){
            cnt = 0;
            while (true){
                once = Math.min(256, contentLen - cnt);
                if ((len = bis.read(content, 0, once))>0) {
                    cnt += len;
                    sb.append(new String(content, 0, len));
//                    System.err.println(new String(content, 0, len));
                    if (cnt >= contentLen) {
                        pack = new HashMap<Integer, String>();
                        pack.put(cmd, sb.toString());
                        break;
                    }
                }
            }
        }
        return pack;
    }

    public void close(){
        try {
            bis.close();
            bos.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
