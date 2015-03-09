package tang.map.threadPool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import tang.map.cycle.chat;
import tang.map.cycle.cycle;
import tang.map.cycle.map;
import tang.map.cycle.person;
import tang.map.cycle.sendphoto;
import tang.map.data.DataToolkit;
import tang.map.data.User;
import tang.map.interfaces.IPerson;
import tang.map.start.login;
import tang.map.start.register;
import tang.map.start.welcome;
import tang.map.team.createTeam;
import tang.map.team.joinTeam;
import tang.map.voiceHelper.VoicePlay;

/**
 * Created by ximing on 2014/12/12.
 */
public class MyHandler extends Handler
{
    private Activity activity;      // the activity class for the context
    public MyHandler(Activity activity)
    {
        this.activity = activity;
    }
    @Override
    public void handleMessage(Message msg)
    {
        /**
         * handler for welcome
         */
        if(msg.what == 0x000)
        {
            welcome wel = (welcome)activity;
            wel.jump();
        }
        /**
         * handler for register
         */
        if(msg.what == 0x001)
        {
            register reg = (register)activity;
            reg.clear();
            Bundle data = msg.getData();
            int success = data.getInt("success");
            if(success == 0)
            {
                reg.show("注册成功");
                reg.jump();
            }
            else if(success == 1)
            {
                reg.show("注册失败");
            }
            else
            {
                reg.show("网络连接失败");
            }
        }
        /**
         * handler for login
         */
        else if(msg.what == 0x002)
        {
            login log = (login)activity;
            log.clear();
            Bundle data = msg.getData();
            String result = data.getString("result");
            if(result != null)
            {
                try
                {
                    JSONObject json = new JSONObject(result);
                    if (json.has("code"))
                    {
                        int code = json.getInt("code");
                        switch (code)
                        {
                            case 0:
                                log.show("登陆成功");
                                log.writeData(json);
                                log.jump();
                                break;
                            case 1:
                                log.show("登陆失败");
                                break;
                            case 2:
                                log.show("网络连接失败");
                                break;
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        /**
         * handler for get users
         */
        else if(msg.what == 0x003)
        {
            map mmap = (map)activity;
            String result = msg.getData().getString("result");
            if(result == null)
            {
                mmap.show("网络连接失败");

            }
            else
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.has("code"))
                    {
                        int code = jsonObject.getInt("code");
                        if (code == 0)
                        {
                            ArrayList<User> users = new ArrayList<User>();
                            if (jsonObject.has("list"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("list");
                                int len = jsonArray.length();
                                for (int i = 0; i < len; i++)
                                {
                                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                                    User user = new User();
                                    if (jsonItem.has("userid"))
                                    {
                                        user.setUserid(jsonItem.getInt("userid"));
                                    }
                                    if (jsonItem.has("type"))
                                    {
                                        SharedPreferences sp = mmap.getSharedPreferences("user", Context.MODE_PRIVATE);
                                        if (sp.getInt("userid", 0) == user.getUserid())
                                            user.setType(0);
                                        else
                                            user.setType(2);
                                    }
                                    if (jsonItem.has("longitude"))
                                    {
                                        if (jsonItem.has("latitude"))
                                        {
                                            user.setLatitude(jsonItem.getDouble("latitude"));
                                            user.setLongitude(jsonItem.getDouble("longitude"));
                                        }
                                    }
                                    if(jsonItem.has("nickname"))
                                    {
                                        user.setNickname(jsonItem.getString("nickname"));
                                    }
                                    if(jsonItem.has("head"))
                                    {
                                        user.setHead(DataToolkit.StringToBitmap(jsonItem.getString("head")));
                                    }
                                    if(jsonItem.has("sex"))
                                    {
                                        user.setSex(jsonItem.getInt("sex"));
                                    }
                                    users.add(user);
                                }
                            }
                            /**
                             * show users
                             */
                            mmap.showUsers(users);
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        /**
         * handler for shout
         */
        else if(msg.what == 0x004)
        {
            map mmap = (map)activity;
            String result = msg.getData().getString("result");
            if(result != null)
            {
                int code = 0;
                try
                {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.has("code"))
                    {
                        code = jsonObject.getInt("code");
                        if(code == 0)
                        {
                            mmap.show("一键喊话成功");
                            mmap.clear();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        /**
         * handler for team message
         */
        else if(msg.what == 0x005)
        {
            map mmap = (map)activity;
            boolean flag = true;
            String result = msg.getData().getString("result");
            if(result != null)
            {
                int userid = 0;
                String content = null;
                try
                {
                    JSONObject json = new JSONObject(result);
                    if (json.has("userid"))
                    {
                        userid = json.getInt("userid");
                    }
                    if (json.has("content"))
                    {
                        content = json.getString("content");
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                /**
                 * set users' ui
                 */
                SharedPreferences sp = mmap.getSharedPreferences("user",Context.MODE_PRIVATE);
                if(sp.contains("userid"))
                {
                    if(userid == sp.getInt("userid", 0))
                        flag = false;
                }
                if (flag)
                {
                    mmap.setUsersUI(0,userid);
                    String voicepath = DataToolkit.stringToVoice(content);
                    VoicePlay voicePlay = new VoicePlay();
                    voicePlay.setPath(voicepath);
                    voicePlay.startPlaying();
                    this.sendEmptyMessageDelayed(0x006,voicePlay.getMediaPlayer().getDuration());
                }
            }
        }
        /**
         * handler for reset users' ui
         */
        else if (msg.what == 0x006)
        {
            map mmap = (map)activity;
            mmap.setUsersUI(1,0);
        }
        /**
         * handler for receiving others message
         */
        else if(msg.what == 0x007)
        {
            chat ch = (chat)activity;
            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            if(result != null)
            {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.has("type"))
                    {
                        switch (json.getInt("type"))
                        {
                            case 0:
                                String content = json.getString("content");
                                ch.addTextToList(1,0,content);
                                break;
                            case 1:
                                String voiceName = DataToolkit.stringToVoice(json.getString("content"));
                                ch.addTextToList(1,1,voiceName);
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        /**
         * handler for voice sending under activity chat
         */
        else if(msg.what == 0x008)
        {
            chat ch = (chat)activity;
            String result = msg.getData().getString("result");
            if(result != null)
            {
                try
                {
                    JSONObject json = new JSONObject(result);
                    if (json.has("code"))
                    {
                        int code = json.getInt("code");
                        if (code == 0)
                        {
                            ch.addTextToList(0, 1, msg.getData().getString("path"));
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        /**
         * handler for text sending under activity chat
         */
        else if(msg.what == 0x009)
        {
            chat ch = (chat)activity;
            String result = msg.getData().getString("result");
            if(result != null)
            {
                try
                {
                    JSONObject json = new JSONObject(result);
                    if (json.has("code"))
                    {
                        int code = json.getInt("code");
                        if (code == 0)
                        {
                            ch.addTextToList(0,0,msg.getData().getString("word"));
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        /**
         * handler for get the cycle list
         */
        else if(msg.what == 0x010)
        {
            cycle cy = (cycle)activity;
            String result = msg.getData().getString("result");
            if (result != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("list"))
                    {
                        JSONArray list = jsonObject.getJSONArray("list");
                        int len = list.length();
                        ArrayList<HashMap<String,Object>> photolist = new ArrayList<HashMap<String, Object>>();
                        HashMap<String,Object> item;
                        for (int i = 0; i < len; i++)
                        {
                            item = new HashMap<String, Object>();
                            JSONObject json = list.getJSONObject(i);
                            if(json.has("userid")){
                                item.put("userid",json.getInt("userid"));
                            }
                            if(json.has("longitude") && json.has("latitude")){
                                item.put("longitude",json.getDouble("longitude"));
                                item.put("latitude",json.getDouble("latitude"));
                            }
                            if(json.has("content")){
                                String content = json.getString("content");
                                String[] res = content.split("#");
                                //res[0] 描述  res[1] 图片字符串
                                item.put("description",res[0]);
                                item.put("photo",res[1]);
                            }
                            if(json.has("time"))
                            {
                                item.put("time",json.getString("time"));
                            }
                            if(json.has("location"))
                            {
                                item.put("location",json.getString("location"));
                            }
                            photolist.add(item);
                        }
                        cy.showList(photolist);
                        /*cy.addToList(photolist);
                        cy.addToMap(photolist);*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        /**
         * handler for sending photo
         */
        else if(msg.what == 0x011)
        {
            sendphoto sp = (sendphoto)activity;
            String result = msg.getData().getString("result");
            if(result != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.has("code"))
                    {
                        int code = jsonObject.getInt("code");
                        sp.jump(code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        else if (msg.what == 0x012)
        {
            String result = msg.getData().getString("result");

            if (result != null) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.has("code")) {
                        int code = json.getInt("code");
                        IPerson per = (person) activity;
                        if (code == 0) {
                            per.notifyUser("退出队伍成功!");
                            per.writeData(0);
                        }else if (code == 101){
                            per.notifyUser("队伍中还有成员，不能退出队伍!");
                        }else{
                            per.notifyUser("您已不在队伍中！");
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
        else if (msg.what == 0x013) {
            String result = msg.getData().getString("result");
            if (result != null) {
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.has("code")) {
                        int code = json.getInt("code");
                        IPerson per = (person) activity;
                        if (code == 0) {
                            per.notifyUser("个人信息修改成功！");
                        }else {
                            per.notifyUser("个人信息修改失败！");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        /**
         * handler for create team
         */
        else if(msg.what == 0x101)
        {
            createTeam ct = (createTeam)activity;
            String result = msg.getData().getString("result");
            JSONObject json = null;
            try{
                if(result != null)
                    json = new JSONObject(result);
                if (json.has("code"))
                {
                    int code = json.getInt("code");
                    if(code == 0)
                    {
                        int gid = json.getInt("gid");
                        ct.show("创建队伍成功!\n队伍id：" + gid);
                        ct.writeData(gid);
                        ct.close();
                    }
                    else
                    if(code == 1){
                        ct.show("队伍已存在!");
                    }else if(code == 101){
                        ct.show("您已有队伍了!");
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        /**
         * handler for join team
         */
        else if(msg.what == 0x102)
        {
            joinTeam jt = (joinTeam)activity;
            String result = msg.getData().getString("result");
            if(result != null)
            {
                try{
                    JSONObject json = new JSONObject(result);
                    if(json.has("code"))
                    {
                        int code = json.getInt("code");
                        if(code == 0)
                        {
                            jt.show("申请发送成功");
                            jt.close();
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
