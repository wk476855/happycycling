package tang.map.cycle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import tang.map.R;
import tang.map.data.DataToolkit;

public class showphoto extends Activity {

    private Button back = null;
    private TextView latlng = null;
    private ImageView photo = null;
    private TextView description = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showphoto);

        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();

        init();
        Bundle data = this.getIntent().getBundleExtra("details");
        latlng.setText(data.getString("latlng"));
        photo.setImageBitmap(DataToolkit.StringToBitmap(data.getString("photo")));
        description.setText(data.getString("description"));
    }

    public void init()
    {
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new MyClickListener());
        latlng = (TextView)findViewById(R.id.latlng);
        photo = (ImageView)findViewById(R.id.photo);
        description = (TextView)findViewById(R.id.description);
    }

    public class MyClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.back)
                finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.showphoto, menu);
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
