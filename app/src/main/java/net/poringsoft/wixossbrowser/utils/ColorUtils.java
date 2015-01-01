package net.poringsoft.wixossbrowser.utils;

/**
 * カラーデータ取得ユーティリティ
 * Created by mry on 2014/05/01.
 */
public class ColorUtils {

    /**
     * カードの色から色コードを取得する
     * @param colorName 色名（日本語）
     * @return 色
     */
    public static int getColor(String colorName) {
        int color;
        if (colorName.equals("白")) {
            color = 0xFFFBF9D2;
        }
        else if (colorName.equals("赤")) {
            color = 0xFFF04228;
        }
        else if (colorName.equals("青")) {
            color = 0xFF6C9AF9;
        }
        else if (colorName.equals("緑")) {
            color = 0xFF76D25B;
        }
        else if (colorName.equals("黒")) {
            color = 0xFFA460DE;
        }
        else {
            //無色の色
            color = 0xFFF9F9F9;
        }
        return color;
    }

    /**
     * カードデッキによる背景色を取得する
     * @param kindName カード種類
     * @return 色
     */
    public static int getCardDeckBgColor(String kindName) {
        int color;
        if (kindName.equals("ルリグ") || kindName.equals("アーツ")) {
            color = 0xFFDDDDDD;
        }
        else {
            color = 0xFF555555;
        }

        return color;
    }

    /**
     * カードデッキによるテキスト文字列の色を取得する
     * @param kindName カード種別
     * @return 色
     */
    public static int getCardDeckTextColor(String kindName) {
        int color;
        if (kindName.equals("ルリグ") || kindName.equals("アーツ")) {
            color = 0xFF222222;
        }
        else {
            color = 0xFFDDDDDD;
        }

        return color;
    }
}
