package com.uk.braiko.mdownloader.downloader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.uk.braiko.mdownloader.Constants;
import com.uk.braiko.mdownloader.DownloadEpisode;
import com.uk.braiko.mdownloader.IntentUtils;

public class MovieDownloadManager {

	private Activity				mActivity;
	private MovieDownloadListener mListener;
	private BroadcastReceiver		mProgressReceiver, mPauseReceiver, mResumeReceiver, mFinishReseiver, mStartReceiver, mFinishAllReceiver, mStatusReceiver;

	public MovieDownloadManager(Activity _activity) {
		mActivity = _activity;

		mProgressReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (mListener != null)
				{
					mListener.onProgress(IntentUtils.getDownloadItem(intent));
				}
			}
		};
		mPauseReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (mListener != null)
				{
					mListener.onPauseMovie(IntentUtils.getDownloadItem(intent));
				}

			}
		};
		mResumeReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (mListener != null)
				{
					mListener.onResumeMovie(IntentUtils.getDownloadItem(intent));
				}

			}
		};
		mFinishReseiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (mListener != null)
				{
					mListener.onFinishMovie(IntentUtils.getDownloadItem(intent));
				}

			}
		};
		mStartReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (mListener != null)
				{
					mListener.onStartMovie(IntentUtils.getDownloadItem(intent));
				}

			}
		};
		mFinishAllReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (mListener != null)
				{
					mListener.onFinishAll();
				}

			}
		};

		mStatusReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (mListener != null)
				{
					mListener.onStatus(IntentUtils.getDownloadItem(intent));
				}

			}
		};
	}

	public void setDownloadListener(MovieDownloadListener _listener) {
		mListener = _listener;
	}

	public void bind() {

		mActivity.registerReceiver(mProgressReceiver, new IntentFilter(Constants.ACTION_PROGRESS));
		mActivity.registerReceiver(mPauseReceiver, new IntentFilter(Constants.ACTION_PAUSE_DOWNLOADING));
		mActivity.registerReceiver(mFinishAllReceiver, new IntentFilter(Constants.ACTION_FINISH_ALL_DOWNLOADING));
		mActivity.registerReceiver(mResumeReceiver, new IntentFilter(Constants.ACTION_RESUME_DOWNLOADING));
		mActivity.registerReceiver(mFinishReseiver, new IntentFilter(Constants.ACTION_FINISH_MOVIE_DOWNLOADING));
		mActivity.registerReceiver(mStartReceiver, new IntentFilter(Constants.ACTION_START_MOVIE_DOWNLOADING));
		mActivity.registerReceiver(mStatusReceiver, new IntentFilter(Constants.ACTION_STATUS));
	}

	public void unbind() {
		try
		{
			mActivity.unregisterReceiver(mProgressReceiver);
			mActivity.unregisterReceiver(mPauseReceiver);
			mActivity.unregisterReceiver(mFinishAllReceiver);
			mActivity.unregisterReceiver(mResumeReceiver);
			mActivity.unregisterReceiver(mFinishReseiver);
			mActivity.unregisterReceiver(mStartReceiver);
			mActivity.unregisterReceiver(mStatusReceiver);

		}
		catch (Exception e)
		{
		}
	}

	// commands

	public void add(DownloadEpisode _item) {
		Intent intent = new Intent(mActivity, DownloaderService.class);
		intent.putExtra(Constants.ACTION_ID, Constants.CMD_START_DOWNLOAD);
		intent.putExtra(Constants.ARG_DOWNLOAD_ITEM, _item);
		mActivity.startService(intent);
	}

	public void delete(DownloadEpisode _item) {
		Intent intent = new Intent(mActivity, DownloaderService.class);
		intent.putExtra(Constants.ACTION_ID, Constants.CMD_DELETE_MOVIE);
		intent.putExtra(Constants.ARG_DOWNLOAD_ITEM, _item);
		mActivity.startService(intent);
	}

	public void pause(DownloadEpisode _item) {
		Intent intent = new Intent(mActivity, DownloaderService.class);
		intent.putExtra(Constants.ACTION_ID, Constants.CMD_PAUSE_MOVIE);
		intent.putExtra(Constants.ARG_DOWNLOAD_ITEM, _item);
		mActivity.startService(intent);
	}

	public void resume(DownloadEpisode _item) {
		Intent intent = new Intent(mActivity, DownloaderService.class);
		intent.putExtra(Constants.ACTION_ID, Constants.CMD_RESUME_MOVIE);
		intent.putExtra(Constants.ARG_DOWNLOAD_ITEM, _item);
		mActivity.startService(intent);
	}
	
	public void getStatus() {
		Intent intent = new Intent(mActivity, DownloaderService.class);
		intent.putExtra(Constants.ACTION_ID, Constants.CMD_GET_STATUS);
		mActivity.startService(intent);
	}
	
}
