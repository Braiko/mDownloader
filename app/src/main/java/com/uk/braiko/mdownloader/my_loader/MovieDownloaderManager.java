package com.uk.braiko.mdownloader.my_loader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.uk.braiko.mdownloader.Constants;
import com.uk.braiko.mdownloader.DownloadEpisode;
import com.uk.braiko.mdownloader.IntentUtils;
import com.uk.braiko.mdownloader.my_loader.logger.L;
import com.uk.braiko.mdownloader.my_loader.logger.logTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by yura on 28.07.14.
 */
public class MovieDownloaderManager {
    private Activity activity;
    private static MovieDownloaderManager instance = new MovieDownloaderManager();
    private HashMap<Long, MovieDownloaderManagerForEpisode> episodeDownloaderById = new HashMap<Long, MovieDownloaderManagerForEpisode>();
    private BroadcastReceiver mProgressReceiver, mPauseReceiver, mResumeReceiver, mFinishReseiver, mStartReceiver, mFinishAllReceiver, mStatusReceiver,mAllStatusReceiver;
    private boolean isBroadCastListenerBind;
    private IOnServerStateListener listener = new IOnServerStateListener() {
        @Override
        public void onnewstateget(ArrayList<DownloadEpisode> episodes) {

        }

        @Override
        public void onFinishAll() {

        }
    };


    private MovieDownloaderManager() {
        InstallBroadcast();
    }

