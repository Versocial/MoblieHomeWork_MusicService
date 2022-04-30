package com.example.musicservice;

import androidx.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=(SeekBar)findViewById(R.id.progress);

        progressBar.setMax(100);

        progressHandler= new Handler(msg -> {
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

        tv_1.setText("播放状态11：停止播放。。。");
    }

    public void play_onclick(View view)
    {
        Intent intent = new Intent(MainActivity.this,MyMusicService.class);

        intent.putExtra("action","play");

        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        tv_1.setText("播放状态11：正在播放。。。");
    }

    public void stop_onclick(View view)
    {
        Intent intent = new Intent(MainActivity.this,MyMusicService.class);

        intent.putExtra("action","stop");

        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        tv_1.setText("播放状态11：停止播放。。。");
    }
    public void pause_onclick(View view)
    {
        Intent intent = new Intent(MainActivity.this,MyMusicService.class);

        intent.putExtra("action","pause");

        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        tv_1.setText("播放状态11：暂停播放。。。");
    }
    public void exit_onclick(View view)
    {
        stop_onclick(view);
        finish();
    }

    public void progressUpdate(Message msg){
            progressHandler.sendMessage(msg);
    }


    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            ((MyMusicService.TheBinder) service).bind(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

}