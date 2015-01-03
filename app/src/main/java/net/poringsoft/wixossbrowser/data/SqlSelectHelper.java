package net.poringsoft.wixossbrowser.data;

import android.app.Application;
import android.database.DatabaseUtils;

import net.poringsoft.wixossbrowser.utils.KanamojiCharUtils;
import net.poringsoft.wixossbrowser.utils.PSUtils;

/**
 * SQL生成用ヘルパークラス
 * Created by mry on 2014/04/30.
 */
public class SqlSelectHelper {
    //検索コマンド
    public static final String SELECT_CARD_CMD_MODEL_NUMBER = "model";  //型番
    public static final String SELECT_CARD_CMD_PRODUCT_CODE = "product";//商品コード
    public static final String SELECT_CARD_CMD_COLOR = "color";         //色
    public static final String SELECT_CARD_CMD_KIND = "kind";           //種類
    public static final String SELECT_CARD_CMD_TYPE = "type";           //タイプ
    public static final String SELECT_CARD_CMD_LIMIT_COND = "limitcond";//限定条件
    public static final String SELECT_CARD_CMD_LEVEL = "level";         //レベル
    public static final String SELECT_CARD_CMD_RARITY = "rarity";       //レア度
    public static final String SELECT_CARD_CMD_ILLUST = "illust";       //イラストレーター
    public static final String SELECT_CARD_CMD_GROWCOST = "growc";      //グロウコスト
    public static final String SELECT_CARD_CMD_COST = "cost";           //コスト
    public static final String SELECT_CARD_CMD_LIMIT = "limit";         //リミット
    public static final String SELECT_CARD_CMD_POWER = "power";         //パワー
    public static final String SELECT_CARD_CMD_GUARD = "guard";         //ガード
    public static final String SELECT_CARD_CMD_ABILITY = "ability";     //能力

    public static final String SELECT_DECK_DIR_CMD_ID = "deckid";       //デッキID

    //並び替え種別
    public static final int SELECT_CARD_SORT_MODEL_NUMBER_ASC = 1;      //型番順
    public static final int SELECT_CARD_SORT_NAME_ASC = 2;              //名前順
    public static final int SELECT_CARD_SORT_NAME_KANA_ASC = 3;         //よみがな順
    public static final int SELECT_CARD_SORT_LIMIT_DESC = 4;            //リミット降順
    public static final int SELECT_CARD_SORT_POWER_DESC = 5;            //パワー降順
    public static final int SELECT_CARD_SORT_KIND_DESC = 6;             //種別降順
    public static final int SELECT_CARD_SORT_KIND_ASC = 7;              //種別昇順

    public SqlSelectHelper(){

    }

    /**
     * カード検索用のSQL文字列（whereより後ろ部分）を作成して返す
     * @param searchText 検索文字列
     * @return 検索を行うSQL文字列
     */
    public static String createSelectCard(String searchText, boolean isTextAndFaq){
        String[] command = searchText.split(":");
        if (command.length < 2)
        {
            return createNameSelect(searchText, isTextAndFaq);
        }

        String key = command[0];
        String value = command[1];
        String select = "";
        if (key.equals(SELECT_CARD_CMD_PRODUCT_CODE))
        {
            //前方一致検索
            select = SqlDao.CARD_COLUMN_PRODUCT_CODE + " LIKE " + DatabaseUtils.sqlEscapeString(value+"%");
        }
        else if (key.equals(SELECT_CARD_CMD_MODEL_NUMBER))
        {
            //完全一致検索
            select = SqlDao.CARD_COLUMN_MODEL_NUMBER + "=" + DatabaseUtils.sqlEscapeString(value);
        }
        else if (key.equals(SELECT_CARD_CMD_COLOR))
        {
            //完全一致検索
            select = SqlDao.CARD_COLUMN_COLOR + "=" + DatabaseUtils.sqlEscapeString(value);
        }
        else if (key.equals(SELECT_CARD_CMD_KIND))
        {
            //完全一致検索
            select = SqlDao.CARD_COLUMN_KIND + "=" + DatabaseUtils.sqlEscapeString(value);
        }
        else if (key.equals(SELECT_CARD_CMD_TYPE))
        {
            //完全一致検索
            select = SqlDao.CARD_COLUMN_TYPE + "=" + DatabaseUtils.sqlEscapeString(value);
        }
        else if (key.equals(SELECT_CARD_CMD_LIMIT_COND))
        {
            //完全一致検索
            select = SqlDao.CARD_COLUMN_LIMIT_CONDITION + "=" + DatabaseUtils.sqlEscapeString(value);
        }
        else if (key.equals(SELECT_CARD_CMD_LEVEL))
        {
            //数値検索
            select = SqlDao.CARD_COLUMN_LEVEL + "=" + String.valueOf(value);
        }
        else if (key.equals(SELECT_CARD_CMD_RARITY))
        {
            //完全一致検索
            select = SqlDao.CARD_COLUMN_RARITY + "=" + DatabaseUtils.sqlEscapeString(value);
        }
        else if (key.equals(SELECT_CARD_CMD_GROWCOST))
        {
            //完全一致検索
            select = SqlDao.CARD_COLUMN_GROWCOST + "=" + DatabaseUtils.sqlEscapeString(value);
        }
        else if (key.equals(SELECT_CARD_CMD_COST))
        {
            //完全一致検索
            select = SqlDao.CARD_COLUMN_COST + "=" + DatabaseUtils.sqlEscapeString(value);
        }
        else if (key.equals(SELECT_CARD_CMD_LIMIT))
        {
            //数値検索
            select = SqlDao.CARD_COLUMN_LIMIT + "=" + String.valueOf(value);
        }
        else if (key.equals(SELECT_CARD_CMD_POWER))
        {
            //数値検索
            select = SqlDao.CARD_COLUMN_POWER + "=" + String.valueOf(value);
        }
        else if (key.equals(SELECT_CARD_CMD_GUARD))
        {
            //完全一致検索
            select = SqlDao.CARD_COLUMN_GUARD + "=" + DatabaseUtils.sqlEscapeString(value);
        }
        else if (key.equals(SELECT_CARD_CMD_ABILITY))
        {
            //部分一致
            select = SqlDao.CARD_COLUMN_TEXT + " LIKE " + DatabaseUtils.sqlEscapeString("%"+value+"%");
        }
        else if (key.equals(SELECT_CARD_CMD_ILLUST))
        {
            //完全一致検索
            select = SqlDao.CARD_COLUMN_ILLUST + "=" + DatabaseUtils.sqlEscapeString(value);
        }

        return select;
    }

