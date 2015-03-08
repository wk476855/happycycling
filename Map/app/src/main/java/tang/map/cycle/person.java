package tang.map.cycle;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import tang.map.R;
import tang.map.data.DataToolkit;
import tang.map.interfaces.IPerson;
import tang.map.threadPool.ExitTeamThread;
import tang.map.threadPool.MyHandler;
import tang.map.threadPool.UpdatePersonThread;

public class person extends Activity implements IPerson{

    private Button back = null;
    private Button exit = null;

    private ImageView head = null;

    private RelativeLayout me = null;
    private ImageView modify = null;
    private EditText nickname = null;
    private EditText passwd = null;
    private EditText repasswd = null;
    private ImageView save = null;
    private ImageView cancel = null;
    private RelativeLayout info = null;
    private RelativeLayout info_modify = null;

    private RelativeLayout team = null;
    private TextView teamname = null;
    private EditText teamslogan = null;
    private ImageView exitTeam = null;

    private ImageView meIV = null;
    private ImageView teamIV = null;
    private RelativeLayout set = null;

    private SharedPreferences user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person);
        user = getSharedPreferences("user", Context.MODE_PRIVATE);

        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();

        init();
        setData();
    }

    private void init()
    {
        set = (RelativeLayout)findViewById(R.id.switchSet);
        if(user.getInt("gid",0) == 0)
            set.setVisibility(View.GONE);
        back = (Button)findViewById(R.id.back);
        exit = (Button)findViewById(R.id.exit);
        back.setOnClickListener(new MyClickListener());
        exit.setOnClickListener(new MyClickListener());

        me = (RelativeLayout)findViewById(R.id.me_layout);
        team = (RelativeLayout)findViewById(R.id.team_layout);

        modify = (ImageView)findViewById(R.id.passwd_modify);
        modify.setOnClickListener(new MyClickListener());
        nickname = (EditText)findViewById(R.id.nickname);
        passwd = (EditText)findViewById(R.id.passwd);
        repasswd = (EditText)findViewById(R.id.repasswd);
        save = (ImageView)findViewById(R.id.save);
        cancel = (ImageView)findViewById(R.id.cancel);
        save.setOnClickListener(new MyClickListener());
        cancel.setOnClickListener(new MyClickListener());

        info = (RelativeLayout)findViewById(R.id.info_layout);
        info_modify = (RelativeLayout)findViewById(R.id.modify_layout);

        teamname = (TextView)findViewById(R.id.team_name);
        teamslogan = (EditText)findViewById(R.id.slogan);
        exitTeam = (ImageView)findViewById(R.id.team_exit);
        exitTeam.setOnClickListener(new MyClickListener());
        if(user.getInt("isleader",0) == 1)
            exitTeam.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.person_team_dismiss));

        head = (ImageView)findViewById(R.id.head);

        meIV = (ImageView)findViewById(R.id.me);
        teamIV = (ImageView)findViewById(R.id.team);
        meIV.setOnClickListener(new MyClickListener());
        teamIV.setOnClickListener(new MyClickListener());
    }

    private void setData()
    {
        String str = user.getString("head","");
        if(user.getString("head","") == "")
            head.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.register_head));
        else
            head.setImageBitmap(DataToolkit.StringToBitmap(user.getString("head", "")));
        nickname.setText(user.getString("nickname", ""));
        teamname.setText(String.valueOf(user.getInt("gid",0)));
    }


    @Override
    public void notifyUser(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void writeData(int gid) {
        SharedPreferences sp = getSharedPreferences("user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("gid",0);
        editor.commit();
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
                case R.id.exit:
                    if(user.contains("cookie")){
                        SharedPreferences.Editor editor = user.edit();
                        editor.putLong("cookie", 0);
                        editor.commit();
                    }
                    Intent i= new Intent(Intent.ACTION_MAIN);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
                    i.addCategory(Intent.CATEGORY_HOME);
                    startActivity(i);
                    break;
                case R.id.passwd_modify:
                    info_modify.setVisibility(View.GONE);
                    info.setVisibility(View.VISIBLE);
                    nickname.setEnabled(true);
                    break;
                case R.id.cancel:
                    info_modify.setVisibility(View.VISIBLE);
                    info.setVisibility(View.GONE);
                    nickname.setEnabled(true);
                    break;
                case R.id.save:
                    Bundle bundle = new Bundle();
                    bundle.putString("nickname", nickname.getText().toString());
                    bundle.putString("oldpassword", passwd.getText().toString());
                    bundle.putString("password", repasswd.getText().toString());
                    new UpdatePersonThread(new MyHandler(person.this), bundle).start();
                    break;
                case R.id.me:
                    me.setVisibility(View.VISIBLE);
                    team.setVisibility(View.GONE);
                    meIV.setBackgroundResource(R.drawable.person_switch_me_selected);
                    teamIV.setBackgroundResource(R.drawable.person_switch_team);
                    break;
                case R.id.team:
                    me.setVisibility(View.GONE);
                    team.setVisibility(View.VISIBLE);
                    meIV.setBackgroundResource(R.drawable.person_switch_me);
                    teamIV.setBackgroundResource(R.drawable.person_switch_team_selected);
                    break;
                case R.id.team_exit:
                    //TODO exit team
                    new ExitTeamThread(new MyHandler(person.this), null).start();
                    break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.person, menu);
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
