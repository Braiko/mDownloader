package com.uk.braiko.mdownloader.my_loader;

import android.content.Intent;

import com.uk.braiko.mdownloader.Constants;
import com.uk.braiko.mdownloader.DownloadEpisode;
import com.uk.braiko.mdownloader.downloader.*;

/**
 * Created by yura on 28.07.14.
 */
public class MovieDownloaderManagerForEpisode implements IMovieDownloadListener{
    private final MovieDownloaderManager parent;
    private final DownloadEpisode episode;
    private IMovieDownloadListener listener = new MovieDownloadListener() {};

    public MovieDownloaderManagerForEpisode(MovieDownloaderManager parent, DownloadEpisode episode) {

        this.parent = parent;
        this.episode = episode;
    }

    public MovieDownloaderManagerForEpisode listenedBy(IMovieDownloadListener listener){
        this.listener = listener;
        return this;
    }


    //###################################################################################################################################################
    // operation
    //###################################################################################################################################################

    public void load(){
        parent.Load(this);
    }

    public void delete(){
        parent.Delete(this);
    }

    public void pause() {
        parent.Pause(this);
    }

    public void resume() {
        parent.Resume(this);
    }

    public void status() {
        parent.Status(this);
    }


    //###################################################################################################################################################
    // getters & setters
    //###################################################################################################################################################

    public DownloadEpisode getEpisode() {
        return episode;
    }

    //###################################################################################################################################################
    // listeners
    //###################################################################################################################################################

    @Override
    public void onProgress(DownloadEpisode downloadItem) {

    }

    @Override
    public void onPauseMovie(DownloadEpisode downloadItem) {

    }

    @Override
    public void onResumeMovie(DownloadEpisode downloadItem) {

    }

    @Override
    public void onFinishMovie(DownloadEpisode downloadItem) {

    }

    @Override
    public void onStartMovie(DownloadEpisode downloadItem) {

    }

    @Override
    public void onFinishAll() {

    }

    @Override
    public void onStatus(DownloadEpisode downloadItem) {

    }

}
