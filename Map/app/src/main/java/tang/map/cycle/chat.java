package tang.map.cycle;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import tang.map.R;
import tang.map.data.CommunicationItem;
import tang.map.data.CommunicationTable;
import tang.map.data.DataToolkit;
import tang.map.data.User;
import tang.map.threadPool.MyHandler;
import tang.map.threadPool.ReceiveMsgThread;
import tang.map.threadPool.SendMsgThread;
import tang.map.voiceHelper.VoicePlay;
import tang.map.voiceHelper.VoiceRecord;

public class chat extends Activity
{
    private ArrayList<CommunicationItem> chatList= new ArrayList<CommunicationItem>();
    private int[] component = { R.id.chatlist_image_me, R.id.chatlist_text_me, R.id.chatlist_voice_me, R.id.chatlist_image_other, R.id.chatlist_text_other, R.id.chatlist_voice_other };
    private int[] layout={R.layout.sendmessage, R.layout.getmessage};
    private Bitmap[] head = new Bitmap[2];

    private Button back = null;
    private ListView chatListView=null;
    private ImageButton chatSwitchTxt = null;
    private ImageButton chatSwitchVoice = null;
    private ImageButton chatVoiceToSend = null;
    private ImageButton chatTextToSend = null;
    private EditText chatText = null;
    private RelativeLayout chatTypeTxt = null;
    private RelativeLayout chatTypeVoice = null;
    private TextView account = null;
    protected MyChatAdapter adapter=null;

    private User user = null;
    private SharedPreferences sp = null;
    private SharedPreferences usersp = null;
    MessageReceiver mr;
    IntentFilter intentFilter;
    private VoiceRecord voiceRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        sp = getSharedPreferences("activityinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("currentActivity", "chat");
        editor.commit();

        mr = new MessageReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("chat");
        registerReceiver(mr, intentFilter);

        init();
        user = (User)getIntent().getExtras().get("user");
        account.setText(user.getNickname());
        if(user.getHead() == null) {
            head[1] = BitmapFactory.decodeResource(getResources(),R.drawable.register_head);
        }
        else
            head[1] = user.getHead();
        usersp = getSharedPreferences("user",Context.MODE_PRIVATE);
        head[0] = DataToolkit.StringToBitmap(usersp.getString("head","").toString());

        int chatid = user.getUserid();
        chatList = (ArrayList)new CommunicationTable().get(chatid);
        adapter = new MyChatAdapter(this,chatList,layout,component,head);
        chatListView.setAdapter(adapter);
/*
        MyHandler myhandler = new MyHandler(chat.this);
        ReceiveMsgThread receiveMsg = new ReceiveMsgThread(myhandler,null);
        receiveMsg.start();*/

    }

    private void init()
    {
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new MyClickListener());
        chatSwitchTxt = (ImageButton)findViewById(R.id.chat_switch_text);
        chatSwitchTxt.setOnClickListener(new MyClickListener());
        chatSwitchVoice = (ImageButton)findViewById(R.id.chat_switch_voice);
        chatSwitchVoice.setOnClickListener(new MyClickListener());
        chatTextToSend = (ImageButton)findViewById(R.id.chat_txt_tosend);
        chatTextToSend.setOnClickListener(new MyClickListener());
        chatVoiceToSend = (ImageButton)findViewById(R.id.chat_voice_tosend);
        chatVoiceToSend.setOnTouchListener(new VoiceSendListener());
        chatTypeTxt = (RelativeLayout)findViewById(R.id.chat_type_txt);
        chatTypeVoice = (RelativeLayout)findViewById(R.id.chat_type_voice);
        account = (TextView)findViewById(R.id.account);
        chatText = (EditText)findViewById(R.id.chat_text);
        chatListView=(ListView)findViewById(R.id.chat_list);
        chatListView.setOnItemClickListener(new ListItemClickListener());

