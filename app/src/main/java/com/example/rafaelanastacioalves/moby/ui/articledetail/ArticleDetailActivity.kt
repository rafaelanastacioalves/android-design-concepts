package com.example.rafaelanastacioalves.moby.ui.articledetail

import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rafaelanastacioalves.moby.R

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
class ArticleDetailActivity : AppCompatActivity() {
    private val mSelectedItemUpButtonFloor = Int.MAX_VALUE
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_detail_activity)
        if (savedInstanceState == null) {
                getFragmentManager().beginTransaction().add(R.id.article_fragment_container, ArticleDetailFragment.newInstance()).commit()
        }
    }
}