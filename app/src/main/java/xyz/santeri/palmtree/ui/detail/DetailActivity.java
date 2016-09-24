package xyz.santeri.palmtree.ui.detail;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;

import net.grandcentrix.thirtyinch.TiActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import xyz.santeri.palmtree.BuildConfig;
import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.data.model.ImageType;

/**
 * @author Santeri Elo
 */
public class DetailActivity extends TiActivity<DetailPresenter, DetailView>
        implements DetailView, OnPreparedListener, OnCompletionListener, SwipeBackActivityBase {
    private static final String EXTRA_IMAGE = "imagedetails";
    private static final String EXTRA_IMAGE_ID = "imageid";
    private SwipeBackActivityHelper swipeHelper;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress)
    MaterialProgressBar progressBar;

    @BindView(R.id.image)
    SubsamplingScaleImageView imageView;

    @BindView(R.id.video)
    EMVideoView videoView;

    public static Intent getStartIntent(Context context, ImageDetails imageDetails) {
        Intent startIntent = new Intent(context, DetailActivity.class);
        startIntent.putExtra(EXTRA_IMAGE, imageDetails);

        return startIntent;
    }

    public static Intent getStartIntent(Context context, int id) {
        Intent startIntent = new Intent(context, DetailActivity.class);
        startIntent.putExtra(EXTRA_IMAGE_ID, id);

        return startIntent;
    }

    @NonNull
    @Override
    public DetailPresenter providePresenter() {
        return new DetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.right_in_fade, 0);

        swipeHelper = new SwipeBackActivityHelper(this);
        swipeHelper.onActivityCreate();

        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView.setDoubleTapZoomDuration(250);
        imageView.setDebug(BuildConfig.DEBUG);
        imageView.setMaxScale(8.0f);

        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);

        getSwipeBackLayout().setFullScreenSwipeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Load data, either from a link or previous activity
        if (getIntent().hasExtra(EXTRA_IMAGE)) {
            getPresenter().load(getIntent().getParcelableExtra(EXTRA_IMAGE));
        } else if (getIntent().hasExtra(EXTRA_IMAGE_ID)) {
            getPresenter().load(getIntent().getIntExtra(EXTRA_IMAGE_ID, 0));
        } else {
            if (getIntent().getData() == null) {
                throw new UnsupportedOperationException("NO EXTRA_IMAGE or EXTRA_IMAGE_ID set and intent data is null");
            }

            Uri data = getIntent().getData();
            String fileId = data.getPath().replaceAll("\\D+", "");

            getPresenter().load(Integer.parseInt(fileId));
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                getPresenter().onShareClicked();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.right_out_fade);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showImage(ImageDetails imageDetails) {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(imageDetails.title());
        getSupportActionBar().setSubtitle(imageDetails.rating());

        if (imageView.isImageLoaded())
            return; // Without this, we will crash horribly on pause/resume

        if (imageDetails.type() == ImageType.IMAGE) {
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            // TODO: fix Error, cannot access an invalid/free'd bitmap here
            Glide.with(this).load(imageDetails.fileUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    progressBar.setVisibility(View.GONE);
                    ObjectAnimator fadeAltAnim = ObjectAnimator.ofFloat(imageView, View.ALPHA, 0, 1);
                    fadeAltAnim.start();

                    imageView.setImage(ImageSource.bitmap(resource));
                }
            });
        } else {
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            videoView.setVideoURI(Uri.parse(imageDetails.fileUrl()));
        }
    }

    @Override
    public void startShareIntent(String shareUrl) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.action_share_title)));
    }

    @Override
    public void showSwipeHint() {
        Snackbar.make(imageView, R.string.hint_swipe, Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void onPrepared() {
        videoView.start();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCompletion() {
        videoView.restart();
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