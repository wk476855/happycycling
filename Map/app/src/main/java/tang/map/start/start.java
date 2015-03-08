package tang.map.start;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import tang.map.R;
import tang.map.service.CommunicationService;

/**
 * Created by ximing on 2014/11/14.
 */
public class start extends Activity {

    private Button registerButton;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        registerButton = (Button)findViewById(R.id.register);
        loginButton = (Button)findViewById(R.id.login);
        registerButton.setOnClickListener(new MyButtonClickListener());
        loginButton.setOnClickListener(new MyButtonClickListener());

        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();

//        startService(new Intent(start.this, CommunicationService.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyButtonClickListener implements Button.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            int id = view.getId();
            Intent intent;
            switch (id)
            {
                case R.id.register:
                    intent = new Intent(start.this,register.class);
                    startActivity(intent);
                    break;
                case R.id.login:
                    intent = new Intent(start.this,login.class);
                    startActivity(intent);
                    break;
            }

        }
    }
}
