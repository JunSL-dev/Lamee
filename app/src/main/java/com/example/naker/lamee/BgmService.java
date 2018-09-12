package com.example.naker.lamee;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class BgmService extends Service {
    MediaPlayer player;

    public BgmService() {
    }

    @Override
    public void onCreate(){

        player = MediaPlayer.create(this,R.raw.bgm);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

        Log.d("TAG","service");

        player.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
