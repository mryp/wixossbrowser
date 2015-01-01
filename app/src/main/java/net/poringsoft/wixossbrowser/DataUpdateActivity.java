package net.poringsoft.wixossbrowser;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import net.poringsoft.wixossbrowser.utils.PSDebug;

/**
 * 内部データ更新用アクティビティ
 * Created by mry on 2014/04/29.
 */
public class DataUpdateActivity extends ActionBarActivity {
    //定数
    //----------------------------------------------------------------------------
    public static final int RESULT_IS_UPDATE = RESULT_FIRST_USER;   //更新あり

    //フィールド
    //-----------------------------------------------------------------------------

    //メソッド
    //-----------------------------------------------------------------------------
    /**
     * 画面起動
     * @param savedInstanceState セーブデータ
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_update);
        PSDebug.d("call");

        restoreActionBar();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, DataUpdateFragment.newInstance())
                .commit();
    }

    /**
     * アクションバー設定
     */
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.action_update_list);
    }
}
