package net.poringsoft.wixossbrowser.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import net.poringsoft.wixossbrowser.utils.PSDebug;
import net.poringsoft.wixossbrowser.utils.PSUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * カードデータ操作クラス
 * Created by mry on 2014/04/29.
 */
public class SqlDao {
    //定数
    //----------------------------------------------------------------------------
    public static final int RETRY_SQL_CALL = 5;		//リトライ回数
    public static final int WAIT_SLEEP_TIME = 50;		//スリープ時間
    public static final String SPLIT_CHAR = "\t";

    //テーブル定数
    //----------------------------------------------------------------------------
    //カードデータ
    public static final String CARD_TABLE_MAME = "card_data";
    public static final String CARD_COLUMN_ID = "rowid";
    public static final String CARD_COLUMN_MODEL_NUMBER = "m_modelumber";
    public static final String CARD_COLUMN_PRODUCT_CODE = "m_productcode";
    public static final String CARD_COLUMN_NAME = "m_name";
    public static final String CARD_COLUMN_NAME_KANA = "m_namekana";
    public static final String CARD_COLUMN_RARITY = "m_rarity";
    public static final String CARD_COLUMN_IMAGE_PATH = "m_imagepath";
    public static final String CARD_COLUMN_ILLUST = "m_illust";
    public static final String CARD_COLUMN_KIND = "m_kind";
    public static final String CARD_COLUMN_TYPE = "m_type";
    public static final String CARD_COLUMN_COLOR = "m_color";
    public static final String CARD_COLUMN_LEVEL = "m_level";
    public static final String CARD_COLUMN_GROWCOST = "m_growcost";
    public static final String CARD_COLUMN_COST = "m_cost";
    public static final String CARD_COLUMN_LIMIT = "m_limit";
    public static final String CARD_COLUMN_POWER = "m_power";
    public static final String CARD_COLUMN_LIMIT_CONDITION = "m_limitcondition";
    public static final String CARD_COLUMN_GUARD = "m_guard";
    public static final String CARD_COLUMN_REGULAR = "m_regular";
    public static final String CARD_COLUMN_ARRIVAL = "m_arrival";
    public static final String CARD_COLUMN_STARTING = "m_starting";
    public static final String CARD_COLUMN_LIFEBURST = "m_lifeburst";
    public static final String CARD_COLUMN_TEXT = "m_text";
    public static final String CARD_COLUMN_QUESTION = "m_question";
    public static final String CARD_COLUMN_ANSWER = "m_answer";
    public static final String CARD_COLUMN_UPTIME = "m_uptime";
    public static final String[] CARD_COLUMNS = {
            CARD_COLUMN_ID,
            CARD_COLUMN_MODEL_NUMBER,
            CARD_COLUMN_PRODUCT_CODE,
            CARD_COLUMN_NAME,
            CARD_COLUMN_NAME_KANA,
            CARD_COLUMN_RARITY,
            CARD_COLUMN_IMAGE_PATH,
            CARD_COLUMN_ILLUST,
            CARD_COLUMN_KIND,
            CARD_COLUMN_TYPE,
            CARD_COLUMN_COLOR,
            CARD_COLUMN_LEVEL,
            CARD_COLUMN_GROWCOST,
            CARD_COLUMN_COST,
            CARD_COLUMN_LIMIT,
            CARD_COLUMN_POWER,
            CARD_COLUMN_LIMIT_CONDITION,
            CARD_COLUMN_GUARD,
            CARD_COLUMN_REGULAR,
            CARD_COLUMN_ARRIVAL,
            CARD_COLUMN_STARTING,
            CARD_COLUMN_LIFEBURST,
            CARD_COLUMN_TEXT,
            CARD_COLUMN_QUESTION,
            CARD_COLUMN_ANSWER,
            CARD_COLUMN_UPTIME,
    };

