package tk.ubublik.huffmancoding.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.ubublik.huffmancoding.R;
import tk.ubublik.huffmancoding.TreeRendererView;
import tk.ubublik.huffmancoding.logic.HuffmanTree;
import tk.ubublik.huffmancoding.logic.Leaf;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TreeVisualizerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TreeVisualizerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TreeVisualizerFragment extends Fragment {

    private static final String ARG_TREE = "tree";

    private OnFragmentInteractionListener mListener;

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
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tree_visualizer, container, false);
        if (getArguments()!=null) {
            Object tree = getArguments().getParcelable(ARG_TREE);
            if (tree instanceof Leaf)
                ((TreeRendererView) view.findViewById(R.id.treeRendererView)).setTree((Leaf) tree);
        }

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
