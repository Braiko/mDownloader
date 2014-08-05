package com.uk.braiko.mdownloader;

public class Constants {

    public final static String YOUTUBE_DEV_ID = "AIzaSyC-E_rG3bJbJrIIoz3IxlgftxHE5u9hTGc";

    public final static String FLURY_ID = "B2W7DH3D6XM7ZRXVCVXK";

    public final static String PREF_LANG = "PREF_LANG";

    public final static long USER_INFO_DIALOG_INTERVAL = 24 * 60 * 60 * 1000 * 1;

    public final static String PREF_VERSION = "PREF_VERSION";

    public final static String PREF_NEW_MOVIES = "PREF_NEW_MOVIEW";
    public final static String PREF_NEW_SHOWS = "PREF_NEW_SHOWS";

    public final static String PREF_SCROLL_SHOWED = "PREF_SCROLL_SHOWED";

    public final static String PREF_LIB_SUB_TAB = "PREF_LIB_SUB_TAB";

    public final static String PREF_CHECK_USER_INFO_TIME = "PREF_CHECK_USER_INFO_TIME";

    public final static String PREF_UPDATE_TIME = "PREF_UPDATE_TIME";
    public final static String PREFS_LOCATE = "PREFS_LOCATE";
    public final static String PREFS_FIRST_START = "PREFS_FIRST_START";
    public final static String PREFS_SELECTED_QUALITY = "PREFS_SELECTED_QUALITY";
    public final static String PREFS_NEED_UPDATE = "PREFS_NEED_UPDATE";

    public final static String PREFS_POST_FIRST_UPDATE = "PREFS_POST_FIRST_UPDATE";

    public final static String PREFS_PLAYER_MODE = "PREFS_PLAYER_MODE";

    public final static String PREFS_SELECTED_NEWS = "PREFS_SELECTED_NEWS";

    public final static String PREFS_UPDATE_PROGRESS = "PREFS_UPDATE_PROGRESS";
    public final static String PREFS_AD_LAST_TIME = "PREFS_AD_LAST_TIME";
    public final static int AD_INTERVAL = 24 * 60 * 60 * 1000 * 1;

    public final static int AD_DIALOG_INTERVAL = 24 * 60 * 60 * 1000 * 2;
    public final static String PREFS_AD_DIALOG_LAST_TIME = "PREFS_AD_DIALOG_LAST_TIME";

    public final static String ARG_ID = "ARG_ID";

    public final static String ARG_NEED_REMOVE = "ARG_NEED_REMOVE";

    // play types
    public final static int PLAY_SHOWS = 1;
    public final static int PLAY_MOVIE = 2;
    public final static int PLAY_DOWNLOADED = 3;

    public final static String ARG_BUNDLE = "ARG_BUNDLE";
    public final static String ARG_TYPE_PLAY = "ARG_TYPE_PLAY";
    public final static String ARG_TITLE = "ARG_TITLE";
    public final static String ARG_LANG = "ARG_LANG";
    public final static String ARG_QUALITY = "ARG_QUALITY";
    public final static String ARG_SEASON_NUM = "ARG_SEASON_NUM";
    public final static String ARG_MOVE_ID = "ARG_MOVE_ID";
    public final static String ARG_EPIZOD_ID = "ARG_EPIZOD_ID";
    public final static String ARG_POSITION = "ARG_POSITION";
    public final static String ARG_SEASON = "ARG_SEASON";
    public final static String ARG_VIDEO_URL = "VIDEO_URL";
    public final static String ARG_VIDEO_POSITION = "ARG_VIDEO_POSITION";
    public final static int ARG_CODE_OPEN_VIDEO = 123;

    public final static int ARG_CODE_OPEN_EXTERNAL_VIDEO = 1113;

    // api
    public final static String JSON_NEWS_NAME = "news_tv.json";
    public final static String JSON_TITLE_NAME = "tv_lite.json";

    public final static String JSON_MOVIES = "movies_lite.json";
    public final static String JSON_MOVIE_UPDATES = "news_movies.json";

    public final static String JSON_CATS_NAME = "cats.json";

    public final static int VIDEO_VIEW_LIMIT = 50;
    public final static String PREFS_VIDEO_VIEWED = "VIDEO_VIEWED";
    public final static String PREFS_VIDEO_DAY = "PREFS_VIDEO_DAY";

    public final static int APP_UPDATE_DELAY = 2000;
    public final static int ZIP_UPDATE_DELAY = 5000;

    public final static int TAB_SHOWS = 12;
    public final static int TAB_NEWS = 234;
    public final static int TAB_LIBRARY = 231;
    public final static int TAB_MOVIE = 2311;

    // movie loader const
    public final static int CMD_START_DOWNLOAD = 23;
    public final static int CMD_DELETE_MOVIE = 26;
    public final static int CMD_PAUSE_MOVIE = 29;
    public final static int CMD_RESUME_MOVIE = 32;
    public final static int CMD_GET_STATUS = 36;
    public final static int CMD_GET_COMMON_STATUS = 39;
    public static final int CMD_SAVE = 42;
    public final static String ACTION_ID = "ACTION_ID";

    // receivers arg
    public static final String PACKEGE_NAME = "com.tdo.showbox.";

    public static final String ACTION_START_MOVIE_DOWNLOADING = PACKEGE_NAME + "ACTION_START_MOVIE_DOWNLOADING";
    public static final String ACTION_STATUS = PACKEGE_NAME + "ACTION_STATUS";
    public static final String ACTION_All_STATUS = PACKEGE_NAME + "ACTION_ALL_STATUS";
    public static final String ACTION_FINISH_MOVIE_DOWNLOADING = PACKEGE_NAME + "ACTION_FINISH_MOVIE_DOWNLOADING";
    public static final String ACTION_PROGRESS = PACKEGE_NAME + "ACTION_PROGRESS";
    public static final String ACTION_FINISH_ALL_DOWNLOADING = PACKEGE_NAME + "ACTION_FINISH_ALL_DOWNLOADING";
    public static final String ACTION_PAUSE_DOWNLOADING = PACKEGE_NAME + "ACTION_PAUSE_DOWNLOADING";
    public static final String ACTION_RESUME_DOWNLOADING = PACKEGE_NAME + "ACTION_RESUME_DOWNLOADING";

    public static final String ARG_DOWNLOAD_ITEM = "ARG_DOWNLOAD_ITEM";
    public static final String ARG_IS_DOWNLOAD_ITEM_LIST = "ARG_IS_DOWNLOAD_ITEM_LIST";

    public static final String DOWNLOADS_FOLDER = "show_box";

    //load episode state
    public final static int STS_WHATE_IN_QUEUE = 1;
    public final static int STS_LOADED = 2;
    public final static int STS_ON_PAUSE = 3;

}
