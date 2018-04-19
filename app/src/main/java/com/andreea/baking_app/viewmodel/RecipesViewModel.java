package com.andreea.baking_app.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.andreea.baking_app.model.Recipe;
import com.andreea.baking_app.web.RecipeService;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipesViewModel extends ViewModel {
    private static final String TAG = RecipesViewModel.class.getSimpleName();
    private LiveData<List<Recipe>> recipes;
    private final RecipeService recipeService;

    public RecipesViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://go.udacity.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        recipeService = retrofit.create(RecipeService.class);
    }

    public LiveData<List<Recipe>> getRecipes() {
        if (recipes != null) {
            Log.d(TAG, "Recipes already retrieved from web service.");
            return recipes;
        }
        final MutableLiveData<List<Recipe>> data = new MutableLiveData<>();
        recipeService.getRecipes().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Successfully retrieved recipes from web service.");
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Recipes service call failed.", t);
                data.setValue(Collections.<Recipe>emptyList());
            }
        });
        recipes = data;
        return recipes;
    }
}
