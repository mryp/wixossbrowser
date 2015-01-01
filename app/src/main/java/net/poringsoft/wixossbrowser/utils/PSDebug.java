package net.poringsoft.wixossbrowser.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;

/**
 * デバッグ用クラス
 * Created by mry on 2014/04/29.
 */
public class PSDebug {
    /**
     * デバッグメッセージ出力用タグ
     */
    private static final String TAG = "wixroid";

    /**
     * デバッグを行うかどうか
     */
    private static boolean isDebuggable = false;

    /**
     * デバッグ出力を行う
     * @param mes 出力メッセージ
     */
    public static void d(String mes)
    {
        if (isDebuggable) {
            int index = 3;    //スレッド内部やこのメソッドの分を飛ばす
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            String prefix = stack[index].getClassName() + "#" + stack[index].getMethodName() + "[" + stack[index].getLineNumber() + "] ";
            Log.d(TAG, prefix + mes);
        }
    }

    /**
     * デバッグ状態を初期化する
     * @param context コンテキスト
     */
    public static void initDebugFlag(Context context)
    {
        isDebuggable = getDebuggable(context);
    }

    /**
     * デバッグ状態を取得する
     * @param context コンテキスト
     * @return デバッグ時はtrue
     */
    private static boolean getDebuggable(Context context) {
        PackageManager manager = context.getPackageManager();
        if (manager == null) {
            return false;
        }

        ApplicationInfo appInfo;
        try {
            appInfo = manager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE) {
            return true;
        }
        return false;
    }
}
