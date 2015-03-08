package tang.map.module;

import org.json.JSONException;

import tang.map.data.Protocol;

/**
 * Created by wk on 2015/1/10.
 */
public class PersonModule extends RootModule{

    public String updatePersonInfo(String nickname, String oldpassword, String password) {
        try {
            json.put("oldpassword", oldpassword);
            json.put("password", password);
            json.put("nickname", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendPool.push(json.toString(), Protocol.USERINFOU_SEND_CMD);
        return getPackContent(Protocol.USERINFOU_REC_CMD);
    }
}
