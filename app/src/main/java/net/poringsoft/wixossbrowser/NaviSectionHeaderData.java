package net.poringsoft.wixossbrowser;

/**
 * ナビゲーションのヘッダ部データ
 * Created by mry on 2014/04/30.
 */
public class NaviSectionHeaderData {
    //フィールド
    //------------------------------------------
    private String m_title;

    /**
     * タイトル部文字列
     * @return タイトル
     */
    public String getTitle()
    {
        return m_title;
    }

    /**
     * コンストラクタ
     * @param title タイトル
     */
    public NaviSectionHeaderData(String title) {
        this.m_title = title;
    }
}
