package net.poringsoft.wixossbrowser;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.poringsoft.wixossbrowser.data.CardInfo;
import net.poringsoft.wixossbrowser.data.DeckDirInfo;
import net.poringsoft.wixossbrowser.data.DeckItemInfo;
import net.poringsoft.wixossbrowser.data.EnvOption;
import net.poringsoft.wixossbrowser.data.SqlAccessManager;
import net.poringsoft.wixossbrowser.utils.PSDebug;
import net.poringsoft.wixossbrowser.utils.PSUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * カード詳細情報表示画面
 * Created by mry on 2014/04/30.
 */
public class CardInfoActivity extends ActionBarActivity {
    //定数
    //--------------------------------------------------------------------------
    public static final String INTENT_MODEL_NUMBER = "INTENT_MODEL_NUMBER";
    public static final String INTENT_SEARCH_TEXT = "INTENT_SEARCH_TEXT";

    public static final int RESULT_IS_UPDATE = RESULT_FIRST_USER;


    //フィールド
    //--------------------------------------------------------------------------
    private ViewPager m_viewPager;
    private SqlAccessManager m_sqlManager = null;

    private String m_title = "";
    private String m_selectModelNumber = "";
    private String m_searchText = "";
    private boolean m_isPageScroll = false;

    private List<CardInfo> m_cardInfoList = null;

    //メソッド
    //--------------------------------------------------------------------------
    /**
     * 画面起動時処理
     * @param savedInstanceState 保存データ
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info);

        //初期表示位置の取得
        if (savedInstanceState != null) {
            m_selectModelNumber = savedInstanceState.getString(INTENT_MODEL_NUMBER);
            m_searchText = savedInstanceState.getString(INTENT_SEARCH_TEXT);
        }
        else {
            Intent intent = getIntent();
            m_selectModelNumber = intent.getStringExtra(INTENT_MODEL_NUMBER);
            m_searchText = intent.getStringExtra(INTENT_SEARCH_TEXT);
        }

        restoreActionBar();
        PSDebug.d("m_selectModelNumber=" + m_selectModelNumber);
        PSDebug.d("m_searchText=" + m_searchText);
        m_sqlManager = new SqlAccessManager(this);
        m_viewPager = (ViewPager)findViewById(R.id.pager);
        m_viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            /**
             * ページ選択時処理
             * @param position 現在の位置
             */
            @Override
            public void onPageSelected(int position) {
                m_isPageScroll = false;
                changePage(position);
            }

