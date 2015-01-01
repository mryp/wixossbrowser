package net.poringsoft.wixossbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import net.poringsoft.wixossbrowser.data.DeckDirInfo;
import net.poringsoft.wixossbrowser.data.DeckOutputManager;
import net.poringsoft.wixossbrowser.data.SqlAccessManager;
import net.poringsoft.wixossbrowser.utils.PSDebug;
import net.poringsoft.wixossbrowser.utils.PSUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * デッキデータ編集用Fragment
 * Created by mry on 2014/05/04.
 */
public class DeckDirFragment extends ListFragment {
    //定数
    //---------------------------------------------------------------------
    public static final String ARG_UPDATE_FLAG = "ARG_UPDATE_FLAG";

    //フィールド
    //---------------------------------------------------------------------
    private SqlAccessManager m_sqlManager = null;
    private List<DeckDirInfo> m_dataList = new ArrayList<DeckDirInfo>();
    private boolean m_isUpdate = false;

    //メソッド
    //---------------------------------------------------------------------
    /**
     * インスタンスを生成する
     * @return インスタンス
     */
    public static DeckDirFragment newInstance() {
        DeckDirFragment fragment = new DeckDirFragment();
        PSDebug.d("call");
        return fragment;
    }

    /**
     * コンストラクタ
     * 基本的には呼び出さない。
     * newInstanceを使用すること
     */
    public DeckDirFragment() {
    }

