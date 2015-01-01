package net.poringsoft.wixossbrowser.data;

import java.util.List;

/**
 * カード情報クラス
 * Created by mry on 2014/04/29.
 */
public class CardInfo {
    //フィールド
    //------------------------------------------------------
    private String m_modelNumber;
    private String m_productCode;
    private String m_name;
    private String m_nameKana;
    private String m_rarity;
    private String m_imageUrl;
    private String m_illust;

    private String m_kind;
    private String m_type;
    private String m_color;
    private int m_level;
    private String m_growCost;
    private String m_cost;
    private int m_limit;
    private int m_power;
    private String m_limitCondition;
    private String m_guard;

    private List<String> m_textList;
    private List<CardFaqInfo> m_faqList;

    private long m_uptime;
    private int m_deckRegistCount;

    //プロパティ
    //------------------------------------------------------
    /**
     * カードの型番を取得する（例：WD01-001）
     */
    public String getModelNumber() {
        return m_modelNumber;
    }

    /**
     * カードの型番をセットする
     */
    public void setModelNumber(String modelNumber) {
        this.m_modelNumber = modelNumber;
    }


    /**
     * 商品型番を取得する（例：WD01）
     */
    public String getProductCode() {
        return m_productCode;
    }

    /**
     * 商品型番をセットする
     */
    public void setProductCode(String productCode) {
        this.m_productCode = productCode;
    }

    /**
     * カード名を取得する（例：満月の巫女　タマヨリヒメ）
     */
    public String getName() {
        return m_name;
    }

    /**
     * カード名をセットする
     */
    public void setName(String name) {
        int delPos = name.indexOf("＜");
        if (delPos != -1)
        {
            name = name.substring(0, delPos);
        }

        this.m_name = name;
    }

    /**
     * カード名の読み仮名を取得する（例：ﾏﾝｹﾞﾂﾉﾐｺﾀﾏﾖﾘﾋﾒ）
     */
    public String getNameKana() {
        return m_nameKana;
    }

    /**
     * カード名の読み仮名を設定する
     */
    public void setNameKana(String nameKana) {
        this.m_nameKana = nameKana;
    }

    /**
     * カードのレア度を取得する（例：ST）
     */
    public String getRarity() {
        return m_rarity;
    }

    /**
     * カードのレア度を設定する
     */
    public void setRarity(String rarity) {
        this.m_rarity = rarity;
    }

    /**
     * 画像ファイルのURLを取得
     */
    public String getImageUrl() {
        return m_imageUrl;
    }

    /**
     * 画像ファイル名を取得
     */
    public String getImageFileName() {
        int pos = m_imageUrl.lastIndexOf("/");
        if (pos != -1)
        {
            return m_imageUrl.substring(pos+1).toLowerCase();
        }
        return m_imageUrl;
    }

    /**
     * 画像ファイルのURLを設定
     */
    public void setImageUrl(String imageUrl) {
        this.m_imageUrl = imageUrl;
    }

    /**
     * イラストレーター名を取得（例：クロサワテツ）
     */
    public String getIllust()
    {
        return m_illust;
    }

    /**
     * イラストレーター名を設定
     */
    public void setIllust(String illust)
    {
        this.m_illust = illust;
    }

    /**
     * カード種別を取得（例：ルリグ）
     */
    public String getKind() {
        return m_kind;
    }

    /**
     * カード種別を設定
     */
    public void setKind(String kind) {
        this.m_kind = kind;
    }

    /**
     * カードタイプを取得（例：タマ）
     */
    public String getType() {
        return m_type;
    }

    /**
     * カードタイプを設定
     */
    public void setType(String type) {
        this.m_type = type;
    }

    /**
     * 色名を取得（例：白）
     */
    public String getColor() {
        return m_color;
    }

    /**
     * 色名を設定
     */
    public void setColor(String color) {
        this.m_color = color;
    }

    /**
     * レベルを取得（例：4）
     */
    public int getLevel() {
        return m_level;
    }

    /**
     * レベルを取得（例：4）文字列版
     */
    public String getLevelString() {
        if (m_level < 0)
        {
            return "-";
        }
        return String.valueOf(m_level);
    }

    /**
     * レベルを設定
     */
    public void setLevel(int level) {
        this.m_level = level;
    }

    /**
     * グロウコストを取得（例：白×3）
     */
    public String getGrowCost() {
        return m_growCost;
    }

    /**
     * グロウコストを設定
     */
    public void setGrowCost(String growCost) {
        this.m_growCost = growCost;
    }

    /**
     * コストを取得（例：青×1）
     */
    public String getCost() {
        return m_cost;
    }

    /**
     * コストを設定
     */
    public void setCost(String cost) {
        this.m_cost = cost;
    }

    /**
     * リミットを取得（例：11）
     */
    public int getLimit() {
        return m_limit;
    }

    /**
     * リミットを取得（例：11）文字列版
     */
    public String getLimitString() {
        if (m_limit < 0)
        {
            return "-";
        }
        return String.valueOf(m_limit);
    }

    /**
     * リミットを設定
     */
    public void setLimit(int limit) {
        this.m_limit = limit;
    }

    /**
     * パワーを取得（3000）
     */
    public int getPower() {
        return m_power;
    }

    /**
     * パワーを取得（文字列版）
     */
    public String getPowerString() {
        if (m_power < 0)
        {
            return "-";
        }
        return String.valueOf(m_power);
    }

    /**
     * パワーを設定
     */
    public void setPower(int power) {
        this.m_power = power;
    }

    /**
     * 限定条件を取得（例：ピルルク限定）
     */
    public String getLimitCondition() {
        return m_limitCondition;
    }

    /**
     * 限定条件を設定
     */
    public void setLimitCondition(String limitCondition) {
        this.m_limitCondition = limitCondition;
    }

    /**
     * ガードを取得（例：有）
     */
    public String getGuard() {
        return m_guard;
    }

    /**
     * ガードを設定
     */
    public void setGuard(String guard) {
        this.m_guard = guard;
    }

    /**
     * カードのスキル・動作・メッセージ内容の文字列リストを取得する
     */
    public List<String> getTextList(){
        return m_textList;
    }

    /**
     * カードのスキル・動作・メッセージ内容の文字列リストを設定する
     */
    public void setTextList(List<String> textList) {
        this.m_textList = textList;
    }

    /**
     * 質問と回答リストを取得する
     */
    public List<CardFaqInfo> getFaqList() {
        return m_faqList;
    }

    /**
     * 質問と回答リストを設定する
     */
    public void setFaqList(List<CardFaqInfo> faqList)
    {
        this.m_faqList = faqList;
    }

    /**
     * データ取得日時を取得
     */
    public long getUptime()
    {
        return m_uptime;
    }

    /**
     * データ取得日時を設定
     */
    public void setUptime(long uptime)
    {
        m_uptime = uptime;
    }

    /**
     * デッキ登録登録枚数を設定する
     * マイデッキの表示項目のみ
     * @return 登録枚数
     */
    public int getDeckRegistCount() {
        return m_deckRegistCount;
    }

    /**
     * デッキ登録登録枚数を設定する
     * マイデッキの表示項目のみ
     * @param deckRegistCount デッキ登録数
     */
    public void setDeckRegistCount(int deckRegistCount) {
        m_deckRegistCount = deckRegistCount;
    }
}
