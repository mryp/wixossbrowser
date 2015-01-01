package net.poringsoft.wixossbrowser.utils;

import net.poringsoft.wixossbrowser.data.EnvOption;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 画像ダウンロード処理クラス
 * Created by mry on 2014/04/29.
 */
public class ImageDownload {

    /**
     * バッファサイズ
     */
    private static final int BUFF_SIZE = 1024*20;

    /**
     * 指定URLにあるファイルをダウンロードし、指定パスに保存する
     * @param url 取得元URL
     * @param filePath 保存先ファイルパス
     * @return 成功時はtrue
     */
    public static boolean startSync(String url, String filePath)
    {
        boolean ret = true;
        InputStream is = null;
        try
        {
            is = getInputStream(url);
            if (is != null)
            {
                saveImageFile(is, filePath);
            }
            else
            {
                ret = false;
            }
        }
        catch (Exception downErr)
        {
            PSDebug.d("画像ファイルダウンロードエラー err=" + downErr.toString());
            downErr.printStackTrace();
            return false;
        }
        finally
        {
            if (is != null)
            {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    ret = false;
                }
            }
        }

        return ret;
    }

    /**
     * ダウンロード用ストリームを取得する
     * @param urlText  ダウンロードURL文字列
     */
    private static InputStream getInputStream(String urlText)
    {
        InputStream inputStream = null;
        HttpURLConnection con;
        try
        {
            URL url = new URL(urlText);
            con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setConnectTimeout(EnvOption.NET_GET_TIMEOUT);
            con.setReadTimeout(EnvOption.NET_GET_TIMEOUT);
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", EnvOption.NET_GET_AGENT);
            con.setRequestProperty("Accept-Language", "ja");
            con.setRequestProperty("Connection", "close");
            con.setRequestProperty("Accept", "*/*");
            con.setRequestProperty("Referer", urlText);

            con.connect();	//接続開始
            int resCode = con.getResponseCode();	//返り値チェック
            switch (resCode)
            {
                case HttpURLConnection.HTTP_OK:			//200	取得成功
                    inputStream = con.getInputStream();
                    break;
                default:
                    break;
            }
        }
        catch (IOException ioerr)
        {
            PSDebug.d("getDownloadInputStream IOException");
            ioerr.printStackTrace();
            inputStream = null;
        }
        catch (OutOfMemoryError memerr)
        {
            PSDebug.d("getDownloadInputStream OutOfMemoryError");
            memerr.printStackTrace();
            inputStream = null;
        }
        catch (Exception err)
        {
            PSDebug.d("getDownloadInputStream Exception");
            err.printStackTrace();
            inputStream = null;
        }

        return inputStream;
    }

    /**
     * ストリームから読み出し指定場所にファイルを保存する
     * @param in 入力用ストリーム
     * @param filePath 保存先ファイルパス
     * @throws IOException スロー
     */
    private static void saveImageFile(InputStream in, String filePath) throws IOException
    {
        FileOutputStream out = new FileOutputStream(filePath, false);
        //int writeSize = 0;
        byte[] bytes = new byte[BUFF_SIZE];
        while(true)
        {
            int readSize = in.read(bytes);
            if (readSize <= 0)
            {
                break;
            }
            //writeSize += readSize;
            out.write(bytes, 0, readSize);
        }

        out.close();
    }

}
