package tk.ubublik.huffmancoding.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.ubublik.huffmancoding.AppUtils;
import tk.ubublik.huffmancoding.R;
import tk.ubublik.huffmancoding.TreeRendererView;
import tk.ubublik.huffmancoding.logic.HuffmanTree;
import tk.ubublik.huffmancoding.logic.Leaf;


public class TreeVisualizerFragment extends Fragment {

    private static final String ARG_TREE = "tree";
    private static final String TREE_VIEW_STATE = "tvs";

    private OnFragmentInteractionListener mListener;
    private Leaf tree;

    public TreeVisualizerFragment() {
        // Required empty public constructor
    }

    public static TreeVisualizerFragment newInstance(Leaf tree) {
        TreeVisualizerFragment fragment = new TreeVisualizerFragment();
        Bundle args = new Bundle();
        if (tree!=null) {
            args.putParcelable(ARG_TREE, tree);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getArguments() != null) {
            tree = getArguments().getParcelable(ARG_TREE);
            getArguments().remove(ARG_TREE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TREE, tree);
        if (getView()!=null){
            outState.putBundle(TREE_VIEW_STATE, ((TreeRendererView)getView().findViewById(R.id.treeRendererView)).writeToBundle());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tree_visualizer, container, false);
        tree = AppUtils.valueOrDefault(AppUtils.tryOrNull(() -> savedInstanceState.getParcelable(ARG_TREE)), tree);
        TreeRendererView treeRendererView = view.findViewById(R.id.treeRendererView);
        treeRendererView.setTree(tree);
        tree = null;
        treeRendererView.readFromBundle(AppUtils.tryOrNull(() -> savedInstanceState.getBundle(TREE_VIEW_STATE)));
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public TreeVisualizerFragment setTree(Leaf tree) {
        if (getView() != null)
            ((TreeRendererView) getView().findViewById(R.id.treeRendererView)).setTree(tree);
        else
            this.tree = tree;
        return this;
    }
}
