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
 * 商品リスト画面HTMLパーサー
 * Created by mry on 2014/04/29.
 */
public class ProductListHtmlParser {

    /**
     * 商品リストからカードリストURL一覧を取得する
     * @param htmlUrl ダウンロードURL
     * @return 商品リスト
     */
    public static List<ProductInfo> Parse(String htmlUrl) {
        PSDebug.d("htmlUrl=" + htmlUrl);

        List<ProductInfo> productList = new ArrayList<ProductInfo>();
        Document doc;
        try {
            doc = Jsoup.connect(htmlUrl)
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

        Elements boxElements = doc.select("div.column2_box_main_contents h5 a");
        for (Element e : boxElements)
        {
            ProductInfo info = new ProductInfo();
            info.setUrl(EnvPath.getAbsoluteUrl(htmlUrl, e.attr("href")));
            info.setTitle(e.text().trim());
            productList.add(info);
        }

        for (ProductInfo info : productList)
        {
            PSDebug.d("ProductInfo title=" + info.getTitle() + " url=" + info.getUrl());
        }
        return productList;
    }
}
