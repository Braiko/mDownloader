package com.uk.braiko.mdownloader.my_loader;

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
import com.uk.braiko.mdownloader.ObjParser;
import com.uk.braiko.mdownloader.my_loader.logger.L;
import com.uk.braiko.mdownloader.my_loader.logger.logTag;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;

/**
 * Created by yura on 28.07.14.
 */
public class DownloaderService extends Service implements IMovieDownloadListener {
    private static final int POOL_SIZE = 2;

    private LinkedBlockingQueue<Long> tasks = new LinkedBlockingQueue<Long>();
    private HashMap<Long, Task> task_by_episodeID = new HashMap<Long, Task>();
    private LinkedBlockingQueue<LoadMovieThread> pool;
    private HashMap<Long, LoadMovieThread> threadFromPool_by_EpizodeID = new HashMap<Long, LoadMovieThread>();

    @Override
    public void onCreate() {
        InitPool();
        super.onCreate();
    }

    //#################################################################################################################################################
    // pool
    //#################################################################################################################################################

    private void InitPool() {
        FillPool();
        StartPoolLoop();
    }

    public void FillPool() {
        pool = new LinkedBlockingQueue<LoadMovieThread>();
        for (int i = 0; i < POOL_SIZE; i++) {
            LoadMovieThread workThread = new LoadMovieThread(getApplicationContext(), this);
            InitWorkThread(workThread);
            try {
                pool.put(workThread);
            } catch (InterruptedException e) {
                e.printStackTrace();
                L.error("can`t put thread in pool", e, logTag.thread, logTag.dowloader_sevice);
            }
        }
    }

    private void InitWorkThread(final LoadMovieThread workThread) {
        workThread.setOnCompliteTask(new IOnCompliteTaskListener() {
            @Override
            public void onComplite() {
                doSynhronizedWork(new WorkWraper() {
                    @Override
                    public void doJob() {
                        try {
                            threadFromPool_by_EpizodeID.remove(workThread.getTask().episode.getEpisode_id());
                            pool.put(workThread);
                            if (pool.size() + 1 >= POOL_SIZE)
                                DownloaderService.this.onFinishAll();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            L.error("can`t put thread in pool", e, logTag.thread, logTag.dowloader_sevice);
                        }
                    }
                });

            }
        });
    }

