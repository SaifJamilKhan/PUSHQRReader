package com.push.pushqrreader.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.push.pushqrreader.PUSHResponseObjects.ErrorResponse;
import com.push.pushqrreader.PUSHResponseObjects.Exercise;
import com.push.pushqrreader.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListOfScansFragment extends Fragment {

    private SimpleAdapter simpleAdpt;

    private ListView mListView;

    ArrayList<Map<String, String>> mScanList = new ArrayList<Map<String, String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_of_scans, container, false);

        simpleAdpt = new FriendsAdapter(getActivity(), mScanList,
                R.layout.list_item_scan, new String[]{"name"},
                new int[]{R.id.scan_text});

        mListView = (ListView) view.findViewById(R.id.scans_list_view);
        mListView.setAdapter(simpleAdpt);
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void configureWithExercisesAndErrors(ArrayList<Exercise> exercises, ArrayList<ErrorResponse> errorResponses) {
        if (exercises != null) {
            for (Exercise exercise : exercises) {
                mScanList.add(createHashmap("title", ""));
            }
        }
        if (errorResponses != null) {
            for (ErrorResponse error : errorResponses) {
                mScanList.add(createHashmap("title", error.error));
            }
        }

    }

    // }
    public static HashMap<String, String> createHashmap(String key, String value) {
        HashMap<String, String> planet = new HashMap<String, String>();
        planet.put(key, value);
        return planet;
    }

    private class FriendsAdapter extends SimpleAdapter {
        List<? extends Map<String, ?>> data;
        private LayoutInflater inflater = null;

        public FriendsAdapter(Context context,
                              List<? extends Map<String, ?>> data, int resource,
                              String[] from, int[] to) {
            super(context, data, resource, from, to);

            this.data = data;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if (vi == null)
                vi = inflater.inflate(R.layout.list_item_scan, null);
            TextView text = (TextView) vi.findViewById(R.id.scan_text);
            if (text != null) {
                text.setText((CharSequence) data.get(position).get("name"));
            }
            return vi;
        }
    }

}
