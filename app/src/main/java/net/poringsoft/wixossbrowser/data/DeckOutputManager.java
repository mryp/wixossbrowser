package net.poringsoft.wixossbrowser.data;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * デッキの共有出力状態を保持するクラス
 * Created by mry on 2014/05/04.
 */
public class DeckOutputManager {

    private static final String REPLACE_KEY = "$$$";

    //フィールド
    //-------------------------------------------------
    private SqlAccessManager m_sqlManager = null;
    private List<CardInfo> m_cardList = null;
    private boolean m_showLevel;
    private boolean m_showCount;
    private boolean m_isCountLast;

    //メソッド
    //-------------------------------------------------
    /**
     * コンストラクタ
     * @param context コンテキスト
     */
    public DeckOutputManager(Context context) {
        m_sqlManager = new SqlAccessManager(context);
        m_showLevel = EnvOption.getEditDeckShareShowLevel(context);
        m_showCount = EnvOption.getEditDeckShareShowCount(context);
        m_isCountLast = EnvOption.getEditDeckShareMoveLast(context);
    }

    /**
     * デッキ内のカードデータをすべて読み込む
     * @param deckDirId デッキID
     * @return カードデータが読み込めたかどうか
     */
    public boolean loadCardInfo(int deckDirId) {
        m_cardList = m_sqlManager.selectCardInfo(SqlSelectHelper.SELECT_DECK_DIR_CMD_ID + ":" + deckDirId);
        if (m_cardList.size() == 0) {
            return false;
        }

        //レベル降順かつ枚数降順で並び替え
        Collections.sort(m_cardList, new Comparator<CardInfo>() {
            @Override
            public int compare(CardInfo cardInfo1, CardInfo cardInfo2) {
                int result;
                if (cardInfo1.getLevel() == cardInfo2.getLevel()) {
                    if (cardInfo1.getDeckRegistCount() == cardInfo2.getDeckRegistCount()) {
                        result = 0;
                    } else if (cardInfo1.getDeckRegistCount() < cardInfo2.getDeckRegistCount()) {
                        result = 1;
                    } else {
                        result = -1;
                    }
                } else if (cardInfo1.getLevel() < cardInfo2.getLevel()) {
                    result = 1;
                } else {
                    result = -1;
                }

                return result;
            }
        });
        return true;
    }

    /**
     * 共有で送るための文字列を生成する
     * カードデータがないときは空文字を返す
     * @return 送信用データ
     */
    public String createSendShareTextData() {
        if (m_cardList.size() == 0) {
            return "";
        }

        StringBuilder output = new StringBuilder();
        output.append("□ルリグデッキ□\n");
        Map<String, Integer> lrigDeckCountTable = getCardNameCountTable(Arrays.asList("ルリグ", "アーツ"));
        for (String key : lrigDeckCountTable.keySet()) {
            if (m_isCountLast) {
                output.append(key.replace(REPLACE_KEY, " x" + lrigDeckCountTable.get(key)));
            }
            else {
                output.append(key.replace(REPLACE_KEY, "[x" + lrigDeckCountTable.get(key) + "]"));
            }
            output.append("\n");
        }

        output.append("\n■メインデッキ■\n");
        Map<String, Integer> mainDeckCountTable = getCardNameCountTable(Arrays.asList("シグニ", "スペル"));
        for (String key : mainDeckCountTable.keySet()) {
            if (m_isCountLast) {
                output.append(key.replace(REPLACE_KEY, " x" + mainDeckCountTable.get(key)));
            }
            else {
                output.append(key.replace(REPLACE_KEY, "[x" + mainDeckCountTable.get(key) + "]"));
            }
            output.append("\n");
        }

        return output.toString();
    }

    /**
     * カード名リストと同じカード名がある枚数のテーブルを生成して返す
     * @param targetKindList テーブルを作成する種類名リスト
     * @return カード名と枚数テーブル
     */
    private Map<String, Integer> getCardNameCountTable(List<String> targetKindList) {
        Map<String, Integer> countTable = new LinkedHashMap<String, Integer>();
        for (CardInfo cardInfo : m_cardList) {
            if (targetKindList.contains(cardInfo.getKind()))
            {
                String name = "";
                if (m_showLevel) {
                    name += "[Lv." + cardInfo.getLevelString() + "]";
                }
                if (m_showCount && !m_isCountLast) {
                    name += REPLACE_KEY;
                }
                name += cardInfo.getName();
                if (m_showCount && m_isCountLast) {
                    name += REPLACE_KEY;
                }
                countTable.put(name, cardInfo.getDeckRegistCount());
            }
        }
        return countTable;
    }

    public Bitmap createCardListImage() {
        if (m_cardList.size() == 0) {
            return null;
        }

        //TODO: 画像リストデッキ出力は未実装
        return null;
    }
}