    //デッキデータ
    public static final String DECK_DIR_TABLE_MAME = "deck_dir_data";
    public static final String DECK_DIR_COLUMN_ID = "rowid";
    public static final String DECK_DIR_COLUMN_NAME = "m_name";
    public static final String DECK_DIR_COLUMN_AUTHER = "m_auther";
    public static final String DECK_DIR_COLUMN_MEMO = "m_memo";
    public static final String DECK_DIR_COLUMN_INDEX = "m_index";
    public static final String DECK_DIR_COLUMN_UPTIME = "m_uptime";
    public static final String[] DECK_DIR_COLUMNS = {
            DECK_DIR_COLUMN_ID,
            DECK_DIR_COLUMN_NAME,
            DECK_DIR_COLUMN_AUTHER,
            DECK_DIR_COLUMN_MEMO,
            DECK_DIR_COLUMN_INDEX,
            DECK_DIR_COLUMN_UPTIME,
    };

    //デッキアイテムデータ
    public static final String DECK_ITEM_TABLE_MAME = "deck_item_data";
    public static final String DECK_ITEM_COLUMN_ID = "rowid";
    public static final String DECK_ITEM_COLUMN_DECK_ID = "m_deckid";
    public static final String DECK_ITEM_COLUMN_MODEL_NUMBER = "m_modelnumber";
    public static final String DECK_ITEM_COLUMN_UPTIME = "m_uptime";
    public static final String[] DECK_ITEM_COLUMNS = {
            DECK_ITEM_COLUMN_ID,
            DECK_ITEM_COLUMN_DECK_ID,
            DECK_ITEM_COLUMN_MODEL_NUMBER,
            DECK_ITEM_COLUMN_UPTIME,
    };


    //フィールド
    //----------------------------------------------------------------------------
    private SQLiteDatabase m_db = null;		//データベースオブジェクト


    //初期化終了処理関連メソッド
    //----------------------------------------------------------------------------
    /**
     * コンストラクタ
     */
    public SqlDao(SQLiteDatabase db)
    {
        m_db = db;
    }

    /**
     * DBクローズ
     */
    public void Close()
    {
        m_db.close();
    }


    //DBアクセスヘルパーメソッド
    //----------------------------------------------------------------------------
    /**
     * トランザクション開始（複数操作を同時に指定する場合はこれを使用する）
     * 正常処理時はsetTransSuccessfulをよび、正常・失敗にかかわらず処理終了後は必ずendTransactionを呼び出すこと
     * 例：
     * 	db.beginTransaction();
     *  try {
     *    ...
     *    db.setTransSuccessful();
     *  } finally {
     *    db.endTrans();
     *  }
     */
    public void beginTransaction()
    {
        m_db.beginTransaction();
    }

    /**
     * トランザクション中に成功したとき
     */
    public void setTransactionSuccessful()
    {
        m_db.setTransactionSuccessful();
    }

    /**
     * トランザクション終了
     */
    public void endTransaction()
    {
        m_db.endTransaction();
    }

    /**
     * DBへデータを挿入する
     */
    private long dbInsert(String table, String nullColumnHack, ContentValues values, int retry)
    {
        long id;
        try
        {
            id = m_db.insert(table, nullColumnHack, values);
        }
        catch (SQLiteException e)
        {
            if (retry == 0)
            {
                return 0;	//あきらめる
            }

            //ちょっとだけ待つ
            try {
                Thread.sleep(WAIT_SLEEP_TIME);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return 0;
            }

            //リトライする
            return dbInsert(table, nullColumnHack, values, retry-1);
        }

        return id;
    }

