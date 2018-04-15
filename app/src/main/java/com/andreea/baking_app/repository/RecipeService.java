package com.andreea.baking_app.repository;

import com.andreea.baking_app.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeService {
    @GET("android-baking-app-json")
    Call<List<Recipe>> getRecipes();
}
