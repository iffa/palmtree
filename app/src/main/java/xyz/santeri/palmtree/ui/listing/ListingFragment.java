package xyz.santeri.palmtree.ui.listing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;

import net.grandcentrix.thirtyinch.TiFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.ui.base.EfficientLinearLayoutManager;
import xyz.santeri.palmtree.ui.base.EndlessScrollListener;
import xyz.santeri.palmtree.ui.base.ItemClickSupport;
import xyz.santeri.palmtree.ui.base.StatefulRecyclerView;
import xyz.santeri.palmtree.ui.detail.DetailActivity;
import xyz.santeri.palmtree.ui.dialog.DialogFactory;
import xyz.santeri.palmtree.ui.listing.adapter.ListingAdapter;
import xyz.santeri.palmtree.util.DeviceUtils;

/**
 * @author Santeri Elo
 */
public class ListingFragment extends TiFragment<ListingPresenter, ListingView>
        implements ListingView, SwipeRefreshLayout.OnRefreshListener {
    private Unbinder unbinder;
    private EndlessScrollListener scrollListener;

    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.recycler)
    StatefulRecyclerView recyclerView;
    EfficientLinearLayoutManager layoutManager;
    ListingAdapter adapter;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    public static ListingFragment newInstance() {
        return new ListingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frontpage, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout.setColorSchemeResources(R.color.accent, R.color.primary, R.color.primary_dark);

        refreshLayout.setOnRefreshListener(this);

        layoutManager =
                new EfficientLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        // Setting extra layout space to match screen height, so a screenful of content is preloaded
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getContext()));

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter = new ListingAdapter());
        recyclerView.setItemAnimator(new AlphaCrossFadeAnimator());

        // Improve user experience by caching more items
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        scrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getPresenter().load(page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
                (recyclerView1, position, v) -> getPresenter().onItemClick(position));
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save the first completely visible item (if it exists) or simply the first one visible
        int visiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        if (visiblePosition == RecyclerView.NO_POSITION) {
            visiblePosition = layoutManager.findFirstVisibleItemPosition();
        }

        getPresenter().saveScrollPosition(visiblePosition);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        App.get(getContext()).refWatcher().watch(this);
    }

    @NonNull
    @Override
    public ListingPresenter providePresenter() {
        return new ListingPresenter(getContext());
    }

    @Override
    public void onRefresh() {
        getPresenter().onRefresh();
    }

    @Override
    public void addImage(ImageDetails imageDetails) {
        adapter.addItem(imageDetails);
    }

    @Override
    public void restoreImages(List<ImageDetails> items, int currentPage, int scrollPosition) {
        scrollListener.setCurrentPage(currentPage);

        adapter.clear();
        adapter.addItems(items);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void openDetails(Integer position) {
        ImageDetails image = adapter.getItemAt(position);

        startActivity(DetailActivity.getStartIntent(getContext(), image.id()));
    }

    @Override
    public void showQualityInfo() {
        FragmentManager fragmentManager = getFragmentManager();

        DialogFactory.newInstance(DialogFactory.DIALOG_LISTING_QUALITY).show(fragmentManager, "listing_quality_dialog");
    }

    @Override
    public void showError(boolean snack) {
        Snackbar.make(refreshLayout, R.string.error_page_load, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void startLoading(boolean swipeToRefresh) {
        if (swipeToRefresh) {
            refreshLayout.setRefreshing(true);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void finishLoading() {
        refreshLayout.setRefreshing(false);

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void clear() {
        adapter.clear();
        scrollListener.setCurrentPage(1);
    }
}
