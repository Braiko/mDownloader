package com.uk.braiko.mdownloader;

import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.uk.braiko.mdownloader.my_loader.IMovieDownloadListener;
import com.uk.braiko.mdownloader.my_loader.MovieDownloaderManager;

import it.gmariotti.cardslib.library.internal.Card;

public class testCard extends Card implements IMovieDownloadListener {
    private MyActivity myActivity;
    private DownloadEpisode episode;
    private SeekBar progress;

    public testCard(MyActivity myActivity, DownloadEpisode episode) {
        super(myActivity, R.layout.test_card);
        this.myActivity = myActivity;
        this.episode = episode;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        InstallGoBtn(view);
        this.progress = (SeekBar)view.findViewById(R.id.seekBar);
    }

    private void InstallGoBtn(View view) {
        view.findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDownloaderManager.with(myActivity).by(episode).listened(testCard.this).load();
            }
        });
    }

    @Override
    public void onProgress(DownloadEpisode downloadItem) {
        progress.setProgress((int) downloadItem.getProgress());
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
