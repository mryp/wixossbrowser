package net.poringsoft.wixossbrowser;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.poringsoft.wixossbrowser.data.CardInfo;
import net.poringsoft.wixossbrowser.data.DeckDirInfo;
import net.poringsoft.wixossbrowser.data.EnvOption;
import net.poringsoft.wixossbrowser.data.SqlAccessHelper;
import net.poringsoft.wixossbrowser.data.SqlAccessManager;
import net.poringsoft.wixossbrowser.data.SqlSelectHelper;
import net.poringsoft.wixossbrowser.utils.PSDebug;
import net.poringsoft.wixossbrowser.utils.PSUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * メイン画面のナビゲーション用Fragment
 * Created by mry
 */
public class NavigationDrawerFragment extends Fragment {
    //定数
    //-------------------------------------------------------------
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    public static final int FIRST_SELECT_POSITION = 1;

    /**
     * 商品別リストのデフォルト定義値（key=検索キー、value=表示ラベル）
     */
    private static final Map<String, String> DEF_MAP_PRODUCT = new LinkedHashMap<String, String>(){
        {
            put("WX01", "WX01 サーブドセレクター");
            put("WX02", "WX02 ステアード セレクター");
            put("WX03", "WX03 スプレッドセレクター");
            put("WX04", "WX04 インフェクテッドセレクター");
            put("WX05", "WX05 ビギニングセレクター");
            put("WD01", "WD01 ホワイトホープ");
            put("WD02", "WD02 レッドアンビション");
            put("WD03", "WD03 ブルーアプリ");
            put("WD04", "WD04 グリーンワナ");
            put("WD05", "WD05 ブラックデザイア");
            put("WD06", "WD06 ブルーリクエスト");
            put("WD07", "WD07 ブラッククレイヴ");
            put("PR", "PR プロモカード");
            put("SP01", "SP01 アニメBOX1");
            put("SP02", "SP02 アニメBOX2");
            put("SP03", "SP03 アニメBOX3");
            put("SP05", "SP04 カードガム");
        }
    };

    /**
     * 色別リストのデフォルト定義値（key=検索キー、value=表示ラベル）
     */
    private static final Map<String, String> DEF_MAP_COLOR = new LinkedHashMap<String, String>(){
        {
            put("白", "白色");
            put("赤", "赤色");
            put("青", "青色");
            put("緑", "緑色");
            put("黒", "黒色");
            put("無", "無色");
        }
    };

    /**
     * 種類別リストのデフォルト定義値（key=検索キー、value=表示ラベル）
     */
    private static final Map<String, String> DEF_MAP_KIND = new LinkedHashMap<String, String>(){
        {
            put("ルリグ", "ルリグ");
            put("アーツ", "アーツ");
            put("シグニ", "シグニ");
            put("スペル", "スペル");
        }
    };

    /**
     * タイプ別リストのデフォルト定義値（key=検索キー、value=表示ラベル）
     */
    private static final Map<String, String> DEF_MAP_TYPE = new LinkedHashMap<String, String>(){
        {
            put("タマ", "タマ");
            put("花代", "花代");
            put("花代/ユヅキ", "花代/ユヅキ");
            put("ピルルク", "ピルルク");
            put("緑子", "緑子");
            put("ウリス", "ウリス");
            put("エルドラ", "エルドラ");
            put("ウムル", "ウムル");
            put("イオナ", "イオナ");
            put("ユヅキ", "ユヅキ");
            put("ミルルン", "ミルルン");
            put("アン", "アン");
            put("リメンバ", "リメンバ");
            put("リメンバ/ピルルク", "リメンバ/ピルルク");
            put("精武：アーム", "精武：アーム");
            put("精武：ウェポン", "精武：ウェポン");
            put("精武：アーム/ウェポン", "精武：アーム/ウェポン");
            put("精武：毒牙", "精武：毒牙");
            put("精像：天使", "精像：天使");
            put("精像：悪魔", "精像：悪魔");
            put("精像：美巧", "精像：美巧");
            put("精械：電機", "精械：電機");
            put("精械：古代兵器", "精械：古代兵器");
            put("精械：迷宮", "精械：迷宮");
            put("精羅：宝石", "精羅：宝石");
            put("精羅：鉱石", "精羅：鉱石");
            put("精羅：植物", "精羅：植物");
            put("精羅：原子", "精羅：原子");
            put("精生：地獣", "精生：地獣");
            put("精生：水獣", "精生：水獣");
            put("精生：空獣", "精生：空獣");
            put("精生：龍獣", "精生：龍獣");
            put("精元", "精元");
            put("-", "タイプ無");
        }
    };

