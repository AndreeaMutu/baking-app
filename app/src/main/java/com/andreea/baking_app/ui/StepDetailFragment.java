package com.andreea.baking_app.ui;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andreea.baking_app.R;
import com.andreea.baking_app.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link RecipeDetailActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 */
public class StepDetailFragment extends Fragment {

    /**
     * The recipe Step this fragment is presenting.
     */
    private Step mItem;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout appBarLayout;

    @BindView(R.id.step_detail_tv)
    TextView stepDescriptionTv;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_detail, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(Constants.ARG_ITEM_STEP)) {
            mItem = arguments.getParcelable(Constants.ARG_ITEM_STEP);
        }

        if (mItem != null) {
            appBarLayout.setTitle(mItem.getShortDescription());
            stepDescriptionTv.setText(mItem.getDescription());
        }

        return rootView;
    }
}