    private void InstallBroadcast() {
        mProgressReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadEpisode move = IntentUtils.getDownloadItem(intent);
                episodeDownloaderById.get(move.getEpisode_id()).onProgress(IntentUtils.getDownloadItem(intent));
            }
        };
        mPauseReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadEpisode move = IntentUtils.getDownloadItem(intent);
                episodeDownloaderById.get(move.getEpisode_id()).onPauseMovie(IntentUtils.getDownloadItem(intent));
            }
        };
        mResumeReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadEpisode move = IntentUtils.getDownloadItem(intent);
                episodeDownloaderById.get(move.getEpisode_id()).onResumeMovie(IntentUtils.getDownloadItem(intent));
            }
        };
        mFinishReseiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadEpisode move = IntentUtils.getDownloadItem(intent);
                episodeDownloaderById.get(move.getEpisode_id()).onFinishMovie(IntentUtils.getDownloadItem(intent));
            }
        };
        mStartReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadEpisode move = IntentUtils.getDownloadItem(intent);
                episodeDownloaderById.get(move.getEpisode_id()).onStartMovie(IntentUtils.getDownloadItem(intent));
            }
        };
        mFinishAllReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                for (Long episodeID : episodeDownloaderById.keySet())
                    episodeDownloaderById.get(episodeID).onFinishAll();
                listener.onFinishAll();
            }
        };

        mStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadEpisode move = IntentUtils.getDownloadItem(intent);
                episodeDownloaderById.get(move.getEpisode_id()).onStatus(IntentUtils.getDownloadItem(intent));
            }
        };
        mAllStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<DownloadEpisode> move = IntentUtils.getDownloadItems(intent);
                listener.onnewstateget(move);
            }
        };
    }


    //###################################################################################################################################################
    // initialization
    //###################################################################################################################################################

    public static MovieDownloaderManager with(Activity activity) {
        if (instance.activity == null)
            instance.activity = activity;
        if (!instance.isBroadCastListenerBind)
            instance.bind();
        return instance;
    }

    public static void destroy(){
        instance.unbind();
    }

    public MovieDownloaderManagerForEpisode by(DownloadEpisode episode) {
        L.info("appare new episode", logTag.dowloader_sevice);
        if (!episodeDownloaderById.containsKey(episode.getId()))
            episodeDownloaderById.put(episode.getEpisode_id(),
                    new MovieDownloaderManagerForEpisode(this, episode));
        return episodeDownloaderById.get(episode.getEpisode_id());
    }

    public void bind() {
        isBroadCastListenerBind = true;
        activity.registerReceiver(mProgressReceiver, new IntentFilter(Constants.ACTION_PROGRESS));
        activity.registerReceiver(mPauseReceiver, new IntentFilter(Constants.ACTION_PAUSE_DOWNLOADING));
        activity.registerReceiver(mFinishAllReceiver, new IntentFilter(Constants.ACTION_FINISH_ALL_DOWNLOADING));
        activity.registerReceiver(mResumeReceiver, new IntentFilter(Constants.ACTION_RESUME_DOWNLOADING));
        activity.registerReceiver(mFinishReseiver, new IntentFilter(Constants.ACTION_FINISH_MOVIE_DOWNLOADING));
        activity.registerReceiver(mStartReceiver, new IntentFilter(Constants.ACTION_START_MOVIE_DOWNLOADING));
        activity.registerReceiver(mStatusReceiver, new IntentFilter(Constants.ACTION_STATUS));
        activity.registerReceiver(mAllStatusReceiver, new IntentFilter(Constants.ACTION_All_STATUS));
    }

    public void unbind() {
        try {
            activity.unregisterReceiver(mProgressReceiver);
            activity.unregisterReceiver(mPauseReceiver);
            activity.unregisterReceiver(mFinishAllReceiver);
            activity.unregisterReceiver(mResumeReceiver);
            activity.unregisterReceiver(mFinishReseiver);
            activity.unregisterReceiver(mStartReceiver);
            activity.unregisterReceiver(mStatusReceiver);
            activity.unregisterReceiver(mAllStatusReceiver);
            this.isBroadCastListenerBind = false;
        } catch (Exception e) {
        }
    }

    //###################################################################################################################################################
    // operation
    //###################################################################################################################################################

    protected void Load(MovieDownloaderManagerForEpisode movieDownloaderManagerForEpisode) {
        sendTaskToService(movieDownloaderManagerForEpisode.getEpisode(), Constants.CMD_START_DOWNLOAD);
    }

    protected void Delete(MovieDownloaderManagerForEpisode movieDownloaderManagerForEpisode) {
        sendTaskToService(movieDownloaderManagerForEpisode.getEpisode(), Constants.CMD_DELETE_MOVIE);
    }

    protected void Pause(MovieDownloaderManagerForEpisode movieDownloaderManagerForEpisode) {
        sendTaskToService(movieDownloaderManagerForEpisode.getEpisode(), Constants.CMD_PAUSE_MOVIE);
    }

    protected void Resume(MovieDownloaderManagerForEpisode movieDownloaderManagerForEpisode) {
        sendTaskToService(movieDownloaderManagerForEpisode.getEpisode(), Constants.CMD_START_DOWNLOAD);

    }

    protected void Status(MovieDownloaderManagerForEpisode movieDownloaderManagerForEpisode) {
        sendTaskToService(movieDownloaderManagerForEpisode.getEpisode(), Constants.CMD_GET_STATUS);

    }

    public void Save(MovieDownloaderManagerForEpisode movieDownloaderManagerForEpisode) {
        sendTaskToService(movieDownloaderManagerForEpisode.getEpisode(), Constants.CMD_SAVE);
    }

    private void sendTaskToService(DownloadEpisode episode, int taskType) {
        //todo change package name for download service
        L.info("send task to service", logTag.dowloader_sevice);
        Intent intent = new Intent(activity, com.uk.braiko.mdownloader.my_loader.DownloaderService.class);
        intent.putExtra(Constants.ACTION_ID, taskType);
        intent.putExtra(Constants.ARG_DOWNLOAD_ITEM, episode);
        activity.startService(intent);
    }

    private void sendTaskToService(int taskType) {
        //todo change package name for download service
        L.info("send task to service", logTag.dowloader_sevice);
        Intent intent = new Intent(activity, com.uk.braiko.mdownloader.my_loader.DownloaderService.class);
        intent.putExtra(Constants.ACTION_ID, taskType);
        activity.startService(intent);
    }

    public MovieDownloaderManager status(){
        sendTaskToService( Constants.CMD_GET_COMMON_STATUS);
        return this;
    }

    public MovieDownloaderManager listened(IOnServerStateListener listener){
        this.listener = listener;
        return this;
    }
}
