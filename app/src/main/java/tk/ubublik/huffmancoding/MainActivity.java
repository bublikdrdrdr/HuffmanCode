package tk.ubublik.huffmancoding;

import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.concurrent.Callable;

import tk.ubublik.huffmancoding.fragments.AboutFragment;
import tk.ubublik.huffmancoding.fragments.StatsFragment;
import tk.ubublik.huffmancoding.fragments.MainFragment;
import tk.ubublik.huffmancoding.fragments.TreeVisualizerFragment;
import tk.ubublik.huffmancoding.logic.HuffmanTree;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener = item -> {
        try {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragment(getFragment(MainFragment.class));
                    return true;
                case R.id.navigation_stats:
                    setFragment(getFragment(StatsFragment.class)).setTables(
                            AppUtils.treeToStatsTable(getTree(HuffmanTree.HuffmanTreeMode.DYNAMIC)),
                            AppUtils.treeToStatsTable(getTree(HuffmanTree.HuffmanTreeMode.STATIC)));
                    return true;
                case R.id.navigation_tree:
                    setFragment(getFragment(TreeVisualizerFragment.class, null)).setTrees(
                            AppUtils.tryOrNull(() -> getTree(HuffmanTree.HuffmanTreeMode.DYNAMIC).getTree()),
                            AppUtils.tryOrNull(() -> getTree(HuffmanTree.HuffmanTreeMode.STATIC).getTree()));
                    return true;
                case R.id.navigation_about:
                    setFragment(getFragment(AboutFragment.class));
                    return true;
            }
            return false;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return true;
        }
    };

    private HuffmanTree getTree(HuffmanTree.HuffmanTreeMode treeMode) {
        return AppUtils.tryOrNull(() -> getFragment(MainFragment.class).getTree(treeMode));
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

    private <F extends Fragment> F setFragment(F fragment) {
        for (Fragment added: getSupportFragmentManager().getFragments()){
            if (added.isAdded() && added.isVisible()) getSupportFragmentManager().beginTransaction().hide(added).commit();
        }
        if (fragment != null) {
            if (!fragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment, fragment.getClass().getCanonicalName()).commit();
            }
            getSupportFragmentManager().beginTransaction().show(fragment).commit();
        }
        return fragment;
    }
}
