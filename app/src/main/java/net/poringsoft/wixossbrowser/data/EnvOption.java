package net.poringsoft.wixossbrowser.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.poringsoft.wixossbrowser.utils.PSDebug;

/**
 * 設定情報保持クラス
 * Created by mry on 2014/04/29.
 */
public class EnvOption {
    //定数
    //---------------------------------------------------------
    //通信関連
    public static final String NET_GET_AGENT = "Mozilla/5.0 (Linux; Android; ja-jp;)";
    public static final int NET_GET_TIMEOUT = 10000;

    //カードリスト関連
    public static final String KEY_CARD_LIST_SELECT_POS = "card_list_select_pos";   //カードリストの選択位置
    public static final String KEY_CARD_LIST_SORT_TYPE = "card_list_sort_type";     //カードリストのソート方法
    public static final String KEY_CARD_LIST_SORT_TYPE_DECK = "card_list_sort_type_deck";   //マイデッキカードリストのソート方法
    public static final String KEY_CARD_LIST_COLOR_DECK = "card_list_color_deck";   //カードリストのカード名の色をデッキに合わせるかどうか
    public static final String KEY_CARD_LIST_ASYNC_DEL_IMAGE = "card_list_async_del_image"; //カードリストの画像非同期読み込みの際に一度表示をクリアーするかどうか
    public static final String KEY_CARD_LIST_SHOW_COUNT= "card_list_show_count";    //カード数をナビゲーションに表示するかどうか
    public static final String KEY_CARD_LIST_STACK_FRAGMENT = "card_list_stack_fragment";   //カードリストの画面切り替えをスタックする
    public static final String KEY_CARD_LIST_SORT_USE_DECK_ = "card_list_sort_use_deck";    //マイデッキ用のソートを使用するかどうか

    //カード詳細関連
    public static final String KEY_CARD_INFO_SHARE_ADD_WIXOSS = "card_info_share_add_wixoss";   //カード検索時の接頭語

    //マイデッキ編集関連
    public static final String KEY_EDIT_DECK_SHARE_SHOW_LEVEL = "edit_deck_share_show_level";   //レベルを表示
    public static final String KEY_EDIT_DECK_SHARE_SHOW_COUNT = "edit_deck_share_show_count";   //枚数を表示
    public static final String KEY_EDIT_DECK_SHARE_MOVE_LAST = "edit_deck_share_move_last";     //枚数を最後に移動

    //検索関連
    public static final String KEY_SEARCH_INCLUDE_BODY_TEXT = "search_include_body_text";       //検索対象を本文（カード情報・FAQ）も含める

    //共通メソッド
    //---------------------------------------------------------
    /**
     * 指定した文字列を設定ファイルに保存する
     * @param context コンテキスト
     * @param key 保存キー
     * @param value 保存値
     */
    private static void putString(Context context, String key, String value) {
        PSDebug.d("key=" + key + " value=" + value);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(key, value).commit();
    }

