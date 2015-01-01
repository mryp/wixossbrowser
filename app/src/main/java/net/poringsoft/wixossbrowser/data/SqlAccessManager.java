package net.poringsoft.wixossbrowser.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import net.poringsoft.wixossbrowser.utils.PSDebug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * カードデータアクセスクラス
 * Created by mry on 2014/04/29.
 */
public class SqlAccessManager {
    //定数
    //-------------------------------------------------------
    public static final int OPEN_MODE_READONLY = 0;			//読み取り専用
    public static final int OPEN_MODE_READWRITE = 1;		//読み書き

    //読み込み時リトライ回数
    private static final int RETRY_SQL_CALL = 10;

    //フィールド
    //-------------------------------------------------------
    private Context m_context = null;

    //メソッド
    //-------------------------------------------------------
    /**
     * コンストラクタ
     */
    public SqlAccessManager(Context context)
    {
        this.m_context = context;
    }


    //ユーティリティメソッド
    //-------------------------------------------------------
    /**
     * DBオープン
     * @param mode オープンモード（OPEN_MODE_～）
     * @return オープンしたDB（失敗した場合はnul）
     */
    private SqlDao open(int mode)
    {
        SqlAccessHelper helper = new SqlAccessHelper(m_context);
        SQLiteDatabase db = null;
        switch (mode)
        {
            case OPEN_MODE_READONLY:
                db = getReadableDatabase(helper, RETRY_SQL_CALL);
                break;
            case OPEN_MODE_READWRITE:
                db = getWritableDatabase(helper, RETRY_SQL_CALL);
                break;
        }

        if (db == null)
        {
            return null;
        }
        return new SqlDao(db);
    }

