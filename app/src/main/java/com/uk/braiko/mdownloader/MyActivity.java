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
        moves.add("http://www.ex.ua/get/4182107");
        moves.add("http://www.ex.ua/get/4182111");
        moves.add("http://www.ex.ua/get/4182124");
        moves.add("http://www.ex.ua/get/4182175");
        moves.add("http://www.ex.ua/get/4182187");
        moves.add("http://www.ex.ua/get/4182220");
        moves.add("http://www.ex.ua/get/4182232");
        moves.add("http://www.ex.ua/get/4182235");
        moves.add("http://www.ex.ua/get/4182239");
        moves.add("http://www.ex.ua/get/4182241");
        moves.add("http://www.ex.ua/get/4182245");
        moves.add("http://www.ex.ua/get/4248965");
        moves.add("http://www.ex.ua/get/4249903");
        moves.add("http://www.ex.ua/get/4254078");
        moves.add("http://www.ex.ua/get/4345471");
        moves.add("http://www.ex.ua/get/4408806");

    }

    private void InistallAddBtn() {
        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.info("add btn press", logTag.debug_and_test);
                if (moves.size() <= lastShown)
                    return;
                DownloadEpisode episode = new DownloadEpisode();
                episode.setFull_path(moves.get(lastShown));
                episode.setEpisode_id(lastShown*7);
                lastShown++;
                listAdapter.add(new testCard(MyActivity.this,episode));
                MovieDownloaderManager.with(MyActivity.this).by(episode).load();
            }
        });
    }

    private void InitList() {
        this.list = (CardListView) findViewById(R.id.list);
        listAdapter = new CardArrayAdapter(this, new ArrayList<Card>());
        list.setAdapter(this.listAdapter);
    }
}
