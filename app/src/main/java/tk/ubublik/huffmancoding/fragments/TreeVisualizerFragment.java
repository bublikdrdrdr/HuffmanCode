package tk.ubublik.huffmancoding.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import tk.ubublik.huffmancoding.AppUtils;
import tk.ubublik.huffmancoding.R;
import tk.ubublik.huffmancoding.TreeRendererView;
import tk.ubublik.huffmancoding.logic.HuffmanTree;
import tk.ubublik.huffmancoding.logic.Leaf;


public class TreeVisualizerFragment extends Fragment {

    private static final String ARG_DYNAMIC_TREE = "adt";
    private static final String ARG_STATIC_TREE = "ast";
    private static final String DYNAMIC_TREE_VIEW_STATE = "dtvs";
    private static final String STATIC_TREE_VIEW_STATE = "stvs";

    private Leaf dynamicTree;
    private Leaf staticTree;

    public TreeVisualizerFragment() {
    }

    public static TreeVisualizerFragment newInstance(Leaf dynamicTree, Leaf staticTree) {
        TreeVisualizerFragment fragment = new TreeVisualizerFragment();
        Bundle args = new Bundle();
        if (dynamicTree!=null) args.putParcelable(ARG_DYNAMIC_TREE, dynamicTree);
        if (dynamicTree!=null) args.putParcelable(ARG_STATIC_TREE, staticTree);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getArguments() != null) {
            dynamicTree = getArguments().getParcelable(DYNAMIC_TREE_VIEW_STATE);
            staticTree = getArguments().getParcelable(STATIC_TREE_VIEW_STATE);
            getArguments().remove(DYNAMIC_TREE_VIEW_STATE);
            getArguments().remove(STATIC_TREE_VIEW_STATE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_STATIC_TREE, staticTree);
        outState.putParcelable(ARG_DYNAMIC_TREE, dynamicTree);
        if (getView()!=null){
            outState.putBundle(DYNAMIC_TREE_VIEW_STATE, ((TreeRendererView)getView().findViewById(R.id.dynamicTreeRendererView)).writeToBundle());
            outState.putBundle(STATIC_TREE_VIEW_STATE, ((TreeRendererView)getView().findViewById(R.id.staticTreeRendererView)).writeToBundle());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tree_visualizer, container, false);
        dynamicTree = AppUtils.valueOrDefault(dynamicTree, AppUtils.tryOrNull(() -> savedInstanceState.getParcelable(ARG_DYNAMIC_TREE)));
        staticTree = AppUtils.valueOrDefault(staticTree, AppUtils.tryOrNull(() -> savedInstanceState.getParcelable(ARG_STATIC_TREE)));
        TreeRendererView dynamicTreeRenderView = view.findViewById(R.id.dynamicTreeRendererView);
        TreeRendererView staticTreeRenderView = view.findViewById(R.id.staticTreeRendererView);
        dynamicTreeRenderView.setTree(dynamicTree);
        staticTreeRenderView.setTree(staticTree);
        dynamicTree = null;
        staticTree = null;
        dynamicTreeRenderView.readFromBundle(AppUtils.tryOrNull(() -> savedInstanceState.getBundle(DYNAMIC_TREE_VIEW_STATE)));
        staticTreeRenderView.readFromBundle(AppUtils.tryOrNull(() -> savedInstanceState.getBundle(STATIC_TREE_VIEW_STATE)));
        Spinner treeFragmentModeSpinner = view.findViewById(R.id.treeFragmentModeSpinner);
        treeFragmentModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setRenderViewVisible(position==0? HuffmanTree.HuffmanTreeMode.DYNAMIC: HuffmanTree.HuffmanTreeMode.STATIC);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setRenderViewVisible(getCurrentTreeMode());
        view.findViewById(R.id.rootButton).setOnClickListener(v -> {
            TreeRendererView treeRendererView = getRendererViewByMode(getCurrentTreeMode());
            if (treeRendererView != null) {
                treeRendererView.toFirstLeaf();
                treeRendererView.invalidate();
            }
        });
        return view;
    }

    private void setRenderViewVisible(HuffmanTree.HuffmanTreeMode mode){
        View view, dynamicView, staticView;
        if ((view = getView())==null) return;
        (dynamicView = view.findViewById(R.id.dynamicTreeRendererView)).setVisibility(View.GONE);
        (staticView = view.findViewById(R.id.staticTreeRendererView)).setVisibility(View.GONE);
        switch (mode){
            case STATIC:
                staticView.setVisibility(View.VISIBLE);
                break;
            case DYNAMIC:
                dynamicView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private TreeRendererView getRendererViewByMode(HuffmanTree.HuffmanTreeMode mode) {
        switch (mode) {
            case STATIC:
                return getView().findViewById(R.id.staticTreeRendererView);
            case DYNAMIC:
                return getView().findViewById(R.id.dynamicTreeRendererView);
            default:
                return null;
        }
    }

    public void setTrees(Leaf dynamicTree, Leaf staticTree) {
        if (getView() != null) {
            ((TreeRendererView) getView().findViewById(R.id.staticTreeRendererView)).setTree(staticTree);
            ((TreeRendererView) getView().findViewById(R.id.dynamicTreeRendererView)).setTree(dynamicTree);
        } else {
            this.dynamicTree = dynamicTree;
            this.staticTree = staticTree;
        }
    }

    private HuffmanTree.HuffmanTreeMode getCurrentTreeMode() {
        if (getView() == null) return null;
        return ((Spinner) getView().findViewById(R.id.treeFragmentModeSpinner)).
                getSelectedItemPosition() == 0 ? HuffmanTree.HuffmanTreeMode.DYNAMIC : HuffmanTree.HuffmanTreeMode.STATIC;
    }
}
