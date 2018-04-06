package tk.ubublik.huffmancoding;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;

import tk.ubublik.huffmancoding.fragments.MainFragment;
import tk.ubublik.huffmancoding.fragments.TreeVisualizerFragment;
import tk.ubublik.huffmancoding.logic.BinaryTree;
import tk.ubublik.huffmancoding.logic.HuffmanTree;
import tk.ubublik.huffmancoding.logic.Leaf;

public class MainActivity extends AppCompatActivity
        implements TreeVisualizerFragment.OnFragmentInteractionListener,
        MainFragment.OnFragmentInteractionListener{

    //private TextView mTextMessage;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setFragment(MainFragment.newInstance());
                        //mTextMessage.setText(R.string.title_home);
                        return true;
                    case R.id.navigation_dashboard:
                        //mTextMessage.setText(R.string.title_dashboard);
                        return true;
                    case R.id.navigation_notifications:
                        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment);
                        Leaf leaf = (fragment instanceof MainFragment)?((MainFragment)fragment).getTree().getTree():null;
                        setFragment(TreeVisualizerFragment.newInstance(leaf));
                        //mTextMessage.setText(R.string.title_notifications);
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //TreeRendererView treeRendererView = findViewById(R.id.treeRendererView);
        HuffmanTree huffmanTree = new BinaryTree(HuffmanTree.HuffmanTreeMode.DYNAMIC);
        huffmanTree.send('a', Character.SIZE);
/*        huffmanTree.send('b', Character.SIZE);
        huffmanTree.send('c', Character.SIZE);
        huffmanTree.send('d', Character.SIZE);
        huffmanTree.send('e', Character.SIZE);*/
        //treeRendererView.setTree(huffmanTree.getTree());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    private void setFragment(Fragment instance){
        getFragmentManager().beginTransaction().replace(R.id.fragment, instance).commit();
    }
}
