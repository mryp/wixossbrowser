package net.poringsoft.wixossbrowser.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DBアクセス用ヘルパークラス
 * Created by mry on 2014/04/29.
 */
public class SqlAccessHelper extends SQLiteOpenHelper {
    //定数
    //-------------------------------------------------------
    public static final String DATABASE_NAME = "card.db";	//データベース名
    public static final int DATABASE_VERSION = 2;			//テーブルバージョン（更新時は数値を1つあげる）

    //テーブル定義
    //-------------------------------------------------------
    /**
     * カテゴリー情報を保持するテーブル
     */
    private static final String CREATE_SQL_CARD_DATA = "create table card_data"
            + "("
            + "rowid integer primary key autoincrement, "
            + "m_modelumber text,"
            + "m_productcode text,"
            + "m_name text,"
            + "m_namekana text,"
            + "m_rarity text,"
            + "m_imagepath text,"
            + "m_illust text,"
            + "m_kind text,"
            + "m_type text,"
            + "m_color text,"
            + "m_level integer,"
            + "m_growcost text,"
            + "m_cost text,"
            + "m_limit integer,"
            + "m_power integer,"
            + "m_limitcondition text,"
            + "m_guard text,"
            + "m_regular integer,"
            + "m_arrival integer,"
            + "m_starting integer,"
            + "m_lifeburst integer,"
            + "m_text text,"
            + "m_question text,"
            + "m_answer text,"
            + "m_uptime integer"
            + ")";

    /**
     * デッキ情報を保持するテーブル
     */
    private static final String CREATE_SQL_DECK_DIR_DATA = "create table deck_dir_data"
            + "("
            + "rowid integer primary key autoincrement, "
            + "m_name text,"
            + "m_auther text,"
            + "m_memo text,"
            + "m_index integer,"
            + "m_uptime integer"
            + ")";

    /**
     * デッキに登録しているカード情報を保持するクラス
     */
    private static final String CREATE_SQL_DECK_ITEM_DATA = "create table deck_item_data"
            + "("
            + "rowid integer primary key autoincrement, "
            + "m_deckid integer,"
            + "m_modelnumber text,"
            + "m_uptime integer"
            + ")";


    //メソッド
    //-------------------------------------------------------
    /**
     * コンストラクタ
     */
    public SqlAccessHelper(Context context)
    {
        super(context, EnvPath.getRootDirPath() + DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * DBテーブル作成メソッド
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.beginTransaction();
        try
        {
            createTableVer1(db);
            createTableVer2(db);
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    /**
     * version 1のテーブルを作成する
     * @param db DB
     */
    private void createTableVer1(SQLiteDatabase db)
    {
        db.execSQL(CREATE_SQL_CARD_DATA);
    }

    /**
     * v2のテーブルを作成する
     * @param db DB
     */
    private void createTableVer2(SQLiteDatabase db)
    {
        db.execSQL(CREATE_SQL_DECK_DIR_DATA);
        db.execSQL(CREATE_SQL_DECK_ITEM_DATA);
    }

    /**
     * DBテーブル更新メソッド
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.beginTransaction();
        try
        {
            if (oldVersion == 1)
            {
                createTableVer2(db);
            }
            /*
            else if (oldVersion == 2)
            {
                //まだない
            }
            */
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }
}
