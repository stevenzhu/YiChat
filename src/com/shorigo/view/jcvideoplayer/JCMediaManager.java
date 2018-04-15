package com.shorigo.view.jcvideoplayer;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;

class JCMediaManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaPlayer.OnVideoSizeChangedListener {

	public MediaPlayer mediaPlayer;
	private static JCMediaManager jcMediaManager;
	public int currentVideoWidth = 0;
	public int currentVideoHeight = 0;
	public JCMediaPlayerListener listener;
	public JCMediaPlayerListener lastListener;
	public int lastState;

	public static JCMediaManager intance() {
		if (jcMediaManager == null) {
			jcMediaManager = new JCMediaManager();
		}
		return jcMediaManager;
	}

	public JCMediaManager() {
		mediaPlayer = new MediaPlayer();
	}

	public void prepareToPlay(Context context, String url) {
		if (TextUtils.isEmpty(url))
			return;
		try {
			mediaPlayer.release();
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(context, Uri.parse(url));
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnSeekCompleteListener(this);
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setOnVideoSizeChangedListener(this);
			mediaPlayer.prepareAsync();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void mute() {
		if (mediaPlayer != null) {
			mediaPlayer.setVolume(0, 0);
		}
	}

	public void unMute() {
		if (mediaPlayer != null && mediaPlayer != null) {
			int max = 100;
			int audioVolume = 100;
			double numerator = max - audioVolume > 0 ? Math.log(max - audioVolume) : 0;
			float volume = (float) (1 - (numerator / Math.log(max)));
			mediaPlayer.setVolume(volume, volume);
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		if (listener != null) {
			listener.onPrepared();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (listener != null) {
			listener.onCompletion();
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		if (listener != null) {
			listener.onBufferingUpdate(percent);
		}
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		if (listener != null) {
			listener.onSeekComplete();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (listener != null) {
			listener.onError(what, extra);
		}
		return true;
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		currentVideoWidth = mp.getVideoWidth();
		currentVideoHeight = mp.getVideoHeight();
		if (listener != null) {
			listener.onVideoSizeChanged();
		}
	}

	public void clearWidthAndHeight() {
		currentVideoWidth = 0;
		currentVideoHeight = 0;
	}

	interface JCMediaPlayerListener {
		void onPrepared();

		void onCompletion();

		void onBufferingUpdate(int percent);

		void onSeekComplete();

		void onError(int what, int extra);

		void onVideoSizeChanged();

		void onBackFullscreen();
	}
}
