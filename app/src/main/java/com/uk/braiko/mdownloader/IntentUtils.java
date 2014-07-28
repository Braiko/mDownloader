package com.uk.braiko.mdownloader;

import android.content.Intent;


public class IntentUtils {
	public static DownloadEpisode getDownloadItem(Intent _intent) {
		return _intent.getParcelableExtra(Constants.ARG_DOWNLOAD_ITEM);
	}

	public static Intent getIntentItem(String _action, DownloadEpisode _episode) {
		Intent intent = new Intent(_action);
		if (_episode != null)
			intent.putExtra(Constants.ARG_DOWNLOAD_ITEM, _episode);
		return intent;
	}
}
