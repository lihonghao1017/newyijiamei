package com.sucetech.yijiamei.view.voice;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/***
 * @author  madreain
 */
public class AudioRecorder implements RecordStrategy {

	private MediaRecorder recorder;
	private String fileName;
	private String fileFolder = Environment.getExternalStorageDirectory()
			.getPath() + "/Test";

	private boolean isRecording = false;

	@Override
	public void ready() {
		// TODO Auto-generated method stub
		File file = new File(fileFolder);
		if (!file.exists()) {
			file.mkdir();
		}
		fileName = getCurrentDate();
		recorder = new MediaRecorder();
		recorder.setOutputFile(fileFolder + "/" + fileName + ".amr");
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置MediaRecorder的音频源为麦克风
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);// 设置MediaRecorder录制的音频格式
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置MediaRecorder录制音频的编码为amr
	}

	// 以当前时间作为文件名
	private String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		if (!isRecording) {
			try {
				recorder.prepare();
				recorder.start();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			isRecording = true;
		}

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if (isRecording) {
			try {
				//下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
				//报错为：RuntimeException:stop failed
				recorder.setOnErrorListener(null);
				recorder.setOnInfoListener(null);
				recorder.setPreviewDisplay(null);
				recorder.stop();
			} catch (IllegalStateException e) {
				// TODO: handle exception
				Log.i("Exception", Log.getStackTraceString(e));
			}catch (RuntimeException e) {
				// TODO: handle exception
				Log.i("Exception", Log.getStackTraceString(e));
			}catch (Exception e) {
				// TODO: handle exception
				Log.i("Exception", Log.getStackTraceString(e));
			}
			//added by ouyang end

			recorder.release();
			isRecording = false;
		}


//		recorder.stop();
//			recorder.release();
//			isRecording = false;
//		}

	}

	@Override
	public void deleteOldFile() {
		// TODO Auto-generated method stub
		File file = new File(fileFolder + "/" + fileName + ".amr");
		file.deleteOnExit();
	}

	@Override
	public double getAmplitude() {
		// TODO Auto-generated method stub
		if (!isRecording) {
			return 0;
		}
		return recorder.getMaxAmplitude();
	}

	@Override
	public String getFilePath() {
		// TODO Auto-generated method stub
		return fileFolder + "/" + fileName + ".amr";
	}

}
