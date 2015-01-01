package net.poringsoft.wixossbrowser.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.WindowManager;
import android.widget.Toast;

import net.poringsoft.wixossbrowser.data.EnvOption;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ユーティリティ Static クラス
 * Created by mry on 2014/04/29.
 */
public class PSUtils {
    private static Toast s_toast = null;
    private static float s_scale = 0.0F;

    /**
     * トースト！（短め）
     * @param c コンテキスト
     * @param msg 表示メッセージ
     */
    public static void toast(Context c, String msg)
    {
        if (s_toast != null)
        {
            s_toast.cancel();
        }
        s_toast = Toast.makeText(c, msg, Toast.LENGTH_SHORT);
        s_toast.show();
    }

    /**
     * 整数変換を試みて失敗したらデフォルトを返す
     * @param text 数値文字列
     * @param def 失敗時のデフォルト値
     * @return 数値
     */
    public static int tryParseInt(String text, int def)
    {
        int ret = 0;
        try
        {
            ret = Integer.parseInt(text);
        }
        catch (Exception e)
        {
            ret = def;
        }

        return ret;
    }

    /**
     * 整数変換（long）を試みて失敗したらデフォルトを返す
     * @param text 数値文字列
     * @param def 失敗時のデフォルト値
     * @return 数値
     */
    public static long tryParseLong(String text, long def)
    {
        long ret = 0;
        try
        {
            ret = Long.parseLong(text);
        }
        catch (Exception e)
        {
            ret = def;
        }

        return ret;
    }

    /**
     * 論理値変換を試みて失敗したらデフォルトを返す
     * @param text true or false
     * @param def 失敗時のデフォルト値
     * @return 論理値
     */
    public static boolean tryParseBoolean(String text, boolean def)
    {
        boolean ret = false;
        try
        {
            ret = Boolean.parseBoolean(text);
        }
        catch (Exception e)
        {
            ret = def;
        }

        return ret;
    }

    /**
     * 文字列リストを整数リストとして降順で並び替える
     * @param strList
     * @return
     */
    public static void sortStringToIntDesc(List<String> strList)
    {
        Collections.sort(strList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                int i1 = PSUtils.tryParseInt(s1, 0);
                int i2 = PSUtils.tryParseInt(s2, 0);
                if (i1 == i2) {
                    return 0;
                } else if (i1 < i2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    /**
     * DimensionのDP値リソースからピクセル値を取得する
     * @param context コンテキスト
     * @param resourceId リソースID
     * @return ピクセル値
     */
    public static int getPixcelFromDp(Context context, int resourceId) {
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * DIP値をPIXCEL値に変換する
     * @param context コンテキスト
     * @param dip DP値
     * @return ピクセル値
     */
    public static int dipToPixcel(Context context, int dip)
    {
        if (s_scale == 0.0F)
        {
            //未取得の場合は取得する
            s_scale = context.getResources().getDisplayMetrics().density;
        }

        return (int) (dip * s_scale + 0.5f);
    }

    /**
     * ダイアログを表示する
     * @param context コンテキスト
     * @param title タイトル
     * @param text メッセージ
     */
    public static void Alert(Context context, String title, String text)
    {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //何もしない
                    }
                })
                .create();
        dialog.show();
    }

    /**
     * ディスプレイの回転を禁止するかどうか
     * 禁止するときはenabeleをtrueにする
     * @param context コンテキスト
     * @param enable 禁止時はtrue, 有効時はfalse
     */
    public static void DisplayTurnEnable(Activity context, boolean enable)
    {
        if (enable)
        {
            OrientationFix(context, true);		//回転不可
            ScreenSleepEnable(context, false);	//スリープ不可
        }
        else
        {
            OrientationFix(context, false);		//回転許可
            ScreenSleepEnable(context, true);	//スリープ可
        }
    }

    /**
     * 縦横固定の設定をActivityに適用する
     * @param fixOrient 固定するならtrue，回転するように戻すならfalse
     */
    public static void OrientationFix(Activity context, boolean fixOrient)
    {
        if (fixOrient)
        {
            int ori = context.getResources().getConfiguration().orientation;
            if (ori == Configuration.ORIENTATION_LANDSCAPE)
            {
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            else
            {
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        else
        {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    /**
     * スクリーンスリープを許可する
     * @param enable trueの時スリープ可、falseのときスリープ不可
     */
    public static void ScreenSleepEnable(Activity context, boolean enable)
    {
        if (enable)
        {
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 	//スリープ許可
        }
        else
        {
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);		//スリープ禁止
        }
    }


    /**
     * バイナリデータ（画像・Flashなど）を取得しバイト配列で返す
     * @param url ダウンロードURL
     * @return バイナリデータ
     */
    public static byte[] getBinData(final String url)
    {
        HttpGet request = new HttpGet(url);
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setSoTimeout(params, EnvOption.NET_GET_TIMEOUT);
        HttpConnectionParams.setConnectionTimeout(params, EnvOption.NET_GET_TIMEOUT);
        DefaultHttpClient httpClient = new DefaultHttpClient(params);

        byte[] rsult = null;
        try {
            rsult = httpClient.execute(request, new ResponseHandler<byte[]>() {
                @Override
                public byte[] handleResponse(HttpResponse response) throws IOException {
                    switch (response.getStatusLine().getStatusCode()) {
                        case HttpStatus.SC_OK:
                            //バイト配列に変換して返す
                            return EntityUtils.toByteArray(response.getEntity());
                        case HttpStatus.SC_NOT_FOUND:
                            throw new RuntimeException("データなしエラー");
                        default:
                            throw new RuntimeException("通信エラー code=" + response.getStatusLine().getStatusCode());
                    }
                }
            });
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return rsult;
    }
}
