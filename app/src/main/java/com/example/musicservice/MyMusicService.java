package com.example.musicservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MyMusicService extends Service {
    public static final int PROGRESS_LEN_SET=1001;
    public static final int PROGRESS_SET=1002;
    public static final int PROGRESS_INC=1003;

    public static final String PROGRESS="progress";
    public static final String LEN="len";

    private Handler progressHandler=null;
    private ProgressUI ui=null;

    private String TAG="music";

    public MyMusicService() {
        Log.d(TAG,"dfs");
    }

    @Override
    public IBinder onBind(Intent intent) {
        onStartCommand(intent,0,0);
        return new TheBinder(this);
    }


    private MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"start");
        //获取意图传递的信息
        String action = intent.getStringExtra("action");

        switch (action)
        {
            case "play":
                if (mediaPlayer == null)
                {
                    mediaPlayer = MediaPlayer.create(this,R.raw.newyear);

                }
                mediaPlayer.start();

                break;
            case "stop":
                if (mediaPlayer !=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                break;
            case "pause":
                if (mediaPlayer !=null && mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                }
                break;
        }

        if(ui!=null){
            Message msg=Message.obtain();
            msg.what=PROGRESS_SET;
            Bundle data=new Bundle();data.putInt(LEN,mediaPlayer.getDuration());
            msg.setData(data);
            ui.progressUpdate(msg);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    interface ProgressUI{
        public void progressUpdate(Message msg);
    };

    public class TheBinder extends Binder {
        private MyMusicService service;
        public TheBinder(MyMusicService service){
            this.service=service;
        }

        void bind(ProgressUI ui){
            service.ui=ui;
        }
    }
}