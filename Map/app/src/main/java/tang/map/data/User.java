package tang.map.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by ximing on 2014/11/14.
 * user object
 */
public class User implements Serializable{
    private String nickname;
    private String password;
    private String head;
    private String account;
    private int sex;
    private int userid;
    private double longitude;
    private double latitude;
    private int gid;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User(){}
    public User(JSONObject jsonObject)
    {
        try {
            this.userid = jsonObject.getInt("userid");
            this.nickname = jsonObject.getString("nickname");
            this.head = jsonObject.getString("head");
            this.sex = jsonObject.getInt("sex");
            this.gid = jsonObject.getInt("team");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }
    public String getNickname()
    {
        return this.nickname;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getPassword()
    {
        return this.password;
    }

    public void setHead(Bitmap bitmap)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
        byte[] image = bos.toByteArray();
        this.head = Base64.encodeToString(image,Base64.DEFAULT);
    }
    public Bitmap getHead()
    {
        if(this.head == null)
            return null;
        byte[] image = Base64.decode(this.head,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
        return  bitmap;
    }

    public void setSex(int sex)
    {
        this.sex = sex;
    }
    public int getSex()
    {
        return this.sex;
    }

    public void setAccount(String account)
    {
        this.account = account;
    }
    public String getAccount()
    {
        return this.account;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject userjson = new JSONObject();
        userjson.put("account",this.account);
        userjson.put("nickname",this.nickname);
        userjson.put("password",this.password);
        userjson.put("sex",this.sex);
        userjson.put("head",this.head);
        return userjson;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }
}
