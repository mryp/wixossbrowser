package net.poringsoft.wixossbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.poringsoft.wixossbrowser.data.CardHtmlParser;
import net.poringsoft.wixossbrowser.data.CardInfo;
import net.poringsoft.wixossbrowser.data.CardListHtmlParser;
import net.poringsoft.wixossbrowser.data.ProductInfo;
import net.poringsoft.wixossbrowser.data.ProductListHtmlParser;
import net.poringsoft.wixossbrowser.data.SqlAccessManager;
import net.poringsoft.wixossbrowser.utils.PSDebug;
import net.poringsoft.wixossbrowser.utils.PSUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * データ更新取得用Fragment
 * Created by mry on 2014/05/05.
 */
public class DataUpdateFragment extends ListFragment {
    //定数
    //---------------------------------------------------------------------
    public static final String ARG_UPDATE_FLAG = "ARG_UPDATE_FLAG";

    //フィールド
    //---------------------------------------------------------------------
    private List<ProductInfo> m_productInfoList = new ArrayList<ProductInfo>();
    private ProgressDialog m_progress = null;
    private boolean m_quiting = false;	//終了フラグ（true時は取得処理を終了させる）
    private boolean m_isUpdate = false;

    //メソッド
    //---------------------------------------------------------------------
    /**
     * インスタンスを生成する
     * @return インスタンス
     */
    public static DataUpdateFragment newInstance() {
        DataUpdateFragment fragment = new DataUpdateFragment();
        PSDebug.d("call");
        return fragment;
    }

    /**
     * コンストラクタ
     * 基本的には呼び出さない。
     * newInstanceを使用すること
     */
    public DataUpdateFragment() {
    }

    /**
     * ビューの生成
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PSDebug.d("call");
        return inflater.inflate(R.layout.fragment_data_update, container, false);
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

        if (savedInstanceState != null)
        {
            PSDebug.d("savedInstanceState=" + savedInstanceState);
            m_isUpdate = savedInstanceState.getBoolean(ARG_UPDATE_FLAG);
        }
        else
        {
            setEmptyText("読み込み中...");
            m_isUpdate = false;
            DownloadProductAsyncTask productAsycTask = new DownloadProductAsyncTask();
            productAsycTask.execute(getString(R.string.url_official_product_list));
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

        outState.putBoolean(ARG_UPDATE_FLAG, m_isUpdate);
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
     * メニュー生成時処理
     * @param menu メニューオブジェクト
     * @param inflater 親画面操作
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.data_update, menu);
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
            case R.id.action_share_official:
                startJumpWebSite(getString(R.string.url_official_product_list));
                return true;
        }
        return super.onOptionsItemSelected(item);
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
     * 商品リスト一覧URLを非同期でダウンロードする
     */
    public class DownloadProductAsyncTask extends AsyncTask<String, String, List<ProductInfo>> {
        /**
         * 処理開始
         * @param strings ダウンロードURLリスト（実際には先頭しか使用しない）
         * @return ダウンロード後解析した結果データリスト
         */
        @Override
        protected List<ProductInfo> doInBackground(String... strings) {
            String baseUrl = strings[0];
            return ProductListHtmlParser.Parse(baseUrl);
        }

        /**
         * 完了処理
         * @param productInfos 取得完了したデータリスト
         */
        @Override
        protected void onPostExecute(List<ProductInfo> productInfos) {
            if (productInfos == null)
            {
                setEmptyText("データのダウンロードに失敗しました");
                return;
            }
            if (productInfos.size() == 0)
            {
                setEmptyText("データが1件もありません");
                return;
            }
            if (getActivity() == null)
            {
                setEmptyText("画面生成に失敗しました。アプリを再起動してください");
                return;
            }

            m_productInfoList = productInfos;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
            for(ProductInfo info : productInfos)
            {
                adapter.add(info.getTitle());
            }
            setListAdapter(adapter);
        }
    }

    /**
     * リストアイテムを選択したとき
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ProductInfo selectInfo = m_productInfoList.get(position);

        final String url = selectInfo.getUrl();
        String message = selectInfo.getTitle() + " のカード情報のダウンロードを行いますか？\n"
                + "※ダウンロードに時間がかかる場合があります\n"
                + "※カード画像が既に存在する場合は画像をダウンロードしません\n";
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("確認")
                .setMessage(message)
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startDataDownload(url);
                        m_isUpdate = true;
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
     * ダウンロード処理を開始する
     * @param url ダウンロード処理を行うカード一覧記載URL
     */
    private void startDataDownload(String url)
    {
        getActivity().setResult(DataUpdateActivity.RESULT_IS_UPDATE, null);
        DownloadCardAsyncTask cardAsyncTask = new DownloadCardAsyncTask();
        cardAsyncTask.execute(url);
    }

