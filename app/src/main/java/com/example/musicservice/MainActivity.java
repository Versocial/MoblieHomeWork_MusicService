package com.example.musicservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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
        Intent intent = new Intent(this,MyMusicService.class);

        intent.putExtra("action","play");

        startService(intent);

        tv_1.setText("播放状态11：正在播放。。。");
    }

    public void stop_onclick(View view)
    {
        Intent intent = new Intent(this,MyMusicService.class);

        intent.putExtra("action","stop");

        startService(intent);

        tv_1.setText("播放状态11：停止播放。。。");
    }
    public void pause_onclick(View view)
    {
        Intent intent = new Intent(this,MyMusicService.class);

        intent.putExtra("action","pause");

        startService(intent);

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

}