    /**
     * 限定条件別リストのデフォルト定義値（key=検索キー、value=表示ラベル）
     */
    private static final Map<String, String> DEF_MAP_LIMIT_COND = new LinkedHashMap<String, String>(){
        {
            put("タマ限定", "タマ限定");
            put("タマ", "タマ");
            put("花代限定", "花代限定");
            put("花代", "花代");
            put("ユヅキ限定", "ユヅキ限定");
            put("ユヅキ", "ユヅキ");
            put("花代/ユヅキ限定", "花代/ユヅキ限定");
            put("ピルルク限定", "ピルルク限定");
            put("ピルルク", "ピルルク");
            put("緑子限定", "緑子限定");
            put("緑子", "緑子");
            put("ウリス限定", "ウリス限定");
            put("ウリス", "ウリス");
            put("エルドラ限定", "エルドラ限定");
            put("エルドラ", "エルドラ");
            put("ウムル限定", "ウムル限定");
            put("イオナ限定", "イオナ限定");
            put("タマ/イオナ限定", "タマ/イオナ限定");
            put("タマ/イオナ", "タマ/イオナ");
            put("ミルルン限定", "ミルルン限定");
            put("ミルルン", "ミルルン");
            put("アン限定", "アン限定");
            put("アン", "アン");
            put("-", "限定条件無");
        }
    };

    /**
     * レベル別リストのデフォルト定義値（key=検索キー、value=表示ラベル）
     */
    private static final Map<String, String> DEF_MAP_LEVEL = new LinkedHashMap<String, String>(){
        {
            put("5", "Lv.5");
            put("4", "Lv.4");
            put("3", "Lv.3");
            put("2", "Lv.2");
            put("1", "Lv.1");
            put("0", "Lv.0");
            put("-1", "レベル無");
        }
    };

    /**
     * レア度別リストのデフォルト定義値（key=検索キー、value=表示ラベル）
     */
    private static final Map<String, String> DEF_MAP_RARITY = new LinkedHashMap<String, String>(){
        {
            put("LR", "LR");
            put("LC", "LC");
            put("SR", "SR");
            put("R", "R");
            put("C", "C");
            put("ST", "ST");
            put("PR", "PR");
            put("SP", "SP");
        }
    };

    private static final Map<String, String> DEF_MAP_GROWCOST = new LinkedHashMap<String, String>(){
        {
            put("-", "グロウコスト無");
        }
    };

    private static final Map<String, String> DEF_MAP_COST = new LinkedHashMap<String, String>(){
        {
            put("-", "コスト無");
        }
    };

    private static final Map<String, String> DEF_MAP_LIMIT = new LinkedHashMap<String, String>(){
        {
            put("-1", "リミット無");
        }
    };

    private static final Map<String, String> DEF_MAP_POWER = new LinkedHashMap<String, String>(){
        {
            put("-1", "パワー無");
        }
    };

    private static final Map<String, String> DEF_MAP_GUARD = new LinkedHashMap<String, String>(){
        {
            put("有", "ガード有");
            put("-", "ガード無");
        }
    };

    private static final Map<String, String> DEF_MAP_ABILITY = new LinkedHashMap<String, String>(){
        {
            put("常", "常時能力");
            put("出", "出現時能力");
            put("起", "起動能力");
            put("ラ", "ライフバースト");
        }
    };

    private static final Map<String, String> DEF_MAP_NODATA = new LinkedHashMap<String, String>(){
        {
            //項目なし
        }
    };

    //フィールド
    //------------------------------------------------------------
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = FIRST_SELECT_POSITION;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    //メソッド
    //--------------------------------------------------------------
    /**
     * コンストラクタ
     */
    public NavigationDrawerFragment() {
    }

    /**
     * 起動時イベント
     * @param savedInstanceState セーブデータ
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PSDebug.d("call");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    /**
     * 画面起動時イベント
     * @param savedInstanceState セーブデータ
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PSDebug.d("call");
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            resetNaviSelectPos(FIRST_SELECT_POSITION);
        }
    }

    /**
     * 画面復旧時
     */
    @Override
    public void onResume() {
        super.onResume();
        PSDebug.d("call");

        if (mDrawerListView.getAdapter() == null) {
            resetListAdapter();
        }
    }

    /**
     * アダプターを設定する
     */
    private void resetListAdapter() {
        ReadNaviListAsyncTask naviTask = new ReadNaviListAsyncTask();
        naviTask.execute("");
    }

    public void resetNaviSelectPos(int pos) {
        if (pos != -1) {
            mCurrentSelectedPosition = pos;
        }
        resetListAdapter();
    }

