package tang.map.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import tang.map.R;
import tang.map.data.DataToolkit;

/**
 * Created by ximing on 2015/1/7.
 */
public class CycleListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<HashMap<String, Object>> photoList;

    public CycleListAdapter(Context context, int layout, ArrayList<HashMap<String, Object>> photoList) {
        this.context = context;
        this.layout = layout;
        this.photoList = photoList;
    }

    @Override
    public int getCount() {
        return photoList.size();
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
            viewHolder.loc = (TextView) convertView.findViewById(R.id.loc);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        HashMap<String,Object> item = photoList.get(position);
        if(item.get("location").toString() != "")
            viewHolder.loc.setText(item.get("location").toString());
        else
            viewHolder.loc.setText(item.get("longitude").toString() + item.get("latitude"));
        viewHolder.time.setText(item.get("time").toString());
        viewHolder.description.setText(item.get("description").toString());
        Bitmap photo = DataToolkit.StringToBitmap(item.get("photo").toString());
        viewHolder.photo.setImageBitmap(photo);
        return convertView;
    }

    class ViewHolder {
        private TextView loc;
        private TextView time;
        private TextView description;
        private ImageView photo;
    }
}
