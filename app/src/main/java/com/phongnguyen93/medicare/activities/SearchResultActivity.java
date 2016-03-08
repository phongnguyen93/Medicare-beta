package com.phongnguyen93.medicare.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Phong Nguyen on 05-Mar-16.
 */
public class SearchResultActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onSearchRequested();
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchManager.setOnDismissListener(new SearchManager.OnDismissListener() {
            @Override
            public void onDismiss() {
                Intent t = new Intent(SearchResultActivity.this, MainActivity.class);
                startActivity(t);
            }
        });
    }


}