    /**
     * 読み込み用DBを生成する
     * @param helper DBヘルパー
     * @param retry リトライ数
     * @return オープンした読み込み用DB
     */
    private SQLiteDatabase getReadableDatabase(SqlAccessHelper helper, int retry)
    {
        SQLiteDatabase db;
        try
        {
            db = helper.getReadableDatabase();
        }
        catch (SQLiteException e)
        {
            if (retry == 0)
            {
                return null;	//あきらめる
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return getReadableDatabase(helper, retry - 1);
        }

        return db;
    }

    /**
     * 書き込み用DBを生成する
     * @param helper DBヘルパー
     * @param retry リトライ回数
     * @return オープンした書き込み可能DB
     */
    private SQLiteDatabase getWritableDatabase(SqlAccessHelper helper, int retry)
    {
        SQLiteDatabase db;
        try
        {
            db = helper.getWritableDatabase();
        }
        catch (SQLiteException e)
        {
            if (retry == 0)
            {
                return null;	//あきらめる
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return getWritableDatabase(helper, retry - 1);
        }

        return db;
    }

    //カードデータ操作
    //-------------------------------------------------------
    /**
     * カードデータを追加する（もし既に存在する場合は更新を行う）
     * @param cardInfoList データを挿入するカードデータリスト
     */
    public void insertCardInfoList(List<CardInfo> cardInfoList)
    {
        SqlDao dao = open(OPEN_MODE_READWRITE);
        if (dao == null)
        {
            return;
        }

        dao.beginTransaction();
        try
        {
            for (CardInfo info : cardInfoList)
            {
                if (dao.selectCardInfoModelNumber(info.getModelNumber()) != null) {
                    //更新
                    dao.updateCardInfo(info);
                }
                else {
                    //新規追加
                    dao.insertCardInfo(info);
                }
            }

            dao.setTransactionSuccessful();
        }
        finally
        {
            dao.endTransaction();
            dao.Close();
        }
    }

    /**
     * カードデータをすべて取得する
     * @return カードデータリスト
     */
    public List<CardInfo> selectCardInfoAll()
    {
        SqlDao dao = open(OPEN_MODE_READONLY);
        if (dao == null)
        {
            return new ArrayList<CardInfo>();
        }

        List<CardInfo> infoList = dao.selectCardInfoAll();
        dao.Close();

        return infoList;
    }

    /**
     * カードデータを検索して返す
     * @param searchText 検索文字列
     * @return カードデータリスト
     */
    public List<CardInfo> selectCardInfo(String searchText)
    {
        SqlDao dao = open(OPEN_MODE_READONLY);
        if (dao == null)
        {
            return new ArrayList<CardInfo>();
        }

        List<CardInfo> infoList;
        dao.beginTransaction();
        try
        {
            int sortType = EnvOption.getCardListSortType(m_context, searchText);
            if (SqlSelectHelper.isDeckSearch(searchText)) {
                int deckDirId = SqlSelectHelper.getDeckDirIdFromSearchText(searchText);
                infoList = selectCardInfoFromDeckDirId(dao, deckDirId, sortType);
            }
            else {
                infoList = dao.selectCardInfo(searchText, sortType
                    , EnvOption.getSearchIncludeBodyText(m_context));
                if (sortType == SqlSelectHelper.SELECT_CARD_SORT_KIND_DESC
                ||  sortType == SqlSelectHelper.SELECT_CARD_SORT_KIND_ASC) {
                    //DBでソートできないのでここで再度ソートする
                    Collections.sort(infoList, new CardInfoComparator(sortType));
                }
            }

            dao.setTransactionSuccessful();
        }
        finally
        {
            dao.endTransaction();
            dao.Close();
        }

        return infoList;
    }

    /**
     * デッキIDに紐づいたカードデータリストを取得する
     * @param dao DBアクセスオブジェクト
     * @param deckDirId デッキID
     * @param sortType ソートタイプ（SqlSelectHelper.SELECT_CARD_SORT～）
     * @return カードデータリスト
     */
    private List<CardInfo> selectCardInfoFromDeckDirId(SqlDao dao, final int deckDirId, final int sortType) {
        if (deckDirId < 0) {
            //デッキIDが不正
            PSDebug.d("デッキIDが不正 deckDirId=" +deckDirId);
            return new ArrayList<CardInfo>();
        }

        Map<String, Integer> modelNameCountTable = new LinkedHashMap<String, Integer>();
        List<DeckItemInfo> deckItemInfoList = dao.selectDeckItemInfoFromDeckId(deckDirId);
        for (DeckItemInfo itemInfo : deckItemInfoList) {
            if (modelNameCountTable.containsKey(itemInfo.getModelNumber())) {
                int count = modelNameCountTable.get(itemInfo.getModelNumber());
                modelNameCountTable.put(itemInfo.getModelNumber(), count+1);
            }
            else {
                modelNameCountTable.put(itemInfo.getModelNumber(), 1);
            }
        }

        List<CardInfo> cardInfoList = new ArrayList<CardInfo>();
        for (String modelName : modelNameCountTable.keySet()) {
            CardInfo cardInfo = dao.selectCardInfoModelNumber(modelName);
            if (cardInfo != null) {
                cardInfo.setDeckRegistCount(modelNameCountTable.get(modelName));
                cardInfoList.add(cardInfo);
            }
        }

        //並び替え
        Collections.sort(cardInfoList, new CardInfoComparator(sortType));
        return cardInfoList;
    }

    /**
     * 指定した検索えっかのカード枚数を取得する
     * @param searchText 検索語
     * @return 個数
     */
    public int selectCountCardInfo(String searchText) {
        SqlDao dao = open(OPEN_MODE_READONLY);
        if (dao == null)
        {
            return 0;
        }

        int count = 0;
        dao.beginTransaction();
        try
        {
            if (SqlSelectHelper.isDeckSearch(searchText)) {
                int deckDirId = SqlSelectHelper.getDeckDirIdFromSearchText(searchText);
                count = selectCountCardInfoFromDeckDirId(dao, deckDirId);
            }
            else {
                count = dao.selectCountCardInfo(searchText);
            }

            dao.setTransactionSuccessful();
        }
        finally
        {
            dao.endTransaction();
            dao.Close();
        }

        return count;
    }

    /**
     * 指定したデッキIDをもつカードデータの登録数を取得する
     * @param dao DBアクセス
     * @param deckDirId デッキID
     * @return カード枚数
     */
    private int selectCountCardInfoFromDeckDirId(SqlDao dao, final int deckDirId) {
        if (deckDirId < 0) {
            //デッキIDが不正
            PSDebug.d("デッキIDが不正 deckDirId=" +deckDirId);
            return 0;
        }

        Map<String, Integer> modelNameCountTable = new LinkedHashMap<String, Integer>();
        List<DeckItemInfo> deckItemInfoList = dao.selectDeckItemInfoFromDeckId(deckDirId);
        for (DeckItemInfo itemInfo : deckItemInfoList) {
            if (modelNameCountTable.containsKey(itemInfo.getModelNumber())) {
                int count = modelNameCountTable.get(itemInfo.getModelNumber());
                modelNameCountTable.put(itemInfo.getModelNumber(), count+1);
            }
            else {
                modelNameCountTable.put(itemInfo.getModelNumber(), 1);
            }
        }

        int count = 0;
        for (String modelName : modelNameCountTable.keySet()) {
            CardInfo cardInfo = dao.selectCardInfoModelNumber(modelName);
            if (cardInfo != null) {
                int registCount = modelNameCountTable.get(modelName);
                cardInfo.setDeckRegistCount(registCount);
                count += registCount;
            }
        }

        return count;
    }

    /**
     * 型番を指定したカードデータを取得する
     * @param modelNumber カード型番
     * @return カードデータ（見つからなかったときはnull）
     */
    public CardInfo selectCardInfoFromModelNumber(String modelNumber)
    {
        SqlDao dao = open(OPEN_MODE_READONLY);
        if (dao == null)
        {
            return null;
        }

        CardInfo info = dao.selectCardInfoModelNumber(modelNumber);
        dao.Close();

        return info;
    }

    /**
     * カードデータの一番先頭に格納されているデータを取得する
     * @return カードデータ
     */
    public CardInfo selectCardInfoFirstItem()
    {
        SqlDao dao = open(OPEN_MODE_READONLY);
        if (dao == null)
        {
            return null;
        }

        CardInfo info = dao.selectCardInfoFirstItem();
        dao.Close();

        return info;
    }

    /**
     * カードデータをすべて削除する
     */
    public void deleteCardInfoAll()
    {
        SqlDao dao = open(OPEN_MODE_READWRITE);
        if (dao == null)
        {
            return;
        }

        dao.deleteCardInfoAll();
        dao.Close();
    }

    /**
     * 商品カテゴリリストを取得する
     * @return 商品コードリスト
     */
    public List<String> selectDistinctCardInfo()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_PRODUCT_CODE);
    }

