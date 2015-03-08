package tang.map.data;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wk on 2015/1/12.
 */
public class CommunicationTable extends RootTable{

    public CommunicationTable(){
        super();
        if(!isTableExist("communication")){
            String sql = "create table communication(userid Integer, sendOrRec Integer, type Integer, content varchar(200), date varchar(255));";
            open();
            db.execSQL(sql);
            close();
        }
    }

    public void insert(CommunicationItem item) {
        open();
        db.execSQL("insert into communication values(?,?,?,?,?)", new Object[]{item.getUserid(), item.getSendOrRec(), item.getType(), item.getContent(), item.getDate()});
        close();
    }

    public List<CommunicationItem> get(int userid){
        List<CommunicationItem> list = new ArrayList<CommunicationItem>();
        open();
        Cursor cursor = db.rawQuery("select  userid, sendOrRec, type, content, date from communication where userid=? order by date desc limit 4", new String[]{String.valueOf(userid)});
        while(cursor.moveToNext()){
            CommunicationItem item = new CommunicationItem();
            item.setUserid(cursor.getInt(0));
            item.setSendOrRec(cursor.getInt(1));
            item.setType(cursor.getInt(2));
            item.setContent(cursor.getString(3));
            item.setDate(cursor.getString(4));
            list.add(item);
        }
        Collections.reverse(list);
        close();
        return list;
    }
}
