package tang.map.cycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.map.BaiduMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import tang.map.R;
import tang.map.data.DataToolkit;
import tang.map.interfaces.ISendPhoto;
import tang.map.threadPool.MyHandler;
import tang.map.threadPool.SendPhotoThread;


public class sendphoto extends Activity implements ISendPhoto{

    private int PICTURE_CODE = 0;
    private int CAMERA_CODE = 1;
    private EditText description = null;
    private ImageView photo = null;
    private Button send = null;
    private Button back = null;

    private View view = null;
    private PopupWindow menu = null;
    private boolean chose_photo = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendphoto);
        init();

        SharedPreferences everysp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = everysp.edit();
        editor.putString("currentActivity", "");
        editor.commit();
    }

    private void init()
    {
        description = (EditText)findViewById(R.id.description);
        photo = (ImageView)findViewById(R.id.photo);
        photo.setOnClickListener(new MyClicklistener());
        send = (Button)findViewById(R.id.send);
        send.setOnClickListener(new MyClicklistener());
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new MyClicklistener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sendphoto, menu);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            System.out.println("keycode:back pressed");
            if(menu.isShowing())
                menu.dismiss();
        }*/
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            String sdcard = Environment.getExternalStorageState();
            if(!sdcard.equals(Environment.MEDIA_MOUNTED))
            {
                Toast.makeText(sendphoto.this, "sdcard unmounted", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap)bundle.get("data");
            photo.setImageBitmap(bitmap);
            chose_photo = true;
            FileOutputStream out = null;
            File file = new File("/sdcard/happyCycling/photo");
            if(!file.exists())
                file.mkdir();
            try
            {
                String filename = new Random().nextInt(1000000) + ".jpg";
                out = new FileOutputStream(new File(file,filename));
                byte[] result = DataToolkit.BitmapToByte(bitmap);
                out.write(result,0,result.length);
                out.close();
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
            photo.setImageBitmap(bitmap);
            chose_photo = true;
        }
        if(menu != null && menu.isShowing())
            menu.dismiss();
    }

    @Override
    public void show(String str) {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void jump(int code) {
        Intent data = new Intent();
        data.putExtra("result",code);
        setResult(0x001,data);
        finish();
    }

    private class MyClicklistener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent;
            switch (id)
            {
                case R.id.back:
                    finish();
                    break;
                case R.id.photo:
                    view = getLayoutInflater().inflate(R.layout.sendphoto_mask,null);
                    menu = new PopupWindow(view,ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
                    menu.setBackgroundDrawable(new PaintDrawable(android.R.color.transparent));
                    menu.showAtLocation(photo, Gravity.BOTTOM,0,0);
                    ImageButton camera = (ImageButton)view.findViewById(R.id.camera);
                    camera.setOnClickListener(new MyClicklistener());
                    ImageButton album = (ImageButton)view.findViewById(R.id.album);
                    album.setOnClickListener(new MyClicklistener());
                    ImageButton cancel = (ImageButton)view.findViewById(R.id.cancel);
                    cancel.setOnClickListener(new MyClicklistener());
                    break;
                case R.id.send:
                    if(islegal()) {
                        photo.setDrawingCacheEnabled(true);
                        Bitmap bitmap = Bitmap.createBitmap(photo.getDrawingCache());
                        photo.setDrawingCacheEnabled(false);
                        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
                        Bundle data = new Bundle();
                        data.putDouble("longitude",Double.valueOf(sp.getFloat("longitude",0f)));
                        data.putDouble("latitude",Double.valueOf(sp.getFloat("latitude",0f)));
                        data.putString("description", description.getText().toString());
                        data.putString("photo", DataToolkit.BitmapToString(bitmap));
                        data.putString("location",sp.getString("location",""));
                        MyHandler myHandler = new MyHandler(sendphoto.this);
                        SendPhotoThread sendPhotoThread = new SendPhotoThread(myHandler, data);
                        sendPhotoThread.start();
                    }
                    break;
                case R.id.camera:
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_CODE);
                    break;
                case R.id.album:
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,PICTURE_CODE);
                    break;
                case R.id.cancel:
                    menu.dismiss();
                    break;
            }
        }
    }

    private boolean islegal()
    {
        boolean result = true;
        String descript = description.getText().toString();
        if(descript == "")
        {
            show("说点什么吧...");
            result = false;
        }
        else if(chose_photo == false)
        {
            show("请选择一张图片...");
            result = false;
        }
        return result;
    }
}
