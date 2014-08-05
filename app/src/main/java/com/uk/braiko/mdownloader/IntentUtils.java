package com.uk.braiko.mdownloader;

import android.content.Intent;
import android.os.Parcelable;

import com.activeandroid.query.Select;

import java.util.ArrayList;


public class IntentUtils {
    public static DownloadEpisode getDownloadItem(Intent _intent) {
        DownloadEpisode result = _intent.getParcelableExtra(Constants.ARG_DOWNLOAD_ITEM);
        if(result==null)
            return result;
        return loadEpisodeFromCache(result);
    }

    public static DownloadEpisode loadEpisodeFromCache(DownloadEpisode episode){
        DownloadEpisode cachedItem = new Select().from(DownloadEpisode.class).where("episode_id=" +
                episode.getEpisode_id()).executeSingle();
        if (cachedItem != null)
            episode = cachedItem;
        else
            episode.save();
        return episode;
    }

    public static ArrayList<DownloadEpisode> getDownloadItems(Intent _intent) {
        ArrayList<DownloadEpisode> result = new ArrayList<DownloadEpisode>();
        for(int i=0;i<i+1;i++){
            DownloadEpisode cachedItem = _intent.getExtras().getParcelable(Constants.ARG_DOWNLOAD_ITEM + i);
            if(cachedItem == null)
                break;
            result.add(loadEpisodeFromCache(cachedItem));
        }
        return result;
    }

    public static Intent getIntentItem(String _action, DownloadEpisode _episode) {
        Intent intent = new Intent(_action);
        intent.putExtra(Constants.ARG_IS_DOWNLOAD_ITEM_LIST, false);
        if (_episode != null)
            intent.putExtra(Constants.ARG_DOWNLOAD_ITEM, _episode);
        return intent;
    }

    public static Intent getIntentItem(String _action, ArrayList<DownloadEpisode> _episode) {
        Intent intent = new Intent(_action);
        if (_episode == null)
            return intent;
        intent.putExtra(Constants.ARG_IS_DOWNLOAD_ITEM_LIST, true);
        for(int i=0;i<_episode.size();i++)
            intent.putExtra(Constants.ARG_DOWNLOAD_ITEM+i, _episode.get(i));
        return intent;
    }
}
