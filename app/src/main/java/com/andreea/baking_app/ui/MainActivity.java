package com.andreea.baking_app.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreea.baking_app.R;
import com.andreea.baking_app.model.Recipe;
import com.andreea.baking_app.viewmodel.RecipesViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recipe_list)
    RecyclerView recyclerView;

    private RecipesViewModel recipesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        recipesViewModel = ViewModelProviders.of(this).get(RecipesViewModel.class);

        recipesViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                recyclerView.setAdapter(new RecipeListAdapter(recipes));
            }
        });
    }

    public static class RecipeListAdapter
            extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

        private final List<Recipe> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recipe item = (Recipe) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra("recipe", item);
                context.startActivity(intent);
            }
        };

        RecipeListAdapter(List<Recipe> items   ) {
            mValues = items;
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Recipe recipe = mValues.get(position);

            holder.mContentView.setText(recipe.getName());

            Picasso picasso = Picasso.get();
            RequestCreator request;
            if (TextUtils.isEmpty(recipe.getImage())) {
                request = picasso.load(R.drawable.cake);
            } else {
                request = picasso.load(recipe.getImage());
            }

            request.placeholder(android.R.drawable.progress_horizontal)
                    .error(android.R.drawable.stat_notify_error)
                    .into(holder.mThumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Successfully loaded recipe thumbnail: " + recipe);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Failed to load recipe thumbnail: " + recipe + e);
                        }
                    });

            holder.itemView.setTag(recipe);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.recipe_image_iv)
            ImageView mThumbnail;
            @BindView(R.id.recipe_name_tv)
            TextView mContentView;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