    /**
     * ビューの生成時イベント
     * @param inflater 親画面操作
     * @param container 親コンテナ
     * @param savedInstanceState セーブデータ
     * @return 生成したビューオブジェクト
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PSDebug.d("call");
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        return mDrawerListView;
    }

    /**
     * ナビゲーション用リストを生成して返す
     * @return アダプター
     */
    private NavigationListAdapter createNavigationAdapter(SqlAccessManager sqlManager) {
        PSDebug.d("call");
        List<NaviSectionHeaderData> sectionList = new ArrayList<NaviSectionHeaderData>();
        List<List<NaviSectionRowData>> rowList = new ArrayList<List<NaviSectionRowData>>();

        sectionList.add(new NaviSectionHeaderData("マイデッキ"));
        rowList.add(setNaviDeckList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("商品カテゴリ"));
        rowList.add(setNaviProductList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("レベル"));
        rowList.add(setNaviLevelList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("レア度"));
        rowList.add(setNaviRarityList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("種類"));
        rowList.add(setNaviKindList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("タイプ"));
        rowList.add(setNaviTypeList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("色"));
        rowList.add(setNaviColorList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("グロウコスト"));
        rowList.add(setNaviGrowCostList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("コスト"));
        rowList.add(setNaviCostList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("リミット"));
        rowList.add(setNaviLimitList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("パワー"));
        rowList.add(setNaviPowerList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("限定条件"));
        rowList.add(setNaviLimitCondList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("ガード"));
        rowList.add(setNaviGuardList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("能力"));
        rowList.add(setNaviAbilityList(sqlManager));

        sectionList.add(new NaviSectionHeaderData("イラストレーター"));
        rowList.add(setNaviIllustList(sqlManager));

        return new NavigationListAdapter(getActivity(), sectionList, rowList);
    }

    /**
     * デッキリストを取得する
     * @param sqlManager DB操作クラス
     * @return デッキリスト
     */
    private List<NaviSectionRowData> setNaviDeckList(SqlAccessManager sqlManager)
    {
        List<NaviSectionRowData> sectionList = new ArrayList<NaviSectionRowData>();
        List<DeckDirInfo> deckDirInfoList = sqlManager.selectDeckDirInfoAll();
        for (DeckDirInfo dirInfo : deckDirInfoList) {
            String searchText = SqlSelectHelper.SELECT_DECK_DIR_CMD_ID + ":" + dirInfo.getId();
            NaviSectionRowData rowData = new NaviSectionRowData(dirInfo.getName()
                    , String.valueOf(sqlManager.selectCountCardInfo(searchText)), searchText);
            sectionList.add(rowData);
        }

        return sectionList;
    }

    /**
     * デッキデータを生成しなおす
     */
    public void deckListChanged() {
        //個数を取り直してセットする
        if (mDrawerListView.getAdapter() != null) {
            SqlAccessManager sqlManager = new SqlAccessManager(getActivity());
            List<List<NaviSectionRowData>> rowList = ((NavigationListAdapter) mDrawerListView.getAdapter()).getRowList();
            rowList.set(0, setNaviDeckList(sqlManager));

            ((NavigationListAdapter) mDrawerListView.getAdapter()).notifyDataSetChanged();
        }
    }

    /**
     * カテゴリ内のリスト情報をセットして返す
     * @param defList 内部保持定義値リスト
     * @param dataList DB内定義値リスト
     * @param searchCmd 検索用コマンド名
     * @return カテゴリ内のリスト情報
     */
    private List<NaviSectionRowData> setNaviList(Map<String, String> defList, List<String> dataList, String searchCmd)
    {
        List<NaviSectionRowData> sectionList = new ArrayList<NaviSectionRowData>();
        for (String data : dataList) {
            if (!defList.containsKey(data)) {
                //定義にないものだけをそのまま追加する
                sectionList.add(new NaviSectionRowData(data, searchCmd + ":" + data));
            }
        }

        //最後に追加
        for (Map.Entry<String, String> defdata : defList.entrySet()) {
            sectionList.add(new NaviSectionRowData(defdata.getValue(), searchCmd + ":" + defdata.getKey()));
        }

        return sectionList;
    }

