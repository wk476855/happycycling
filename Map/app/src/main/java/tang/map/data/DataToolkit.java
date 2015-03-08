package tang.map.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.BitSet;
import java.util.Random;

/**
 * Created by wk on 2014/11/2.
 */

public class DataToolkit {

    public static int byteArrayToInt(byte[] data, int len){
        int value = 0;
        for(int i = 0; i < len; i++){
            int shift = i * 8;
            value += (0x000000ff &  data[i]) << shift;
        }
        return value;
    }

    public static byte[] intToByteArray(int value, int byteSize){
        byte[] data = new byte[byteSize];
        for(int i = 0; i < byteSize; i++){
            int shift = i * 8;
            data[i] = (byte) ((value >> shift) & 0xFF);
        }
        return data;
    }

    public static byte[] stringToByteArray(String str){
        return str.getBytes(Charset.forName("utf-8"));
    }

    public static String byteArrayToString(byte[] data){
        return new String(data, Charset.forName("utf-8"));
    }

    public static String voiceToString(String voiceName){
//        StringBuilder sb = new StringBuilder();
        String sb = "";
        byte[] data = new byte[1200];
        int len;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(voiceName));
            try {
                while ((len = fis.read(data)) > 0){
                     sb += (Base64.encodeToString(data, Base64.NO_WRAP));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb;
    }

    public static String stringToVoice(String content){
        Random random = new Random();
        String pathName = "/sdcard/happyCycling/";
        String voiceName = String.valueOf(random.nextInt(100000)) + ".3gp";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(pathName + voiceName));
            try {
                for(int i = 0; i < content.length()/1600; i++){
                    String str = content.substring(1600*i, 1600*(i+1));
                    fos.write(Base64.decode(str, Base64.NO_WRAP));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.err.println(voiceName);
        return pathName + voiceName;
    }

    public static String BitmapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
        byte[] image = bos.toByteArray();
        return Base64.encodeToString(image,Base64.DEFAULT);
    }

    public static Bitmap StringToBitmap(String str)
    {
        byte[] image = Base64.decode(str,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        return  bitmap;
    }

    public static byte[] BitmapToByte(Bitmap bitmap)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
        return bos.toByteArray();
    }

    public static byte[] compressImage(Bitmap bitmap)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
        int options = 100;
        while(bos.toByteArray().length / 1024 > 10)
        {
            options -= 5;
            if(options < 0)
                break;
            bos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG,options,bos);
        }
        return bos.toByteArray();
    }
}
