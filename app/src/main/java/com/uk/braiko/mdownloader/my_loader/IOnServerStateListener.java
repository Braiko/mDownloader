package com.uk.braiko.mdownloader.my_loader;

import com.uk.braiko.mdownloader.DownloadEpisode;

import java.util.ArrayList;

public interface IOnServerStateListener {
    public void onnewstateget(ArrayList<DownloadEpisode> episodes);
    public void onFinishAll();
}
