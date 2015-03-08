package tang.map.start;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Date;

import tang.map.R;
import tang.map.cycle.home;
import tang.map.interfaces.IWelcome;
import tang.map.service.CommunicationService;
import tang.map.threadPool.MyHandler;

public class welcome extends Activity implements IWelcome {

    private ImageView pic;
    private ImageView words;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        startService(new Intent(welcome.this, CommunicationService.class));

        pic = (ImageView)findViewById(R.id.pic);
        words = (ImageView)findViewById(R.id.words);
        Animation pic_animation = AnimationUtils.loadAnimation(
                welcome.this, R.anim.start_animation);
        Animation words_animation = AnimationUtils.loadAnimation(
                welcome.this,R.anim.words_animation);
        pic.startAnimation(pic_animation);
        words.startAnimation(words_animation);
        words_animation.setAnimationListener(new MyAnimationListener());

        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
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

    @Override
    public void jump() {
        Intent intent = new Intent(welcome.this,start.class);
        startActivity(intent);
    }

    private class MyAnimationListener implements Animation.AnimationListener
    {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            jump();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