        chatTypeTxt.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
    protected void onPause() {
        super.onPause();
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("currentActivity", "");
//        editor.commit();
        if(mr != null)
            unregisterReceiver(mr);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("currentActivity", "");
//        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("currentActivity", "");
//        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("currentActivity", "chat");
//        editor.commit();
    }
    public class MyClickListener implements View.OnClickListener
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
                case R.id.chat_switch_text:
                    chatTypeTxt.setVisibility(View.VISIBLE);
                    chatTypeVoice.setVisibility(View.GONE);
                    break;
                case R.id.chat_switch_voice:
                    chatTypeTxt.setVisibility(View.GONE);
                    chatTypeVoice.setVisibility(View.VISIBLE);
                    break;
                case R.id.chat_txt_tosend:
                    String word = ( chatText.getText() + "" ).toString();
                    if(word.length()==0)
                        return;
                    chatText.setText("");

                    MyHandler myHandler = new MyHandler(chat.this);
                    Bundle data = new Bundle();
                    data.putString("word",word);
                    data.putInt("type",0);
                    data.putInt("gid",0);
                    data.putInt("userid",user.getUserid());
                    data.putInt("method", 0);
                    SendMsgThread sendvoice = new SendMsgThread(myHandler,data);
                    sendvoice.setId(0x009);
                    sendvoice.start();
                    break;
            }

        }
    }

    private class VoiceSendListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageButton speak = (ImageButton) v;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                speak.setBackgroundResource(R.drawable.chat_voice_sending);
                String path = "/sdcard/happyCycling/" + new Random().nextInt(100000) + ".3gp";
                voiceRecord = new VoiceRecord(chat.this);
                voiceRecord.setPath(path);
                if(!voiceRecord.startRecord())
                {
                    Toast.makeText(chat.this,"开启录音有错误",Toast.LENGTH_SHORT).show();
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                speak.setBackgroundResource(R.drawable.chat_voice_send);
                if(!voiceRecord.stopRecord())
                {
                    Toast.makeText(chat.this, "录音时间不能少于1秒", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    MyHandler myHandler = new MyHandler(chat.this);
                    Bundle data = new Bundle();
                    data.putString("path",voiceRecord.getPath());
                    data.putInt("type",1);
                    data.putInt("gid", 0);
                    data.putInt("userid", user.getUserid());
                    data.putInt("method", 0);
                    SendMsgThread sendvoice = new SendMsgThread(myHandler,data);
                    sendvoice.setId(0x008);
                    sendvoice.start();
                }
            }

            return false;
        }
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            int type = chatList.get(position).getType();
            if(type == 1)
            {
                String voice = chatList.get(position).getContent();
                VoicePlay voicePlay = new VoicePlay();
                voicePlay.setPath(voice);
                voicePlay.startPlaying();
            }
        }
    }

    public void addTextToList(int who,int type, String text)
    {
        CommunicationItem item = new CommunicationItem();
        item.setSendOrRec(who);
        item.setType(type);
        item.setContent(text);
        chatList.add(item);
        adapter.notifyDataSetChanged();
        chatListView.setSelection(adapter.getCount() - 1);
    }

    private class MyChatAdapter extends BaseAdapter
    {
        Context context = null;
        ArrayList<CommunicationItem> chatList = null;
        int[] layout;
        int[] component;
        Bitmap head[];


        public MyChatAdapter(Context context,
                             ArrayList<CommunicationItem> chatList, int[] layout,
                              int[] component,Bitmap head[])
        {
            super();
            this.context = context;
            this.chatList = chatList;
            this.layout = layout;
            this.component = component;
            this.head = head;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return chatList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        class ViewHolder
        {
            public ImageView head = null;
            public TextView text = null;
            public ImageView trumpet = null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            CommunicationItem item = chatList.get(position);
            int who = item.getSendOrRec();
            convertView = LayoutInflater.from(context).inflate(layout[who], null);
            holder = new ViewHolder();
            holder.head = (ImageView) convertView.findViewById(component[who * 3 + 0]);
            holder.text = (TextView) convertView.findViewById(component[who * 3 + 1]);
            holder.trumpet = (ImageView) convertView.findViewById(component[who * 3 + 2]);
            holder.head.setBackground(new BitmapDrawable(getResources(), head[who]));
            int type = item.getType();
            if(type == 0)
            {
                holder.trumpet.setVisibility(View.GONE);
                holder.text.setText(item.getContent());
            }
            else
                holder.text.setVisibility(View.GONE);
            return convertView;
        }
    }


    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MyHandler myhandler = new MyHandler(chat.this);
            ReceiveMsgThread receiveMsg = new ReceiveMsgThread(myhandler,null);
            receiveMsg.start();
        }
    }

}
