package tang.map.data;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wk on 2014/11/30.
 */
public class ApplicationTable extends RootTable{

    public ApplicationTable(){
        super();
        if(!isTableExist("application")){
            String sql = "create table application(aid Integer primary key, userid Integer, nickname varchar(255), verify varchar(255), head blob);";
            open();
            db.execSQL(sql);
            close();
        }
    }


    public void insert(Application application){
        open();
        Cursor cursor = db.rawQuery("select * from application where aid="+application.getAid(), null);
        if(cursor.getCount() < 1)
            db.execSQL("insert into application values(?,?,?,?,?)", new Object[]{application.getAid(), application.getUserid(), application.getNickname(), application.getVerify(), application.getHead().getBytes()});
        close();
    }

    public void delete(int aid){
        open();
        db.execSQL("delete from application where aid=" + aid);
        close();
    }

    public List<Application> get(){
        List<Application> list = new ArrayList<Application>();
        open();
        Cursor cursor = db.rawQuery("select userid, nickname, aid, verify, head from application", null);
        while(cursor.moveToNext()){
            Application application = new Application();
            application.setUserid(cursor.getInt(0));
            application.setNickname(cursor.getString(1));
            application.setAid(cursor.getInt(2));
            application.setVerify(cursor.getString(3));
            application.setHead(new String(cursor.getBlob(4)));
            list.add(application);
        }
        close();
        return list;
    }
}
