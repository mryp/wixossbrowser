package net.poringsoft.wixossbrowser.data;

/**
 * 商品別カードリストデータ
 * Created by mry on 2014/04/29.
 */
public class ProductInfo {

    private String m_title;
    private String m_url;

    /**
     * タイトル文字列を取得（例：ウィクロスTCG 構築済みデッキ ホワイトホープ 〔WXD-01〕）
     * @return タイトル文字列
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * タイトル文字列を設定
     * @param title タイトル文字列
     */
    public void setTitle(String title) {
        this.m_title = title;
    }

    /**
     * カードリストURLを取得（例：http://www.takaratomy.co.jp/products/wixoss/card/card_list.php?product_id=2）
     * @return URL文字列
     */
    public String getUrl() {
        return m_url;
    }

    /**
     * カードリストURLを設定
     * @param url URL文字列
     */
    public void setUrl(String url) {
        this.m_url = url;
    }

}
