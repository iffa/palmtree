package xyz.santeri.palmtree.ui.listing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;

import net.grandcentrix.thirtyinch.TiFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.base.ListingType;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.ui.base.EfficientLinearLayoutManager;
import xyz.santeri.palmtree.ui.base.EndlessScrollListener;
import xyz.santeri.palmtree.ui.base.ItemClickSupport;
import xyz.santeri.palmtree.ui.detail.DetailActivity;
import xyz.santeri.palmtree.ui.dialog.DialogFactory;
import xyz.santeri.palmtree.ui.dialog.DialogType;
import xyz.santeri.palmtree.ui.dialog.SnackbarFactory;
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
    RecyclerView recyclerView;
    EfficientLinearLayoutManager layoutManager;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    private static final String ARG_CATEGORY = "category";

    public static ListingFragment newInstance(ListingType listingType) {
        ListingFragment fragment = new ListingFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, listingType);
        fragment.setArguments(args);

        return fragment;
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
        recyclerView.setAdapter(getPresenter().getListingAdapter());
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

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(
                        (rv, pos, v) -> getPresenter().onItemClick(pos))
                .setOnItemLongClickListener((rv, pos, v) -> {
                    getPresenter().onItemLongClick(pos);
                    return true;
                });
    }

    @Override
    public void onPause() {
        super.onPause();

        getPresenter().putRecyclerState(layoutManager.onSaveInstanceState());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getPresenter().getRecyclerState() != null) {
            layoutManager.onRestoreInstanceState(getPresenter().getRecyclerState());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        App.get(getContext()).refWatcher().watch(this);
    }

    @NonNull
    @Override
    public ListingPresenter providePresenter() {
        return new ListingPresenter(getContext(), (ListingType) getArguments().getSerializable(ARG_CATEGORY));
    }

    @Override
    public void onRefresh() {
        getPresenter().onRefresh();
    }

    @Override
    public void openDetails(ImageDetails image) {
        startActivity(DetailActivity.getStartIntent(getContext(), image.id()));
    }

    @Override
    public void showQualityInfo() {
        DialogFactory.newInstance(DialogType.DIALOG_LISTING_QUALITY).show(getFragmentManager(), "listing_quality_dialog");
    }

    @Override
    public void openDialogDetails(ImageDetails item) {
        DialogFactory.newImageDialogInstance(item).show(getFragmentManager(), "details_dialog");
    }

    @Override
    public void showError(int page, Throwable throwable, @StringRes int message) {
        refreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);

        SnackbarFactory.createErrorSnackbar(refreshLayout, true, message,
                v -> {
                    if (page == 1) {
                        getPresenter().onRefresh();
                    } else {
                        getPresenter().load(page);
                    }
                }, v -> {
                    // Presenter should tell us to do this but it is not necessary for a one-liner
                    DialogFactory.newErrorDialogInstance(throwable.getMessage())
                            .show(getFragmentManager(), "error_details_dialog");
                }).show();
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
    public void restoreCurrentPage(int currentPage) {
        scrollListener.setCurrentPage(currentPage);
    }
}
