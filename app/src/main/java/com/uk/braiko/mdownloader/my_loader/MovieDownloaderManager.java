package com.uk.braiko.mdownloader.my_loader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.uk.braiko.mdownloader.Constants;
import com.uk.braiko.mdownloader.DownloadEpisode;
import com.uk.braiko.mdownloader.IntentUtils;
import com.uk.braiko.mdownloader.my_loader.logger.L;
import com.uk.braiko.mdownloader.my_loader.logger.logTag;

import java.util.HashMap;

/**
 * Created by yura on 28.07.14.
 */
public class MovieDownloaderManager {
    private Activity activity;
    private static MovieDownloaderManager instance = new MovieDownloaderManager();
    private HashMap<Long, MovieDownloaderManagerForEpisode> episodeDownloaderById = new HashMap<Long, MovieDownloaderManagerForEpisode>();
    private BroadcastReceiver mProgressReceiver, mPauseReceiver, mResumeReceiver, mFinishReseiver, mStartReceiver, mFinishAllReceiver, mStatusReceiver;


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
                DownloadEpisode move = IntentUtils.getDownloadItem(intent);
                episodeDownloaderById.get(move.getEpisode_id()).onFinishAll();
            }
        };

        mStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadEpisode move = IntentUtils.getDownloadItem(intent);
                episodeDownloaderById.get(move.getEpisode_id()).onStatus(IntentUtils.getDownloadItem(intent));
            }
        };
    }


    //###################################################################################################################################################
    // initialization
    //###################################################################################################################################################

    public static MovieDownloaderManager with(Activity activity) {
        if (instance.activity == null)
            instance.activity = activity;
        return instance;
    }

    public MovieDownloaderManagerForEpisode by(DownloadEpisode episode) {
        L.info("appare new episode", logTag.dowloader_sevice);
        if (!episodeDownloaderById.containsKey(episode.getId()))
            episodeDownloaderById.put(episode.getEpisode_id(),
                    new MovieDownloaderManagerForEpisode(this, episode));
        return episodeDownloaderById.get(episode.getEpisode_id());
    }

    //###################################################################################################################################################
    // operation
    //###################################################################################################################################################

    protected void Load(MovieDownloaderManagerForEpisode movieDownloaderManagerForEpisode) {
        L.info("send load task to service", logTag.dowloader_sevice);
        Intent intent = new Intent(activity, com.uk.braiko.mdownloader.my_loader.DownloaderService.class);
        intent.putExtra(Constants.ACTION_ID, Constants.CMD_START_DOWNLOAD);
        intent.putExtra(Constants.ARG_DOWNLOAD_ITEM, movieDownloaderManagerForEpisode.getEpisode());
        activity.startService(intent);
    }

    protected void Delete(MovieDownloaderManagerForEpisode movieDownloaderManagerForEpisode) {

    }

    protected void Pause(MovieDownloaderManagerForEpisode movieDownloaderManagerForEpisode) {

    }

    protected void Resume(MovieDownloaderManagerForEpisode movieDownloaderManagerForEpisode) {

    }

    protected void Status(MovieDownloaderManagerForEpisode movieDownloaderManagerForEpisode) {

    }
}
