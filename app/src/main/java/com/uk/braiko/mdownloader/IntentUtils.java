package com.uk.braiko.mdownloader;

import android.content.Intent;

import com.activeandroid.query.Select;


public class IntentUtils {
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

    public static Intent getIntentItem(String _action, DownloadEpisode _episode) {
        Intent intent = new Intent(_action);
        if (_episode != null)
            intent.putExtra(Constants.ARG_DOWNLOAD_ITEM, _episode);
        return intent;
    }
}
