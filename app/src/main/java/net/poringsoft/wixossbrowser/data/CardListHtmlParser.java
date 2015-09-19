package net.poringsoft.wixossbrowser.data;

import net.poringsoft.wixossbrowser.utils.PSDebug;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * カード一覧画面HTMLの解析
 * Created by mry on 2014/04/29.
 */
public class CardListHtmlParser {
    /**
     * カード一覧URLからカード情報URLの一覧を取得する
     * @param htmlUrl URL文字列
     * @return ダウンロード対象URL文字列リスト
     */
    public static List<String> Parse(String htmlUrl) {
        PSDebug.d("htmlUrl=" + htmlUrl);
        List<String> cardList = new ArrayList<String>();

        Document doc = connectUrl(htmlUrl);
        if (doc == null)
        {
            return cardList;
        }

        //ページ一覧を取得
        List<String> pageUrlList = new ArrayList<String>();
        Elements boxElements = doc.select("ul.simple-pagination li a");
        for (Element e : boxElements)
        {
            String url = e.attr("href");
            pageUrlList.add(htmlUrl + url.replace("?", "&"));
        }

        //ページごとの処理
        for (String pageUrl : pageUrlList)
        {
            PSDebug.d("pageUrl=" + pageUrl);
            Document pageDoc;
            if (pageUrl.endsWith("page=1"))
            {
                pageDoc = doc;  //現在のを再利用する
            }
            else
            {
                pageDoc = connectUrl(pageUrl);
                if (pageDoc == null)
                {
                    return cardList;
                }
            }

            cardList.addAll(parsePage(htmlUrl, pageDoc));
        }

        return cardList;
    }

    /**
     * カードページのURL一覧を取得して返す
     * @param baseUrl ダウロードURL
     * @param doc パースdoc
     * @return URL一覧リスト
     */
    private static List<String> parsePage(String baseUrl, Document doc)
    {
        ArrayList<String> urlList = new ArrayList<String>();
        Elements boxElements = doc.select("tr.table_layout_bg_01 td a");
        for (Element e : boxElements)
        {
            urlList.add(EnvPath.getAbsoluteUrl(baseUrl, e.attr("href")));
        }

        Elements box2Elements = doc.select("tr.table_layout_bg_02 td a");
        for (Element e : box2Elements)
        {
            urlList.add(EnvPath.getAbsoluteUrl(baseUrl, e.attr("href")));
        }

        for (String url : urlList)
        {
            PSDebug.d("cardUrl=" + url);
        }
        return urlList;
    }

    /**
     * 指定したURLからHTMLをダウンロードして返す
     * @param url ダウンロードURL
     * @return パースDOC
     */
    private static Document connectUrl(String url)
    {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent(EnvOption.NET_GET_AGENT)
                    .timeout(EnvOption.NET_GET_TIMEOUT)
                    .get();
        } catch (IOException e) {
            doc = null;
            e.printStackTrace();
        }
        if (doc == null) {
            return null;
        }

        return doc;
    }
}
