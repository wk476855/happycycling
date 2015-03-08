package tang.map.team;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tang.map.R;
import tang.map.adapter.MessageListAdapter;
import tang.map.data.Application;
import tang.map.data.ApplicationTable;
import tang.map.module.TeamModule;

public class applytoteam extends Activity {

    private ListView messages = null;
    private MessageListAdapter messageListAdapter = null;
    private Button back = null;
    private ImageView head = null;
    private ApplicationTable at = null;
    private List<Application> messagelist = new ArrayList<Application>();
    int msgid = -1;
    int code = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applytoteam);
        init();
        at = new ApplicationTable();
        messagelist = at.get();
        messageListAdapter.setMessagelist(messagelist);
        messageListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init()
    {
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new MyClickListener());
        head = (ImageView)findViewById(R.id.head);
        messages = (ListView)findViewById(R.id.messages);
        messageListAdapter = new MessageListAdapter(this,R.layout.messages,messagelist,this);
        messages.setAdapter(messageListAdapter);
    }

    private class MyClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id)
            {
                case R.id.back:
                    finish();
                    break;
                case R.id.agree:
                    //TODO agree to join in the team
                    code = 0;
                    break;
                case R.id.reject:
                    //TODO reject to join in the team
                    finish();
                    break;
            }
//            new Thread(){
//                @Override
//                public void run() {
//                    TeamModule tm = new TeamModule();
//                    tm.accept(msgid, code);
//                }
//            }.start();
        }
    }
}