    /**
     * DB更新
     */
    private long dbUpdate(String table, ContentValues values, String whereClause, String[] whereArgs, int retry)
    {
        int ret;
        try
        {
            ret = m_db.update(table, values, whereClause, whereArgs);
        }
        catch (SQLiteException e)
        {
            if (retry == 0)
            {
                return 0;	//あきらめる
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return dbUpdate(table, values, whereClause, whereArgs, retry-1);
        }

        return ret;
    }

    /**
     * DB削除呼び出し
     */
    private int dbDelete(String table, String whereClause, String[] whereArgs, int retry)
    {
        int ret;
        try
        {
            ret = m_db.delete(table, whereClause, whereArgs);
        }
        catch (SQLiteException e)
        {
            if (retry == 0)
            {
                return 0;	//あきらめる
            }
            try {
                Thread.sleep(WAIT_SLEEP_TIME);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return dbDelete(table, whereClause, whereArgs, retry-1);
        }

        return ret;
    }

    /**
     * bool値をSQLで保存するint値に変換する
     */
    public int ConvBoolToSqlInt(boolean b)
    {
        return b ? 1 : 0;
    }

    /**
     * SQLで保存されているint値をbool値に変換する
     */
    public boolean ConvSqlIntToBool(int i)
    {
        return i != 0;
    }

    /**
     * 現在の日時を取得する
     * @return 日時
     */
    public long getNowTime()
    {
        return (new Date()).getTime();
    }


    //カードデータ
    //----------------------------------------------------------------------------
    /**
     * カードデータを新しく追加する
     * @param info カードデータ
     * @return 挿入後のデータID
     */
    public long insertCardInfo(CardInfo info)
    {
        return dbInsert(CARD_TABLE_MAME, null, getNewCardValues(info), RETRY_SQL_CALL);
    }

    /**
     * カードデータを更新する
     * @param info カード情報
     */
    public void updateCardInfo(CardInfo info)
    {
        String where = CARD_COLUMN_MODEL_NUMBER + "='" + info.getModelNumber() + "'";
        dbUpdate(CARD_TABLE_MAME,  getNewCardValues(info), where, null, RETRY_SQL_CALL);
    }

    /**
     * カードデータのテーブルをすべて削除する
     */
    public void deleteCardInfoAll()
    {
        dbDelete(CARD_TABLE_MAME, null, null, RETRY_SQL_CALL);
    }

    /**
     * カード情報をデータセット用のデータに変換して返す
     * @param info カードデータ
     * @return insert/update用カラム・値テーブル
     */
    private ContentValues getNewCardValues(CardInfo info)
    {
        ContentValues values = new ContentValues();

        values.put(CARD_COLUMN_MODEL_NUMBER, info.getModelNumber());
        values.put(CARD_COLUMN_PRODUCT_CODE, info.getProductCode());
        values.put(CARD_COLUMN_NAME, info.getName());
        values.put(CARD_COLUMN_NAME_KANA, info.getNameKana());
        values.put(CARD_COLUMN_RARITY, info.getRarity());
        values.put(CARD_COLUMN_IMAGE_PATH, info.getImageUrl());
        values.put(CARD_COLUMN_ILLUST, info.getIllust());
        values.put(CARD_COLUMN_KIND, info.getKind());
        values.put(CARD_COLUMN_TYPE, info.getType());
        values.put(CARD_COLUMN_COLOR, info.getColor());
        values.put(CARD_COLUMN_LEVEL, info.getLevel());
        values.put(CARD_COLUMN_GROWCOST, info.getGrowCost());
        values.put(CARD_COLUMN_COST, info.getCost());
        values.put(CARD_COLUMN_LIMIT, info.getLimit());
        values.put(CARD_COLUMN_POWER, info.getPower());
        values.put(CARD_COLUMN_LIMIT_CONDITION, info.getLimitCondition());
        values.put(CARD_COLUMN_GUARD, info.getGuard());
        values.put(CARD_COLUMN_REGULAR, info.getRegular());
        values.put(CARD_COLUMN_ARRIVAL, info.getArrival());
        values.put(CARD_COLUMN_STARTING, info.getStarting());
        values.put(CARD_COLUMN_LIFEBURST, info.getLifeburst());
        StringBuilder textBuff = new StringBuilder();
        for (String textItem : info.getTextList())
        {
            textBuff.append(textItem);
            textBuff.append(SPLIT_CHAR);
        }
        values.put(CARD_COLUMN_TEXT, textBuff.toString());
        StringBuilder quetion = new StringBuilder();
        StringBuilder answer = new StringBuilder();
        for (CardFaqInfo faqInfo : info.getFaqList())
        {
            quetion.append(faqInfo.getQuestion());
            quetion.append(SPLIT_CHAR);
            answer.append(faqInfo.getAnswer());
            answer.append(SPLIT_CHAR);
        }
        PSDebug.d("question=" + quetion.toString());
        values.put(CARD_COLUMN_QUESTION, quetion.toString());
        PSDebug.d("answer=" + answer.toString());
        values.put(CARD_COLUMN_ANSWER, answer.toString());
        values.put(CARD_COLUMN_UPTIME, getNowTime());

        return values;
    }

    /**
     * SQLクエリーからカード情報を取得する
     * @param cursor Selectカーソル
     * @return カード情報
     */
    private CardInfo createCardInfo(Cursor cursor)
    {
        CardInfo cardInfo = new CardInfo();
        int id = cursor.getInt(0);
        PSDebug.d("id=" + id);

        cardInfo.setModelNumber(cursor.getString(1));
        cardInfo.setProductCode(cursor.getString(2));
        cardInfo.setName(cursor.getString(3));
        cardInfo.setNameKana(cursor.getString(4));
        cardInfo.setRarity(cursor.getString(5));
        cardInfo.setImageUrl(cursor.getString(6));
        cardInfo.setIllust(cursor.getString(7));
        cardInfo.setKind(cursor.getString(8));
        cardInfo.setType(cursor.getString(9));
        cardInfo.setColor(cursor.getString(10));
        cardInfo.setLevel(cursor.getInt(11));
        cardInfo.setGrowCost(cursor.getString(12));
        cardInfo.setCost(cursor.getString(13));
        cardInfo.setLimit(cursor.getInt(14));
        cardInfo.setPower(cursor.getInt(15));
        cardInfo.setLimitCondition(cursor.getString(16));
        cardInfo.setGuard(cursor.getString(17));
        cardInfo.setRegular(cursor.getInt(18));
        cardInfo.setArrival(cursor.getInt(19));
        cardInfo.setStarting(cursor.getInt(20));
        cardInfo.setLifeburst(cursor.getInt(21));
        cardInfo.setTextList(Arrays.asList(cursor.getString(22).split(SPLIT_CHAR)));

        String questionText = cursor.getString(23);
        String answerText = cursor.getString(24);
        String[] question = questionText.split(SPLIT_CHAR);
        String[] answer = answerText.split(SPLIT_CHAR);

        ArrayList<CardFaqInfo> faqList = new ArrayList<CardFaqInfo>();
        for (int i=0; i<question.length; i++)
        {
            CardFaqInfo faqInfo = new CardFaqInfo();
            faqInfo.setQuestion(question[i]);
            faqInfo.setAnswer(answer[i]);
            faqList.add(faqInfo);
        }
        cardInfo.setFaqList(faqList);
        cardInfo.setUptime(cursor.getLong(25));

        return cardInfo;
    }

    /**
     * 指定した条件でカード情報を検索し取得する
     * @param select 検索条件
     * @param order 並び替え条件
     * @param limit 最大個数
     * @return 見つかったカードデータリスト
     */
    public List<CardInfo> selectCardInfo(String select, String order, String limit)
    {
        PSDebug.d("select=" + select);
        PSDebug.d("order=" + order);
        ArrayList<CardInfo> list = new ArrayList<CardInfo>();
        Cursor cursor = m_db.query(CARD_TABLE_MAME, CARD_COLUMNS, select, null, null, null, order, limit);
        while (cursor.moveToNext())
        {
            list.add(createCardInfo(cursor));
        }
        cursor.close();

        return list;
    }

    /**
     * 指定した条件でカード情報を検索し取得する
     * @param searchText 検索条件
     * @return 見つかった個数
     */
    public int selectCountCardInfo(String searchText)
    {
        String select = SqlSelectHelper.createSelectCard(searchText, false);
        if (select == null || select.equals(""))
        {
            return 0;
        }

        Cursor mCount= m_db.rawQuery("select count(*) from " + CARD_TABLE_MAME + " where " + select, null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();

        return count;
    }

    /**
     * 指定したカード番号のデータを返す
     * データが存在しない場合はnullを返す
     * @param modelNumber カード型番
     * @return カード情報
     */
    public CardInfo selectCardInfoModelNumber(String modelNumber)
    {
        String select = CARD_COLUMN_MODEL_NUMBER + "='" + modelNumber + "'";
        List<CardInfo> cardInfoList = selectCardInfo(select, null, "1");
        if (cardInfoList.size() > 0)
        {
            return cardInfoList.get(0);
        }

        return null;
    }

    /**
     * カードデータの一番先頭のデータを返す（データの存在有無チェック用）
     * @return カードデータ
     */
    public CardInfo selectCardInfoFirstItem()
    {
        List<CardInfo> cardInfoList = selectCardInfo(null, null, "1");
        if (cardInfoList.size() > 0)
        {
            return cardInfoList.get(0);
        }

        return null;
    }

    /**
     * カード情報をすべて取得する
     * @return カードデータリスト
     */
    public List<CardInfo> selectCardInfoAll()
    {
        return selectCardInfo(null, null, null);
    }

    /**
     * 検索条件を指定してカード情報を取得する
     * @param searchText 検索文字列
     * @return カードデータリスト
     */
    public List<CardInfo> selectCardInfo(String searchText, int sortType, boolean isTextAndFaq) {
        String select = SqlSelectHelper.createSelectCard(searchText, isTextAndFaq);
        String order = SqlSelectHelper.createOrderCard(sortType);

        if (select == null || select.equals(""))
        {
            return new ArrayList<CardInfo>();   //空を返す
        }
        return selectCardInfo(select, order, null);
    }

    /**
     * 指定した列項目の重複排除した項目リストを取得する
     * @param column 列名
     * @return 取得した列名の重複排除文字列リスト
     */
    public List<String> selectDistinctCardInfoString(String column)
    {
        Cursor cursor = m_db.query(true, CARD_TABLE_MAME, new String[]{ column }, null, null, null, null, null, null);
        ArrayList<String> list = new ArrayList<String>();
        while (cursor.moveToNext())
        {
            list.add(cursor.getString(0));
        }
        cursor.close();

        return list;
    }

    //デッキデータ
    //----------------------------------------------------------------------------
    /**
     * デッキデータを新しく追加する
     * @param info デッキ情報
     * @return デッキID
     */
    public long insertDeckDirInfo(DeckDirInfo info)
    {
        return dbInsert(DECK_DIR_TABLE_MAME, null, getNewDeckDirValues(info), RETRY_SQL_CALL);
    }

    /**
     * デッキデータを更新する
     * @param info デッキ情報
     */
    public void updateDeckDirInfo(DeckDirInfo info)
    {
        String where = DECK_DIR_COLUMN_ID + "=" + info.getId();
        dbUpdate(DECK_DIR_TABLE_MAME,  getNewDeckDirValues(info), where, null, RETRY_SQL_CALL);
    }

    /**
     * デッキデータのテーブルをすべて削除する
     */
    public void deleteDeckDirInfoAll()
    {
        dbDelete(DECK_DIR_TABLE_MAME, null, null, RETRY_SQL_CALL);
    }

    /**
     * 指定したデッキデータを削除する
     * @param deckDirId デッキID
     * @return 成功時はtrue
     */
    public boolean deleteDeckDirInfo(int deckDirId)
    {
        String delSelect = DECK_DIR_COLUMN_ID + "=" + deckDirId;
        dbDelete(DECK_DIR_TABLE_MAME, delSelect, null, RETRY_SQL_CALL);
        return true;
    }

    /**
     * デッキデータからデータセット用のデータに変換して返す
     * @param info デッキ情報
     * @return insert/update用テーブル
     */
    private ContentValues getNewDeckDirValues(DeckDirInfo info)
    {
        ContentValues values = new ContentValues();

        values.put(DECK_DIR_COLUMN_NAME, info.getName());
        values.put(DECK_DIR_COLUMN_AUTHER, info.getAuther());
        values.put(DECK_DIR_COLUMN_MEMO, info.getMemo());
        values.put(DECK_DIR_COLUMN_INDEX, info.getIndex());
        values.put(DECK_DIR_COLUMN_UPTIME, getNowTime());

        return values;
    }

    /**
     * SQLクエリーからデッキデータを取得する
     * @param cursor Selectカーソル
     * @return デッキ情報
     */
    private DeckDirInfo createDeckDirInfo(Cursor cursor)
    {
        DeckDirInfo info = new DeckDirInfo();
        info.setId(cursor.getInt(0));
        info.setName(cursor.getString(1));
        info.setAuther(cursor.getString(2));
        info.setMemo(cursor.getString(3));
        info.setIndex(cursor.getInt(4));
        info.setUptime(cursor.getInt(5));

        return info;
    }

    /**
     * 指定した条件でデッキデータを検索し取得する
     * @param select 検索条件
     * @param order 並び替え条件
     * @param limit 最大取得数
     * @return デッキ情報リスト
     */
    public List<DeckDirInfo> selectDeckDirInfo(String select, String order, String limit)
    {
        PSDebug.d("select=" + select);
        PSDebug.d("order=" + order);
        ArrayList<DeckDirInfo> list = new ArrayList<DeckDirInfo>();
        Cursor cursor = m_db.query(DECK_DIR_TABLE_MAME, DECK_DIR_COLUMNS, select, null, null, null, order, limit);
        while (cursor.moveToNext())
        {
            list.add(createDeckDirInfo(cursor));
        }
        cursor.close();

        return list;
    }

    /**
     * すべてのデッキデータを取得する
     * @return デッキ情報リスト
     */
    public List<DeckDirInfo> selectDeckDirInfoAll()
    {
        String order = DECK_DIR_COLUMN_INDEX + " ASC";
        return selectDeckDirInfo(null, order, null);
    }

    /**
     * 指定したデッキIDを持つデータを取得する
     * @param deckId デッキID
     * @return デッキ情報
     */
    public DeckDirInfo selectDeckDirInfo(int deckId)
    {
        String select = DECK_DIR_COLUMN_ID + "=" + deckId;
        List<DeckDirInfo> deckList = selectDeckDirInfo(select, null, null);
        if (deckList.size() == 0)
        {
            return null;
        }

        return deckList.get(0);
    }

    //デッキアイテム
    //----------------------------------------------------------------------------
    /**
     * デッキ登録アイテムを新しく追加する
     * @param info デッキ登録アイテム
     * @return DBのID
     */
    public long insertDeckItemInfo(DeckItemInfo info)
    {
        return dbInsert(DECK_ITEM_TABLE_MAME, null, getNewDeckItemValues(info), RETRY_SQL_CALL);
    }

    /**
     * デッキ登録アイテムを更新する
     * @param info デッキ登録アイテム
     */
    public void updateDeckItemInfo(DeckItemInfo info)
    {
        String where = DECK_ITEM_COLUMN_ID + "=" + info.getId();
        dbUpdate(DECK_ITEM_TABLE_MAME,  getNewDeckItemValues(info), where, null, RETRY_SQL_CALL);
    }

    /**
     * デッキ登録アイテムのテーブルをすべて削除する
     */
    public void deleteDeckItemInfoAll()
    {
        dbDelete(DECK_ITEM_TABLE_MAME, null, null, RETRY_SQL_CALL);
    }

    /**
     * デッキ登録アイテムの指定したデータを1件削除する
     * @param deckId デッキID
     * @param modelNumber カード型番
     */
    public boolean deleteDeckItemInfoLimitOne(int deckId, String modelNumber)
    {
        String select = DECK_ITEM_COLUMN_DECK_ID + "=" + deckId
                + " AND " + DECK_ITEM_COLUMN_MODEL_NUMBER + "=" + DatabaseUtils.sqlEscapeString(modelNumber);
        List<DeckItemInfo> itemList = selectDeckInfo(select, null, "1");
        if (itemList.size() == 0) {
            return false;
        }

        String delSelect = DECK_ITEM_COLUMN_ID + "=" + itemList.get(0).getId();
        dbDelete(DECK_ITEM_TABLE_MAME, delSelect, null, RETRY_SQL_CALL);
        return true;
    }

    /**
     * デッキ登録アイテムの指定した項目に該当するデータをすべて削除する
     * @param deckId デッキID
     * @param modelNumber カード型番
     * @return 正常に削除した場合はtrue
     */
    public boolean deleteDeckItemInfo(int deckId, String modelNumber)
    {
        String delSelect = DECK_ITEM_COLUMN_DECK_ID + "=" + deckId
                + " AND " + DECK_ITEM_COLUMN_MODEL_NUMBER + "=" + DatabaseUtils.sqlEscapeString(modelNumber);
        dbDelete(DECK_ITEM_TABLE_MAME, delSelect, null, RETRY_SQL_CALL);
        return true;
    }

    /**
     * デッキ登録アイテムの指定したデッキIDに紐づいているデータをすべて削除する
     * @param deckId デッキID
     * @return 削除成功時はtrue
     */
    public boolean deleteDeckItemInfo(int deckId)
    {
        String delSelect = DECK_ITEM_COLUMN_DECK_ID + "=" + deckId;
        dbDelete(DECK_ITEM_TABLE_MAME, delSelect, null, RETRY_SQL_CALL);
        return true;
    }

    /**
     * デッキ登録アイテムからデータセット用のデータに変換して返す
     * @param info デッキ登録アイテム
     * @return insert/update用データテーブル
     */
    private ContentValues getNewDeckItemValues(DeckItemInfo info)
    {
        ContentValues values = new ContentValues();

        values.put(DECK_ITEM_COLUMN_DECK_ID, info.getDeckId());
        values.put(DECK_ITEM_COLUMN_MODEL_NUMBER, info.getModelNumber());
        values.put(DECK_ITEM_COLUMN_UPTIME, info.getUptime());

        return values;
    }

    /**
     * SQLクエリーからデッキ登録アイテムを取得する
     * @param cursor Selectカーソル
     * @return デッキ登録アイテム
     */
    private DeckItemInfo createDeckItemInfo(Cursor cursor)
    {
        DeckItemInfo info = new DeckItemInfo();
        info.setId(cursor.getInt(0));
        info.setDeckId(cursor.getInt(1));
        info.setModelNumber(cursor.getString(2));
        info.setUptime(cursor.getInt(3));

        return info;
    }

    /**
     * 指定した条件でデッキ登録アイテムを検索し取得する
     * @param select 検索条件
     * @param order 並び替え条件
     * @param limit 最大取得数
     * @return デッキ登録アイテムリスト
     */
    public List<DeckItemInfo> selectDeckInfo(String select, String order, String limit)
    {
        PSDebug.d("select=" + select);
        PSDebug.d("order=" + order);
        ArrayList<DeckItemInfo> list = new ArrayList<DeckItemInfo>();
        Cursor cursor = m_db.query(DECK_ITEM_TABLE_MAME, DECK_ITEM_COLUMNS, select, null, null, null, order, limit);
        while (cursor.moveToNext())
        {
            list.add(createDeckItemInfo(cursor));
        }
        cursor.close();

        return list;
    }

    /**
     * すべてのデッキ登録アイテムを取得する
     * @return デッキ登録アイテムリスト
     */
    public List<DeckItemInfo> selectDeckItemInfoAll()
    {
        return selectDeckInfo(null, null, null);
    }

    /**
     * デッキIDをもつデッキ登録アイテムを取得する
     * @param deckId デッキID
     * @return デッキ登録アイテムリスト
     */
    public List<DeckItemInfo> selectDeckItemInfoFromDeckId(int deckId)
    {
        String select = DECK_ITEM_COLUMN_DECK_ID + "=" + deckId;
        return selectDeckInfo(select, null, null);
    }

    /**
     * 指定したデッキIDと型番をもつデッキ登録アイテムを取得する
     * @param deckId デッキID
     * @param modelNumber カード型番
     * @return デッキ登録アイテムリスト
     */
    public List<DeckItemInfo> selectDeckItemInfoFromModelNumber(int deckId, String modelNumber)
    {
        String select = DECK_ITEM_COLUMN_DECK_ID + "=" + deckId
                + " AND " + DECK_ITEM_COLUMN_MODEL_NUMBER + "=" + DatabaseUtils.sqlEscapeString(modelNumber);
        return selectDeckInfo(select, null, null);
    }
}



