package net.poringsoft.wixossbrowser.data;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 環境パス関連クラス
 * Created by mry on 2014/05/02.
 */
public class EnvPath {
    //定数
    //---------------------------------------------------------
    private static final String NAME_NOMEDIA_FILE = ".nomedia";	//画像非表示用ファイル
    private static final String NAME_CARD_IMAGE_DIR = "card";	//カード画像
    private static final String NAME_MAIN_DIR = "wixroid";      //メインフォルダ名


    //フィールド
    //---------------------------------------------------------
    private static String m_rootDir = "";
    private static String m_cardImageDir = "";


    //メソッド
    //---------------------------------------------------------
    /**
     * 初期化
     */
    public static void init() {
        m_rootDir = "";
        m_cardImageDir = "";
    }

    /**
     * ルートディレクトリパスを取得する
     * アプリケーションデータはこのフォルダ以下に保存する
     */
    public static String getRootDirPath() {
        if (m_rootDir.equals("")) {
            File sdcard = Environment.getExternalStorageDirectory();
            if (sdcard == null || sdcard.equals("")) {
                return "";
            }

            m_rootDir = getDirPath(sdcard.getPath() + "/" + NAME_MAIN_DIR);
            createNomeidaFile(m_rootDir);
        }

        return m_rootDir;
    }

    /**
     * NOMEDIAファイルを作成する
     * @param dir 作成するフォルダパス
     */
    private static void createNomeidaFile(String dir) {
        String filePath = dir + NAME_NOMEDIA_FILE;
        File file = new File(filePath);
        if (!file.exists()) {
            try
            {
                //空のファイルを作成する
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * ディレクトリパスを取得する
     * パスに指定したディレクトリが存在しないときは作成する
     * @param dirPath ディレクトリパス
     * @return 作成したディレクトリパス
     */
    private static String getDirPath(String dirPath) {
        if (!dirPath.endsWith("/")) {
            dirPath = dirPath + "/";	//最後は必ず"/"で閉じるようにする
        }

        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();	//フォルダがないときは作成する
        }

        return dirPath;
    }

    /**
     * カード画像保存フォルダパス
     * @return カード画像保存ディレクトリパス
     */
    public static String getCardImageDir() {
        if (m_cardImageDir.equals("")) {
            m_cardImageDir = getDirPath(getRootDirPath() + NAME_CARD_IMAGE_DIR);
        }

        return m_cardImageDir;
    }

    /**
     * カード画像保存ファイルパス
     * @param fileName 画像ファイル名
     * @return 画像ファイルパス
     */
    public static String getCardImageFile(String fileName)
    {
        return getCardImageDir() + fileName;
    }

    /**
     * 相対URLを絶対URLに変換して返す
     * @param baseUrlText ベースとなるURL文字列
     * @param fileUrlText 相対ファイルURL文字列
     * @return 絶対URL文字列
     */
    public static String getAbsoluteUrl(String baseUrlText, String fileUrlText) {
        URL fileUrl = null;
        try {
            URL baseUrl = new URL(baseUrlText);
            fileUrl = new URL(baseUrl, fileUrlText);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (fileUrl == null) {
            return "";
        }

        return fileUrl.toString();
    }
}
