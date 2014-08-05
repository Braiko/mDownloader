package com.uk.braiko.mdownloader.my_loader;

import android.content.Context;

import com.uk.braiko.mdownloader.DownloadEpisode;
import com.uk.braiko.mdownloader.NetUtils;
import com.uk.braiko.mdownloader.my_loader.logger.L;
import com.uk.braiko.mdownloader.my_loader.logger.logTag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.uk.braiko.mdownloader.my_loader.DownloaderService.getMovieFullName;
import static com.uk.braiko.mdownloader.my_loader.DownloaderService.refreshLink;
import static com.uk.braiko.mdownloader.my_loader.DownloaderService.renameMovie;

/**
 * Created by yura on 28.07.14.
 */
public class LoadMovieThread extends Thread {
    private volatile boolean isFree = true;
    private IOnCompliteTaskListener compliteListener;
    private DownloaderService.Task task;
    private volatile boolean isBreakDownloading = false;
    private Context context;
    private IMovieDownloadListener downloadListener;
    private boolean isDeleted = false;

    public LoadMovieThread(Context context, IMovieDownloadListener downloadListener) {
        this.context = context;
        this.downloadListener = downloadListener;
        this.start();
    }

    public boolean isFree() {
        return isFree;
    }

    public void doTask(DownloaderService.Task t) {
        isFree = false;
        isBreakDownloading = false;
        isDeleted = false;
        task = t;
    }

    public void setOnCompliteTask(IOnCompliteTaskListener listener) {
        this.compliteListener = listener;
    }

    public DownloaderService.Task getTask() {
        return task;
    }


    public void BreakDownloaded() {
        isBreakDownloading = true;
    }

    public void DeleteMove() {
        isBreakDownloading = true;
        isDeleted = true;
    }


    @Override
    public void run() {
        super.run();
        while (true) {
            if (isFree) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //todo log it
                }
                continue;
            }
            ProccessTask();
            isFree = true;
            compliteListener.onComplite();
        }
    }

    private void ProccessTask() {
        downloadEpisode(task.episode);
    }

    private void downloadEpisode(DownloadEpisode episode) {
        L.info("start download epizode", logTag.dowloader_sevice);
        long downloadPosition;
        try {
            PrepareEpisodeForDownload(episode);
            File downloadingFile = getDownloadingFile(episode);
            downloadPosition = downloadingFile.length();
            InputStream in = getInputConnectionForEpisode(episode, downloadPosition);
            long movieLength = episode.getFile_size() - downloadPosition;
            if (downloadPosition > 0)
                ON_RESUME(episode);
            else
                ON_START(episode);
            startMainDownloadLoop(episode, in, downloadingFile, downloadPosition);
            episode.setIs_downloading(0);
            if (isDeleted) {
                episode.setProgress(0);
                episode.setPercent(0);
                episode.save();
                return;
            }
            ON_PROGRESS(episode);
            if (episode.getFile_size() == episode.getProgress())
                renameMovie(context, episode).save();
            ON_FINISH_EPISODE(episode);
        } catch (Exception e) {
            episode.save();
            ON_PAUSED(episode);
            e.printStackTrace();
            L.error("some very big error in file load", e, logTag.dowloader_sevice);
            if (!NetUtils.isNetworkAvailable(context)) {
                episode.save();
                ON_PAUSED(episode);
                //todo
            }
        }
    }

    private InputStream getInputConnectionForEpisode(DownloadEpisode episode, long downloadPosition) throws IOException {
        URL url = new URL(episode.getLink());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Range", "bytes=" + downloadPosition + "-");
        connection.setDoInput(true);
        InputStream in = null;
        try {
            in = new BufferedInputStream(connection.getInputStream());
        } catch (FileNotFoundException e) {
            L.error("link need refresh", e, logTag.dowloader_sevice);
            refreshLink(episode);
            url = new URL(episode.getLink());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + downloadPosition + "-");
            connection.setDoInput(true);
            in = new BufferedInputStream(connection.getInputStream());
        }

        long movieLength = connection.getContentLength();
        episode.setFile_size(movieLength + downloadPosition);
        episode.save();
        return in;
    }

    private void PrepareEpisodeForDownload(DownloadEpisode episode) {
        episode.setIs_downloading(1);
        episode.save();
    }


    public File getDownloadingFile(DownloadEpisode episode) throws IOException {
        String fileName = episode.getFull_path();
        if (fileName.equals("")) {
            fileName = getMovieFullName(context, episode, true);
            episode.setFull_path(fileName);
            episode.save();
        }
        File downloadingFile = new File(episode.getFull_path());
        if (!downloadingFile.exists()) downloadingFile.createNewFile();
        return downloadingFile;
    }

    public void startMainDownloadLoop(DownloadEpisode episode, InputStream in, File downloadingFile, long downloadPosition) throws IOException {
        int percent = episode.getPercent();
        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(downloadingFile, true), 8192);
        byte[] data = new byte[8192];
        int x;
        long movieLength = episode.getFile_size() - downloadPosition;
        long lastProgress = downloadPosition;
        int bufferSize = 8192;
        int maxInterationCount = 15;
        int currentIterationCount = 0;
        while (true) {
            if (isBreakDownloading) {
                L.info("download thread is stoped download epizode with id (" + episode.getEpisode_id() + ")", logTag.dowloader_sevice);
                bout.flush();
                bout.close();
                episode.setProgress(downloadPosition);
                episode.setPercent(percent);
                episode.save();
                if (isDeleted)  ON_FINISH_EPISODE(episode);
                else            ON_PAUSED(episode);
                return;
            }
            if ((x = in.read(data, 0, 8192)) < 0) {
                episode.setPercent(100);
                episode.setProgress(movieLength);
                break;
            }
            bout.write(data, 0, x);
            downloadPosition += x;
            episode.setProgress(downloadPosition);
            percent = (int) (((double) downloadPosition / (double) episode.getFile_size()) * 100);
            L.debug("receive " + x + " byte of doanloaded file by id " + episode.getEpisode_id() + "( " + percent + " %)", logTag.dowloader_sevice);
            episode.setPercent(percent);
            episode.save();
            currentIterationCount++;
            if (currentIterationCount >= maxInterationCount) {
                ON_PROGRESS(episode);
                currentIterationCount = 0;
            }
        }
        bout.close();
        L.info("file was dowloaded, episod id = " + episode.getEpisode_id(), logTag.dowloader_sevice);
    }

    private void ON_FINISH_EPISODE(DownloadEpisode episode) {
        downloadListener.onFinishMovie(episode);
    }

    private void ON_PROGRESS(DownloadEpisode episode) {
        downloadListener.onProgress(episode);
    }

    private void ON_START(DownloadEpisode episode) {
        downloadListener.onStartMovie(episode);
    }

    private void ON_PAUSED(DownloadEpisode episode) {
        downloadListener.onPauseMovie(episode);
    }


    private void ON_RESUME(DownloadEpisode episode) {
        downloadListener.onResumeMovie(episode);
    }

}
