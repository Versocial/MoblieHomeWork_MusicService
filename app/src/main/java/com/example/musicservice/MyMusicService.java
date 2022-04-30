package com.example.musicservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
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
    private MainActivity context=null;

    public MyMusicService(Handler progressHandler) {
        if(getBaseContext() instanceof MainActivity){
            this.context=(MainActivity) getBaseContext();
                Log.d("sdf","dsf");
        }
        Log.d("sdf","dfs");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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

        if(context!=null){
            Message msg=Message.obtain();
            msg.what=PROGRESS_SET;
            Bundle data=new Bundle();data.putInt(LEN,mediaPlayer.getDuration());
            msg.setData(data);
            context.progressUpdate(msg);
        }

        return super.onStartCommand(intent, flags, startId);
    }



}