package com.shalatan.devjoke.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences


class SharedPreferencesClass {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var context: Context

    public fun getPrefs(activity: Activity): SharedPreferences {
        return activity.getPreferences(Context.MODE_PRIVATE)
    }


}