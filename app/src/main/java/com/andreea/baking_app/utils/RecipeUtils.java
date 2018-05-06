package com.andreea.baking_app.utils;

import android.support.annotation.NonNull;

import com.andreea.baking_app.model.Ingredient;
import com.andreea.baking_app.model.Recipe;

public final class RecipeUtils {

    public static final String RECIPE_KEY = "recipe";

    public static final String STEP_KEY = "item_step";
    public static final String STEP_POS_KEY = "step_pos";
    public static final String STEP_LIST_KEY = "steps";

    public static final String VIDEO_POSITION_KEY = "position";
    public static final String VIDEO_READY = "play";

    private RecipeUtils() {
    }

    @NonNull
    public static String formatIngredients(Recipe recipe) {
        StringBuilder ingredientsFormatter = new StringBuilder();
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredientsFormatter.append(String.format("\u2022 %s %s %s \n", ingredient.getQuantity(), ingredient.getMeasure(), ingredient.getIngredient()));
        }
        return ingredientsFormatter.toString();
    }
}
