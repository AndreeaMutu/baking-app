package com.andreea.baking_app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.andreea.baking_app.model.Ingredient;
import com.andreea.baking_app.model.Recipe;
import com.andreea.baking_app.model.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.andreea.baking_app.ui.Constants.RECIPE_KEY;
import static com.andreea.baking_app.ui.Constants.STEP_LIST_KEY;

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
    @BindView(R.id.ingredients_title_tv)
    TextView ingredientsTitleTv;
    @BindView(R.id.recipe_ingredients_tv)
    TextView ingredientsTv;

    private Recipe recipe;
    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelable(RECIPE_KEY);
        } else {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(RECIPE_KEY)) {
                recipe = intent.getParcelableExtra(RECIPE_KEY);
            }
        }
        if (recipe == null) {
            return;
        }
        toolbar.setTitle(recipe.getName());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (recipe.getServings() != 0) {
            ingredientsTitleTv.append(String.format(" (%s servings)", recipe.getServings()));
        }
        List<Ingredient> ingredients = recipe.getIngredients();
        StringBuilder ingredientsFormatter = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            ingredientsFormatter.append(String.format("\u2022 %s %s %s \n", ingredient.getQuantity(), ingredient.getMeasure(), ingredient.getIngredient()));
        }
        ingredientsTv.setText(ingredientsFormatter.toString());

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECIPE_KEY, recipe);
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
                // TODO: 17.04.2018 make card selected
                int position = (int) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Constants.STEP_KEY, mSteps.get(position));
                    StepDetailFragment fragment = new StepDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.step_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, StepDetailActivity.class);
                    intent.putExtra(Constants.STEP_POS_KEY, position);
                    intent.putParcelableArrayListExtra(STEP_LIST_KEY, (ArrayList<? extends Parcelable>) mSteps);
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
            Step step = mSteps.get(position);
            holder.mIdView.setText(String.valueOf(step.getId()));
            holder.mContentView.setText(step.getShortDescription());

            holder.itemView.setTag(position);
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
