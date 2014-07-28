package com.uk.braiko.mdownloader.downloader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.uk.braiko.mdownloader.Constants;
import com.uk.braiko.mdownloader.DownloadEpisode;
import com.uk.braiko.mdownloader.IntentUtils;
import com.uk.braiko.mdownloader.IoUtils;
import com.uk.braiko.mdownloader.NetUtils;
import com.uk.braiko.mdownloader.ObjParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

public class DownloaderService extends Service {

    private TrackLoader mLoaderThread;
    private volatile DownloadEpisode mDownloadingEpisode;
    private Object locker;

    public volatile boolean isBreakDownloading;
    public volatile boolean isSleep;

    @Override
    public void onCreate() {
        super.onCreate();
        // init
        mLoaderThread = new TrackLoader();
        locker = new Object();
        isBreakDownloading = false;
        isSleep = false;
    }

    public DownloaderService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent != null)
                throw new Exception("getting intent empty");
            switch (intent.getIntExtra(Constants.ACTION_ID, -1)) {
                case Constants.CMD_START_DOWNLOAD: {

                    DownloadEpisode episode = IntentUtils.getDownloadItem(intent);
                    startDownload(episode);
                    break;
                }
                case Constants.CMD_DELETE_MOVIE: {
                    DownloadEpisode episode = IntentUtils.getDownloadItem(intent);
                    deleteMovie(episode);
                    break;
                }
                case Constants.CMD_PAUSE_MOVIE: {
                    DownloadEpisode episode = IntentUtils.getDownloadItem(intent);
                    pauseMovie(episode);
                    break;
                }
                case Constants.CMD_GET_STATUS: {
                    if (mDownloadingEpisode != null) {
                        ON_STATUS(mDownloadingEpisode);
                    }

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //todo log??
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setSleep() {
        isSleep = true;
    }

    private void breakDoqnloading() {
        isBreakDownloading = true;
    }

    private void pauseMovie(DownloadEpisode _episode) {
        if (_episode != null) {
        }
    }

    private void deleteMovie(DownloadEpisode _episode) {
        if (_episode != null) {
            new Delete().from(DownloadEpisode.class).where("episode_id=" + _episode.getEpisode_id()).executeSingle();
            try {
                if (mDownloadingEpisode.getEpisode_id() == _episode.getEpisode_id()) {
                    _episode.setNeed_delete(true);
                    mDownloadingEpisode.setNeed_delete(true);
                } else {
                    try {
                        File file = new File(_episode.getFull_path());
                        file.delete();
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }

            synchronized (locker) {
                try {
                    locker.notifyAll();
                } catch (Exception ex) {
                }
            }
        }
    }

    private void startDownload(DownloadEpisode _episode) {
        if (_episode != null) {
            isSleep = false;
            if (new Select().from(DownloadEpisode.class).where("episode_id=" + _episode.getEpisode_id()).executeSingle() == null) {
                _episode.setPercent(0);
                _episode.setProgress(0);
                _episode.setNeed_pause(0);
                _episode.save();
            }

            try {
                if (_episode.getEpisode_id() == mDownloadingEpisode.getEpisode_id()) {
                    setSleep();
                    mDownloadingEpisode = null;
                    DownloadEpisode temp = new DownloadEpisode();
                    temp.setEpisode_id(13275832);
                    ON_PAUSE(temp);
                    return;
                }
            } catch (Exception e) {
            }

            breakDoqnloading();

            // _episode.setIs_downloading(0);
            mDownloadingEpisode = new Select().from(DownloadEpisode.class).where("episode_id=" + _episode.getEpisode_id()).executeSingle();

            try {
                if (mLoaderThread.getState() == Thread.State.NEW)
                    mLoaderThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (locker) {
                try {
                    locker.notifyAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    private void refreshLink(DownloadEpisode _epizode) {
        try {
            URL url = new URL(_epizode.getVk_link());
            URLConnection hc = url.openConnection();
            hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            GZIPInputStream is = (GZIPInputStream) hc.getContent();
            final String response = IoUtils.gzipToString(is);

            ObjParser.refreshLink(_epizode, response, _epizode.getQuality());
            _epizode.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private DownloadEpisode renameMovie(DownloadEpisode _episode) {
        File file = new File(_episode.getFull_path());
        File newFile = new File(getMovieFullName(getApplicationContext(), _episode, false));
        file.renameTo(newFile);
        _episode.setFull_path(newFile.getAbsolutePath());
        return _episode;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void ON_STATUS(DownloadEpisode _episode) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_STATUS, _episode));
    }

    private void ON_START(DownloadEpisode _episode) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_START_MOVIE_DOWNLOADING, _episode));
    }

    private void ON_FINISH_EPISODE(DownloadEpisode _episode) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_FINISH_MOVIE_DOWNLOADING, _episode));
    }

    private void ON_PROGRESS(DownloadEpisode _episode) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_PROGRESS, _episode));
    }

    private void ON_FINISH_ALL() {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_FINISH_ALL_DOWNLOADING, null));
    }

    private void ON_PAUSE(DownloadEpisode _episode) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_PAUSE_DOWNLOADING, _episode));
    }

    private void ON_RESUME(DownloadEpisode _episode) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_RESUME_DOWNLOADING, _episode));
    }

    private String getMovieFullName(Context _context, DownloadEpisode _episode, boolean _isTemp) {
        File file;

        try {
            file = new File(Environment.getExternalStorageDirectory(), Constants.DOWNLOADS_FOLDER);
        } catch (Exception e) {
            file = _context.getExternalFilesDir(null);
        }

        if (!file.exists())
            file.mkdirs();

        String show_descr = _episode.getShows_info();
        if (show_descr == null)
            show_descr = "";

        String fileName = (_episode.getTitle() + show_descr).replaceAll("\\W", "-");

        fileName += ".mp4";

        if (_isTemp)
            fileName += ".temp";

        return new File(file, fileName).getAbsolutePath();
    }
}
