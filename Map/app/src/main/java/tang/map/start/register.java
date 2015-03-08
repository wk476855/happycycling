package tang.map.start;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import tang.map.R;
import tang.map.data.DataToolkit;
import tang.map.data.User;
import tang.map.interfaces.IRegister;
import tang.map.threadPool.MyHandler;
import tang.map.threadPool.RegisterThread;

/**
 * Created by ximing on 2014/11/14.
 */
public class register extends Activity implements IRegister {

    private final int CAMERA_CODE = 0;
    private final int PICTURE_CODE = 1;
    private RadioButton boy = null;
    private RadioButton girl = null;
    private Button back = null;
    private Button finish = null;
    private Button camera = null;
    private Button album = null;
    private ImageView headImage = null;
    private ProgressDialog regiterProgress = null;

    //four input
    private EditText account = null;
    private EditText name = null;
    private EditText passwd = null;
    private EditText repasswd = null;

    //user
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        init();

        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();
    }

    private void init()
    {
        //radio group for sex
        boy = (RadioButton)findViewById(R.id.boy);
        girl = (RadioButton)findViewById(R.id.girl);
        boy.setOnClickListener(new MyClickListener());
        girl.setOnClickListener(new MyClickListener());

        //back button
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new MyClickListener());

        //finish button
        finish = (Button)findViewById(R.id.finish);
        finish.setOnClickListener(new MyClickListener());

        //four input
        account = (EditText)findViewById(R.id.account);
        name = (EditText)findViewById(R.id.name);
        passwd = (EditText)findViewById(R.id.passwd);
        repasswd = (EditText)findViewById(R.id.repasswd);

        //head image
        camera = (Button)findViewById(R.id.camera);
        camera.setOnClickListener(new MyClickListener());
        album = (Button)findViewById(R.id.album);
        album.setOnClickListener(new MyClickListener());
        headImage = (ImageView)findViewById(R.id.head);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void show(String str)
    {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    @Override
    public void clear() {
        regiterProgress.dismiss();
    }

    @Override
    public void jump()
    {
        Intent intent = new Intent(this,login.class);
        Bundle userBundle = new Bundle();
        userBundle.putSerializable("user",user);
        intent.putExtras(userBundle);
        startActivity(intent);
    }

    private class MyClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            int id = v.getId();
            Intent intent = null;
            switch (id)
            {
                case R.id.boy :
                    girl.setChecked(false);
                    user.setSex(1);
                    break;
                case R.id.girl :
                    boy.setChecked(false);
                    user.setSex(0);
                    break;
                case R.id.back :
                    finish();
                    break;
                case R.id.finish :
                    if(isLegal())
                    {
                        regiterProgress = ProgressDialog.show(register.this,"Registering...","please wait...",true,false);
                        user.setAccount(account.getText().toString());
                        user.setNickname(name.getText().toString());
                        user.setPassword(passwd.getText().toString());
                        headImage.setDrawingCacheEnabled(true);
                        user.setHead(Bitmap.createBitmap(headImage.getDrawingCache()));
                        headImage.setDrawingCacheEnabled(false);
                        Bundle registerData = new Bundle();
                        registerData.putSerializable("user",user);
                        MyHandler registerHandler = new MyHandler(register.this);
                        RegisterThread registerThread = new RegisterThread(registerHandler,registerData);
                        registerThread.start();
                    }
                    break;
                case R.id.album:
                    intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,PICTURE_CODE);
                    break;
                case R.id.camera:
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            String sdcard = Environment.getExternalStorageState();
            if(!sdcard.equals(Environment.MEDIA_MOUNTED))
            {
                Toast.makeText(register.this,"sdcard unmounted", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap)bundle.get("data");
            FileOutputStream out = null;
            File file = new File("/sdcard/happyCycling/");
            if(!file.exists())
                file.mkdir();
            try {
                out = new FileOutputStream(new File(file,"head_tmp.jpg"));
                byte[] result = DataToolkit.compressImage(bitmap);
                out.write(result,0,result.length);
                out.close();
                bitmap = BitmapFactory.decodeByteArray(result,0,result.length);
                headImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == PICTURE_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            cursor.moveToFirst();
            String imgpath = cursor.getString(1);
            Bitmap bitmap = BitmapFactory.decodeFile(imgpath);
            byte[] result = DataToolkit.compressImage(bitmap);
            bitmap = BitmapFactory.decodeByteArray(result,0,result.length);
            headImage.setImageBitmap(bitmap);
        }
    }



    private boolean isLegal()
    {
        boolean islegal = true;
        String account_s = account.getText().toString();
        String name_s = name.getText().toString();
        String passwd_s = passwd.getText().toString();
        String repasswd_s = repasswd.getText().toString();
        boolean boystate = boy.isChecked();
        boolean girlstate = girl.isChecked();
        if(boystate == false && girlstate == false)
        {
            boy.requestFocus();
            islegal = false;
            Toast.makeText(register.this,"choose sex",Toast.LENGTH_SHORT).show();
        }
        else if(account_s.equals("") || account_s.contains(" "))
        {
            account.setText("");
            account.requestFocus();
            islegal = false;
            Toast.makeText(register.this,"account illegal", Toast.LENGTH_SHORT).show();
        }
        else if(name_s == null || name_s.contains(" "))
        {
            name.setText("");
            name.requestFocus();
            islegal = false;
            Toast.makeText(register.this,"name illegal", Toast.LENGTH_SHORT).show();
        }
        else if(passwd_s == null || repasswd_s == null || !passwd_s.equals(repasswd_s))
        {
            passwd.setText("");
            repasswd.setText("");
            passwd.requestFocus();
            islegal = false;
            Toast.makeText(register.this,"password illegal", Toast.LENGTH_SHORT).show();
        }
        return islegal;
    }
}
