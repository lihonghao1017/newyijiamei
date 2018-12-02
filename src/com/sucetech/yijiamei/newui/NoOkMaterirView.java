package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;
import com.sucetech.yijiamei.view.voice.AudioRecorder;
import com.sucetech.yijiamei.view.voice.RecordButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lihh on 2018/10/18.
 */

public class NoOkMaterirView extends BaseView implements View.OnClickListener {
    private ImageView img01delete, img03delete, img02delete;
    private ImageView img01, img03, img02;
    private RecoveryMaterirView recoveryMaterirView;
    private List<FormImage> datas;
    private RecordButton mRecordButton;
    private ImageView paly;
    private View voiceLayout;
    private MediaPlayer mediaPlayer;
    private TextView end;
    private String audioPath;
    private AnimationDrawable animationDrawable;

    public NoOkMaterirView(Context context, ViewGroup ParentView, RecoveryMaterirView recoveryMaterirView) {
        super(context, ParentView);
        this.recoveryMaterirView = recoveryMaterirView;
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        switch (status) {
            case imgMsg:
                FormImage formImage = (FormImage) obj;
                if (formImage.id == R.id.paizhaoLayout) {
                    setPhotos(formImage);
                    datas.add(formImage);
                }
                break;
        }
    }
    public void onkeyDown(){
        onClick(this.findViewById(R.id.back));
    }
    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.no_ok_materir_layout, null);
        addView(v);
        v.findViewById(R.id.back).setOnClickListener(this);
        v.findViewById(R.id.nextAction).setOnClickListener(this);
        v.findViewById(R.id.paizhaoLayout).setOnClickListener(this);
        img01 = v.findViewById(R.id.img01);
        img03 = v.findViewById(R.id.img03);
        img02 = v.findViewById(R.id.img02);
        img01delete = v.findViewById(R.id.img01delete);
        img03delete = v.findViewById(R.id.img03delete);
        img02delete = v.findViewById(R.id.img02delete);
        img01delete.setOnClickListener(this);
        img03delete.setOnClickListener(this);
        img02delete.setOnClickListener(this);
        datas = new ArrayList<>();
        voiceLayout = v.findViewById(R.id.voiceLayout);
        paly = v.findViewById(R.id.play);
        paly.setOnClickListener(this);
        paly.setImageResource(R.drawable.sound_anim);
        end = v.findViewById(R.id.end);
        mRecordButton = (RecordButton) v.findViewById(R.id.btn_record);
        mRecordButton.setAudioRecord(new AudioRecorder());
        //语音发送的回调
        mRecordButton.setRecordListener(new RecordButton.RecordListener() {
            @Override
            public void recordEnd(String filePath, float time) {
//                voiceFile.setText(filePath);
                Log.e("LLL", "filePath-->" + filePath);
                Log.e("LLL", "time-->" + time);//秒
                audioPath=filePath;
                voiceLayout.setVisibility(View.VISIBLE);
                //设置播放音频数据文件
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();
                    int du = mediaPlayer.getDuration();
                    end.setText(du / 1000 + "s");
                } catch (IOException e) {
                    e.printStackTrace();
                    mediaPlayer = null;
                }
            }
        });
    }

    private volatile boolean isPlaying;

    /**
     * @description 播放音频
     * @author ldm
     * @time 2017/2/9 16:54
     */
    private void playAudio() {
        if (null != mediaPlayer && !isPlaying) {
            isPlaying = true;
            ((AnimationDrawable) paly.getDrawable()).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startPlay();
                }
            }).start();
        }
    }

    /**
     * @description 开始播放音频文件
     * @author ldm
     * @time 2017/2/9 16:56
     */
    private void startPlay() {
        try {
            //设置播放监听事件
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //播放完成
                    playEndOrFail();
                }
            });


//            //播放发生错误监听事件
//            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                @Override
//                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                    playEndOrFail(false);
//                    return true;
//                }
//            });
            //播放器音量配置
            mediaPlayer.setVolume(1, 1);
            //是否循环播放
            mediaPlayer.setLooping(false);
            //准备及播放
//            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            //播放失败正理
            playEndOrFail();
        }

    }

    /**
     * @description 停止播放或播放失败处理
     * @author ldm
     * @time 2017/2/9 16:58
     */
    private void playEndOrFail() {
        isPlaying = false;
        ((AnimationDrawable) paly.getDrawable()).stop();
        if (null != mediaPlayer) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img01delete:
                img01.setImageDrawable(null);
                img01.setTag(null);
                img01delete.setVisibility(View.GONE);
                break;
            case R.id.img02delete:
                img02.setImageDrawable(null);
                img02.setTag(null);
                img02delete.setVisibility(View.GONE);
                break;
            case R.id.img03delete:
                img03.setImageDrawable(null);
                img03.setTag(null);
                img03delete.setVisibility(View.GONE);
                break;
            case R.id.back:
                prentView.removeView(this);
                prentView.setVisibility(View.GONE);
                break;
            case R.id.nextAction:
                if (datas.size() > 0) {
                    recoveryMaterirView.setNoOKItem(datas,audioPath);
                    this.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "请拍照不合格证据", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.paizhaoLayout:
                if (img01.getTag() == null || img02.getTag() == null || img03.getTag() == null)
                    ((MainActivity) getContext()).requestPicture(R.id.paizhaoLayout);
                else
                    Toast.makeText(getContext(), "请删除", Toast.LENGTH_LONG).show();
                break;
            case R.id.play:
                if (isPlaying) {
                    playEndOrFail();
                } else {
                    if (mediaPlayer==null){
                        try {
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(audioPath);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    playAudio();
                }
                break;
        }
    }

    private void setPhotos(FormImage formImage) {
        if (img01.getTag() == null) {
            img01.setImageBitmap(formImage.mBitmap);
            img01.setTag(formImage);
            img01delete.setVisibility(View.VISIBLE);
        } else if (img02.getTag() == null) {
            img02.setImageBitmap(formImage.mBitmap);
            img02.setTag(formImage);
            img02delete.setVisibility(View.VISIBLE);
        } else if (img03.getTag() == null) {
            img03.setImageBitmap(formImage.mBitmap);
            img03.setTag(formImage);
            img03delete.setVisibility(View.VISIBLE);
        }

    }
}
