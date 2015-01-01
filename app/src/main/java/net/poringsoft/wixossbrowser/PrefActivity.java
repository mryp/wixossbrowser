package net.poringsoft.wixossbrowser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.SearchRecentSuggestions;

import net.poringsoft.wixossbrowser.data.SearchSuggestionProvider;
import net.poringsoft.wixossbrowser.utils.PSUtils;

/**
 * 設定画面
 * Created by mry on 2014/05/03.
 */
public class PrefActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        //検索履歴削除
        PreferenceScreen delSearchHistory = (PreferenceScreen)this.findPreference("privacy_del_search_history");
        if (delSearchHistory != null) {
            delSearchHistory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String text = "検索履歴をすべて削除します。\nよろしいですか？";
                    AlertDialog dialog = new AlertDialog.Builder(PrefActivity.this)
                        .setTitle("検索履歴削除")
                        .setMessage(text)
                        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(PrefActivity.this
                                        , SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
                                suggestions.clearHistory();

                                PSUtils.toast(PrefActivity.this, "検索履歴を削除しました");
                            }
                        })
                        .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //何もしない
                            }
                        })
                        .create();
                    dialog.show();
                    return true;
                }
            });
        }

        //バージョン情報セット
        PreferenceScreen versionScreen = (PreferenceScreen)this.findPreference("version_info");
        if (versionScreen != null) {
            String versionName = "X.X.X";
            PackageManager pm = this.getPackageManager();
            if (pm != null) {
                try {
                    PackageInfo info = pm.getPackageInfo(this.getPackageName(), 0);
                    versionName = info.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    versionName = "X.X.X";
                }
            }
            versionScreen.setTitle("version " + versionName);
            versionScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.url_poringsoft)));
                    startActivity(intent);
                    return true;
                }
            });
        }
    }
}
