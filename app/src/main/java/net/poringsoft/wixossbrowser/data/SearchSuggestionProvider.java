package net.poringsoft.wixossbrowser.data;

import android.content.SearchRecentSuggestionsProvider;

/**
 * カード検索用プロバイダ
 * Created by mry on 2014/05/02.
 */
public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    //定数
    public final static String AUTHORITY = "net.poringsoft.wixossbrowser.data.SearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    /**
     * コンストラクタ
     */
    public SearchSuggestionProvider()
    {
        setupSuggestions(AUTHORITY, MODE);
    }
}