    /**
     * 商品コードリストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviProductList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfo();
        return setNaviList(DEF_MAP_PRODUCT, dataList, SqlSelectHelper.SELECT_CARD_CMD_PRODUCT_CODE);
    }

    /**
     * 色別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviColorList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoColor();
        return setNaviList(DEF_MAP_COLOR, dataList, SqlSelectHelper.SELECT_CARD_CMD_COLOR);
    }

    /**
     * 種類別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviKindList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoKind();
        return setNaviList(DEF_MAP_KIND, dataList, SqlSelectHelper.SELECT_CARD_CMD_KIND);
    }

    /**
     * タイプ別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviTypeList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoType();
        return setNaviList(DEF_MAP_TYPE, dataList, SqlSelectHelper.SELECT_CARD_CMD_TYPE);
    }

    /**
     * 限定条件別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviLimitCondList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoLimitCond();
        return setNaviList(DEF_MAP_LIMIT_COND, dataList, SqlSelectHelper.SELECT_CARD_CMD_LIMIT_COND);
    }

    /**
     * レベル別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviLevelList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoLevel();
        return setNaviList(DEF_MAP_LEVEL, dataList, SqlSelectHelper.SELECT_CARD_CMD_LEVEL);
    }

    /**
     * レア度別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviRarityList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoRarity();
        return setNaviList(DEF_MAP_RARITY, dataList, SqlSelectHelper.SELECT_CARD_CMD_RARITY);
    }

    /**
     * グロウコスト別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviGrowCostList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoGrowCost();
        Collections.sort(dataList); //名前順でソート
        return setNaviList(DEF_MAP_GROWCOST, dataList, SqlSelectHelper.SELECT_CARD_CMD_GROWCOST);
    }

    /**
     * コスト別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviCostList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoCost();
        Collections.sort(dataList); //名前順でソート
        return setNaviList(DEF_MAP_COST, dataList, SqlSelectHelper.SELECT_CARD_CMD_COST);
    }

    /**
     * リミット別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviLimitList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoLimit();
        PSUtils.sortStringToIntDesc(dataList);
        return setNaviList(DEF_MAP_LIMIT, dataList, SqlSelectHelper.SELECT_CARD_CMD_LIMIT);
    }

    /**
     * パワー別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviPowerList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoPower();
        PSUtils.sortStringToIntDesc(dataList);
        return setNaviList(DEF_MAP_POWER, dataList, SqlSelectHelper.SELECT_CARD_CMD_POWER);
    }

    /**
     * ガード別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviGuardList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoGuard();
        Collections.sort(dataList); //名前順でソート
        return setNaviList(DEF_MAP_GUARD, dataList, SqlSelectHelper.SELECT_CARD_CMD_GUARD);
    }

    /**
     * 能力別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviAbilityList(SqlAccessManager sqlManager)
    {
        List<String> dataList = new ArrayList<String>();
        return setNaviList(DEF_MAP_ABILITY, dataList, SqlSelectHelper.SELECT_CARD_CMD_ABILITY);
    }

    /**
     * イラストレーター別リストを取得する
     * @param sqlManager 検索用DB
     * @return ナビゲーションの行リスト
     */
    private List<NaviSectionRowData> setNaviIllustList(SqlAccessManager sqlManager)
    {
        List<String> dataList = sqlManager.selectDistinctCardInfoIllust();
        Collections.sort(dataList); //名前順でソート
        return setNaviList(DEF_MAP_NODATA, dataList, SqlSelectHelper.SELECT_CARD_CMD_ILLUST);
    }


    /**
     * カード詳細一覧画面からカード情報をダウンロードする
     */
    public class ReadNaviListAsyncTask extends AsyncTask<String, String, NavigationListAdapter> {
        /**
         * 実処理
         * @param text
         * @return
         */
        @Override
        protected NavigationListAdapter doInBackground(String... text) {
            PSDebug.d("call");
            SqlAccessManager accessManger = new SqlAccessManager(getActivity());
            return createNavigationAdapter(accessManger);
        }

        /**
         * 後処理
         * @param result
         */
        @Override
        protected void onPostExecute(NavigationListAdapter result) {
            PSDebug.d("call");
            mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectItem(position);
                }
            });
            mDrawerListView.setAdapter(result);
            mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
            selectItem(mCurrentSelectedPosition);
        }
    }

    /**
     * ドロワーがオープン状態かどうか
     * @return 開いているときはtrue
     */
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * リストアイテム選択時処理
     * @param position リスト位置
     */
    public void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null && mDrawerListView != null) {
            Object item = mDrawerListView.getItemAtPosition(position);
            if (item != null && item.getClass() == NaviSectionRowData.class)
            {
                //選択した項目を上位に通知する
                NaviSectionRowData rowData = (NaviSectionRowData)item;
                mCallbacks.onNavigationDrawerItemSelected(rowData.getLabel(), rowData.getSearchText());
            }
        }
    }

    /**
     * 画面アタッチ時
     * @param activity 親画面
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    /**
     * 画面デタッチ時
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * 現在地保存
     * @param outState 保存先データ
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    /**
     * 設定変更時処理
     * @param newConfig 新しい設定値
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * メニュー生成時処理
     * @param menu 親メニュー
     * @param inflater コンテナ操作
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * メニュー項目選択時処理
     * @param item 選択メニュー
     * @return 処理したかどうか
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ActionBar生成
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    /**
     * ActionBarを取得する
     * @return アクションバーオブジェクト
     */
    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * 選択項目上位通知用インターフェース
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(String title, String searchText);
    }
}
