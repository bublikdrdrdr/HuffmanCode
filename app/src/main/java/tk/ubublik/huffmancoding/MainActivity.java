package tk.ubublik.huffmancoding;

import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import tk.ubublik.huffmancoding.fragments.AboutFragment;
import tk.ubublik.huffmancoding.fragments.MainFragment;
import tk.ubublik.huffmancoding.fragments.TreeVisualizerFragment;
import tk.ubublik.huffmancoding.logic.BinaryTree;
import tk.ubublik.huffmancoding.logic.HuffmanTree;
import tk.ubublik.huffmancoding.logic.Leaf;

public class MainActivity extends AppCompatActivity
        implements TreeVisualizerFragment.OnFragmentInteractionListener,
        MainFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener = item -> {
        try {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragment(getFragment(MainFragment.class));
                    return true;
                case R.id.navigation_about:
                    setFragment(getFragment(AboutFragment.class));
                    return true;
                case R.id.navigation_tree:
                    setFragment(getFragment(TreeVisualizerFragment.class, null).setTree(AppUtils.tryOrNull(() -> getTree().getTree())));
                    return true;
            }
            return false;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return true;
        }
    };

    private HuffmanTree getTree() {
        return AppUtils.tryOrNull(() -> getFragment(MainFragment.class).getTree());
    }

    private <C extends Fragment> C getFragment(Class<C> fragmentClass) {
        return getFragment(fragmentClass, null);
    }

    @SuppressWarnings("unchecked")
    private <C extends Fragment> C getFragment(Class<C> fragmentClass, @Nullable Callable<C> callable) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentClass.getCanonicalName());
        if (!fragmentClass.isInstance(fragment)) {
            fragment = AppUtils.tryOrNull(callable);
            if (!fragmentClass.isInstance(fragment))
                fragment = AppUtils.tryOrNull(fragmentClass::newInstance);
        }
        return (C) fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void setFragment(Fragment fragment) {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (current != null) getSupportFragmentManager().beginTransaction().hide(current).commit();
        if (fragment != null) {
            if (!fragment.isAdded())
                getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment, fragment.getClass().getCanonicalName()).commit();
            getSupportFragmentManager().beginTransaction().show(fragment).commit();
        }
    }
}
