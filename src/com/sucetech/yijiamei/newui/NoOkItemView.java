package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;

import java.io.IOException;
import java.util.List;

/**
 * Created by lihh on 2018/10/19.
 */

public class NoOkItemView extends BaseView implements View.OnClickListener{
    private ImageView img01, img02, img03;
    public  List<FormImage> imgs;
    private ImageView paly;
    private View voiceLayout;
    private MediaPlayer mediaPlayer;
    private TextView end;
    private String audioPath;

    public NoOkItemView(Context context, ViewGroup ParentView) {
        super(context, ParentView);
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        if (status==EventStatus.showNoOKIemt){
            imgs= (List<FormImage>) obj;
            for (int i = 0; i <imgs.size() ; i++) {
                setPhotos(imgs.get(i));
                if (i==0){
                    img01.setVisibility(View.VISIBLE);
                }else if(i==1){
                    img02.setVisibility(View.VISIBLE);
                }else if(i==2){
                    img03.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    public void showSoundLayout(String audioPath){
        this.audioPath=audioPath;
        if (audioPath!=null){
            voiceLayout.setVisibility(View.VISIBLE);
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioPath);
                mediaPlayer.prepare();
                int du = mediaPlayer.getDuration();
                end.setText(du / 1000 + "s");
            } catch (IOException e) {
                e.printStackTrace();
                mediaPlayer = null;
            }
        }
    }

    @Override
    public void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.no_ok_item_layout, null);
        addView(view);
        img01 = view.findViewById(R.id.img01);
        img02 = view.findViewById(R.id.img02);
        img03 = view.findViewById(R.id.img03);
        voiceLayout = view.findViewById(R.id.voiceLayout);
        paly = view.findViewById(R.id.play);
        paly.setOnClickListener(this);
        paly.setImageResource(R.drawable.sound_anim);
        end = view.findViewById(R.id.end);
    }

    private void setPhotos(FormImage formImage) {
        if (img01.getTag() == null) {
            img01.setImageBitmap(formImage.mBitmap);
            img01.setTag(formImage);
        } else if (img02.getTag() == null) {
            img02.setImageBitmap(formImage.mBitmap);
            img02.setTag(formImage);
        } else if (img03.getTag() == null) {
            img03.setImageBitmap(formImage.mBitmap);
            img03.setTag(formImage);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.play){
            if (isPlaying) {
                playEndOrFail();
            } else {
                playAudio();
            }
        }

    }
    private volatile boolean isPlaying;

    private void playAudio() {
        if (mediaPlayer==null){
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioPath);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
}
