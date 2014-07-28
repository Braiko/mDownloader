package com.uk.braiko.mdownloader.my_loader;

import com.uk.braiko.mdownloader.DownloadEpisode;

public interface IMovieDownloadListener {
    public void onProgress(DownloadEpisode downloadItem) ;

    public void onPauseMovie(DownloadEpisode downloadItem) ;

    public void onResumeMovie(DownloadEpisode downloadItem) ;

    public void onFinishMovie(DownloadEpisode downloadItem) ;

    public void onStartMovie(DownloadEpisode downloadItem) ;

    public void onFinishAll() ;

    public void onStatus(DownloadEpisode downloadItem) ;
}
