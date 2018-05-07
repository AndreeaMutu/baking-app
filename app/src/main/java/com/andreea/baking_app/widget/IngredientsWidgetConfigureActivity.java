package com.andreea.baking_app.widget;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.andreea.baking_app.R;
import com.andreea.baking_app.model.Recipe;
import com.andreea.baking_app.utils.RecipeUtils;
import com.andreea.baking_app.viewmodel.RecipesViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The configuration screen for the {@link IngredientsWidget IngredientsWidget} AppWidget.
 */
public class IngredientsWidgetConfigureActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "com.andreea.baking_app.widget.IngredientsWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private Spinner spinner;
    private Map<String, String> recipesIngredients = new HashMap<>();

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = IngredientsWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String recipe = spinner.getSelectedItem().toString();
            saveRecipePref(context, mAppWidgetId, recipe);
            saveIngredientsPref(context, mAppWidgetId, recipe, recipesIngredients.get(recipe));

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            IngredientsWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public IngredientsWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveRecipePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadRecipePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteRecipePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    static void saveIngredientsPref(Context context, int appWidgetId, String recipe, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + recipe, text);
        prefs.apply();
    }

    static String loadIngredientsPref(Context context, int appWidgetId, String recipe) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String ingredients = prefs.getString(PREF_PREFIX_KEY + appWidgetId + recipe, null);
        if (ingredients != null) {
            return ingredients;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteIngredientsPref(Context context, int appWidgetId, String recipe) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + recipe);
        prefs.apply();
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.ingredients_widget_configure);
        RecipesViewModel recipesViewModel = ViewModelProviders.of(this).get(RecipesViewModel.class);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);
        spinner = (Spinner) findViewById(R.id.appwidget_spinner);

        recipesViewModel.getRecipes().observe(this, recipes -> {
            if (recipes != null) {
                for (Recipe recipe : recipes) {
                    String ingredients = RecipeUtils.formatIngredients(recipe);
                    recipesIngredients.put(recipe.getName(), ingredients);
                }
                List<String> recipeNames = new ArrayList<>(recipesIngredients.keySet());
                // Set dropdown recipe names
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, recipeNames);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
            }
        });

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

}

