package tang.map.interfaces;


import java.util.ArrayList;

import tang.map.data.User;

/**
 * Created by ximing on 2014/12/25.
 */
public interface IMap extends IRegister {
    public void showUsers(ArrayList<User> users);
    public void setUsersUI(int type,int userid);
}