    /**
     * 設定ファイルに保存されている文字列を取得する
     * @param context コンテキスト
     * @param key 保存キー
     * @param def 保存されていなかったときのデフォルト値
     * @return 取得したデータ
     */
    private static String getString(Context context, String key, String def) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String result = pref.getString(key, def);
        PSDebug.d("key=" + key + " value=" + result + " def=" + def);
        return result;
    }

    /**
     * 指定した整数値を設定ファイルに保存する
     * @param context コンテキスト
     * @param key 保存キー
     * @param value 保存値
     */
    private static void putInt(Context context, String key, int value) {
        PSDebug.d("key=" + key + " value=" + value);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(key, value).commit();
    }

    /**
     * 設定ファイルに保存されている整数値を取得する
     * @param context コンテキスト
     * @param key 保存キー
     * @param def 保存されていなかったときのデフォルト値
     * @return 取得したデータ
     */
    private static int getInt(Context context, String key, int def) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int result = pref.getInt(key, def);
        PSDebug.d("key=" + key + " value=" + result + " def=" + def);
        return result;
    }

    /**
     * 指定した論理値を設定ファイルに保存する
     * @param context コンテキスト
     * @param key 保存キー
     * @param value 保存値
     */
    private static void putBoolean(Context context, String key, boolean value) {
        PSDebug.d("key=" + key + " value=" + value);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(key, value).commit();
    }

    /**
     * 設定ファイルに保存されている論理値を取得する
     * @param context コンテキスト
     * @param key 保存キー
     * @param def 保存されていなかったときのデフォルト値
     * @return 取得したデータ
     */
    private static boolean getBoolean(Context context, String key, boolean def) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean result = pref.getBoolean(key, def);
        PSDebug.d("key=" + key + " value=" + result + " def=" + def);
        return result;
    }

    //カードリスト関連
    //---------------------------------------------------------
    /**
     * カード一覧画面の並び替え順を設定する
     * @param context コンテキスト
     * @param sortType ソート種別（SqlSelectHelper.SELECT_CARD_SORT_～）
     */
    public static void putCardListSortType(Context context, int sortType) {
        putInt(context, KEY_CARD_LIST_SORT_TYPE, sortType);
    }

    /**
     * カード一覧画面の並び替え順を取得する
     * @param context コンテキスト
     * @return 並び替え順（SqlSelectHelper.SELECT_CARD_SORT_～）
     */
    public static int getCardListSortType(Context context) {
        return getInt(context, KEY_CARD_LIST_SORT_TYPE, SqlSelectHelper.SELECT_CARD_SORT_MODEL_NUMBER_ASC);
    }

    /**
     * カード一覧でのマイデッキ時の並び替え順を設定する
     * @param context コンテキスト
     * @param sortType ソート種別（SqlSelectHelper.SELECT_CARD_SORT_～）
     */
    public static void putCardListSortTypeDeck(Context context, int sortType) {
        putInt(context, KEY_CARD_LIST_SORT_TYPE_DECK, sortType);
    }

    /**
     * カード一覧でのマイデッキ時の並び替え順を設定する
     * @param context コンテキスト
     * @return 並び替え順（SqlSelectHelper.SELECT_CARD_SORT_～）
     */
    public static int getCardListSortTypeDeck(Context context) {
        return getInt(context, KEY_CARD_LIST_SORT_TYPE_DECK, SqlSelectHelper.SELECT_CARD_SORT_KIND_DESC);
    }


    /**
     * リストのカード名をデッキ色に合わせるかどうか
     * @param context コンテキスト
     * @return デッキ色に合わせるかどうか（合わせるときはtrue）
     */
    public static boolean getCardListColorDeck(Context context) {
        return getBoolean(context, KEY_CARD_LIST_COLOR_DECK, false);
    }

    /**
     * リストの画像読み込み時に読み込み前に画像を削除を行うかどうか
     * ※ 画像読み込みは非同期で行われるためリスト項目表示時に遅れて表示される。
     * そのため一度別画像読み込み済みのImageViewの場合はその画像が残った状態が一瞬でる場合がある
     * trueの場合は画像をいったん消すこと（ただし処理は遅くなる）
     * @param context コンテキスト
     * @return 画像読み込み前にキャッシュを削除するときはtrue
     */
    public static boolean getCardListAsyncImageDel(Context context) {
        return getBoolean(context, KEY_CARD_LIST_ASYNC_DEL_IMAGE, false);
    }

    /**
     * ナビゲーションメニュー項目ごとのカード枚数を表示するかどうか
     * @param context コンテキスト
     * @return 表示するときはtrue
     */
    public static boolean getCardListShowCount(Context context) {
        return getBoolean(context, KEY_CARD_LIST_SHOW_COUNT, false);
    }

    /**
     * カードリスト画面の切り替えをスタックするかどうか
     * @param context コンテキスト
     * @return スタックするときはtrue
     */
    public static boolean getCardListStackFragment(Context context) {
        return getBoolean(context, KEY_CARD_LIST_STACK_FRAGMENT, false);
    }

    /**
     * カードリスト画面の並び替え設定をマイデッキで別で保持するかどうか
     * @param context コンテキスト
     * @return マイデッキを別の設定で保持する場合はtrue
     */
    public static boolean getCardListSortUseDeck(Context context) {
        return getBoolean(context, KEY_CARD_LIST_SORT_USE_DECK_, true);
    }

    /**
     * カードリスト画面のソートタイプを取得する（マイデッキ・カテゴリの自動判別対応版）
     * @param context コンテキスト
     * @param searchText カードリスト画面の検索文字列
     * @return ソートタイプ
     */
    public static int getCardListSortType(Context context, String searchText) {
        int sortType;
        if (EnvOption.getCardListSortUseDeck(context) && SqlSelectHelper.isDeckSearch(searchText)) {
            sortType =EnvOption.getCardListSortTypeDeck(context);
        }
        else {
            sortType = EnvOption.getCardListSortType(context);
        }
        return sortType;
    }

    //カード詳細関連
    //---------------------------------------------------------
    /**
     * カードデータの共有の際に検索語の先頭にWIXOSSを付与するかどうか
     * @param context コンテキスト
     * @return 付与するときはtrue
     */
    public static boolean getCardInfoShareAddWixoss(Context context) {
        return getBoolean(context, KEY_CARD_INFO_SHARE_ADD_WIXOSS, true);
    }


    //マイデッキ編集関連
    //--------------------------------------------------------
    /**
     * デッキ共有時にレベルを表示
     * @param context コンテキスト
     * @return 表示するときtrue
     */
    public static boolean getEditDeckShareShowLevel(Context context) {
        return getBoolean(context, KEY_EDIT_DECK_SHARE_SHOW_LEVEL, true);
    }

    /**
     * デッキ共有時に枚数を表示
     * @param context コンテキスト
     * @return 表示するときtrue
     */
    public static boolean getEditDeckShareShowCount(Context context) {
        return getBoolean(context, KEY_EDIT_DECK_SHARE_SHOW_COUNT, true);
    }

    /**
     * デッキ共有時の枚数を最後に表示
     * @param context コンテキスト
     * @return 最後に移動するときはtrue、先頭に移動するときはfalse
     */
    public static boolean getEditDeckShareMoveLast(Context context) {
        return getBoolean(context, KEY_EDIT_DECK_SHARE_MOVE_LAST, false);
    }

    //検索関連
    //-----------------------------------------------------------
    /**
     * カード検索時に本文とFAQを検索対象に加える
     * @param context コンテキスト
     * @return 検索対象に加えるときはtrue
     */
    public static boolean getSearchIncludeBodyText(Context context) {
        return getBoolean(context, KEY_SEARCH_INCLUDE_BODY_TEXT, false);
    }


}