            /**
             * スクロール状態が変更した時
             * @param state スクロール状態
             */
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:   //ドラッグ中
                        m_isPageScroll = true;
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:   //止まりかけ中
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:       //停止中
                        m_isPageScroll = false;
                        break;
                }
            }
        });
        m_isPageScroll = false;

        ReadCardListAsyncTask readTask = new ReadCardListAsyncTask();
        readTask.execute(m_searchText);
    }

    /**
     * 状態保存を行う
     * @param outState 保存先データ
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PSDebug.d("call");

        outState.putString(INTENT_MODEL_NUMBER, m_selectModelNumber);
        outState.putString(INTENT_SEARCH_TEXT, m_searchText);
    }

    /**
     * タイトル部をカード情報からセットする
     * @param cardInfo カードデータ
     */
    private void setTitleFromCardInfo(CardInfo cardInfo)
    {
        m_title = "[" + cardInfo.getRarity() + "] " + cardInfo.getModelNumber();
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
     * ページ選択が行われた時
     * @param position ページ番号
     */
    private void changePage(int position) {
        //タイトルを変更
        CardInfo cardInfo = m_cardInfoList.get(position);
        setTitleFromCardInfo(cardInfo);

        //現在の選択状態
        m_selectModelNumber = cardInfo.getModelNumber();
        PSDebug.d("m_selectModelNumber=" + m_selectModelNumber);
    }

    /**
     * カード詳細一覧画面からカード情報をダウンロードする
     */
    public class ReadCardListAsyncTask extends AsyncTask<String, String, ArrayList<CardInfo>> {
        /**
         * 処理開始
         * @param text 検索文字列（最初の項目のみ使用する）
         * @return カードリスト
         */
        @Override
        protected ArrayList<CardInfo> doInBackground(String... text) {
            String searchText = text[0];
            ArrayList<CardInfo> cardInfoList = new ArrayList<CardInfo>();
            if (searchText == null || searchText.equals("")) {
                cardInfoList.addAll(m_sqlManager.selectCardInfoAll());
            }
            else {
                cardInfoList.addAll(m_sqlManager.selectCardInfo(searchText));
            }
            return cardInfoList;
        }

        /**
         * 完了処理
         * @param result カードリスト
         */
        @Override
        protected void onPostExecute(ArrayList<CardInfo> result) {
            m_cardInfoList = result;
            if (result.size() == 0) {
                PSUtils.toast(CardInfoActivity.this, "データが見つかりませんでした");
                return;
            }

            //アダプターにセット
            setTitleFromCardInfo(m_cardInfoList.get(0));
            m_viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), result));
            m_viewPager.setCurrentItem(getCardInfoFromModelName(m_selectModelNumber));
        }

        /**
         * 型番名からカード情報を検索してその位置を返す
         * @param modelName カード型番
         * @return 見つかった場合はリストの位置（見つからなかったときは0を返す）
         */
        private int getCardInfoFromModelName(String modelName)
        {
            PSDebug.d("modelName=" + modelName);
            for (int i=0; i<m_cardInfoList.size(); i++)
            {
                if (m_cardInfoList.get(i).getModelNumber().equals(modelName))
                {
                    return i;
                }
            }

            //見つからなかったのでデフォルト位置を返す
            return 0;
        }
    }

    /**
     * 横ページアダプター
     */
    public class PageAdapter extends FragmentStatePagerAdapter {
        //フィールド
        //-----------------------------------------------
        private List<CardInfo> m_cardList;

        //メソッド
        //-----------------------------------------------
        /**
         * コンストラクタ
         * @param fm フラグメント管理オブジェクト
         * @param cardList カードリスト
         */
        public PageAdapter(FragmentManager fm, ArrayList<CardInfo> cardList) {
            super(fm);

            m_cardList = cardList;
        }

        /**
         * カードアイテムを取得する
         * @param position 位置
         * @return カード情報（CardInfo）
         */
        @Override
        public Fragment getItem(int position) {
            return CardInfoFragment.newInstance(m_cardList.get(position).getModelNumber());
        }

        /**
         * リスト数を返す
         * @return リスト数
         */
        @Override
        public int getCount() {
            return m_cardList.size();
        }
    }


    //メニュー処理
    //-------------------------------------------------------
    /**
     * メニューオープン処理
     * @param menu メニューオブジェクト
     * @return 使用したかどうか
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_info, menu);
        /*
        MenuItem m_addMenuItem = menu.getItem(R.id.action_add);
        MenuItem m_shareMenuItem = menu.getItem(R.id.action_add);
        */
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * メニュー選択時処理
     * @param item 選択アイテム
     * @return メニューを選択したかどうか
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (m_isPageScroll) {
            //スクロール中は選択できないようにする
            PSDebug.d("スクロール禁止中！");
            return true;
        }

        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                showSelectDeckDir();
                return true;
            case R.id.action_share:
                sendIntentSearch();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * マイデッキリストを表示し追加を行うデッキを選択する
     */
    private void showSelectDeckDir() {
        final List<DeckDirInfo> dirList = m_sqlManager.selectDeckDirInfoAll();
        List<String> dirTitleList = new ArrayList<String>();
        for (DeckDirInfo dirInfo : dirList)
        {
            String dirName = dirInfo.getName();
            List<DeckItemInfo> itemList = m_sqlManager.selectDeckItemInfo(dirInfo.getId(), m_selectModelNumber);
            if (itemList.size() > 0) {
                dirName = dirName + "[x" + itemList.size() + "]";
            }
            dirTitleList.add(dirName);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("デッキ登録")
                .setItems(dirTitleList.toArray(new CharSequence[1]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        DeckDirInfo selectDir = dirList.get(pos);
                        showSelectDeckDirAddCount(selectDir);
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //何もしない
                    }
                })
                .create();
        dialog.show();
    }

    /**
     * デッキに格納する数を選択するダイアログを表示する
     * @param selectDir 編集するデッキ情報
     */
    private void showSelectDeckDirAddCount(final DeckDirInfo selectDir) {
        final CharSequence[] selectCountList = new CharSequence[] {
                "すべて登録解除",
                "1枚登録",
                "2枚登録",
                "3枚登録",
                "4枚登録",
        };

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(selectDir.getName() + "に登録する枚数")
                .setItems(selectCountList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        //配列の位置がそのまま枚数になる
                        addDeckItem(selectDir, pos);
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //何もしない
                    }
                })
                .create();
        dialog.show();
    }

    /**
     * 指定したデッキに現在のアイテムを追加する
     * @param deckDirInfo デッキ情報
     * @param count 追加個数（0を指定すると削除だけ行う）
     */
    private void addDeckItem(DeckDirInfo deckDirInfo, int count) {
        //いったん全部削除する
        this.setResult(CardInfoActivity.RESULT_IS_UPDATE);
        m_sqlManager.deleteDeckItemInfo(deckDirInfo.getId(), m_selectModelNumber);
        if (count == 0) {
            PSUtils.toast(this, deckDirInfo.getName() + "への登録を解除しました");
            return; //削除だけを行い終了
        }

        List<DeckItemInfo> itemInfoList = new ArrayList<DeckItemInfo>();
        for (int i=0; i<count; i++) {
            DeckItemInfo itemInfo = new DeckItemInfo();
            itemInfo.setDeckId(deckDirInfo.getId());
            itemInfo.setModelNumber(m_selectModelNumber);
            itemInfoList.add(itemInfo);
        }
        if (m_sqlManager.insertDeckItemInfo(itemInfoList))
        {
            PSUtils.toast(this, deckDirInfo.getName() +"に" + m_selectModelNumber + "を"+count+"枚登録しました");
        }
        else
        {
            PSUtils.toast(this, "登録に失敗しました");
        }
    }

    /**
     * 現在のカード情報からWEB検索を行う
     */
    private void sendIntentSearch()
    {
        CardInfo cardInfo = m_sqlManager.selectCardInfoFromModelNumber(m_selectModelNumber);
        if (cardInfo == null) {
            PSUtils.toast(this, "選択しているカード情報見つかりませんでした");
            return;
        }
        String searchText = cardInfo.getName();
        if (EnvOption.getCardInfoShareAddWixoss(this)) {
            searchText = "WIXOSS " + searchText;
        }

        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, searchText);
        startActivity(intent);
    }
}
