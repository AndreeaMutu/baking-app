package com.andreea.baking_app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.andreea.baking_app.R;
import com.andreea.baking_app.model.Step;
import com.andreea.baking_app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Step detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeDetailActivity}.
 */
public class StepDetailActivity extends AppCompatActivity {
    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.previous_step_fab)
    FloatingActionButton previousStepFab;
    @BindView(R.id.next_step_fab)
    FloatingActionButton nextStepFab;

    private List<Step> steps = new ArrayList<>();
    private int stepPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Intent intent = getIntent();
            stepPosition = intent.getIntExtra(Constants.STEP_POS_KEY, 0);
            steps = intent.getParcelableArrayListExtra(Constants.STEP_LIST_KEY);
            displayFragment(steps, stepPosition);
            displayNavigationButtons();
        } else {
            stepPosition = savedInstanceState.getInt(Constants.STEP_POS_KEY, 0);
            steps = savedInstanceState.getParcelableArrayList(Constants.STEP_LIST_KEY);
        }

        nextStepFab.setOnClickListener(v -> {
            stepPosition++;
            displayNavigationButtons();
            displayFragment(steps, stepPosition);
        });
        previousStepFab.setOnClickListener(v -> {
            stepPosition--;
            displayNavigationButtons();
            displayFragment(steps, stepPosition);
        });
    }

    private void displayNavigationButtons() {
        if (stepPosition == 0) {
            previousStepFab.setVisibility(View.GONE);
        } else if (stepPosition == steps.size() - 1) {
            nextStepFab.setVisibility(View.GONE);
        } else {
            previousStepFab.setVisibility(View.VISIBLE);
            nextStepFab.setVisibility(View.VISIBLE);
        }
    }

    private void displayFragment(List<Step> steps, int stepPosition) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(Constants.STEP_KEY,
                steps.get(stepPosition));
        StepDetailFragment fragment = new StepDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_detail_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, RecipeDetailActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.STEP_LIST_KEY, (ArrayList<? extends Parcelable>) steps);
        outState.putInt(Constants.STEP_POS_KEY, stepPosition);
    }
}
