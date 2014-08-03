package com.uk.braiko.mdownloader;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.File;
import java.util.UUID;

@Table(name = "DownloadEpisodes")
public class DownloadEpisode extends Model implements Parcelable {
    @Column(name = "poster")
    private String poster;

    @Column(name = "title")
    private String title;

    @Column(name = "episode_id")
    private long episode_id;

    @Column(name = "link")
    private String link;

    @Column(name = "progress")
    private long progress;

    @Column(name = "size")
    private long size;

    @Column(name = "percent")
    private int percent;

    @Column(name = "leng")
    private String leng;

    @Column(name = "full_path")
    private String full_path;

    @Column(name = "is_downloading")
    private int is_downloading;

    private boolean need_delete;

    @Column(name = "need_pause")
    private int need_pause;

    @Column(name = "view_position")
    private long view_position;

    @Column(name = "file_size")
    private long file_size;

    @Column(name = "shows_info")
    private String shows_info;

    @Column(name = "quality")
    private int quality;

    @Column(name = "vk_link")
    private String vk_link;

    @Column(name = "is_movie")
    private int is_movie;

    @Column(name = "season_num")
    private int season_num;

    @Column(name = "episode_num")
    private int episode_num;

    public DownloadEpisode() {
        super();
        this.poster = "";
        this.title = "";
        this.episode_id = 0;
        this.link = "";
        this.progress = 0;
        this.size = 0;
        this.percent = 0;
        this.leng = "en";
        this.full_path = "";
        this.is_downloading = 0;
        need_pause = 0;
        need_delete = false;
        view_position = 0;
        file_size = 0;
        quality = 0;
        vk_link = "";
        is_movie = 0;
        season_num = 0;
        episode_num = 0;
    }

    public int getEpisode_num() {
        return episode_num;
    }

    public void setEpisode_num(int episode_num) {
        this.episode_num = episode_num;
    }

    public int getSeason_num() {
        return season_num;
    }

    public void setSeason_num(int season_num) {
        this.season_num = season_num;
    }

    public String getVk_link() {
        return vk_link;
    }

    public void setVk_link(String vk_link) {
        this.vk_link = vk_link;
    }

    public int getIs_movie() {
        return is_movie;
    }

    public void setIs_movie(int is_movie) {
        this.is_movie = is_movie;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getShows_info() {
        return shows_info;
    }

    public void setShows_info(String shows_info) {
        this.shows_info = shows_info;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public long getView_position() {
        return view_position;
    }

    public void setView_position(long view_position) {
        this.view_position = view_position;
    }

    public boolean isNeed_delete() {
        return need_delete;
    }

    public void setNeed_delete(boolean need_delete) {
        this.need_delete = need_delete;
    }

    public int isNeed_pause() {
        return need_pause;
    }

    public void setNeed_pause(int need_pause) {
        this.need_pause = need_pause;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getEpisode_id() {
        return episode_id;
    }

    public void setEpisode_id(long episode_id) {
        this.episode_id = episode_id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getLeng() {
        return leng;
    }

    public void setLeng(String leng) {
        this.leng = leng;
    }

    public String getFull_path() {

        //fixme it for test

        if (this.full_path.equals("")) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //handle case of no SDCARD present
                full_path = "";
            } else {
                String dir = Environment.getExternalStorageDirectory() + File.separator + "show_box";
                //create folder
                File folder = new File(dir); //folder name
                folder.mkdirs();

                //create file
                File file = new File(dir, UUID.randomUUID().toString() + ".jpg.temp");
                full_path = file.getPath();
            }
            this.save();
        }
        return full_path;

    }

    public void setFull_path(String full_path) {
        this.full_path = full_path;
    }

    public int isIs_downloading() {
        return is_downloading;
    }

    public void setIs_downloading(int is_downloading) {
        this.is_downloading = is_downloading;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(full_path);
        dest.writeString(leng);
        dest.writeString(link);
        dest.writeString(poster);
        dest.writeString(title);
        dest.writeLong(episode_id);
        dest.writeLong(progress);
        dest.writeLong(size);
        dest.writeInt(percent);
        dest.writeInt(is_downloading);
        dest.writeInt(need_pause);
        dest.writeLong(file_size);
        if (shows_info == null) {
            shows_info = "";
        }
        dest.writeString(shows_info);
        dest.writeInt(quality);
        dest.writeString(vk_link);
        dest.writeInt(is_movie);
        dest.writeInt(season_num);
        dest.writeInt(episode_num);
    }

    private DownloadEpisode(Parcel _parsel) {
        super();
        full_path = _parsel.readString();
        leng = _parsel.readString();
        link = _parsel.readString();
        poster = _parsel.readString();
        title = _parsel.readString();
        episode_id = _parsel.readLong();
        progress = _parsel.readLong();
        size = _parsel.readLong();
        percent = _parsel.readInt();
        is_downloading = _parsel.readInt();
        need_pause = _parsel.readInt();
        file_size = _parsel.readLong();
        shows_info = _parsel.readString();
        quality = _parsel.readInt();
        vk_link = _parsel.readString();
        is_movie = _parsel.readInt();
        season_num = _parsel.readInt();
        episode_num = _parsel.readInt();
    }

    public static final Creator<DownloadEpisode> CREATOR = new Creator<DownloadEpisode>() {

        @Override
        public DownloadEpisode createFromParcel(Parcel source) {
            return new DownloadEpisode(source);
        }

        @Override
        public DownloadEpisode[] newArray(int size) {
            return new DownloadEpisode[size];
        }
    };
}
