package tang.map.team;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import tang.map.R;
import tang.map.interfaces.ICreateTeam;
import tang.map.interfaces.IRoot;
import tang.map.module.ShareModule;
import tang.map.module.TeamModule;
import tang.map.threadPool.CreateTeamThread;
import tang.map.threadPool.MyHandler;

public class createTeam extends Activity implements ICreateTeam{

    private TextView teamName = null;
    private TextView announce = null;
    private Button finish = null;
    private Button back = null;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createteam);

        teamName = (TextView) findViewById(R.id.teamname);
        announce = (TextView) findViewById(R.id.announce);
        finish =  (Button) findViewById(R.id.finish);
        finish.setOnClickListener(new MyClickListener());
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new MyClickListener());

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if(id == R.id.finish) {
                String name = teamName.getText().toString();
                String content = announce.getText().toString();
                Bundle data = new Bundle();
                data.putString("name",name);
                data.putString("content",content);
                MyHandler createTeam = new MyHandler(createTeam.this);
                CreateTeamThread createTeamThread = new CreateTeamThread(createTeam,data);
                createTeamThread.start();
            }
            else if(id == R.id.back)
            {
                finish();
            }
        }
    }


    @Override
    public void writeData(int gid) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("gid", gid);
        editor.commit();
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void show(String str) {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.createteam, menu);
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
}
