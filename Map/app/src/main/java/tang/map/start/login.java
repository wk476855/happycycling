package tang.map.start;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import tang.map.R;
import tang.map.cycle.home;
import tang.map.data.User;
import tang.map.interfaces.ILogin;
import tang.map.service.CommunicationService;
import tang.map.threadPool.LoginThread;
import tang.map.threadPool.MyHandler;

/**
 * Created by ximing on 2014/11/14.
 */
public class login extends Activity implements ILogin
{

    private Button back = null;
    private Button finish = null;
    private EditText account = null;
    private EditText passwd = null;
    private User user = null;
    private SharedPreferences sp;

    private ProgressDialog loginProgress = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
//        sp = this.getSharedPreferences("user", Context.MODE_PRIVATE);
//        if (sp.contains("cookie")) {
//            long cookie = sp.getLong("cookie", 0);
//            long now = new Date().getTime();
//            if (now - cookie < 30 * 60 * 1000) {
//                Intent intent = new Intent(this, home.class);
//                startActivity(intent);
//                return;
//            }
//        }


        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();
        init();
    }

    public void init()
    {

        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new MyClickListener());
        finish = (Button)findViewById(R.id.finish);
        finish.setOnClickListener(new MyClickListener());

        account = (EditText)findViewById(R.id.account);
        passwd = (EditText)findViewById(R.id.passwd);

        user = new User();
        user = (User)this.getIntent().getSerializableExtra("user");
        if(user == null)
        {
            user = new User();
        }
        else
        {
            account.setText(user.getAccount());
            passwd.setText(user.getPassword());
            finish.callOnClick();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void show(String str) {
        Toast.makeText(this,str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void writeData(JSONObject json) throws JSONException
    {
        SharedPreferences sp = getSharedPreferences("user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(json.has("userid"))
            editor.putInt("userid", json.getInt("userid"));
        if(json.has("team"))
        {
            editor.putInt("gid", json.getInt("team"));
        }
        if(json.has("nickname"))
            editor.putString("nickname", json.getString("nickname"));
        if(json.has("session"))
            editor.putLong("session", json.getLong("session"));
        if(json.has("head"))
            editor.putString("head", json.getString("head"));
        if(json.has("sex"))
            editor.putInt("sex", json.getInt("sex"));
        if(json.has("isleader")) {
            editor.putInt("isleader", json.getInt("isleader"));
        }
        editor.putString("account", user.getAccount());
        editor.putString("password",user.getPassword());
        editor.putLong("cookie", new Date().getTime());
        editor.commit();
    }

    @Override
    public void clear() {
        loginProgress.dismiss();
    }

    @Override
    public void jump() {
        Intent intent = new Intent(this, home.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class MyClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            int id = v.getId();
            switch (id)
            {
                case R.id.back:
                    finish();
                    break;
                case R.id.finish:
                    if (isLegal())
                    {
                        loginProgress = ProgressDialog.show(login.this,"Logining...","please wait...",true,false);
                        user.setAccount(account.getText().toString());
                        user.setPassword(passwd.getText().toString());
                        Bundle loginData = new Bundle();
                        loginData.putSerializable("user",user);
                        MyHandler loginHandler = new MyHandler(login.this);
                        LoginThread loginThread = new LoginThread(loginHandler,loginData);
                        loginThread.start();
                    }
            }
        }
    }

    private boolean isLegal()
    {
        boolean islegal = true;
        String account_s = account.getText().toString();
        String passwd_s = passwd.getText().toString();
        if(account_s.equals(""))
        {
            account.requestFocus();
            islegal = false;
            Toast.makeText(login.this, "fill account", Toast.LENGTH_SHORT).show();
        }
        else if(passwd_s.equals(""))
        {
            passwd.requestFocus();
            islegal = false;
            Toast.makeText(login.this,"fill password",Toast.LENGTH_SHORT).show();
        }
        return islegal;
    }
}
