package tang.map.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by wk on 2014/11/30.
 */
public class RootTable {

    SQLiteDatabase db = null;

    public RootTable(){
    }

    public void open(){
        File file = new File("/sdcard/happyCycling/");
        if(!file.exists()) {
            file.mkdirs();
        }
        db = SQLiteDatabase.openOrCreateDatabase("/sdcard/happyCycling/happyCycling.db", null);
    }

    public void close(){
        db.close();
    }

    boolean isTableExist(String table) {
        open();
        boolean isTableExist=true;
        Cursor c = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='" + table + "'", null);
        if(c.getCount() < 1)
            isTableExist = false;
        c.close();
        close();
        return isTableExist;
    }
}
