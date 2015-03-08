package tang.map.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tang.map.R;
import tang.map.data.Application;
import tang.map.data.ApplicationTable;
import tang.map.data.DataToolkit;
import tang.map.threadPool.DealMessageThread;
import tang.map.threadPool.MyHandler;

/**
 * Created by ximing on 2015/1/10.
 */
public class MessageListAdapter extends BaseAdapter
{
    private Context context;
    private int layout;
    private List<Application> messagelist;
    private Activity activity;

    public MessageListAdapter(Context context, int layout, List<Application> messagelist,Activity activity) {
        this.context = context;
        this.layout = layout;
        this.messagelist = messagelist;
        this.activity = activity;
    }

    public void setMessagelist(List<Application> messagelist)
    {
        this.messagelist = messagelist;
    }
    @Override
    public int getCount() {
        return messagelist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.userId = (TextView) convertView.findViewById(R.id.userId);
            viewHolder.verify = (TextView) convertView.findViewById(R.id.verify);
            viewHolder.head = (ImageView) convertView.findViewById(R.id.head);
            viewHolder.agree = (ImageView)convertView.findViewById(R.id.agree);
            viewHolder.reject = (ImageView) convertView.findViewById(R.id.reject);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        Application item = messagelist.get(position);
        viewHolder.userId.setText(item.getNickname());
        String str = item.getVerify();
        if(str.length() > 5)
            str = str.substring(0,6);
        viewHolder.verify.setText(str);
        Bitmap photo = DataToolkit.StringToBitmap(item.getHead());
        viewHolder.head.setImageBitmap(photo);
        viewHolder.agree.setOnClickListener(new MyOnClickListener(activity,item.getAid(),0));
        viewHolder.reject.setOnClickListener(new MyOnClickListener(activity,item.getAid(),1));
        return convertView;
    }

    class ViewHolder {
        private TextView userId;
        private TextView verify;
        private ImageView head;
        private ImageView agree;
        private ImageView reject;
    }
}

class MyOnClickListener implements View.OnClickListener
{

    private int id;
    private int type;
    private Activity activity;
    public MyOnClickListener(Activity activity,int id,int type)
    {
        this.id = id;
        this.type = type;
        this.activity = activity;
    }
    @Override
    public void onClick(View view) {
        int button = view.getId();
        ImageView iv = (ImageView)view;
        if(button == R.id.agree)
        {
            iv.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(),R.drawable.notification_button_agree_dis));
            iv.setClickable(false);
        }
        else if(button == R.id.reject)
        {
            iv.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(),R.drawable.notification_button_reject_dis));
            iv.setClickable(false);
        }
        Bundle data = new Bundle();
        data.putInt("id",this.id);
        data.putInt("code",this.type);
        MyHandler accept = new MyHandler(activity);
        DealMessageThread dt = new DealMessageThread(accept,data);
        dt.start();
        ApplicationTable at = new ApplicationTable();
        at.delete(id);
    }
}