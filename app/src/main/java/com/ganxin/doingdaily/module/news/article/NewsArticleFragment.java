package com.ganxin.doingdaily.module.news.article;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ganxin.doingdaily.R;
import com.ganxin.doingdaily.common.constants.ConstantValues;
import com.ganxin.doingdaily.common.data.model.NewsContentlistBean;
import com.ganxin.doingdaily.framework.BaseFragment;

import butterknife.BindView;

/**
 * Description : 新闻详情页面  <br/>
 * author : WangGanxin <br/>
 * date : 2017/1/17 <br/>
 * email : mail@wangganxin.me <br/>
 */
public class NewsArticleFragment extends BaseFragment<NewsArticleContract.View, NewsArticleContract.Presenter> implements NewsArticleContract.View {

    @BindView(R.id.headImage)
    ImageView headImage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.webView)
    WebView webView;

    /**
     * @param type
     * @param bean
     * @return
     */
    public static NewsArticleFragment newInstance(int type, NewsContentlistBean bean) {
        NewsArticleFragment fragment = new NewsArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantValues.KEY_VIEW_TYPE, type);
        bundle.putSerializable(ConstantValues.KEY_BEAN, bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int setContentLayout() {
        return R.layout.fragment_news_article;
    }

    @Override
    public void setUpView(View view) {

        AppCompatActivity mAppCompatActivity = (AppCompatActivity) getActivity();
        mAppCompatActivity.setSupportActionBar(toolbar);

        ActionBar actionBar = mAppCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setHasOptionsMenu(true); //处理 onOptionsItemSelected方法不被调用

        NewsContentlistBean bean = (NewsContentlistBean) getArguments().getSerializable(ConstantValues.KEY_BEAN);
        int type = getArguments().getInt(ConstantValues.KEY_VIEW_TYPE);

        if (bean != null) {
            switch (type) {
                case ConstantValues.VIEW_TYPE_TXT:
                    headImage.setVisibility(View.GONE);
                    break;
                case ConstantValues.VIEW_TYPE_IMAGE:
                    if (bean.getImageurls().get(0).getUrl().endsWith(".gif")) {
                        Glide.with(getContext())
                                .load(bean.getImageurls().get(0).getUrl())
                                .asGif()
                                .placeholder(R.drawable.placeholder_img_loading)
                                .dontAnimate()
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(headImage);
                    } else {
                        Glide.with(getContext())
                                .load(bean.getImageurls().get(0).getUrl())
                                .placeholder(R.drawable.placeholder_img_loading)
                                .crossFade()
                                .centerCrop()
                                .into(headImage);
                    }

                    headImage.setVisibility(View.VISIBLE);
                    ViewCompat.setTransitionName(headImage, ConstantValues.SHARE_IMAGE);
                    break;
            }


            collapsingToolbarLayout.setTitle(bean.getTitle());

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webView.getSettings().setDomStorageEnabled(true);// 开启DOM storage API 功能
            webView.getSettings().setDatabaseEnabled(true);// 开启database storage API功能
            webView.getSettings().setAppCacheEnabled(true); // 开启Application Cache功能

            String html = bean.getHtml().replaceFirst("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", ""); //去除第一张IMG标签图片
            webView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBack();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_news_article, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected NewsArticleContract.Presenter setPresenter() {
        return new NewsArticlePresenter();
    }
}
