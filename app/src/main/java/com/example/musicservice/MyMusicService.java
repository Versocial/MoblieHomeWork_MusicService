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

import java.util.Timer;
import java.util.TimerTask;

public class MyMusicService extends Service {
    public static final int PROGRESS_LEN_SET=1001;
    public static final int PROGRESS_SET=1002;
    public static final int PROGRESS_INC=1003;

    public static final String PROGRESS="progress";
    public static final String LEN="len";
    private int period=100;//ms

    private Handler progressHandler=null;
    private ProgressUI ui=null;

    private String TAG="music";
    private UiBinder binder =null;

    public MyMusicService() {
        Log.d(TAG,"create");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"binding");
        binder=new UiBinder(this);
        return binder;
    }


    private MediaPlayer mediaPlayer=null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获取意图传递的信息
        String action = intent.getStringExtra("action");
        Log.d(TAG,"start: "+action);

        switch (action)
        {
            case "play":
                if (mediaPlayer == null)
                {
                    mediaPlayer = mediaPlayer();
                }
                mediaPlayer.start();

                int initProgress=intent.getIntExtra("init",0);
                if(initProgress!=0){
                    mediaPlayer.seekTo(initProgress*period);
                }

                if(binder!=null) {
                    binder.startTimer();
                }
                break;
            case "stop":
                if (mediaPlayer !=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                if(binder!=null){
                    binder.stopTimer();
                }
                break;
            case "pause":
                if (mediaPlayer !=null && mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                }

                if(binder!=null){
                    binder.stopTimer();

                }
                break;
        }


        return super.onStartCommand(intent, flags, startId);
    }

    MediaPlayer mediaPlayer(){
        return MediaPlayer.create(this,R.raw.newyear);
    }

    interface ProgressUI{
        public void progressUpdate(int len);
        public void setProgressBarLen(int len);
    };



    class UiBinder extends Binder {
        private MyMusicService service=null;
        private Timer timer=null;

        public UiBinder(MyMusicService service){
            this.service=service;
        }

        void bind(ProgressUI ui){
            service.ui=ui;
            if(mediaPlayer!=null) {
                service.ui.setProgressBarLen(mediaPlayer.getDuration() / period);
            }
            startTimer();
        }

        void unBind(){
            stopTimer();
        }

        void startTimer(){
            if(timer==null) {
                timer = new Timer("timer - " + TAG);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(ui!=null&&mediaPlayer!=null){
                            ui.progressUpdate(mediaPlayer.getCurrentPosition()/period);
                        }
                    }
                }, period, period);
            }
        }
        void stopTimer(){
            if(timer!=null){
                timer.cancel();
                timer=null;
            }
        }

        public void setMediaTime(int progress){
            if(service.mediaPlayer==null){
                service.mediaPlayer=mediaPlayer();
            }
            service.mediaPlayer.seekTo(progress*period);
        }



    }

}