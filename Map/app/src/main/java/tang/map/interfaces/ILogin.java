package tang.map.interfaces;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by ximing on 2014/12/25.
 */
public interface ILogin extends IRegister {
    public void writeData(JSONObject json) throws JSONException;
}
