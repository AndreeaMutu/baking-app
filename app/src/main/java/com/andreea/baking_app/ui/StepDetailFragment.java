package com.andreea.baking_app.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andreea.baking_app.R;
import com.andreea.baking_app.model.Step;
import com.andreea.baking_app.utils.Constants;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link RecipeDetailActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 */
public class StepDetailFragment extends Fragment {
    private static String TAG = StepDetailFragment.class.getSimpleName();
    /**
     * The recipe Step this fragment is presenting.
     */
    private Step step;

    @BindView(R.id.step_detail_tv)
    TextView stepDescriptionTv;

    @BindView(R.id.video_view)
    PlayerView playerView;

    private long playbackPosition;
    private boolean playWhenReady = true;

    private SimpleExoPlayer player;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            step = savedInstanceState.getParcelable(Constants.STEP_KEY);
            playbackPosition = savedInstanceState.getLong(Constants.VIDEO_POSITION_KEY);
            playWhenReady = savedInstanceState.getBoolean(Constants.VIDEO_READY);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null && arguments.containsKey(Constants.STEP_KEY)) {
                step = arguments.getParcelable(Constants.STEP_KEY);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_detail, container, false);
        ButterKnife.bind(this, rootView);
        if (step != null) {
            stepDescriptionTv.setText(step.getDescription());
            CollapsingToolbarLayout appBarLayout = getActivity().findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(step.getShortDescription());
            }
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.STEP_KEY, step);
        if (player != null) {
            outState.putLong(Constants.VIDEO_POSITION_KEY, player.getCurrentPosition());
            outState.putBoolean(Constants.VIDEO_READY, player.getPlayWhenReady());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        if (TextUtils.isEmpty(step.getVideoURL())) {
            playerView.setVisibility(View.GONE);
            return;
        }
        playerView.setVisibility(View.VISIBLE);
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getActivity()),
                    new DefaultTrackSelector(), new DefaultLoadControl());
            playerView.setPlayer(player);
            MediaSource mediaSource = buildMediaSource(Uri.parse((step.getVideoURL())));
            player.prepare(mediaSource);
            player.seekTo(playbackPosition);
            player.setPlayWhenReady(playWhenReady);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("baking-app"))
                .createMediaSource(uri);
    }

    private void hideSystemUi() {
        boolean isLandscape = getResources().getBoolean(R.bool.landscape);
        boolean isTablet = getResources().getBoolean(R.bool.tablet);
        if (!isTablet && isLandscape){
            playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }
}