    /**
     * ビューの生成
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PSDebug.d("call");
        return inflater.inflate(R.layout.fragment_deck_dir, container, false);
    }

    /**
     * 画面表示完了
     * @param activity 親画面
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PSDebug.d("call");
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
    }

    /**
     * Activity生成完了
     * @param savedInstanceState セーブデータ
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        //初期化
        m_sqlManager = new SqlAccessManager(getActivity());

        if (savedInstanceState != null)
        {
            PSDebug.d("savedInstanceState=" + savedInstanceState);
            m_isUpdate = savedInstanceState.getBoolean(ARG_UPDATE_FLAG);
        }
        else
        {
            setEmptyText("読み込み中...");
            m_isUpdate = false;
            startAsyncReadAdapter();
        }

        //長押し処理追加
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return onListItemLongClick(i);
            }
        });
    }

    /**
     * データの保存
     * @param outState 保存対象データ
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PSDebug.d("call");

        outState.putBoolean(ARG_UPDATE_FLAG, m_isUpdate);
    }

    /**
     * メニュー設定
     * @param menu メニューアイテム
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.deck_dir, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
            case R.id.action_new_deck:
                showCreateNewDeckDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 新しくデッキを作成するダイアログを表示する
     */
    private void showCreateNewDeckDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View inputView = factory.inflate(R.layout.input_new_deck, null);
        if (inputView == null) {
            return;
        }
        final EditText editNameText = (EditText)inputView.findViewById(R.id.editNameText);
        final EditText editMemoText = (EditText)inputView.findViewById(R.id.editMemoText);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("デッキ作成")
                .setView(inputView)
                .setPositiveButton("作成する", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (editNameText.getText() != null && editMemoText.getText() != null) {
                            insertDeckDirInfo(editNameText.getText().toString().trim()
                                    , editMemoText.getText().toString().trim());
                        }
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
     * 指定したデッキを編集するダイアログを表示する
     * @param selectDeckInfo 選択されたデッキ情報
     */
    private void showCreateEditDeckDialog(final DeckDirInfo selectDeckInfo) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View inputView = factory.inflate(R.layout.input_new_deck, null);
        if (inputView == null) {
            return;
        }
        final EditText editNameText = (EditText)inputView.findViewById(R.id.editNameText);
        editNameText.setText(selectDeckInfo.getName());
        final EditText editMemoText = (EditText)inputView.findViewById(R.id.editMemoText);
        editMemoText.setText(selectDeckInfo.getMemo());

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("デッキ作成")
                .setView(inputView)
                .setPositiveButton("作成する", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (editNameText.getText() != null && editMemoText.getText() != null) {
                            insertDeckDirInfo(editNameText.getText().toString().trim()
                                    , editMemoText.getText().toString().trim()
                                    , selectDeckInfo.getId(), selectDeckInfo.getIndex());
                        }
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
     * デッキ情報を作成・更新する（新規用）
     */
    private void insertDeckDirInfo(String name, String memo) {
        insertDeckDirInfo(name, memo, -1, 0);
    }

    /**
     * デッキ情報を作成・更新する（新規・更新用）
     * @param name デッキ名
     * @param memo メモ（空文字可能）
     * @param id デッキID（新規のときは-1を指定）
     * @param index 並び位置（新しい値をセットする場合は0を指定）
     */
    private void insertDeckDirInfo(String name, String memo, int id, int index) {
        if (name.equals("")) {
            PSUtils.toast(getActivity(),  "デッキ名が入力されていません");
            return;
        }
        if (id == -1) {
            //新規のときは既にあるかどうかをチェック
            for (DeckDirInfo info : m_dataList) {
                if (info.getName().equals(name)) {
                    PSUtils.toast(getActivity(), info.getName() + " はすでに登録されています");
                    return;
                }
            }
        }

        //名前とメモをセット
        DeckDirInfo newInfo = DeckDirInfo.newInstance();
        newInfo.setId(id);
        newInfo.setName(name);
        if (!memo.equals("")) {
            newInfo.setMemo(memo);
        }

        //インデックスをセット
        if (index == 0) {
            if (m_dataList.size() != 0) {
                DeckDirInfo lastInfo = m_dataList.get(m_dataList.size() - 1);
                index = lastInfo.getIndex() + 1;    //最後+1を追加
            }
        }
        newInfo.setIndex(index);

        m_sqlManager.insertDeckDirInfo(newInfo);
        startAsyncReadAdapter();
        setResultUpdateFlag();
    }

    /**
     * 更新フラグをONにする
     */
    private void setResultUpdateFlag() {
        m_isUpdate = true;
        getActivity().setResult(DeckDirActivity.RESULT_IS_UPDATE);
    }

    /**
     * リストが1件もなかったときの表示文字の設定
     * @param text 表示文字列
     */
    @Override
    public void setEmptyText(CharSequence text) {
        TextView tv = (TextView)getListView().getEmptyView();
        if (tv != null) {
            tv.setText(text);
        }
    }

    /**
     * リストアイテムをクリックしたとき
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showItemActionMenu(m_dataList.get(position));
    }

    /**
     * リストアイテムを長押ししたとき
     * @param position 行位置
     * @return 処理を行ったかどうか
     */
    public boolean onListItemLongClick(final int position) {
        showItemActionMenu(m_dataList.get(position));
        return true;
    }

    /**
     * アイテム項目の操作メニューを表示する
     * @param deckDirInfo 捜査対象デッキ
     */
    private void showItemActionMenu(final DeckDirInfo deckDirInfo) {
        final CharSequence[] CLICK_LIST_ITEMS = new CharSequence[] {
                "共有",
                "編集",
                "削除",
        };
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("データ編集")
                .setItems(CLICK_LIST_ITEMS, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        if (CLICK_LIST_ITEMS[pos].equals("編集")) {
                            showCreateEditDeckDialog(deckDirInfo);
                        }
                        else if (CLICK_LIST_ITEMS[pos].equals("削除")) {
                            showDeleteItemDialog(deckDirInfo);
                        }
                        else if (CLICK_LIST_ITEMS[pos].equals("共有")) {
                            shareDeckInfo(deckDirInfo);
                        }
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
     * データを共有する
     * @param deckDirInfo デッキ情報
     */
    private void shareDeckInfo(DeckDirInfo deckDirInfo) {
        DeckOutputManager output = new DeckOutputManager(getActivity());
        if (!output.loadCardInfo(deckDirInfo.getId()))
        {
            PSUtils.toast(getActivity(), "カードデータがありません");
            return;
        }

        String subject = deckDirInfo.getName();
        String bodyText = output.createSendShareTextData();
        if (!deckDirInfo.getMemo().equals("")) {
            bodyText += "\nMEMO: " + deckDirInfo.getMemo();
        }
        shareText("デッキ共有", subject, bodyText);
    }

    /**
     * 指定したテキストデータを共有インテントとして投げる
     * @param dialogTitle ダイアログ用タイトル
     * @param subject メールアカウント時のサブジェクト
     * @param text 本文
     */
    private void shareText(String dialogTitle, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, dialogTitle));
    }

    /**
     * 削除確認ダイアログを表示してから削除を行う
     * @param deckDirInfo 削除を行うデッキ情報
     */
    private void showDeleteItemDialog(final DeckDirInfo deckDirInfo) {
        if (m_dataList.size() == 1) {
            PSUtils.toast(getActivity(), "データが一つしかないため削除できません");
            return;
        }

        String message = deckDirInfo.getName() + "を削除しますか？\n"
                + "削除を実行するするとデッキに含まれているカードデータの登録情報も解除されます";
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("削除の確認")
                .setMessage(message)
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        m_sqlManager.deleteDeckDirInfo(deckDirInfo);
                        startAsyncReadAdapter();
                        setResultUpdateFlag();
                        PSUtils.toast(getActivity(), deckDirInfo.getName() + "を削除しました");
                    }
                })
                .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //何もしない
                    }
                })
                .create();
        dialog.show();
    }

    /**
     * 非同期によるリストアダプターの読み込み開始
     */
    private void startAsyncReadAdapter() {
        ReadListAdapterAsyncTask task = new ReadListAdapterAsyncTask();
        task.execute("");
    }

    /**
     * デッキデータ一覧を取得しアダプターにセットする
     */
    public class ReadListAdapterAsyncTask extends AsyncTask<String, String, List<DeckDirInfo>> {
        /**
         * 更新前処理
         */
        @Override
        protected void onPreExecute() {
            if (m_sqlManager == null) {
                m_sqlManager = new SqlAccessManager(getActivity());
            }
        }

        /**
         * 処理開始
         * @param text 未使用...
         * @return デッキ情報リスト
         */
        @Override
        protected List<DeckDirInfo> doInBackground(String... text) {
            return m_sqlManager.selectDeckDirInfoAll();
        }

        /**
         * 完了処理
         * @param result デッキ情報リスト
         */
        @Override
        protected void onPostExecute(List<DeckDirInfo> result) {
            m_dataList = result;
            if (result.size() == 0)
            {
                String messageText = "データが見つかりません";
                setEmptyText(messageText);
            }

            PSDebug.d("getActivity=" + getActivity());
            if (getActivity() != null) {
                setListAdapter(new DeckDirListAdapter(getActivity(), result));
            }
        }
    }
}