    /**
     * カード詳細一覧画面からカード情報をダウンロードする
     */
    public class DownloadCardAsyncTask extends AsyncTask<String, String, String> {
        private SqlAccessManager m_sqlManager = null;

        /**
         * 更新前処理
         */
        @Override
        protected void onPreExecute() {
            m_quiting = false;
            m_sqlManager = new SqlAccessManager(getActivity());
            ProgressDialog prog = initProgressDlg(new ProgressDialog(getActivity()), 1);
            prog.setTitle("データ更新");
            prog.setMessage("カード情報URLを取得中...");
            prog.show();
        }

        /**
         * 処理開始
         * @param urls URL一覧（最初の項目のみ使用）
         * @return ダウンロード結果文字列（成功時は空文字を返す）
         */
        @Override
        protected String doInBackground(String... urls) {
            String startUrl = urls[0];
            List<String> cardUrlList = CardListHtmlParser.Parse(startUrl);
            if (cardUrlList == null || cardUrlList.size() == 0)
            {
                return "カード一覧情報の取得に失敗しました";
            }
            if (m_quiting)
            {
                return "キャンセルされました";
            }

            int progressCount = 0;
            ArrayList<CardInfo> cardInfoList = new ArrayList<CardInfo>();
            for (String cardUrl : cardUrlList)
            {
                publishProgress("カード情報取得中...", String.valueOf(progressCount), String.valueOf(cardUrlList.size()));
                if (m_quiting)
                {
                    return "キャンセルされました(残り="+String.valueOf(cardUrlList.size()-progressCount)+"枚)";
                }

                CardInfo cardInfo = CardHtmlParser.Parse(cardUrl);
                if (cardInfo != null)
                {
                    //取得成功
                    cardInfoList.add(cardInfo);
                }

                progressCount++;
            }

            //DB更新
            if (cardInfoList.size() > 0) {
                m_sqlManager.insertCardInfoList(cardInfoList);
            }

            if (progressCount != cardUrlList.size())
            {
                return "一部カードデータ取得失敗("+String.valueOf(cardUrlList.size()-progressCount)+"枚)";
            }
            publishProgress("カード情報取得処理終了", String.valueOf(cardUrlList.size()), String.valueOf(cardUrlList.size()));
            return "";
        }

        /**
         * 処理更新状況
         * @param values 更新内容（[0]=テキスト、[1]=現在の処理個数、[2]=全体の処理対象個数
         */
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            String text = values[0];
            int num = PSUtils.tryParseInt(values[1], 0);
            int maxNum = PSUtils.tryParseInt(values[2], 1);
            m_progress.setMessage(text);
            m_progress.setProgress(num);
            m_progress.setMax(maxNum);
        }

        /**
         * 完了処理
         * @param result 処理結果情報文字列（正常時は空）
         */
        @Override
        protected void onPostExecute(String result) {
            closeProgressDlg();
            if (result.equals("")) {
                PSUtils.toast(getActivity(), "処理が完了しました");
            }
            else {
                PSUtils.toast(getActivity(), result);
            }
        }
    }

    /**
     * ダウンロード中ダイアログを表示する
     * ダイアログ表示中は画面回転・スリープを禁止する
     * @param progress ダイアログオブジェクト
     * @param count 最大数
     * @return ダイアログオブジェクト
     */
    private ProgressDialog initProgressDlg(ProgressDialog progress, int count)
    {
        PSUtils.DisplayTurnEnable(getActivity(), true);
        m_progress = progress;
        m_progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        m_progress.setIndeterminate(false);
        m_progress.setCancelable(false);
        m_progress.setMax(count);
        m_progress.setProgress(0);
        m_progress.setTitle(" ");
        m_progress.setMessage(" ");
        m_progress.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_quiting = true;	//キャンセルフラグを立てる
            }
        });

        return m_progress;
    }

    /**
     * ダイアログを閉じる
     * 画面回転・スリープの禁止状態を解除する
     */
    private void closeProgressDlg()
    {
        if (m_progress == null)
        {
            return;	//まだ開いてすらいないので何もしない
        }
        try
        {
            if (m_progress.isShowing())
            {
                m_progress.setCancelable(true);
                m_progress.dismiss();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            PSDebug.d("ダイアログクローズ失敗（すでに閉じられている？）");
        }

        PSUtils.DisplayTurnEnable(getActivity(), false);
    }
}
