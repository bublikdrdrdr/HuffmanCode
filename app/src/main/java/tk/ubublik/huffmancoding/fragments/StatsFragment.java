package tk.ubublik.huffmancoding.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.List;

import tk.ubublik.huffmancoding.R;
import tk.ubublik.huffmancoding.StatsAdapter;
import tk.ubublik.huffmancoding.StatsLeaf;
import tk.ubublik.huffmancoding.logic.HuffmanTree;

public class StatsFragment extends Fragment {

    private StatsAdapter statsAdapter = new StatsAdapter();
    private ArrayList<StatsLeaf> dynamicList = new ArrayList<>();
    private ArrayList<StatsLeaf> staticList = new ArrayList<>();

    private static final String dynamicListKey = "dlk";
    private static final String staticListKey = "slk";

    public StatsFragment() {
    }

    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){
            dynamicList = savedInstanceState.getParcelableArrayList(dynamicListKey);
            staticList = savedInstanceState.getParcelableArrayList(staticListKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView statsRecycler = view.findViewById(R.id.statsRecycler);
        statsRecycler.setLayoutManager(linearLayoutManager);
        statsRecycler.setAdapter(statsAdapter);
        statsAdapter.setList(getCurrentList(view));
        statsAdapter.notifyDataSetChanged();
        ((Spinner)view.findViewById(R.id.treeFragmentModeSpinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statsAdapter.setList(position==0?dynamicList:staticList);
                statsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(dynamicListKey, dynamicList);
        outState.putParcelableArrayList(staticListKey, staticList);
    }

    public void setTables(ArrayList<StatsLeaf> dynamicList, ArrayList<StatsLeaf> staticList){
        this.dynamicList = dynamicList;
        this.staticList = staticList;
        if (getView()!=null) {
            statsAdapter.setList(getCurrentList(getView()));
            statsAdapter.notifyDataSetChanged();
        }
    }

    private HuffmanTree.HuffmanTreeMode getCurrentTreeMode(View view){
        return ((Spinner)view.findViewById(R.id.treeFragmentModeSpinner))
                .getSelectedItemPosition()==0?
                HuffmanTree.HuffmanTreeMode.DYNAMIC: HuffmanTree.HuffmanTreeMode.STATIC;
    }

    private List<StatsLeaf> getCurrentList(View view){
        return getCurrentTreeMode(view)== HuffmanTree.HuffmanTreeMode.DYNAMIC?dynamicList:staticList;
    }
}
