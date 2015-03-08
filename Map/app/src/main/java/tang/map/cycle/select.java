package tang.map.cycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import tang.map.R;

public class select extends Activity {

    private RadioGroup sex = null;
    private RadioButton sexBoy = null;
    private RadioButton sexGirl = null;
    private RadioGroup dist = null;
    private RadioButton distFive = null;
    private RadioButton distTen = null;
    private RadioButton distHundred = null;
    private Button back = null;
    private Button finish = null;
    private int sexType = -1;
    private int distType = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        init();
        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();

    }

    private void init()
    {
        sex = (RadioGroup)findViewById(R.id.sex);
        sexBoy = (RadioButton)findViewById(R.id.sex_boy);
        sexGirl = (RadioButton)findViewById(R.id.sex_girl);
        dist = (RadioGroup)findViewById(R.id.dist);
        distFive = (RadioButton)findViewById(R.id.dist_five);
        distTen = (RadioButton)findViewById(R.id.dist_ten);
        distHundred = (RadioButton)findViewById(R.id.dist_hundred);
        back = (Button)findViewById(R.id.back);
        finish = (Button)findViewById(R.id.finish);
        back.setOnClickListener(new MyClickListener());
        finish.setOnClickListener(new MyClickListener());
        sex.setOnCheckedChangeListener(new SelectListener());
        dist.setOnCheckedChangeListener(new SelectListener());
    }

    private boolean isSelected()
    {
        if(sexType == -1 && distType == 0)
        {
            Toast.makeText(this,"您还没选择一种筛选方式",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }
    private class MyClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.back)
                finish();
            else if(id == R.id.finish)
            {
                if(isSelected()) {
                    Intent data = new Intent();
                    data.putExtra("sex", sexType);
                    data.putExtra("distance", distType);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        }
    }

    private class SelectListener implements RadioGroup.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getId();
            if(id == sex.getId())
            {
                if (checkedId == sexBoy.getId())
                {
                    sexType = 1;
                    sexBoy.setBackgroundResource(R.drawable.select_sex_boy_selected);
                    sexGirl.setBackgroundResource(R.drawable.select_sex_girl);
                }
                else if(checkedId == sexGirl.getId())
                {
                    sexType = 0;
                    sexBoy.setBackgroundResource(R.drawable.select_sex_boy);
                    sexGirl.setBackgroundResource(R.drawable.select_sex_girl_selected);
                }
            }
            else if(id == dist.getId())
            {
                if(checkedId == distFive.getId())
                {
                    distType = 5000;
                    distFive.setBackgroundResource(R.drawable.select_dist_5_selected);
                    distTen.setBackgroundResource(R.drawable.select_dist_10);
                    distHundred.setBackgroundResource(R.drawable.select_dist_100);
                }
                else if(checkedId == distTen.getId())
                {
                    distType = 10000;
                    distFive.setBackgroundResource(R.drawable.select_dist_5);
                    distTen.setBackgroundResource(R.drawable.select_dist_10_selected);
                    distHundred.setBackgroundResource(R.drawable.select_dist_100);
                }
                else if(checkedId == distHundred.getId())
                {
                    distType = 100000;
                    distFive.setBackgroundResource(R.drawable.select_dist_5);
                    distTen.setBackgroundResource(R.drawable.select_dist_10);
                    distHundred.setBackgroundResource(R.drawable.select_dist_100_selected);
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select, menu);
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
