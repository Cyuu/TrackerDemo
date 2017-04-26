package com.cyun.tracker.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cyun.tracker.R;

import static com.cyun.tracker.R.id.fab;

/**
 * 详情页--目前没在用
 */
public class DetailActivity extends AppCompatActivity {

    FloatingActionButton fab_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab_user = (FloatingActionButton) findViewById(fab);
        fab_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();

                Snackbar.make(view, "个人信息" , Snackbar.LENGTH_SHORT).setAction("立即查看", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });
    }
}
