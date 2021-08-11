package com.github.welcome_to_school_manager.helpers.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SharedPreferencesHelper
{
    private Context context;
    private final String DATA = "password";
    SharedPreferences sharedPref;
    public SharedPreferencesHelper(Context context)
    {
        this.context = context;
        sharedPref = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
    }
    public  Map<String, Object> getPreferences()
    {
        Map<String,Object> savePass = new HashMap<>();
        SharedPreferences sharedPref = ((Activity)context).getSharedPreferences(DATA, Context.MODE_PRIVATE);
        savePass.put("pass",sharedPref.getString("pass", ""));
        return  hasData()?savePass:null;
    }
    public void deletePreferences()
    {
        SharedPreferences sharedPref = ((Activity)context).getSharedPreferences(DATA, Context.MODE_PRIVATE);
        sharedPref.edit().clear().apply();

    }
    public void addPreferences(Map<String,Object> savePass)
    {
        SharedPreferences sharedPref = ((Activity)context).getSharedPreferences(DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("pass",String.valueOf(savePass.get("pass")));
        editor.apply();
    }
    public boolean hasData()
    {
        SharedPreferences sharedPref = ((Activity)context).getSharedPreferences(DATA, Context.MODE_PRIVATE);
        String estado = sharedPref.getString("pass", "");
        return !Objects.requireNonNull(estado).isEmpty();
    }
}
