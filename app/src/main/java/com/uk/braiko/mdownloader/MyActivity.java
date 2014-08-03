package com.uk.braiko.mdownloader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.uk.braiko.mdownloader.my_loader.MovieDownloaderManager;
import com.uk.braiko.mdownloader.my_loader.logger.L;
import com.uk.braiko.mdownloader.my_loader.logger.logTag;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


public class MyActivity extends Activity {

    private CardListView list;
    private CardArrayAdapter listAdapter;
    ArrayList<String> moves = new ArrayList<String>();
    int lastShown = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        InitList();
        InistallAddBtn();
        InitMoves();
    }

    private void InitMoves() {
        moves.add("http://st.gdefon.com/wallpapers_original/wallpapers/399273_priroda_leto_derevya_trava_zelen_2048x1365_(www.GdeFon.ru).jpg");
        moves.add("http://img0.joyreactor.cc/pics/post/full/%D0%BA%D1%80%D0%B0%D1%81%D0%B8%D0%B2%D1%8B%D0%B5-%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8-%D0%9F%D1%80%D0%B8%D1%80%D0%BE%D0%B4%D0%B0-%D1%80%D0%B5%D0%BA%D0%B0-1105667.jpeg");
        moves.add("http://kapuchel.net/uploads/posts/2013-10/1381844088__20111023_1953965362.jpg");
        moves.add("http://polten.info/uploads/posts/2014-05/1401542486_elitefon.ru_1126.jpg");
        moves.add("http://www.salon.donetsk.ua/upload/iblock/d08/d080ed81476585fce2a9806a84bb3552.JPG");
        moves.add("http://mykrai.files.wordpress.com/2012/04/dsc004201.jpg");
        moves.add("http://ic.pics.livejournal.com/ksu_sedelnikova/45692733/7195/original.jpg");
        moves.add("http://s1.goodfon.ru/image/461970-3000x1674.jpg");
        moves.add("http://www.hqoboi.com/img/nature/nature-panorama_021.jpg");
        moves.add("http://s4.goodfon.ru/image/244736-3888x2592.jpg");
        moves.add("http://uc.kr.ua/wp-content/uploads/2012/08/DSCN1620.jpg");
        moves.add("http://s5.goodfon.ru/image/178698-3000x1345.jpg");
        moves.add("http://dlm4.meta.ua/pic/0/21/187/1S0b6RuN3y.JPG?&srd=dlm2.meta.ua");
        moves.add("http://webcommunity.org.ua/files/2011/02/1-134.jpg");
        moves.add("http://www.nice-places.com/data/articles/gallery/362/3160.JPG");
        moves.add("http://ukraine-foto.org.ua/wp-content/uploads/2010/11/155.jpg");
        moves.add("http://upload.wikimedia.org/wikipedia/uk/9/90/Polissya_Natural_Reserve.JPG");
        moves.add("http://kudarom.com.ua/blog/wp-content/uploads/2013/06/step.jpg");
        moves.add("http://s3.goodfon.ru/image/271285-3504x2336.jpg");
        moves.add("http://upload.wikimedia.org/wikipedia/uk/4/4d/Grus_grus_in_PNR.JPG");
        moves.add("http://www.freelancerbay.com/files/users/VictoriaKart/portfolio/4958_gjgj.jpg");
        moves.add("http://s1.1zoom.ru/big0/66/331058-svetik.jpg");
        moves.add("http://sirko.org.ua/wp-content/uploads/2013/05/IMG_0269.jpg");
        moves.add("http://www.epochtimes.com.ua/upload/iblock/4ac/4ac1cbcfcf34f98ecbb895743e997c96.jpg");
        moves.add("http://exploreua.com/wp-content/uploads/2012/11/12165335.jpg");
        moves.add("http://upload.wikimedia.org/wikipedia/commons/9/97/Coral_reef_in_Ras_Muhammad_nature_park_(Iolanda_reef).jpg");
        moves.add("http://mw2.google.com/mw-panoramio/photos/medium/45888990.jpg");
        moves.add("http://ekolog.ck.ua/wp-content/uploads/2013/04/DSCF6324.jpg");
        moves.add("http://kolyan.net/uploads/posts/2013-04/1365545522_84-69.jpg");
        moves.add("http://img0.joyreactor.cc/pics/post/full/%D0%A3%D0%BA%D1%80%D0%B0%D0%B8%D0%BD%D0%B0-%D0%BE%D0%B7%D0%B5%D1%80%D0%BE-%D0%B2%D0%BE%D0%B4%D0%B0-%D0%B7%D0%B0%D0%BA%D0%B0%D1%82-1240382.jpeg");
        moves.add("http://s5.goodfon.ru/image/250578-2560x1600.jpg");
        moves.add("http://www.nastol.com.ua/images/201406/nastol.com.ua_101104.jpg");
        moves.add("http://upload.wikimedia.org/wikipedia/commons/2/20/Ukraine._Kamenets-Podolsky.jpg");
        moves.add("http://www.nastol.com.ua/images/201406/nastol.com.ua_101921.jpg");
        moves.add("http://www.nastol.com.ua/images/201403/nastol.com.ua_89245.jpg");
    }

    private void InistallAddBtn() {
        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.info("add btn press", logTag.debug_and_test);
                if (moves.size() <= lastShown)
                    return;
                DownloadEpisode episode = new DownloadEpisode();
                episode.setLink(moves.get(lastShown));
                episode.setEpisode_id(lastShown);
                lastShown++;
                listAdapter.add(new testCard(MyActivity.this,episode));
            }
        });
    }

    private void InitList() {
        this.list = (CardListView) findViewById(R.id.list);
        listAdapter = new CardArrayAdapter(this, new ArrayList<Card>());
        list.setAdapter(this.listAdapter);
    }
}
