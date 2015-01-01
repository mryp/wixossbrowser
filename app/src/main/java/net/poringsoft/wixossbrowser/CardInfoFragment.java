package net.poringsoft.wixossbrowser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.poringsoft.wixossbrowser.data.CardFaqInfo;
import net.poringsoft.wixossbrowser.data.CardInfo;
import net.poringsoft.wixossbrowser.data.EnvPath;
import net.poringsoft.wixossbrowser.utils.ColorUtils;
import net.poringsoft.wixossbrowser.data.SqlAccessManager;
import net.poringsoft.wixossbrowser.utils.PSDebug;
import net.poringsoft.wixossbrowser.utils.PSUtils;

import java.io.File;

/**
 * カード表示詳細表示Fragment
 * Created by mry on 2014/05/01.
 */
public class CardInfoFragment extends Fragment {
    //定数
    //---------------------------------------------------------------------
    private static final String ARG_SELECT_MODEL_NUMBER = "model_number";


    //フィールド
    //---------------------------------------------------------------------
    private String m_selectModelNumber = "";


    //初期化メソッド
    //---------------------------------------------------------------------
    /**
     * インスタンスを生成する
     * @param modelNumber カード型番
     * @return インスタンス
     */
    public static CardInfoFragment newInstance(String modelNumber) {
        CardInfoFragment fragment = new CardInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SELECT_MODEL_NUMBER, modelNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * コンストラクタ
     * 基本的には呼び出さない。
     * newInstanceを使用すること
     */
    public CardInfoFragment() {
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
     * ビューの生成
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PSDebug.d("container=" + container);
        return inflater.inflate(R.layout.fragment_card_info, container, false);
    }

    /**
     * Activity生成完了
     * @param savedInstanceState セーブデータ
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        PSDebug.d("savedInstanceState=" + savedInstanceState);
        if (savedInstanceState != null)
        {
            //型番の復元
            m_selectModelNumber = savedInstanceState.getString(ARG_SELECT_MODEL_NUMBER);
        }
        else
        {
            //親画面からの型番の取得
            Bundle argment = this.getArguments();
            if (argment != null)
            {
                m_selectModelNumber = argment.getString(ARG_SELECT_MODEL_NUMBER);
            }
        }

        //型番からカードデータを取得し表示にセットする
        SqlAccessManager sqlManager = new SqlAccessManager(getActivity());
        CardInfo cardInfo = sqlManager.selectCardInfoFromModelNumber(m_selectModelNumber);
        if (cardInfo != null)
        {
            setCardInfo(cardInfo, getView());
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

        outState.putString(ARG_SELECT_MODEL_NUMBER, m_selectModelNumber);
    }

    //カードデータセット処理
    //-------------------------------------------------------
    /**
     * カード詳細情報をビューに設定する
     * @param cardInfo カードデータ
     * @param parentView 親ビュー
     */
    private void setCardInfo(final CardInfo cardInfo, View parentView) {
        //タイトル部の設定
        TextView titleView = (TextView)parentView.findViewById(R.id.titleTextView);
        final String titleText = "Lv." + cardInfo.getLevelString() + " " + cardInfo.getName();
        titleView.setText(titleText);
        titleView.setBackgroundColor(ColorUtils.getCardDeckBgColor(cardInfo.getKind()));
        titleView.setTextColor(ColorUtils.getCardDeckTextColor(cardInfo.getKind()));
        titleView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setClipBoard("カード名", titleText);
                return true;
            }
        });

        //カード画像
        ImageView imageView = (ImageView)parentView.findViewById(R.id.cardImageView);
        ImageLoader.getInstance().displayImage("file://" + EnvPath.getCardImageFile(cardInfo.getImageFileName()), imageView);
        imageView.setBackgroundColor(ColorUtils.getColor(cardInfo.getColor()));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = EnvPath.getCardImageFile(cardInfo.getImageFileName());
                clickImageView(new File(path));
            }
        });

        //カード詳細
        TextView detailView = (TextView)parentView.findViewById(R.id.detailTextView);
        final String detailText = cardInfo.getNameKana() + "\n"
                + "種類　　：" + cardInfo.getKind() + "\n"
                + "タイプ　：" + cardInfo.getType() + "\n"
                + "色　　　：" + cardInfo.getColor() + "\n"
                + "グロウＣ：" + cardInfo.getGrowCost() + "\n"
                + "コスト　：" + cardInfo.getCost() + "\n"
                + "リミット：" + cardInfo.getLimitString() + "\n"
                + "パワー　：" + cardInfo.getPowerString() + "\n"
                + "限定条件：" + cardInfo.getLimitCondition() + "\n"
                + "ガード　：" + cardInfo.getGuard();
        detailView.setText(detailText);
        detailView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setClipBoard("カード詳細", detailText);
                return true;
            }
        });

        //カードテキスト
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout messageLayout = (LinearLayout)parentView.findViewById(R.id.messageLayout);
        for (String text : cardInfo.getTextList()) {
            if (!text.equals("")) {
                addMessageLayout(inflater, messageLayout, text);
            }
        }
        addMessageLayout(inflater, messageLayout, "Illust " + cardInfo.getIllust());

        //FAQ
        LinearLayout faqLayout = (LinearLayout)parentView.findViewById(R.id.faqLayout);
        for (CardFaqInfo faqInfo : cardInfo.getFaqList()) {
            if (faqInfo.getQuestion().equals("")) {
                continue;
            }
            addFaqLayout(inflater, faqLayout, faqInfo);
        }
    }

    /**
     * 指定した文字列をコピーする
     * @param label 表示ラベル
     * @param text コピーする文字列
     */
    private void setClipBoard(String label, String text) {
        PSDebug.d("text="+text);
        if (Build.VERSION.SDK_INT >= 11) {
            ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
        }
        else {
            android.text.ClipboardManager clipboardOld = (android.text.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardOld.setText(text);
        }

        PSUtils.toast(getActivity(), label + "をクリップボードにコピーしました");
    }

    /**
     * メッセージの項目をレイアウトに追加する
     * カードの表記文字列またはイラスト名をセットすること
     * @param inflater 親操作オブジェクト
     * @param addLayout 追加先オブジェクト
     * @param text 表示メッセージ
     */
    private void addMessageLayout(LayoutInflater inflater, LinearLayout addLayout, final String text) {
        final TextView messageTextView = (TextView)inflater.inflate(R.layout.card_info_message, null);
        if (messageTextView != null) {
            //TODO: 指定した文字（たとえば[常時能力]など）の色を変化させるのは未実装
            messageTextView.setText(text);
            messageTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setClipBoard("選択したメッセージ", text);
                    return true;
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(PSUtils.getPixcelFromDp(getActivity(), R.dimen.card_info_side_space)
                    , 0
                    , PSUtils.getPixcelFromDp(getActivity(), R.dimen.card_info_side_space)
                    , PSUtils.getPixcelFromDp(getActivity(), R.dimen.card_info_side_space));
            addLayout.addView(messageTextView, params);
        }
    }

    /**
     * FAQの項目をレイアウトに追加する
     * @param inflater 親操作オブジェクト
     * @param addLayout 追加先オブジェクト
     * @param faqInfo FAQカードデータ
     */
    private void addFaqLayout(LayoutInflater inflater, LinearLayout addLayout, final CardFaqInfo faqInfo) {
        final LinearLayout insertFaqLayout = (LinearLayout)inflater.inflate(R.layout.card_info_faq, null);
        if (insertFaqLayout != null) {
            final TextView questionTextView = (TextView)insertFaqLayout.findViewById(R.id.questionTextView);
            final TextView answerTextView = (TextView)insertFaqLayout.findViewById(R.id.answerTextView);
            questionTextView.setText("Q：" + faqInfo.getQuestion());
            answerTextView.setText("A：" + faqInfo.getAnswer());
            answerTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setClipBoard("選択したFAQ", "Q：" + faqInfo.getQuestion() + "\n" + "A：" + faqInfo.getAnswer());
                    return true;
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(PSUtils.getPixcelFromDp(getActivity(), R.dimen.card_info_side_space)
                    , 0
                    , PSUtils.getPixcelFromDp(getActivity(), R.dimen.card_info_side_space)
                    , PSUtils.getPixcelFromDp(getActivity(), R.dimen.card_info_side_space));
            addLayout.addView(insertFaqLayout, params);
        }
    }

    /**
     * 画像を表示する
     * @param imageFile 画像ファイル
     */
    private void clickImageView(File imageFile) {
        //画像ファイルを共有で他のアプリに投げる
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse("file://"+imageFile.getAbsolutePath());
        intent.setDataAndType(uri, "image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            getActivity().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            PSUtils.toast(getActivity(), "画像を表示できるアプリがありません");
        }
    }


}
