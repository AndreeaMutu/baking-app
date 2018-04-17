package com.andreea.baking_app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.andreea.baking_app.R;
import com.andreea.baking_app.model.Recipe;
import com.andreea.baking_app.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.andreea.baking_app.ui.Constants.RECIPE_KEY;

/**
 * An activity representing a list of Steps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeDetailActivity extends AppCompatActivity {
    private static final String TAG = RecipeDetailActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.step_detail_container)
    @Nullable
    FrameLayout recipeDetailContainer;
    @BindView(R.id.step_list)
    RecyclerView recyclerView;

    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RECIPE_KEY)) {
            recipe = intent.getParcelableExtra(RECIPE_KEY);
        }
        toolbar.setTitle(recipe.getName());
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (recipeDetailContainer != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        assert recyclerView != null;
        setupRecyclerView(recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new StepListAdapter(this, recipe.getSteps(), mTwoPane));
    }

    public static class StepListAdapter
            extends RecyclerView.Adapter<StepListAdapter.ViewHolder> {

        private final RecipeDetailActivity mParentActivity;
        private final List<Step> mSteps;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Step item = (Step) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Constants.ARG_ITEM_STEP, item);
                    StepDetailFragment fragment = new StepDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.step_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, StepDetailActivity.class);
                    intent.putExtra(Constants.ARG_ITEM_STEP, item);

                    context.startActivity(intent);
                }
            }
        };

        StepListAdapter(RecipeDetailActivity parent,
                        List<Step> items,
                        boolean twoPane) {
            mSteps = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(String.valueOf(mSteps.get(position).getId()));
            holder.mContentView.setText(mSteps.get(position).getShortDescription());

            holder.itemView.setTag(mSteps.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mSteps.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.id_text)
            TextView mIdView;
            @BindView(R.id.content)
            TextView mContentView;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
