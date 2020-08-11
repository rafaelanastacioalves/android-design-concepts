package com.rafaelanastacioalves.design.concepts.ui.articledetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rafaelanastacioalves.design.concepts.R

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
class ArticleDetailActivity : AppCompatActivity() {
    private val mSelectedItemUpButtonFloor = Int.MAX_VALUE
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_detail_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.article_fragment_container, ArticleDetailFragment.newInstance()).commit()
        }
    }
}