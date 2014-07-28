package com.uk.braiko.mdownloader.my_loader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;

/**
 * Created by yura on 28.07.14.
 */
public class DownloaderService extends Service implements IMovieDownloadListener {
    private static final int POOL_SIZE = 12;

    private LinkedBlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();
    private LinkedBlockingQueue<LoadMovieThread> pool;
    private HashMap<Task,LoadMovieThread> threadFromPool_by_task = new HashMap<Task, LoadMovieThread>();

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

    public void FillPool(){
        pool = new LinkedBlockingQueue<LoadMovieThread>();
        for(int i = 0;i<POOL_SIZE;i++) {
            final LoadMovieThread workThread = new LoadMovieThread(getApplicationContext());
            workThread.setOnStateChange(this);
            workThread.setOnCompliteTask(new IOnCompliteTaskListener(){
                @Override
                public void onComplite() {
                    try {
                        threadFromPool_by_task.remove(workThread.getTask());
                        pool.put(workThread);
                        if(pool.size() == POOL_SIZE)
                            DownloaderService.this.onFinishAll();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        L.error("can`t put thread in pool", e, logTag.thread, logTag.dowloader_sevice);
                    }
                }
            });
            try {
                pool.put(workThread);
            } catch (InterruptedException e) {
                e.printStackTrace();
                L.error("can`t put thread in pool", e, logTag.thread, logTag.dowloader_sevice);
            }
        }
    }

    public void StartPoolLoop(){
        (new Thread(){
            @Override
            public void run() {
                super.run();
                while (true)
                {
                    try {
                        Task t = tasks.take();
                        ExecuteTask(t);
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


    private void ExecuteTask(Task task) {
        if(task.status == Constants.CMD_START_DOWNLOAD || task.status == Constants.CMD_DELETE_MOVIE)
        {
            L.info("start execute new task",logTag.dowloader_sevice);
            LoadMovieThread thread;
            try {
                thread = pool.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                L.error("can`t get thread from pool", e, logTag.thread, logTag.dowloader_sevice);
                //todo додати в статус щось?
                return;
            }
            thread.doTask(task);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.info("get command", logTag.dowloader_sevice);
        try {
            if (intent == null)
                throw new Exception("getting intent empty");
            switch (intent.getIntExtra(Constants.ACTION_ID, -1)) {
                case Constants.CMD_START_DOWNLOAD: {
                    L.info("service parse command as Constants.CMD_START_DOWNLOAD", logTag.dowloader_sevice);
                    DownloadEpisode episode = IntentUtils.getDownloadItem(intent);
                    Task t = new Task();
                    t.episode = episode;
                    t.status = Constants.CMD_START_DOWNLOAD;
                    tasks.put(t);
                    break;
                }
                case Constants.CMD_DELETE_MOVIE: {
                    DownloadEpisode episode = IntentUtils.getDownloadItem(intent);
                    break;
                }
                case Constants.CMD_PAUSE_MOVIE: {
                    DownloadEpisode episode = IntentUtils.getDownloadItem(intent);
                    break;
                }
                case Constants.CMD_GET_STATUS: {


                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.error("some error when service get task",e, logTag.dowloader_sevice);
        }
        return super.onStartCommand(intent, flags, startId);
    }



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




    //###############################################################################################################################################################################
    // static utils
    //###############################################################################################################################################################################


    public static String getMovieFullName(Context _context, DownloadEpisode _episode, boolean _isTemp) {
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
    public static DownloadEpisode renameMovie(Context context,DownloadEpisode _episode) {
        File file = new File(_episode.getFull_path());
        File newFile = new File(getMovieFullName(context, _episode, false));
        file.renameTo(newFile);
        _episode.setFull_path(newFile.getAbsolutePath());
        return _episode;
    }
    //###############################################################################################################################################################################
    // life circle callback
    //###############################################################################################################################################################################

    @Override
    public IBinder onBind(Intent intent) {
        L.info("download service was bimd",logTag.debug_and_test,logTag.dowloader_sevice);
        return null;
    }

    //###############################################################################################################################################################################
    // addition data type
    //###############################################################################################################################################################################

    protected class Task{
        int status;
        DownloadEpisode episode;
    }
}
