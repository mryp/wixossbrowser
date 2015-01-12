package net.poringsoft.wixossbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.poringsoft.wixossbrowser.data.CardInfo;
import net.poringsoft.wixossbrowser.data.SqlAccessManager;
import net.poringsoft.wixossbrowser.data.SqlSelectHelper;
import net.poringsoft.wixossbrowser.utils.PSDebug;
import net.poringsoft.wixossbrowser.utils.PSUtils;

import java.util.ArrayList;

/**
 * カード一覧表示Fragment
 * Created by mry on 2014/05/02.
 */
public class CardListFragment extends ListFragment {
    //定数
    //---------------------------------------------------------------------
    private static final String ARG_TITLE = "title";
    private static final String ARG_SEARCH_TEXT = "searchText";


    //フィールド
    //---------------------------------------------------------------------
    private CardListAdapter m_adapter = null;
    private ListView m_listView = null;
    private String m_searchText = "";
    private String m_title = "";
    private SqlAccessManager m_sqlManager = null;

    //メソッド
    //---------------------------------------------------------------------
    /**
     * インスタンスを生成する
     * @return インスタンス
     */
    public static CardListFragment newInstance(String title, String searchText) {
        CardListFragment fragment = new CardListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_SEARCH_TEXT, searchText);
        fragment.setArguments(args);
        PSDebug.d("args title=" + args);
        PSDebug.d("args searchText=" + searchText);
        return fragment;
    }

    public String getArgTitle() {
        return m_title;
    }

    public String getArgSearchText() {
        return m_searchText;
    }

    /**
     * コンストラクタ
     * 基本的には呼び出さない。
     * newInstanceを使用すること
     */
    public CardListFragment() {
    }

    /**
     * Activityに関連付けられた時
     * Activity関連チェックを行う
     * @param activity 親画面
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PSDebug.d("activity=" + activity);
    }

    /**
     * Fragmentが生成された時
     * Fragment操作データの構築を行う
     * @param savedInstanceState セーブデータ
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PSDebug.d("savedInstanceState=" + savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Fragmentのビュー要求
     * Fragmentで表示するビューの生成を行う
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PSDebug.d("container=" + container);
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    /**
     * ActivityのonCreate完了通知
     * 親Activityの生成完了後に必要なデータの生成
     * @param savedInstanceState セーブデータ
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PSDebug.d("getActivity=" + getActivity());

        //初期化
        m_sqlManager = new SqlAccessManager(getActivity());
        m_listView = getListView();

        //引数処理
        Bundle argment = this.getArguments();
        if (argment != null) {
            PSDebug.d("argment復元");
            m_searchText = argment.getString(ARG_SEARCH_TEXT);
            m_title = argment.getString(ARG_TITLE);
        }
        else if (savedInstanceState != null) {
            PSDebug.d("savedInstanceState復元");
            m_searchText = savedInstanceState.getString(ARG_SEARCH_TEXT);
            m_title = savedInstanceState.getString(ARG_TITLE);
        }
        startAsyncReadAdapter();

        PSDebug.d("title=" + m_title);
        PSDebug.d("searchText=" + m_searchText);
        ((MainActivity)getActivity()).onSectionAttached(m_title, m_searchText);

        //長押し処理追加
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return onListItemLongClick(i);
            }
        });
    }

    /**
     * 非同期によるリストアダプターの読み込み開始
     */
    private void startAsyncReadAdapter() {
        ReadCardListAsyncTask task = new ReadCardListAsyncTask();
        task.execute(m_searchText);
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
        outState.putString(ARG_TITLE, m_title);
    }

    /**
     * 復旧時
     */
    @Override
    public void onResume() {
        super.onResume();

        PSDebug.d("call");
    }

    /**
     * 停止時
     */
    @Override
    public void onPause() {
        super.onPause();

        PSDebug.d("call");
    }

    /**
     * リストが1件もなかったときの表示文字の設定
     * @param text 表示文字列
     */
    @Override
    public void setEmptyText(CharSequence text) {
        TextView tv = (TextView)m_listView.getEmptyView();
        if (tv != null) {
            tv.setText(text);
        }
    }

    /**
     * リストアイテムを選択したとき
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (l.getHeaderViewsCount() > 0) {
            position = position - getListView().getHeaderViewsCount();
        }
        if (position < 0) {
            return; //ヘッダ部なので何もしない
        }
        CardInfo selectInfo = (CardInfo)m_adapter.getItem(position);

        //カード情報詳細画面へ遷移する
        Intent intent = new Intent(getActivity(), CardInfoActivity.class);
        intent.putExtra(CardInfoActivity.INTENT_MODEL_NUMBER, selectInfo.getModelNumber());
        intent.putExtra(CardInfoActivity.INTENT_SEARCH_TEXT, m_searchText);
        getActivity().startActivityForResult(intent, MainActivity.REQ_CODE_CARD_INFO);
    }

    /**
     * リストアイテム項目を長押ししたとき
     * @param pos 位置
     * @return 使用したかどうか
     */
    public boolean onListItemLongClick(int pos) {
        if (!SqlSelectHelper.isDeckSearch(m_searchText)) {
            //デッキリスト以外は処理しない
            return false;
        }
        final int deckDirId = SqlSelectHelper.getDeckDirIdFromSearchText(m_searchText);
        if (deckDirId < 0) {
            //デッキ番号が取得できなかったので処理できない
            return false;
        }
        if (getListView().getHeaderViewsCount() > 0)
        {
            pos = pos - getListView().getHeaderViewsCount();
        }

        final int selectPos = pos;
        String message = "この項目を"+m_title+"から登録解除しますか？";
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("登録解除の確認")
                .setMessage(message)
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        CardInfo selectInfo = (CardInfo) m_adapter.getItem(selectPos);
                        if (m_sqlManager.deleteDeckItemInfoLimitOne(deckDirId, selectInfo.getModelNumber())) {
                            PSUtils.toast(getActivity(), selectInfo.getModelNumber() + "を登録解除しました");
                        }
                        else {
                            PSUtils.toast(getActivity(), "登録解除に失敗しました");
                        }
                        startAsyncReadAdapter();
                    }
                })
                .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //何もしない
                    }
                })
                .create();
        dialog.show();
        return true;
    }

    /**
     * カード詳細一覧画面からカード情報をダウンロードする
     */
    public class ReadCardListAsyncTask extends AsyncTask<String, String, ArrayList<CardInfo>> {
        private String m_taskSearchText = "";
        
        /**
         * 更新前処理
         */
        @Override
        protected void onPreExecute() {
            if (m_sqlManager == null) {
                m_sqlManager = new SqlAccessManager(getActivity());
            }
            setListAdapter(null);
        }

        /**
         * 処理開始
         * 空文字を指定するとすべてのデータを取得する
         * @param text 検索時文字列（最初の項目のみ使用）
         * @return 検索により取得したカードデータ
         */
        @Override
        protected ArrayList<CardInfo> doInBackground(String... text) {
            String searchText = text[0];
            m_taskSearchText = searchText;
            PSDebug.d("searchText=" + searchText);
            ArrayList<CardInfo> cardInfoList = new ArrayList<CardInfo>();
            try
            {
                if (searchText == null || searchText.equals("")) {
                    cardInfoList.addAll(m_sqlManager.selectCardInfoAll());
                }
                else {
                    cardInfoList.addAll(m_sqlManager.selectCardInfo(searchText));
                }
            }
            catch (Exception e)
            {
                PSDebug.d("カード検索処理例が発生 e=" + e.getMessage());
                cardInfoList = null;
            }

            return cardInfoList;
        }

        /**
         * 完了処理
         * @param result カードデータリスト
         */
        @Override
        protected void onPostExecute(ArrayList<CardInfo> result) {
            if (result == null)
            {
                String messageText = "検索処理に失敗しました";
                setEmptyText(messageText);
            }
            else if (result.size() == 0)
            {
                String messageText = "データが見つかりません";
                if (m_taskSearchText.startsWith(SqlSelectHelper.SELECT_CARD_CMD_ABILITY)) {
                    messageText = "新バージョン用のカードデータがありません\nメニュー→「データ取得」を選択してください";
                }
                else if (m_sqlManager.selectCardInfoFirstItem() == null) {
                    messageText = "カードデータがありません\nメニュー→「データ取得」を選択してください";
                }
                setEmptyText(messageText);
            }

            PSDebug.d("getActivity=" + getActivity());
            if (getActivity() != null && getListView() != null) {
                m_adapter = new CardListAdapter(getActivity(), result);
                ListView listView = getListView();
                if (listView.getHeaderViewsCount() == 0) {
                    LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View headerView = inflater.inflate(R.layout.main_list_header, null);
                    TextView lrigTextView = (TextView)headerView.findViewById(R.id.lrigTextView);
                    TextView shiguniTextView = (TextView)headerView.findViewById(R.id.shiguniTextView);
                    lrigTextView.setText(getHeaderTextLrig(result));
                    shiguniTextView.setText(getHeaderTextShiguni(result));
                    listView.addHeaderView(headerView);
                }

                setListAdapter(m_adapter);
            }
        }

        private String getHeaderTextLrig(ArrayList<CardInfo> cardInfoLsit) {
            int lrig = 0;
            int arts = 0;

            for (CardInfo info : cardInfoLsit) {
                int registCount = info.getDeckRegistCount();
                if (registCount == 0)
                {
                    registCount = 1;    //マイデッキではないときは1枚固定
                }

                if (info.getKind().equals("ルリグ"))
                {
                    lrig += registCount;
                }
                else if (info.getKind().equals("アーツ"))
                {
                    arts += registCount;
                }
            }

            return "ルリグ×" + lrig + " アーツ×" + arts;
        }

        private String getHeaderTextShiguni(ArrayList<CardInfo> cardInfoLsit) {
            int shiguni = 0;
            int spell = 0;

            for (CardInfo info : cardInfoLsit) {
                int registCount = info.getDeckRegistCount();
                if (registCount == 0)
                {
                    registCount = 1;    //マイデッキではないときは1枚固定
                }

                if (info.getKind().equals("シグニ"))
                {
                    shiguni += registCount;
                }
                else if (info.getKind().equals("スペル"))
                {
                    spell += registCount;
                }
            }

            return "シグニ×" + shiguni + " スペル×" + spell;
        }
    }
}