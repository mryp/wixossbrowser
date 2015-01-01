package net.poringsoft.wixossbrowser.data;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * カード情報の並び替えクラス
 * Created by mry on 2014/05/21.
 */
public class CardInfoComparator implements Comparator<CardInfo> {
    //定数
    //-----------------------------------------------------------------
    /**
     * 種別の重み情報保持
     */
    private static final Map<String, Integer> SORT_KIND_WEIGHT = new LinkedHashMap<String, Integer>(){
        {
            put("ルリグ", 4);
            put("アーツ", 3);
            put("シグニ", 2);
            put("スペル", 1);
        }
    };

    private static final int KIND_SORT_DIC_ASC = -1;    //種別ソートで昇順ソート
    private static final int KIND_SORT_DIC_DESC = 1;    //種別ソートで降順ソート

    //フィールド
    //-----------------------------------------------------------------
    private int m_sortType = 0;     //ソートタイプ


    //メソッド
    //-----------------------------------------------------------------
    /**
     * コンストラクタ
     * @param sortType ソートタイプ（SELECT_CARD_SORT_～）
     */
    public CardInfoComparator(int sortType) {
        m_sortType = sortType;
    }

    /**
     * 並び替え開始
     * @param cardInfo1 カード情報１
     * @param cardInfo2 カード情報２
     * @return どちらが上位かを1, 0, -1 で表す
     */
    @Override
    public int compare(CardInfo cardInfo1, CardInfo cardInfo2) {
        int result = 0;
        switch (m_sortType) {
            case SqlSelectHelper.SELECT_CARD_SORT_MODEL_NUMBER_ASC:
                result = cardInfo1.getModelNumber().compareTo(cardInfo2.getModelNumber());
                break;
            case SqlSelectHelper.SELECT_CARD_SORT_NAME_ASC:
                result = cardInfo1.getName().compareTo(cardInfo2.getName());
                break;
            case SqlSelectHelper.SELECT_CARD_SORT_NAME_KANA_ASC:
                result = cardInfo1.getNameKana().compareTo(cardInfo2.getNameKana());
                break;
            case SqlSelectHelper.SELECT_CARD_SORT_LIMIT_DESC:
                result = compareLimitDesc(cardInfo1, cardInfo2);
                break;
            case SqlSelectHelper.SELECT_CARD_SORT_POWER_DESC:
                result = comparePowerDesc(cardInfo1, cardInfo2);
                break;
            case SqlSelectHelper.SELECT_CARD_SORT_KIND_DESC:
                result = compareSortKind(cardInfo1, cardInfo2, KIND_SORT_DIC_DESC);
                break;
            case SqlSelectHelper.SELECT_CARD_SORT_KIND_ASC:
                result = compareSortKind(cardInfo1, cardInfo2, KIND_SORT_DIC_ASC);
                break;
        }

        return result;
    }

    /**
     * リミット大きい順で並び替える
     * @param cardInfo1 カード情報１
     * @param cardInfo2 カード情報2
     * @return 比較結果
     */
    private int compareLimitDesc(CardInfo cardInfo1, CardInfo cardInfo2) {
        int result;
        if (cardInfo1.getLimit() == cardInfo2.getLimit()) {
            result = 0;
        }
        else if (cardInfo1.getLimit() < cardInfo2.getLimit()) {
            result = 1;
        }
        else {
            result = -1;
        }

        return result;
    }

    /**
     * パワー大きい順で並び替える
     * @param cardInfo1 カード情報１
     * @param cardInfo2 カード情報２
     * @return 比較結果
     */
    private int comparePowerDesc(CardInfo cardInfo1, CardInfo cardInfo2) {
        int result;
        if (cardInfo1.getPower() == cardInfo2.getPower()) {
            result = 0;
        }
        else if (cardInfo1.getPower() < cardInfo2.getPower()) {
            result = 1;
        }
        else {
            result = -1;
        }

        return result;
    }

    /**
     * カード種別ごとに並び替える
     * @param cardInfo1 カード情報１
     * @param cardInfo2 カード情報２
     * @param sort 同じ種別時のLv,リミット、パワーで並び替えるときの設定値（昇順のときは-1, 降順のときは1を指定する）
     * @return 比較結果
     */
    private int compareSortKind(CardInfo cardInfo1, CardInfo cardInfo2, int sort) {
        int weight1 = 0;
        if (SORT_KIND_WEIGHT.containsKey(cardInfo1.getKind())) {
            weight1 = SORT_KIND_WEIGHT.get(cardInfo1.getKind());
        }
        int weight2= 0;
        if (SORT_KIND_WEIGHT.containsKey(cardInfo2.getKind())) {
            weight2 = SORT_KIND_WEIGHT.get(cardInfo2.getKind());
        }

        int result;
        if (weight1 < weight2) {
            result = 1;
        }
        else if (weight1 > weight2) {
            result = -1;
        }
        else {
            result = compareLimitDesc(cardInfo1, cardInfo2);
            if (result == 0) {
                result = comparePowerDesc(cardInfo1, cardInfo2);
            }
            result = result * sort; //ソート向きによって反転させる
        }

        return result;
    }
}
