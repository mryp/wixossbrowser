package net.poringsoft.wixossbrowser;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import net.poringsoft.wixossbrowser.utils.PSDebug;

/**
 * デッキデータ編集画面
 * Created by mry on 2014/05/04.
 */
public class DeckDirActivity extends ActionBarActivity {
    //定数
    //-----------------------------------------------------------------------------
    public static final int RESULT_IS_UPDATE = RESULT_FIRST_USER;

    //フィールド
    //-----------------------------------------------------------------------------

    //メソッド
    //-----------------------------------------------------------------------------
    /**
     * コンストラクタ
     * @param savedInstanceState 保存データ
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_dir);
        PSDebug.d("call");

        restoreActionBar();
        setResult(Activity.RESULT_OK);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, DeckDirFragment.newInstance())
                .commit();
    }

    /**
     * アクションバー設定
     */
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.action_edit_deck);
    }

}
