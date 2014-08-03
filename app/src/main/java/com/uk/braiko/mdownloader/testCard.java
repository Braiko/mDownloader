package com.uk.braiko.mdownloader;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.uk.braiko.mdownloader.my_loader.IMovieDownloadListener;
import com.uk.braiko.mdownloader.my_loader.MovieDownloaderManager;

import it.gmariotti.cardslib.library.internal.Card;

public class testCard extends Card implements IMovieDownloadListener {
    private MyActivity myActivity;
    private DownloadEpisode episode;
    private SeekBar progress;
    private TextView status;
    private TextView name;

    public testCard(MyActivity myActivity, DownloadEpisode episode) {
        super(myActivity, R.layout.test_card);
        this.myActivity = myActivity;
        this.episode = episode;
        InstallSwipe();
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        InstallGoBtn(view);
        InstallDeleteBtn(view);
        InstallName(view);
        this.progress = (SeekBar) view.findViewById(R.id.seekBar);
        this.status = (TextView) view.findViewById(R.id.status);
    }

    private void InstallName(View view) {
        this.name = (TextView)view.findViewById(R.id.name);
        name.setText(episode.getFull_path()+"\n\t::::: id ="+episode.getId());
    }

    private void InstallSwipe() {
        this.setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                MovieDownloaderManager.with(myActivity).by(episode).listened(testCard.this).delete();
            }
        });
    }

    private void InstallDeleteBtn(View view) {
        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDownloaderManager.with(myActivity).by(episode).listened(testCard.this).delete();
            }
        });
    }

    private void InstallGoBtn(final View view) {

        final Button btn = (Button) view.findViewById(R.id.go);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn.getText().equals("go")) {
                    MovieDownloaderManager.with(myActivity).by(episode).listened(testCard.this).load();
                    btn.setText("stop");
                } else {
                    MovieDownloaderManager.with(myActivity).by(episode).listened(testCard.this).pause();
                    btn.setText("go");
                }
            }
        });
    }

    @Override
    public void onProgress(DownloadEpisode downloadItem) {
        progress.setProgress((int) downloadItem.getPercent());
        status.setText("progress ::" + downloadItem.getFile_size()
        +"/"+downloadItem.getProgress() + "( "+ downloadItem.getPercent()+"% )");
        status.setBackgroundColor(Color.GREEN);
    }

    @Override
    public void onPauseMovie(DownloadEpisode downloadItem) {
        status.setText("pause ::" + downloadItem.getFile_size()
                +"/"+downloadItem.getProgress() + "( "+ downloadItem.getPercent()+"% )");
        status.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onResumeMovie(DownloadEpisode downloadItem) {
        status.setText("resume ::" + downloadItem.getFile_size()
                +"/"+downloadItem.getProgress() + "( "+ downloadItem.getPercent()+"% )");
        status.setBackgroundColor(Color.BLUE);
    }

    @Override
    public void onFinishMovie(DownloadEpisode downloadItem) {
        status.setText("finish this ::");
        status.setBackgroundColor(Color.RED);
    }

    @Override
    public void onStartMovie(DownloadEpisode downloadItem) {
        status.setText("start ::" + downloadItem.getFile_size()
                +"/"+downloadItem.getProgress() + "( "+ downloadItem.getPercent()+"% )");
        status.setBackgroundColor(Color.YELLOW);
    }

    @Override
    public void onFinishAll() {
        status.setText("finish all ::");
        status.setBackgroundColor(Color.RED);
    }

    @Override
    public void onStatus(DownloadEpisode downloadItem) {

    }
}