    /**
     * カード名称から検索を行うための文字列を取得する
     * @param searchText 検索文字列
     * @return SQLのSelect用文字列
     */
    private static String createNameSelect(String searchText, boolean isTextAndFaq) {
        String kanaSearchText = KanamojiCharUtils.zenkakuHiraganaToZenkakuKatakana(searchText);

        //部分一致検索
        String nameQuery = DatabaseUtils.sqlEscapeString("%"+searchText+"%");
        String kanaQuery = DatabaseUtils.sqlEscapeString("%"+kanaSearchText+"%");

        StringBuilder select = new StringBuilder();
        select.append(SqlDao.CARD_COLUMN_NAME);
        select.append(" LIKE ");
        select.append(nameQuery);
        select.append(" OR ");
        select.append(SqlDao.CARD_COLUMN_NAME_KANA);
        select.append(" LIKE ");
        select.append(kanaQuery);
        if (isTextAndFaq) {
            select.append(" OR ");
            select.append(SqlDao.CARD_COLUMN_TEXT);
            select.append(" LIKE ");
            select.append(nameQuery);
            select.append(" OR ");
            select.append(SqlDao.CARD_COLUMN_QUESTION);
            select.append(" LIKE ");
            select.append(nameQuery);
            select.append(" OR ");
            select.append(SqlDao.CARD_COLUMN_ANSWER);
            select.append(" LIKE ");
            select.append(nameQuery);
        }

        return select.toString();
    }

    /**
     * カードリストの並び替え文字列を取得する
     * @param sortType 検索種別（SELECT_CARD_SORT_～）
     * @return SQLの並び替え用文字列
     */
    public static String createOrderCard(int sortType) {
        String order = "";
        switch (sortType) {
            case SELECT_CARD_SORT_NAME_ASC:
                order = SqlDao.CARD_COLUMN_NAME + " ASC";
                break;
            case SELECT_CARD_SORT_NAME_KANA_ASC:
                order = SqlDao.CARD_COLUMN_NAME_KANA + " ASC";
                break;
            case SELECT_CARD_SORT_LIMIT_DESC:
                order = SqlDao.CARD_COLUMN_LIMIT + " DESC";
                break;
            case SELECT_CARD_SORT_POWER_DESC:
                order = SqlDao.CARD_COLUMN_POWER + " DESC";
                break;
            case SELECT_CARD_SORT_MODEL_NUMBER_ASC:
            default:
                order = SqlDao.CARD_COLUMN_MODEL_NUMBER + " ASC";
                break;
        }

        return order;
    }

    /**
     * 指定した検索語がデッキ検索かどうか
     * @param searchText 検索文字列
     * @return デッキ検索のときはtrue
     */
    public static boolean isDeckSearch(String searchText) {
        if (searchText.equals("")) {
            return true;    //初回表示時は検索語が空になっており、かつ初期位置がデッキのため
        }
        return searchText.startsWith(SELECT_DECK_DIR_CMD_ID);
    }

    /**
     * 検索文字列からデッキIDを取得する
     * @param searchText デッキ検索用文字列
     * @return デッキID
     */
    public static int getDeckDirIdFromSearchText(String searchText){
        String[] command = searchText.split(":");
        if (command.length < 2)
        {
            return -1;
        }

        int deckDirId = -1;
        String key = command[0];
        String value = command[1];
        if (key.equals(SELECT_DECK_DIR_CMD_ID)) {
            deckDirId = PSUtils.tryParseInt(value, -1);
        }

        return deckDirId;
    }
}
