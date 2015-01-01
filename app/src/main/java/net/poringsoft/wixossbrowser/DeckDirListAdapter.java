package net.poringsoft.wixossbrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.poringsoft.wixossbrowser.data.DeckDirInfo;

import java.util.List;

/**
 * デッキデータ表示用リストアダプター
 * Created by mry on 2014/05/04.
 */
public class DeckDirListAdapter extends BaseAdapter {
    //フィールド
    //--------------------------------------------------------------
    private Context m_context;
    private LayoutInflater m_layoutInf;
    private List<DeckDirInfo> m_deckList;


    //メソッド
    //--------------------------------------------------------------
    /**
     * コンストラクタ
     */
    public DeckDirListAdapter(Context context, List<DeckDirInfo> cardList) {
        m_context = context;
        m_deckList = cardList;
        m_layoutInf = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * リストの個数
     * @return 個数
     */
    @Override
    public int getCount() {
        return m_deckList.size();
    }

    /**
     * 指定位置のアイテムを取得する
     * @param i アイテムの位置
     * @return アイテムオブジェクト
     */
    @Override
    public Object getItem(int i) {
        return m_deckList.get(i);
    }

    /**
     * 指定位置のアイテムIDを取得する
     * @param i アイテムID
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
     * @param viewGroup 親アイテムビュー
     * @return 生成したアイテムビュー
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DeckDirInfo deckDirInfo = m_deckList.get(i);
        if (view == null) {
            view = m_layoutInf.inflate(R.layout.deck_dir_list_item, null);
        }
        if (view == null) {
            return null;
        }

        TextView nameTextView = (TextView)view.findViewById(R.id.nameAutherTextView);
        nameTextView.setText(deckDirInfo.getName());

        TextView memoTextView = (TextView)view.findViewById(R.id.memoTextView);
        if (deckDirInfo.getMemo().equals("")) {
            memoTextView.setVisibility(View.GONE);
        }
        else {
            memoTextView.setText(deckDirInfo.getMemo());
            memoTextView.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
