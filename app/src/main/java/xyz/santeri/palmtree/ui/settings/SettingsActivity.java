package xyz.santeri.palmtree.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import net.xpece.android.support.preference.Fixes;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;
import xyz.santeri.palmtree.R;

/**
 * @author Santeri Elo
 */
public class SettingsActivity extends AppCompatActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper swipeHelper;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fixes.updateLayoutInflaterFactory(getLayoutInflater());

        overridePendingTransition(R.anim.right_in_fade, 0);

        swipeHelper = new SwipeBackActivityHelper(this);
        swipeHelper.onActivityCreate();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, SettingsFragment.newInstance())
                    .commit();
        }

        getSwipeBackLayout().setFullScreenSwipeEnabled(true);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        swipeHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && swipeHelper != null)
            return swipeHelper.findViewById(id);
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.right_out_fade);
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return swipeHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
