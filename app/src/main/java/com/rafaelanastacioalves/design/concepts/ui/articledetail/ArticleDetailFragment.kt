package com.rafaelanastacioalves.design.concepts.ui.articledetail

import android.database.Cursor
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.rafaelanastacioalves.design.concepts.R
import kotlinx.android.synthetic.main.article_detail_fragment.*
import kotlinx.android.synthetic.main.article_detail_reading_layout.*

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a [ArticleListActivity] in two-pane mode (on
 * tablets) or a [ArticleDetailActivity] on handsets.
 */
class ArticleDetailFragment
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    : Fragment() {
    private val mCursor: Cursor? = null
    private val mItemId: Long = 0
    private val mRootView: View? = null
    private val mMutedColor = -0xcccccd

    //    private ObservableScrollView mScrollView;
    //    private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
    //    private ColorDrawable mStatusBarColorDrawable;
    private val mTopInset = 0
    private val mPhotoContainerView: View? = null
    private val mPhotoView: ImageView? = null
    private val mScrollY = 0
    private val mIsCard = false
    private val mStatusBarFullOpacityBottom = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.article_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        bindViews()
    }

    private fun setupActionBar() {
        if (null != toolbar) {
            val appCompatActivity = activity as AppCompatActivity
            appCompatActivity.setSupportActionBar(toolbar)
            appCompatActivity?.supportActionBar?.setDisplayShowTitleEnabled(false);
            appCompatActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true);
        }
    }

    private fun  bindViews() {

        //        if (mRootView == null) {
        //            return;
        //        }
        //
        //        TextView titleView = (TextView) mRootView.findViewById(R.id.article_title);
                val bylineView : TextView = article_byline
                bylineView.setMovementMethod(LinkMovementMethod())
        //        TextView bodyView = (TextView) mRootView.findViewById(R.id.article_body);
        //        bodyView.setTextColor(getResources().getColor(R.color.reading_text));
        //        AppCompatActivity mActivity = (AppCompatActivity) getActivity();
        //
        //
        //        if (mCursor != null) {
        //            mRootView.setVisibility(View.VISIBLE);
        //            titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
                    bylineView.setText(Html.fromHtml(getString(R.string.article_detail_parallax_subtitle)))
        //            bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));
        //
        //            mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
        //                @Override
        //                public void onClick(View view) {
        //                    startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
        //                            .setType("text/plain")
        //                            .setText("Some sample text")
        //                            .getIntent(), getString(R.string.action_share)));
        //                }
        //            });
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //            Log.d(TAG,"Trying to load image from " + mCursor.getString(ArticleLoader.Query.PHOTO_URL));
        //
        //            Picasso.with(mActivity)
        //                    .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
        //                    .into(mPhotoView, new Callback() {
        //                        @Override
        //                        public void onSuccess() {
        //                            ((AppCompatActivity) getActivity()).supportStartPostponedEnterTransition();
        //                        }
        //
        //                        @Override
        //                        public void onError() {
        //
        //                        }
        //
        //
        //                    });
        //
        //
        //
        //
        //
        //        } else {
        //            mRootView.setVisibility(View.GONE);
        //            titleView.setText("N/A");
        //            bylineView.setText("N/A" );
        //            bodyView.setText("N/A");
        //        }
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //        }
            }
        //    @Override
        //    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
        //    }
        //    @Override
        //    public void onDestroy() {
        //        super.onDestroy();
        //        Picasso.with(getActivity()).cancelRequest(mPhotoView);
        //
        //    }
        //
        //    @Override
        //    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //        if(cursorLoader.getId() == LOADER_ID_ARTICLE_WITH_ID){
        //            if(cursor.moveToFirst()){
        //                Picasso.with(getActivity())
        //                        .load(cursor.getString(ArticleLoader.Query.PHOTO_URL))
        //                        .fetch();
        //            }
        //
        //            if (!isAdded()) {
        //                if (cursor != null) {
        //                    Log.i(TAG,"Fragment not added yet \n + \ncursor =! null");
        //                    cursor.close();
        //                }
        //                Log.i(TAG,"Fragment not added yet \n + \ncursor = null");
        //                return;
        //            }
        //
        //            mCursor = cursor;
        //            if (mCursor != null && !mCursor.moveToFirst()) {
        //                Log.e(TAG, "Error reading item detail cursor");
        //                mCursor.close();
        //                mCursor = null;
        //
        //            }
        //
        //            bindViews();
        //        }
        //
        //    }
        //    @Override
        //    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //        Log.d(TAG,"Resetting loader");
        //        mCursor = null;
        //        bindViews();
        //    }
    companion object {
        private const val TAG = "ArticleDetailFragment"
        const val EXTRA_ARTICLE_ID = "article_id_extra"
        const val ARG_ITEM_ID = "item_id"
        private const val PARALLAX_FACTOR = 1.25f
        var LOADER_ID_ARTICLE_WITH_ID = 1
        fun newInstance(): ArticleDetailFragment {
            val arguments = Bundle()
            val fragment = ArticleDetailFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}