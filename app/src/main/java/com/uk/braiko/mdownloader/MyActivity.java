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
        moves.add("http://vid.lsw.redtubefiles.com//_videos_t4vn23s9jc5498tgj49icfj4678//0000352//_mp4//0352268.mp4?st=JN76wN4iJTfBAFqsyTLINg&e=1406633276");

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
                episode.setEpisode_id(lastShown*7);
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
