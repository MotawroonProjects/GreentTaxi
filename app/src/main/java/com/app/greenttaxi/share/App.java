package com.app.greenttaxi.share;


import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.app.greenttaxi.language.Language;


public class App extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Language.updateResources(newBase,"ar"));
    }


    @Override
    public void onCreate() {
        super.onCreate();
//        TypefaceUtil.setDefaultFont(this, "DEFAULT", "fonts/Tajawal-Regular.ttf");
//        TypefaceUtil.setDefaultFont(this, "MONOSPACE", "fonts/Tajawal-Regular.ttf");
//        TypefaceUtil.setDefaultFont(this, "SERIF", "fonts/Tajawal-Regular.ttf");
//        TypefaceUtil.setDefaultFont(this, "SANS_SERIF", "fonts/Tajawal-Regular.ttf");

    }
}