    /**
     * 色一覧リストを取得する
     * @return 色名リスト
     */
    public List<String> selectDistinctCardInfoColor()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_COLOR);
    }

    /**
     * カード種別リストを取得する
     * @return 種別リスト
     */
    public List<String> selectDistinctCardInfoKind()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_KIND);
    }

    /**
     * カードタイプリストを取得する
     * @return タイプリスト
     */
    public List<String> selectDistinctCardInfoType()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_TYPE);
    }

    /**
     * 限定条件リストを取得する
     * @return 限定条件リスト
     */
    public List<String> selectDistinctCardInfoLimitCond()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_LIMIT_CONDITION);
    }

    /**
     * Lv一覧を取得する
     * @return Lvリスト
     */
    public List<String> selectDistinctCardInfoLevel()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_LEVEL);
    }

    /**
     * レア度一覧を取得する
     * @return レア度リスト
     */
    public List<String> selectDistinctCardInfoRarity()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_RARITY);
    }

    /**
     * グロウコスト一覧を取得する
     * @return グロウコストリスト
     */
    public List<String> selectDistinctCardInfoGrowCost()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_GROWCOST);
    }

    /**
     * コスト一覧を取得する
     * @return コスト一覧
     */
    public List<String> selectDistinctCardInfoCost()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_COST);
    }

    /**
     * リミット一覧を取得する
     * @return コスト一覧
     */
    public List<String> selectDistinctCardInfoLimit()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_LIMIT);
    }

    /**
     * パワー一覧を取得する
     * @return パワー一覧
     */
    public List<String> selectDistinctCardInfoPower()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_POWER);
    }

    /**
     * ガード一覧を取得する
     * @return ガード一覧
     */
    public List<String> selectDistinctCardInfoGuard()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_GUARD);
    }

    /**
     * イラストレーター一覧を取得する
     * @return イラストレーターリスト
     */
    public List<String> selectDistinctCardInfoIllust()
    {
        return selectDistinctCardInfoString(SqlDao.CARD_COLUMN_ILLUST);
    }

    /**
     * 指定した列の重複なし項目リストを返す
     * @param column 列名
     * @return 重複なし項目のリスト
     */
    public List<String> selectDistinctCardInfoString(String column)
    {
        SqlDao dao = open(OPEN_MODE_READONLY);
        if (dao == null)
        {
            return new ArrayList<String>();
        }

        List<String> list = dao.selectDistinctCardInfoString(column);
        dao.Close();

        return list;
    }

    //デッキデータ操作
    //-------------------------------------------------------
    /**
     * デッキデータの追加を行う（既に存在する場合は更新する
     * @param deckDirInfo デッキ情報
     */
    public void insertDeckDirInfo(DeckDirInfo deckDirInfo)
    {
        SqlDao dao = open(OPEN_MODE_READWRITE);
        if (dao == null)
        {
            return;
        }

        dao.beginTransaction();
        try
        {
            if (deckDirInfo.getId() == -1) {
                int id = (int)dao.insertDeckDirInfo(deckDirInfo);
                PSDebug.d("insert id=" + id);
                deckDirInfo.setId(id);
            }
            else {
                dao.updateDeckDirInfo(deckDirInfo);
                PSDebug.d("update id=" + deckDirInfo.getId());
            }

            dao.setTransactionSuccessful();
        }
        finally
        {
            dao.endTransaction();
            dao.Close();
        }
    }

    /**
     * デッキデータをすべて取得する
     * @return デッキ情報リスト
     */
    public List<DeckDirInfo> selectDeckDirInfoAll()
    {
        SqlDao dao = open(OPEN_MODE_READWRITE);
        if (dao == null)
        {
            return new ArrayList<DeckDirInfo>();
        }

        List<DeckDirInfo> infoList;
        dao.beginTransaction();
        try
        {
            infoList = dao.selectDeckDirInfoAll();
            if (infoList.size() == 0) {
                //デフォルトを追加する
                DeckDirInfo info = DeckDirInfo.newInstance();
                int id = (int)dao.insertDeckDirInfo(info);
                info.setId(id);
                infoList.add(info);
            }

            dao.setTransactionSuccessful();
        }
        finally
        {
            dao.endTransaction();
            dao.Close();
        }

        return infoList;
    }

    /**
     * デッキアイテムをすべて追加する
     * 同じデータがあるかどうかにかかわらず追加を行う
     * @param deckItemInfoList 追加するデッキ情報リスト
     */
    public boolean insertDeckItemInfo(List<DeckItemInfo> deckItemInfoList)
    {
        SqlDao dao = open(OPEN_MODE_READWRITE);
        if (dao == null)
        {
            return false;
        }

        dao.beginTransaction();
        try
        {
            for (DeckItemInfo deckItemInfo : deckItemInfoList) {
                int id = (int) dao.insertDeckItemInfo(deckItemInfo);
                deckItemInfo.setId(id);
            }

            dao.setTransactionSuccessful();
        }
        finally
        {
            dao.endTransaction();
            dao.Close();
        }

        return true;
    }

    /**
     * 指定したデッキIDをもつデッキ登録アイテムリストを取得する
     * @param deckId デッキID
     * @return デッキ登録アイテムリスト
     */
    public List<DeckItemInfo> selectDeckItemInfo(int deckId)
    {
        SqlDao dao = open(OPEN_MODE_READONLY);
        if (dao == null)
        {
            return new ArrayList<DeckItemInfo>();
        }

        List<DeckItemInfo> infoList = dao.selectDeckItemInfoFromDeckId(deckId);
        dao.Close();

        return infoList;
    }

    /**
     * 指定したカード型番をもつデッキ登録アイテムリストを取得する
     */
    public List<DeckItemInfo> selectDeckItemInfo(int deckId, String modelNumber)
    {
        SqlDao dao = open(OPEN_MODE_READONLY);
        if (dao == null)
        {
            return new ArrayList<DeckItemInfo>();
        }

        List<DeckItemInfo> infoList = dao.selectDeckItemInfoFromModelNumber(deckId, modelNumber);
        dao.Close();

        return infoList;
    }

    /**
     * 指定したデッキ登録アイテムに該当する項目を1件削除する
     * （同じデッキに複数枚登録されている場合は先頭の1件だけ削除する）
     * @param deckId デッキID
     * @param modelNumber カード型番
     * @return 削除完了時はtrue
     */
    public boolean deleteDeckItemInfoLimitOne(int deckId, String modelNumber) {
        SqlDao dao = open(OPEN_MODE_READWRITE);
        if (dao == null)
        {
            return false;
        }

        boolean result = dao.deleteDeckItemInfoLimitOne(deckId, modelNumber);
        dao.Close();

        return result;
    }

    /**
     * 指定したデッキ登録アイテムに該当するデータをすべて削除する
     * （同じデッキに複数枚登録されている場合はすべて削除する）
     * @param deckId デッキID
     * @param modelNumber カード型番
     * @return 削除完了時はtrue
     */
    public boolean deleteDeckItemInfo(int deckId, String modelNumber) {
        SqlDao dao = open(OPEN_MODE_READWRITE);
        if (dao == null)
        {
            return false;
        }

        boolean result = dao.deleteDeckItemInfo(deckId, modelNumber);
        dao.Close();

        return result;
    }


    /**
     * 指定したデッキデータとそれに紐づいている登録データを削除する
     * @param deckDirInfo デッキ情報
     * @return 削除完了時はtrue
     */
    public boolean deleteDeckDirInfo(DeckDirInfo deckDirInfo) {
        SqlDao dao = open(OPEN_MODE_READWRITE);
        if (dao == null)
        {
            return false;
        }
        int deckId =deckDirInfo.getId();
        if (deckId == -1)
        {
            return false;
        }

        boolean result = false;
        dao.beginTransaction();
        try
        {
            result = dao.deleteDeckDirInfo(deckId);
            result = dao.deleteDeckItemInfo(deckId);

            dao.setTransactionSuccessful();
        }
        finally
        {
            dao.endTransaction();
            dao.Close();
        }

        return result;
    }
}