    public void StartPoolLoop() {
        (new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        ExecuteTaskForThread(pool.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        L.error("can`t get task from queue", e, logTag.thread, logTag.dowloader_sevice);
                    }
                }
            }
        }).start();
    }

    //#################################################################################################################################################
    // task
    //#################################################################################################################################################


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.info("get command", logTag.dowloader_sevice);
        try {
            if (intent == null)
                throw new Exception("getting intent empty");
            DownloadEpisode episode = getDownloadItem(intent);
            switch (intent.getIntExtra(Constants.ACTION_ID, -1)) {
                case Constants.CMD_START_DOWNLOAD: {
                    L.info("service parse command as Constants.CMD_START_DOWNLOAD", logTag.dowloader_sevice);
                    StartMove(episode);
                    break;
                }
                case Constants.CMD_DELETE_MOVIE: {
                    DeleteMove(episode);
                    break;
                }
                case Constants.CMD_PAUSE_MOVIE: {
                    PauseMove(episode);
                    break;
                }
                case Constants.CMD_GET_STATUS: {
                    getStatus(episode);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.error("some error when service get task", e, logTag.dowloader_sevice);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void PauseMove(final DownloadEpisode episode) {
        doSynhronizedWork(new WorkWraper() {
            @Override
            public void doJob() {
                if (task_by_episodeID.containsKey(episode.getEpisode_id()))
                    task_by_episodeID.get(episode.getEpisode_id()).episode.save();
                if (threadFromPool_by_EpizodeID.containsKey(episode.getEpisode_id())) {
                    threadFromPool_by_EpizodeID.get(episode.getEpisode_id()).BreakDownloaded();
                    return;
                }
                if (tasks.contains(episode.getEpisode_id())) {
                    tasks.remove(episode.getEpisode_id());
                }
            }
        });
    }

    private void DeleteMove(final DownloadEpisode episode) {
        doSynhronizedWork(new WorkWraper() {
            @Override
            public void doJob() {
                if (threadFromPool_by_EpizodeID.containsKey(episode.getEpisode_id())) {
                    threadFromPool_by_EpizodeID.get(episode.getEpisode_id()).BreakDownloaded();
                    return;
                }
                if (tasks.contains(episode.getEpisode_id())) {
                    tasks.remove(episode.getEpisode_id());
                }
                removeFile(episode);
                onFinishMovie(episode);
            }
        });
    }

    private void StartMove(final DownloadEpisode episode) {
        doSynhronizedWork(new WorkWraper() {
            @Override
            public void doJob() {
                if (threadFromPool_by_EpizodeID.containsKey(episode.getEpisode_id())) return;
                if (tasks.contains(episode.getEpisode_id())) {
                    task_by_episodeID.get(episode.getEpisode_id()).status = Constants.CMD_START_DOWNLOAD;
                    return;
                }
                Task t = new Task();
                t.episode = episode;
                t.status = Constants.CMD_START_DOWNLOAD;
                task_by_episodeID.put(episode.getEpisode_id(), t);
                try {
                    tasks.put(episode.getEpisode_id());
                } catch (InterruptedException e) {
                    L.error("can`t put task to the queue", e, logTag.dowloader_sevice);
                    e.printStackTrace();
                }
            }
        });
    }

    private void getStatus(final DownloadEpisode episode) {
        doSynhronizedWork(new WorkWraper() {
            @Override
            public void doJob() {
                if (task_by_episodeID.containsKey(episode.getEpisode_id()))
                    onStatus(task_by_episodeID.get(episode.getEpisode_id()).episode);
                onStatus(episode);
            }
        });
    }

    private void ExecuteTaskForThread(final LoadMovieThread thread) {
        final Task task;
        L.info("getting free thread from pool", logTag.dowloader_sevice);

        try {
            long id = tasks.take();
            task = task_by_episodeID.get(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
            L.error("can`t get new task, thar can be execute in pool thread", e, logTag.thread, logTag.dowloader_sevice);
            try {
                pool.put(thread);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                //fixme when we can`t return thread in pool we must destriy it and create new
            }
            return;
        }
        doSynhronizedWork(new WorkWraper() {
            @Override
            public void doJob() {
                if (task.status == Constants.CMD_START_DOWNLOAD
                        || task.status == Constants.CMD_RESUME_MOVIE) {

                    threadFromPool_by_EpizodeID.put(task.episode.getEpisode_id(), thread);
                    thread.doTask(task);
                }
            }
        });
    }

    //###############################################################################################################################################################################
    // synchronizer
    //###############################################################################################################################################################################


    private synchronized void doSynhronizedWork(WorkWraper wraper) {
        wraper.doJob();
    }

    //###############################################################################################################################################################################
    // broadcast sender
    //###############################################################################################################################################################################


    @Override
    public void onProgress(DownloadEpisode downloadItem) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_PROGRESS, downloadItem));
    }

    @Override
    public void onPauseMovie(DownloadEpisode downloadItem) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_PAUSE_DOWNLOADING, downloadItem));
    }

    @Override
    public void onResumeMovie(DownloadEpisode downloadItem) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_RESUME_DOWNLOADING, downloadItem));
    }

    @Override
    public void onFinishMovie(DownloadEpisode downloadItem) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_FINISH_MOVIE_DOWNLOADING, downloadItem));
    }

    @Override
    public void onStartMovie(DownloadEpisode downloadItem) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_START_MOVIE_DOWNLOADING, downloadItem));
    }

    @Override
    public void onFinishAll() {
        sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL_DOWNLOADING));
    }

    @Override
    public void onStatus(DownloadEpisode downloadItem) {
        sendBroadcast(IntentUtils.getIntentItem(Constants.ACTION_STATUS, downloadItem));

    }


    //###############################################################################################################################################################################
    // static utils
    //###############################################################################################################################################################################


    public static String getMovieFullName(Context _context, DownloadEpisode _episode, boolean _isTemp) {

        //fixme
//        File file;
//
//        try {
//            file = new File(Environment.getExternalStorageDirectory(), Constants.DOWNLOADS_FOLDER);
//        } catch (Exception e) {
//            file = _context.getExternalFilesDir(null);
//        }
//
//        if (!file.exists())
//            file.mkdirs();
//
//        String show_descr = _episode.getShows_info();
//        if (show_descr == null)
//            show_descr = "";
//
//        String fileName = (_episode.getTitle() + show_descr).replaceAll("\\W", "-");
//
//        fileName += ".mp4";
//
//        if (_isTemp)
//            fileName += ".temp";
//
//        return new File(file, fileName).getAbsolutePath();
        String full_path;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //handle case of no SDCARD present
            full_path = "";
        } else {
            String dir = Environment.getExternalStorageDirectory() + File.separator + "show_box";
            //create folder
            File folder = new File(dir); //folder name
            folder.mkdirs();

            //create file
            File file = new File(dir, UUID.randomUUID().toString() + ".jpg");
            full_path = file.getPath();
        }
        return full_path;
    }

    public static void refreshLink(DownloadEpisode _epizode) {
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

    public static DownloadEpisode renameMovie(Context context, DownloadEpisode episode) {
        File file = new File(episode.getFull_path());
        File newFile = new File(getMovieFullName(context, episode, false));
        file.renameTo(newFile);
        episode.setFull_path(newFile.getAbsolutePath());
        return episode;
    }


    protected static void removeFile(DownloadEpisode episode) {
        File file = new File(episode.getFull_path());
        if (file.exists()) file.delete();
        new Delete().from(DownloadEpisode.class).where("episode_id=" + episode.getEpisode_id()).executeSingle();

    }

    public static DownloadEpisode getDownloadItem(Intent _intent) {
        DownloadEpisode result = _intent.getParcelableExtra(Constants.ARG_DOWNLOAD_ITEM);
        DownloadEpisode cachedItem = new Select().from(DownloadEpisode.class).where("episode_id=" +
                result.getEpisode_id()).executeSingle();
        if (cachedItem != null)
            result = cachedItem;
        else
            result.save();
        return result;
    }

    //###############################################################################################################################################################################
    // life circle callback
    //###############################################################################################################################################################################

    @Override
    public IBinder onBind(Intent intent) {
        L.info("download service was bind", logTag.debug_and_test, logTag.dowloader_sevice);
        return null;
    }

    //###############################################################################################################################################################################
    // addition data type
    //###############################################################################################################################################################################

    protected class Task {
        volatile int status;
        DownloadEpisode episode;
    }

    private interface WorkWraper {
        public void doJob();
    }
}
