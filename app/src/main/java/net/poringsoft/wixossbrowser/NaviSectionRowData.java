package net.poringsoft.wixossbrowser;


/**
 * ナビゲーションのボディ部データ
 * Created by mry on 2014/04/30.
 */
public class NaviSectionRowData {
    private String m_label;
    private String m_rightLabel;
    private String m_searchText;

    /**
     * ラベル
     * @return ラベル文字列
     */
    public String getLabel()
    {
        return m_label;
    }

    /**
     * 補足値（右端表示）の取得
     * @return 数値文字列
     */
    public String getValue()
    {
        return m_rightLabel;
    }

    /**
     * 補足値（右端表示）のセット
     * @param value 表示文字列
     */
    public void setValue(String value) {
        m_rightLabel = value;
    }

    /**
     * 検索情報
     * @return 検索文字列
     */
    public String getSearchText()
    {
        return m_searchText;
    }


    /**
     * コンストラクタ
     */
    public NaviSectionRowData(String label, String searchText) {
        this.m_label = label;
        this.m_rightLabel = "";
        this.m_searchText = searchText;
    }

    /**
     * コンストラクタ
     */
    public NaviSectionRowData(String label, String rightLabel, String searchText) {
        this.m_label = label;
        this.m_rightLabel = rightLabel;
        this.m_searchText  = searchText;
    }
}
