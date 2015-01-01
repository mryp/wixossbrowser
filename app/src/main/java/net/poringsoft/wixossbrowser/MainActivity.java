package net.poringsoft.wixossbrowser;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import net.poringsoft.wixossbrowser.data.EnvOption;
import net.poringsoft.wixossbrowser.data.SearchSuggestionProvider;
import net.poringsoft.wixossbrowser.data.SqlSelectHelper;
import net.poringsoft.wixossbrowser.utils.PSDebug;

/**
 * メイン画面
 * Created by mry
 */
public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    //定数
    //-------------------------------------------------------------------------
    public static final int REQ_CODE_UPDATE = 1;       //データ取得画面から
    public static final int REQ_CODE_PREF = 2;         //設定画面から
    public static final int REQ_CODE_DECK_DIR = 3;     //デッキ編集画面から
    public static final int REQ_CODE_CARD_INFO = 4;    //カード詳細画面から

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_SEARCH_TEXT = "ARG_SEARCH_TEXT";


    //フィールド
    //-------------------------------------------------------------------------
    private NavigationDrawerFragment m_navigationDrawerFragment;
    private CharSequence m_title = "";
    private String m_searchText = "";
    private boolean m_isReloadCardList = false;         //カードデータの再読み込みが必要かどうか
    private boolean m_isReloadNavigation = false;       //ナビゲーションデータの再読み込みが必要かどうか


    //メソッド
    //-------------------------------------------------------------------------
    /**
     * 画面起動時処理
     * @param savedInstanceState 保存データ
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_title = getTitle();
        if (savedInstanceState != null)
        {
            m_searchText = savedInstanceState.getString(ARG_SEARCH_TEXT);
            m_title = savedInstanceState.getString(ARG_TITLE);
        }
        m_navigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        m_navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));
    }

    /**
     * 画面起動（検索時）
     * @param intent 新しいインテント
     */
    @Override
    protected void onNewIntent(Intent intent) {
        PSDebug.d("call");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            m_searchText = getQueryString(intent);
            m_title = "検索：" + m_searchText;
            m_isReloadCardList = true;
        }
    }

    /**
     * データの保存
     * @param outState 保存先データ
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PSDebug.d("call");

        outState.putString(ARG_SEARCH_TEXT, m_searchText);
        outState.putString(ARG_TITLE, m_title.toString());
    }

    /**
     * 復旧操作時
     */
    @Override
    protected void onResume() {
        super.onResume();

        PSDebug.d("call");
    }

    /**
     * Fragmentの復旧時
     */
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        PSDebug.d("call m_isReloadNavigation=" + m_isReloadNavigation + " m_isReloadCardList=" + m_isReloadCardList);
        if (m_isReloadNavigation) {
            m_isReloadNavigation = false;
            m_searchText = "";
            m_title = "";
            m_navigationDrawerFragment.resetNaviSelectPos(1);
        }
        if (m_isReloadCardList) {
            m_isReloadCardList = false;
            startFragment(m_title.toString(), m_searchText, false);
        }
        m_navigationDrawerFragment.deckListChanged();
    }

    /**
     * 停止時
     */
    @Override
    protected void onPause() {
        super.onPause();

        PSDebug.d("call");
    }

    /**
     * 検索語句を取得する
     * @param intent 検索時のインテント
     * @return 検索文字列
     */
    private String getQueryString(Intent intent)
    {
        String queryString = intent.getStringExtra(SearchManager.QUERY);
        PSDebug.d("queryString=" + queryString);

        //検索語を保存する
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
        suggestions.saveRecentQuery(queryString, null);

        PSDebug.d("query=" + queryString);
        return queryString;
    }

    /**
     * ナビメニュー選択時処理
     */
    @Override
    public void onNavigationDrawerItemSelected(String title, String searchText) {
        startFragment(title, searchText, false);
    }

    /**
     * Fragmentの表示を開始する
     * @param title タイトル
     * @param searchText 検索文字列
     * @param isReload 強制的に再読み込みを行うかどうか(タイトル・検索文字が同じでも再読み込みを行う)
     */
    private void startFragment(String title, String searchText, boolean isReload)
    {
        PSDebug.d("title=" + title + " searchText=" + searchText);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment topFragment = fragmentManager.findFragmentById(R.id.container);
        if (!isReload && topFragment != null && topFragment instanceof CardListFragment) {
            CardListFragment cardListFragment = (CardListFragment)topFragment;
            if (cardListFragment.getArgTitle().equals(title) && cardListFragment.getArgSearchText().equals(searchText)) {
                return; //同じなので処理しない
            }
        }

        FragmentTransaction flTrans = fragmentManager.beginTransaction();
        if (EnvOption.getCardListStackFragment(this)) {
            if (!isReload && topFragment != null) {
                //現在の画面をスタックに積む
                flTrans = flTrans.addToBackStack(null);
            }
        }
        flTrans.replace(R.id.container, CardListFragment.newInstance(title, searchText)).commit();
    }

    /**
     * ナビメニュー選択後処理
     */
    public void onSectionAttached(String title, String searchText) {
        m_title = title;
        m_searchText = searchText;
        restoreActionBar();
    }

    /**
     * アクションバー設定
     */
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(m_title);
    }

    /**
     * メニュー設定
     * @param menu メニューアイテム
     * @return メニュー設定したかどうか
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!m_navigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * メニュー表示前イベント
     * @param menu メニューオブジェクト
     * @return 処理を行ったかどうか
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!m_navigationDrawerFragment.isDrawerOpen()) {
            String nowSortMenu = "";
            PSDebug.d("m_searchText=" + m_searchText);
            int sortType = EnvOption.getCardListSortType(this, m_searchText);
            switch (sortType) {
                case SqlSelectHelper.SELECT_CARD_SORT_MODEL_NUMBER_ASC:
                    nowSortMenu = "型番順";
                    break;
                case SqlSelectHelper.SELECT_CARD_SORT_NAME_ASC:
                    nowSortMenu = "カード名順";
                    break;
                case SqlSelectHelper.SELECT_CARD_SORT_NAME_KANA_ASC:
                    nowSortMenu = "よみがな順";
                    break;
                case SqlSelectHelper.SELECT_CARD_SORT_LIMIT_DESC:
                    nowSortMenu = "リミット大きい順";
                    break;
                case SqlSelectHelper.SELECT_CARD_SORT_POWER_DESC:
                    nowSortMenu = "パワー大きい順";
                    break;
                case SqlSelectHelper.SELECT_CARD_SORT_KIND_DESC:
                    nowSortMenu = "種別降順";
                    break;
                case SqlSelectHelper.SELECT_CARD_SORT_KIND_ASC:
                    nowSortMenu = "種別昇順";
                    break;
            }
            MenuItem sortItem = menu.findItem(R.id.action_sort);
            if (sortItem != null) {
                sortItem.setTitle("並び替え(現在:" + nowSortMenu + ")");
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * メニュー選択時処理
     * @param item 選択アイテム
     * @return メニューを選択したかどうか
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit_deck:
                startEditDeck();
                return true;
            case R.id.action_update_list:
                startUpdateList();
                return true;
            case R.id.action_search:
                onSearchRequested();
                return true;
            case R.id.action_settings:
                startSetting();
                return true;
            case R.id.action_sort:
                //サブメニューを使用するため何もしない
                return true;
            case R.id.action_sort_model:
                setSortType(SqlSelectHelper.SELECT_CARD_SORT_MODEL_NUMBER_ASC);
                return true;
            case R.id.action_sort_name:
                setSortType(SqlSelectHelper.SELECT_CARD_SORT_NAME_ASC);
                return true;
            case R.id.action_sort_kana:
                setSortType(SqlSelectHelper.SELECT_CARD_SORT_NAME_KANA_ASC);
                return true;
            case R.id.action_sort_limit:
                setSortType(SqlSelectHelper.SELECT_CARD_SORT_LIMIT_DESC);
                return true;
            case R.id.action_sort_power:
                setSortType(SqlSelectHelper.SELECT_CARD_SORT_POWER_DESC);
                return true;
            case R.id.action_sort_kind_desc:
                setSortType(SqlSelectHelper.SELECT_CARD_SORT_KIND_DESC);
                return true;
            case R.id.action_sort_kind_asc:
                setSortType(SqlSelectHelper.SELECT_CARD_SORT_KIND_ASC);
                return true;
            case R.id.action_share_official:
                startJumpWebSite(getString(R.string.url_official_top));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * マイデッキ編集画面へ遷移
     */
    private void startEditDeck() {
        Intent intent = new Intent(this, DeckDirActivity.class);
        this.startActivityForResult(intent, REQ_CODE_DECK_DIR);
    }

    /**
     * キャッシュ更新画面へ遷移
     */
    private void startUpdateList() {
        Intent intent = new Intent(this, DataUpdateActivity.class);
        this.startActivityForResult(intent, REQ_CODE_UPDATE);
    }

    /**
     * 指定したURLでブラウザを開く
     * @param url URL文字列
     */
    private void startJumpWebSite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    /**
     * 設定画面へ遷移する
     */
    private void startSetting() {
        Intent intent = new Intent(this, PrefActivity.class);
        this.startActivityForResult(intent, REQ_CODE_PREF);
    }

    /**
     * 画面戻りイベント
     * @param requestCode 呼び出し元（この画面で指定）コード
     * @param resultCode 呼び出された側の結果コード
     * @param data インテント
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        PSDebug.d("requestCode=" + requestCode + " resultCode=" + resultCode);
        if (requestCode == REQ_CODE_UPDATE) {
            if (resultCode == DataUpdateActivity.RESULT_IS_UPDATE) {
                m_isReloadNavigation = true;
            }
        }
        else if (requestCode == REQ_CODE_DECK_DIR) {
            if (resultCode == DeckDirActivity.RESULT_IS_UPDATE) {
                m_isReloadNavigation = true;
            }
        }
        else if (requestCode == REQ_CODE_PREF) {
            m_isReloadCardList = true;  //Resumeで再読み込み
        }
        else if (requestCode == REQ_CODE_CARD_INFO) {
            if (SqlSelectHelper.isDeckSearch(m_searchText) && resultCode == CardInfoActivity.RESULT_IS_UPDATE) {
                m_isReloadCardList = true;  //Resumeで再読み込み
            }
        }
    }

    /**
     * 並び替え開始
     * @param sortType ソートタイプ（SqlSelectHelper.SELECT_CARD_SORT_～）
     */
    private void setSortType(int sortType)
    {
        if (EnvOption.getCardListSortUseDeck(this) && SqlSelectHelper.isDeckSearch(m_searchText)) {
            EnvOption.putCardListSortTypeDeck(this, sortType);
        }
        else {
            EnvOption.putCardListSortType(this, sortType);
        }
        startFragment(m_title.toString(), m_searchText, true);
    }
}
