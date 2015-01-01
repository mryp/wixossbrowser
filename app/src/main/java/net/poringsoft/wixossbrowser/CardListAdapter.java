package net.poringsoft.wixossbrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.poringsoft.wixossbrowser.data.CardInfo;
import net.poringsoft.wixossbrowser.data.EnvPath;
import net.poringsoft.wixossbrowser.utils.ColorUtils;
import net.poringsoft.wixossbrowser.data.EnvOption;

import java.util.ArrayList;

/**
 * カード一覧リスト用アダプター
 * Created by mry on 2014/04/27.
 */
public class CardListAdapter extends BaseAdapter {

    private LayoutInflater m_layoutInf;
    private ArrayList<CardInfo> m_cardList;
    private boolean m_useDeckColor = false;
    private boolean m_asyncImageClear = false;

    /**
     * コンストラクタ
     */
    public CardListAdapter(Context context, ArrayList<CardInfo> cardList) {
        m_cardList = cardList;
        m_layoutInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_useDeckColor = EnvOption.getCardListColorDeck(context);
        m_asyncImageClear = EnvOption.getCardListAsyncImageDel(context);
    }

    /**
     * リスト個数
     * @return リスト個数
     */
    @Override
    public int getCount() {
        return m_cardList.size();
    }

    /**
     * 指定位置のアイテムを取得する
     * @param i 位置
     * @return 指定位置のオブジェクト（CardInfo）
     */
    @Override
    public Object getItem(int i) {
        return m_cardList.get(i);
    }

    /**
     * 指定位置のアイテムIDを取得する
     * @param i 位置
     * @return アイテムID
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * リストビューアイテムを生成して返す
     * @param i 位置
     * @param view アイテムビュー
     * @param viewGroup 親ビュー
     * @return 生成したアイテムビュー
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CardInfo info = m_cardList.get(i);
        if (view == null) {
            view = m_layoutInf.inflate(R.layout.main_list_item, null);
        }
        if (view == null) {
            return null;
        }

        //カラーバー
        LinearLayout colorBar = (LinearLayout)view.findViewById(R.id.colorBar);
        colorBar.setBackgroundColor(ColorUtils.getColor(info.getColor()));

        //カード画像
        ImageView cardImage = (ImageView)view.findViewById(R.id.cardImageView);
        if (m_asyncImageClear) {
            cardImage.setImageBitmap(null);
        }
        ImageLoader.getInstance().displayImage("file://" + EnvPath.getCardImageFile(info.getImageFileName()), cardImage);

        //カードタイトル部
        TextView text = (TextView)view.findViewById(R.id.titleTextView);
        text.setText(getTitleText(info));
        if (m_useDeckColor)
        {
            text.setBackgroundColor(ColorUtils.getCardDeckBgColor(info.getKind()));
            text.setTextColor(ColorUtils.getCardDeckTextColor(info.getKind()));
        }
        else
        {
            text.setTextColor(0xFF000000);
        }

        //カード説明部
        TextView subText = (TextView)view.findViewById(R.id.subTextView);
        subText.setText(getBodyText(info));

        return view;
    }

    /**
     * タイトル部の文字列を生成して返す
     * @param info カード情報
     * @return タイトル部（レア度、Lv、名前）
     */
    private String getTitleText(CardInfo info)
    {
        return "[" + info.getRarity() + "] Lv."
                + info.getLevelString() + " " + info.getName();
    }

    /**
     * 本体部の文字列を生成して返す
     * @param info カード情報
     * @return 説明部（種別、タイプ、色）
     */
    private String getBodyText(CardInfo info)
    {
        String bodyText = "";
        if (info.getDeckRegistCount() > 0) {
            bodyText = "枚数　：" + info.getDeckRegistCount() + "\n";
        }
        bodyText += "種別　：" + info.getKind() + "\n"
                + "タイプ：" + info.getType() + "\n"
                + "色　　：" + info.getColor();
        return bodyText;
    }
}
