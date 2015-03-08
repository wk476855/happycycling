package tang.map.cycle;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import tang.map.R;
import tang.map.adapter.CycleListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class cycle_list extends Fragment {

    private View root = null;
    private ListView listView = null;
    private ArrayList<HashMap<String,Object>> photolist = new ArrayList<HashMap<String, Object>>();
    public cycle_list() {
        // Required empty public constructor
    }

    public void setPhotolist(ArrayList<HashMap<String,Object>> photolist)
    {
        this.photolist = photolist;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.cycle_list, container, false);
        listView = (ListView)root.findViewById(R.id.photos);
        CycleListAdapter adapter = new CycleListAdapter(getActivity(),R.layout.photolistitem,this.photolist);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return root;
    }


}
