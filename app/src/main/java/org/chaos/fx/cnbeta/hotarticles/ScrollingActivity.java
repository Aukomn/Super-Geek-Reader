package org.chaos.fx.cnbeta.hotarticles;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import org.chaos.fx.cnbeta.R;

public class ScrollingActivity extends BaseActivity {
    private int position;
    private TextView tvd_content;
    private TextView tvd_author;
    private TextView tvd_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ArticleData article = getIntent().getParcelableExtra("articleData");
        position = getIntent().getIntExtra("position", 0);
        final CollapsingToolbarLayout collapsingToolbar=(CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
         collapsingToolbar.setTitle(article.title);
        tvd_author = (TextView) findViewById(R.id.detail_author);
        tvd_date = (TextView) findViewById(R.id.detail_date);
        tvd_content = (TextView) findViewById(R.id.detail_content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvd_content.setText(Html.fromHtml(article.content, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvd_content.setText(Html.fromHtml(article.content));
        }
        tvd_date.setText(article.datetime);
        tvd_author.setText(article.reference);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "感谢使用", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
