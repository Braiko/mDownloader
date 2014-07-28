package com.uk.braiko.mdownloader.downloader;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.uk.braiko.mdownloader.DownloadEpisode;
import com.uk.braiko.mdownloader.NetUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TrackLoader extends Thread {

    private volatile DownloadEpisode mDownloadingEpisode;
    public TrackLoader(DownloadEpisode mDownloadingEpisode){

    }
    public void run() {
        try {
            while (true) {
                if (mDownloadingEpisode == null)
                    mDownloadingEpisode = new Select().from(DownloadEpisode.class).where("percent!=100").executeSingle();
                if (mDownloadingEpisode != null) {
                    if (mDownloadingEpisode.isNeed_pause() == 0) {
                        downloadEpisode(mDownloadingEpisode);
                    } else {
                        synchronized (locker) {
                            locker.wait();
                        }
                    }
                } else {
                    synchronized (locker) {
                        locker.wait();
                    }
                }
            }
        } catch (Exception e) {
        }
    }


    private void downloadEpisode(DownloadEpisode _episode) {

        long downloadPosition = 0;
        int percent = 0;

        try {
            ON_START(_episode);

            isBreakDownloading = false;
            isSleep = false;

            _episode.setIs_downloading(1);
            _episode.save();

            String fileName = _episode.getFull_path();

            if (fileName.equals("")) {
                fileName = getMovieFullName(getApplicationContext(), _episode, true);
                _episode.setFull_path(fileName);
                _episode.save();
            }

            File downloadingFile = new File(_episode.getFull_path());

            if (downloadingFile.exists()) {
                downloadPosition = downloadingFile.length();
            } else {
                downloadPosition = 0;
                downloadingFile.createNewFile();
            }

            //	System.out.println(_episode.getLink());

            URL url = new URL(_episode.getLink());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Range", "bytes=" + downloadPosition + "-");

            connection.setDoInput(true);
            // connection.setDoOutput(true);
            InputStream in = null;
            try {
                in = new BufferedInputStream(connection.getInputStream());
            } catch (FileNotFoundException e) {
                refreshLink(_episode);

                url = new URL(_episode.getLink());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Range", "bytes=" + downloadPosition + "-");
                connection.setDoInput(true);
                in = new BufferedInputStream(connection.getInputStream());
            }

            long movieLength = connection.getContentLength();
            _episode.setFile_size(movieLength + downloadPosition);
            _episode.save();

            FileOutputStream fos = new FileOutputStream(downloadingFile, true);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 8192);

            byte[] data = new byte[8192];
            int x = 0;
            while ((x = in.read(data, 0, 8192)) >= 0) {
                bout.write(data, 0, x);
                downloadPosition += x;
                _episode.setProgress(downloadPosition);

                percent = (int) (((double) downloadPosition / (double) _episode.getFile_size()) * 100);

                _episode.setPercent(percent);

                if (_episode.isNeed_delete()) {
                    try {
                        File file = new File(_episode.getFull_path());
                        file.delete();
                    } catch (Exception e) {
                    }
                    new Delete().from(DownloadEpisode.class).where("episode_id=" + _episode.getEpisode_id()).executeSingle();
                    mDownloadingEpisode = null;
                    return;
                }
                if (isBreakDownloading) {
                    bout.flush();
                    bout.close();

                    _episode.setProgress(downloadPosition);
                    _episode.setPercent(percent);
                    _episode.save();
                    ON_PAUSE(_episode);
                    return;
                }

                if (isSleep) {
                    // new Update(DownloadEpisode.class).set("is_downloading=0").where("episode_id=" + _episode.getEpisode_id()).execute();
                    _episode.setFile_size(movieLength + downloadPosition);
                    _episode.save();

                    synchronized (locker) {
                        locker.wait();
                        return;
                    }
                }

                ON_PROGRESS(_episode);
            }
            bout.close();

            _episode.setPercent(100);
            _episode.setProgress(movieLength);

            _episode.setIs_downloading(0);
            renameMovie(_episode).save();

            mDownloadingEpisode = null;

            ON_FINISH_EPISODE(_episode);
        } catch (Exception e) {
            e.printStackTrace();
            if (!NetUtils.isNetworkAvailable(getApplicationContext())) {

                try {

                    mDownloadingEpisode = null;
                    DownloadEpisode temp = new DownloadEpisode();
                    temp.setEpisode_id(13275832);
                    ON_PAUSE(temp);

                    synchronized (locker) {
                        locker.wait();
                        return;
                    }
                } catch (Exception ex) {

                }
            }
        }
    }
}