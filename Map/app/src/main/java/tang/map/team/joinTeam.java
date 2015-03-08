package tang.map.team;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import tang.map.R;
import tang.map.interfaces.IJoinTeam;
import tang.map.module.TeamModule;
import tang.map.threadPool.JoinTeamThread;
import tang.map.threadPool.MyHandler;

public class joinTeam extends Activity implements IJoinTeam{

    private EditText teamid = null;
    private Button finish = null;
    private Button back = null;
    private EditText verify = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jointeam);

        teamid = (EditText) findViewById(R.id.teamname);
        finish = (Button) findViewById(R.id.finish);
        finish.setOnClickListener(new MyOnClickListener());
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new MyOnClickListener());
        verify = (EditText) findViewById(R.id.verify);
    }

    @Override
    public void close() {
        finish();
    }


    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if(id == R.id.finish) {
                int teamId = Integer.parseInt(teamid.getText().toString());
                Bundle data = new Bundle();
                data.putInt("teamId", teamId);
                data.putString("verify", verify.getText().toString());
                MyHandler joinTeam = new MyHandler(joinTeam.this);
                JoinTeamThread joinTeamThread = new JoinTeamThread(joinTeam,data);
                joinTeamThread.start();
            }
            else if(id == R.id.back)
            {
                finish();
            }
        }
    }


    @Override
    public void show(String str) {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.jointeam, menu);
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
