package tang.map.module;

import org.json.JSONException;

import java.net.PortUnreachableException;

import tang.map.data.Protocol;

/**
 * Created by wk on 2014/11/28.
 */
public class TeamModule extends RootModule{

    public String createTeam(String teamName, String des){
        try{
            json.put("teamname", teamName);
            json.put("desp", des);
            sendPool.push(json.toString(), Protocol.CREATE_SEND_CMD);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return getPackContent(Protocol.CREATE_REC_CMD);
    }

    public String updateTeamInfo(String des){
        try{
            json.put("desp", des);
            sendPool.push(json.toString(), Protocol.UPDATEINFO_SEND_CMD);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return getPackContent(Protocol.UPDATEINFO_REC_CMD);
    }

    public String joinTeam(int gid, String verify){
        try{
            json.put("gid", gid);
            json.put("vertify", verify);
            sendPool.push(json.toString(), Protocol.JOIN_SEND_CMD);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return getPackContent(Protocol.JOIN_REC_CMD);
    }


    public String request(){
        return getPackContent(Protocol.REQUEST_REC_CMD);
    }

    public void accept(int reqId, int code){
        try{
            json.put("id", reqId);
            json.put("code", code);
            sendPool.push(json.toString(), Protocol.ACCEPT_REC_SEND);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String quit(){
        sendPool.push("{}", Protocol.QUIT_SEND_CMD);
        return getPackContent(Protocol.QUIT_REC_CMD);
    }

    public String kickout(int userId){
       try{
           json.put("userid", userId);
           sendPool.push(json.toString(), Protocol.KICKOUT_SEND_CMD);
       }catch (JSONException e){
           e.printStackTrace();
       }
        return getPackContent(Protocol.KICKOUT_REC_CMD);
    }

    public String showTeam(){
        sendPool.push("{}", Protocol.SHOWTEAM_SEND_CMD);
        return getPackContent(Protocol.SHOWTEAM_REC_CMD);
    }

    public String getTeamIfo(){
        sendPool.push("{}", Protocol.GETINFO_SEND_CMD);
        return getPackContent(Protocol.GETINFO_REC_CMD);
    }

    public void help(){
        sendPool.push("{}", Protocol.HELP_SEND_CMD);
    }
}
