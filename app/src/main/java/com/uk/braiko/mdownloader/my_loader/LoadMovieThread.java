package com.uk.braiko.mdownloader.my_loader;

import android.content.Context;

import com.activeandroid.query.Delete;
import com.uk.braiko.mdownloader.DownloadEpisode;
import com.uk.braiko.mdownloader.NetUtils;
import com.uk.braiko.mdownloader.my_loader.logger.L;
import com.uk.braiko.mdownloader.my_loader.logger.logTag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private IOnCompliteTaskListener listener;
    private DownloaderService.Task task;
    private IMovieDownloadListener onStateChange;
    private volatile boolean isBreakDownloading = false;
    private volatile boolean isSleep = false;
    private Context context;

    public LoadMovieThread(Context context) {
        this.context = context;
        this.start();
    }

    public boolean isFree() {
        return isFree;
    }

    public void doTask(DownloaderService.Task t) {
        task = t;
        isFree = false;

        boolean isBreakDownloading = false;
        boolean isSleep = false;
    }

    public void setOnCompliteTask(IOnCompliteTaskListener listener) {
        this.listener = listener;
    }

    public DownloaderService.Task getTask() {
        return task;
    }

    public void setOnStateChange(IMovieDownloadListener onStateChange) {
        this.onStateChange = onStateChange;
    }

    public void BreakDownloaded() {
        isBreakDownloading = true;
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
                }
                continue;
            }
            ProccessTask();
        }
    }

    private void ProccessTask() {
        downloadEpisode(task.episode);
    }

    private void downloadEpisode(DownloadEpisode episode) {
        L.info("start download epizode", logTag.dowloader_sevice);
        long downloadPosition;
        int percent;
        try {
            ON_START(episode);
            episode.setIs_downloading(1);
            episode.save();

            String fileName = episode.getFull_path();

            if (fileName.equals("")) {
                fileName = getMovieFullName(context, episode, true);
                episode.setFull_path(fileName);
                episode.save();
            }

            File downloadingFile = new File(episode.getFull_path());

            if (downloadingFile.exists()) {
                downloadPosition = downloadingFile.length();
            } else {
                downloadPosition = 0;
                downloadingFile.createNewFile();
            }

            //	System.out.println(episode.getLink());

            URL url = new URL(episode.getLink());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Range", "bytes=" + downloadPosition + "-");

            connection.setDoInput(true);
            // connection.setDoOutput(true);
            InputStream in = null;
            try {
                in = new BufferedInputStream(connection.getInputStream());
            } catch (FileNotFoundException e) {
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

            FileOutputStream fos = new FileOutputStream(downloadingFile, true);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 8192);

            byte[] data = new byte[8192];
            int x = 0;
            while ((x = in.read(data, 0, 8192)) >= 0) {
                bout.write(data, 0, x);
                downloadPosition += x;
                episode.setProgress(downloadPosition);

                percent = (int) (((double) downloadPosition / (double) episode.getFile_size()) * 100);

                episode.setPercent(percent);

                if (episode.isNeed_delete()) {
                    try {
                        File file = new File(episode.getFull_path());
                        file.delete();
                    } catch (Exception e) {
                    }
                    new Delete().from(DownloadEpisode.class).where("episode_id=" + episode.getEpisode_id()).executeSingle();
                    return;
                }
                if (isBreakDownloading) {
                    bout.flush();
                    bout.close();

                    episode.setProgress(downloadPosition);
                    episode.setPercent(percent);
                    episode.save();
                    ON_PAUSE(episode);
                    return;
                }

                if (isSleep) {
                    // new Update(DownloadEpisode.class).set("is_downloading=0").where("episode_id=" + episode.getEpisode_id()).execute();
                    episode.setFile_size(movieLength + downloadPosition);
                    episode.save();
                    stopDo();
                }

                ON_PROGRESS(episode);
            }
            bout.close();

            episode.setPercent(100);
            episode.setProgress(movieLength);

            episode.setIs_downloading(0);
            renameMovie(context, episode).save();
            ON_FINISH_EPISODE(episode);
        } catch (Exception e) {
            e.printStackTrace();
            if (!NetUtils.isNetworkAvailable(context)) {

                try {
                    DownloadEpisode temp = new DownloadEpisode();
                    temp.setEpisode_id(13275832);
                    ON_PAUSE(temp);

                    stopDo();
                } catch (Exception ex) {

                }
            }
        }
    }

    private void stopDo() {
        isFree = true;
        isBreakDownloading = false;
        isSleep = false;
        listener.onComplite();
    }

    private void ON_PAUSE(DownloadEpisode episode) {

    }

    private void ON_FINISH_EPISODE(DownloadEpisode episode) {

    }

    private void ON_PROGRESS(DownloadEpisode episode) {

    }

    private void ON_START(DownloadEpisode episode) {

    }
}
