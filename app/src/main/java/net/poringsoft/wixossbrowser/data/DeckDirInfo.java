package net.poringsoft.wixossbrowser.data;

import java.util.List;

/**
 * カードデッキ情報クラス
 * Created by mry on 2014/05/03.
 */
public class DeckDirInfo {
    //フィールド
    //----------------------------------------------------
    private int m_rowId;
    private String m_name;
    private String m_auther;
    private String m_memo;
    private int m_index;
    private long m_uptime;
    private List<DeckItemInfo> m_itemList;

    //プロパティ
    //-----------------------------------------------------
    /**
     * データIDを取得
     * 未使用時は-1
     * @return データID（DB番号）
     */
    public int getId()
    {
        return m_rowId;
    }

    /**
     * データIDを設定
     * @param id データID
     */
    public void setId(int id)
    {
        m_rowId = id;
    }

    /**
     * デッキ名称を取得
     * @return デッキ名称
     */
    public String getName() {
        return m_name;
    }

    /**
     * デッキ名称を設定
     * @param name デッキ名称
     */
    public void setName(String name) {
        this.m_name = name;
    }

    /**
     * デッキ作成者を取得
     * @return デッキ作成者
     */
    public String getAuther() {
        return m_auther;
    }

    /**
     * デッキ作成者を設定
     * @param auther デッキ作成者
     */
    public void setAuther(String auther) {
        this.m_auther = auther;
    }

    /**
     * デッキメモを取得
     * @return メモ
     */
    public String getMemo() {
        return m_memo;
    }

    /**
     * デッキメモを設定
     * @param memo メモ
     */
    public void setMemo(String memo) {
        this.m_memo = memo;
    }

    /**
     * データ位置を取得（0以上）
     * @return データ位置
     */
    public int getIndex()
    {
        return m_index;
    }

    /**
     * データ位置を設定
     * @param index データ位置
     */
    public void setIndex(int index)
    {
        m_index = index;
    }

    /**
     * データ取得日時を取得
     * @return 日時
     */
    public long getUptime()
    {
        return m_uptime;
    }

    /**
     * データ取得日時を設定
     * @param uptime 日時
     */
    public void setUptime(long uptime)
    {
        m_uptime = uptime;
    }

    //メソッド
    //--------------------------------------------------
    /**
     * 初期値をセットしたインスタンスを取得する
     * @return 初期値を設定したインスタンス
     */
    public static DeckDirInfo newInstance() {
        DeckDirInfo info = new DeckDirInfo();
        info.setId(-1);
        info.setName("マイデッキ1");
        info.setAuther("名無しのセレクター");
        info.setMemo("");
        info.setIndex(0);
        return info;
    }
}
