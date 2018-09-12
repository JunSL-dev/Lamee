package com.example.naker.lamee;

import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AnimationManager {
    private final static String TAG = "AnimationManager";
    private static AnimationManager manager;

    private AnimationManager(){

    }

    public static AnimationManager getAnimationManager(){
        if (manager==null){
            manager = new AnimationManager();
        }

        return manager;
    }

    public void crossFade(final View view, boolean show){
        if(show){
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);

            view.animate().alpha(1f).setDuration(500).setListener(null);
        } else{
            view.animate().alpha(0f).setDuration(800).setListener(null);

            final Handler handler = new Handler();

            new Thread(){
                @Override
                public void run() {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setVisibility(View.GONE);
                        }
                    },800);
                }
            }.start();
        }
    }

    public void blink(View view){
        while(true){
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);

            view.animate().alpha(1f).setDuration(1000).setListener(null);
            view.animate().alpha(0f).setDuration(1000).setListener(null);

            view.setVisibility(View.GONE);
        }
    }
}
