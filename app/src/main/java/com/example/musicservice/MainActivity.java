package com.example.musicservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MyMusicService.ProgressUI {

    private String TAG="main";

    private TextView tv_1;
    private SeekBar progressBar;
    private Handler progressHandler=null;
    private MyMusicServiceCon connection = new MyMusicServiceCon();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=(SeekBar)findViewById(R.id.progress);

        progressBar.setMax(100);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    connection.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        progressHandler= new Handler(msg -> {
            Log.d(TAG,msg.what+" msg");
            switch (msg.what){
                case MyMusicService.PROGRESS_SET:
                    int progress=msg.getData().getInt(MyMusicService.PROGRESS);
                    progressBar.setProgress(progress);
                    break;
                case MyMusicService.PROGRESS_INC:
                    progressBar.setProgress(progressBar.getProgress()+1);
                    break;
                case MyMusicService.PROGRESS_LEN_SET:
                    int len=msg.getData().getInt(MyMusicService.LEN);
                    progressBar.setProgress(len);
                    break;
                default:
                    Log.e(TAG,"unknown msg.what :"+msg.what);
                    break;
            }
            return false;
        });
        tv_1 = (TextView)findViewById(R.id.tv_1);

        tv_1.setText("播放状态：停止播放。。。");
    }

    public void play_onclick(View view)
    {
        musicServiceDo("play");
        tv_1.setText("播放状态：正在播放。。。");
    }

    public void stop_onclick(View view)
    {
        musicServiceDo("stop");
        tv_1.setText("播放状态：停止播放。。。");
    }
    public void pause_onclick(View view)
    {
        musicServiceDo("pause");
        tv_1.setText("播放状态：暂停播放。。。");
    }
    public void exit_onclick(View view)
    {
        Log.d(TAG,"exit");

        stop_onclick(view);
        Intent intent = new Intent(this,MyMusicService.class);
        stopService(intent);
        unbindService(connection);
        finish();
    }

    @Override
    public void progressUpdate(int len){
            progressBar.setProgress(len);
        Log.d(TAG,""+len);
    }

    @Override
    public void setProgressBarLen(int len) {
        if(len>=0) {
            progressBar.setMax(len);
        }
        Log.d(TAG,""+len);
    }


    class MyMusicServiceCon implements ServiceConnection {
        MyMusicService.UiBinder service=null;

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            this.service=((MyMusicService.UiBinder) service);
            this.service.bind(MainActivity.this);
            Log.d(TAG,"on service con");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            if(service!=null){
                service.unBind();
                service=null;
            }
            Log.d(TAG,"on service disCon");
        }

        public void setProgress(int barLen){
            if(service==null){
                Log.d(TAG,"play "+barLen);
                Intent intent = new Intent(MainActivity.this,MyMusicService.class);
                intent.putExtra("action","play");
                intent.putExtra("init",barLen);
                startService(intent);
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
                tv_1.setText("播放状态：正在播放。。。");
            }
            else {
                service.setMediaTime(barLen);
            }
        };
    };

    private void musicServiceDo(String cmd){
        Log.d(TAG,cmd);
        Intent intent = new Intent(this,MyMusicService.class);
        intent.putExtra("action",cmd);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

}