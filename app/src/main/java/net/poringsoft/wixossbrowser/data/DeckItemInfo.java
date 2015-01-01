package net.poringsoft.wixossbrowser.data;

/**
 * デッキに登録しているカード情報クラス
 * Created by mry on 2014/05/04.
 */
public class DeckItemInfo {
    //フィールド
    //-----------------------------------------------------
    private int m_rowid;
    private int m_deckId;
    private String m_modelNumber;
    private long m_uptime;


    //プロパティ
    //-----------------------------------------------------
    /**
     * データIDを取得（データベースID）
     * @return データID
     */
    public int getId() {
        return m_rowid;
    }

    /**
     * データIDを設定
     * @param rowid データID
     */
    public void setId(int rowid) {
        this.m_rowid = rowid;
    }

    /**
     * デッキデータIDを取得
     * @return デッキデータID
     */
    public int getDeckId() {
        return m_deckId;
    }

    /**
     * デッキデータIDを設定
     * @param deckId デッキデータID
     */
    public void setDeckId(int deckId) {
        this.m_deckId = deckId;
    }

    /**
     * カード型番を取得
     * @return カード型番文字列
     */
    public String getModelNumber() {
        return m_modelNumber;
    }

    /**
     * カード型番を設定
     * @param modelNumber カード型番文字列
     */
    public void setModelNumber(String modelNumber) {
        this.m_modelNumber = modelNumber;
    }

    /**
     * 更新日時を取得
     * @return 日時
     */
    public long getUptime() {
        return m_uptime;
    }

    /**
     * 更新日時を設定
     * @param uptime 日時
     */
    public void setUptime(long uptime) {
        this.m_uptime = uptime;
    }
}
