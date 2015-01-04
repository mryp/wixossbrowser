package net.poringsoft.wixossbrowser.data;

import net.poringsoft.wixossbrowser.utils.ImageDownload;
import net.poringsoft.wixossbrowser.utils.KanamojiCharUtils;
import net.poringsoft.wixossbrowser.utils.PSDebug;
import net.poringsoft.wixossbrowser.utils.PSUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * カードHTMLを解析してカード情報を取得するクラス
 * メモ： http://jsoup.org/apidocs/
 * Created by mry on 2014/04/29.
 */
public class CardHtmlParser {

    /**
     * コンストラクタ
     */
    public CardHtmlParser() {
    }

    /**
     * カード情報URL画面からHTMLを解析してカード情報を返す
     * @param htmlUrl カード情報URL
     * @return カード情報
     */
    public static CardInfo Parse(String htmlUrl)
    {
        PSDebug.d("htmlUrl=" + htmlUrl);
        CardInfo cardInfo = new CardInfo();
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
        if (doc == null)
        {
            PSDebug.d("カードデータ取得失敗");
            return null;
        }

        try
        {
            //カード名
            Elements nameElements = doc.select("div.card_detail_title");
            for (Element e : nameElements)
            {
                String modelNumber = e.select("p").text();
                cardInfo.setModelNumber(modelNumber);
                String[] modelSplit = modelNumber.split("-");
                if (modelSplit.length >= 2)
                {
                    cardInfo.setProductCode(modelSplit[0]);
                }

                cardInfo.setName(e.select("h3").text());
                cardInfo.setNameKana(getCardNameKana(e.select("span.card_detail_kana").text()));
                break;  //1項目だけ使用する
            }
            if (cardInfo.getModelNumber().equals("")) {
                PSDebug.d("カードデータなし（型番が空）");
                return null;
            }

            //レア度
            Elements rarityElements = doc.select("div.card_rarity");
            for (Element e : rarityElements)
            {
                cardInfo.setRarity(e.text());
                break;  //1項目だけ使用する
            }

            //カード情報
            Elements dateElements = doc.select("div.card_detail_date");
            for (Element e : dateElements)
            {
                //カード画像
                Elements imageElements = e.select("p.card_img");
                for (Element image: imageElements)
                {
                    cardInfo.setImageUrl(EnvPath.getAbsoluteUrl(htmlUrl, image.select("img").attr("src")));
                    cardInfo.setIllust(getIllustName(image.text()));
                    break;  //最初の項目のみセットする
                }

                //カードパラメーター
                Elements tableElements = e.select("table tr td");
                int rowNum = 0;
                ArrayList<String> textList = new ArrayList<String>();
                for (Element table : tableElements)
                {
                    switch (rowNum)
                    {
                        case 0:
                            cardInfo.setKind(table.text());
                            break;
                        case 1:
                            cardInfo.setType(table.text());
                            break;
                        case 2:
                            cardInfo.setColor(table.text());
                            break;
                        case 3:
                            cardInfo.setLevel(getIntText(table));
                            break;
                        case 4:
                            cardInfo.setGrowCost(table.text());
                            break;
                        case 5:
                            cardInfo.setCost(table.text());
                            break;
                        case 6:
                            cardInfo.setLimit(getIntText(table));
                            break;
                        case 7:
                            cardInfo.setPower(getIntText(table));
                            break;
                        case 8:
                            cardInfo.setLimitCondition(table.text());
                            break;
                        case 9:
                            cardInfo.setGuard(table.text());
                            break;
                        default:
                            textList.add(getCardMessageText(table).trim());
                            cardInfo.setRegular(cardInfo.getRegular() + getCountImageFile(table, "icon_txt_regular.png"));
                            cardInfo.setArrival(cardInfo.getArrival() + getCountImageFile(table, "icon_txt_arrival.png"));
                            cardInfo.setStarting(cardInfo.getStarting() + getCountImageFile(table, "icon_txt_starting.png"));
                            cardInfo.setLifeburst(cardInfo.getLifeburst() + getCountImageFile(table, "icon_txt_burst.png"));
                            break;
                    }
                    rowNum++;
                }

                cardInfo.setTextList(textList);
                break;
            }

            //FAQ
            ArrayList<CardFaqInfo> faqList = new ArrayList<CardFaqInfo>();
            Elements faqElements = doc.select("div.card_FAQ p");
            for (Element e : faqElements)
            {
                CardFaqInfo faqInfo = new CardFaqInfo();
                Elements questionList = e.select("span.card_ruleFAQ_q");
                for (Element question : questionList)
                {
                    faqInfo.setQuestion(question.text().trim());
                    break;  //最初の項目のみセットする
                }

                Elements answerList = e.select("span.card_ruleFAQ_a");
                for (Element answer : answerList)
                {
                    faqInfo.setAnswer(answer.text().trim());
                    break;  //最初の項目のみセットする
                }

                faqList.add(faqInfo);
            }
            cardInfo.setFaqList(faqList);
        }
        catch (Exception e)
        {
            PSDebug.d("カードデータ解析失敗");
            return null;
        }

        //カード画像
        try {
            File imageFile = new File(EnvPath.getCardImageFile(cardInfo.getImageFileName()));
            if (!imageFile.exists())
            {
                //ダウンロードする
                PSDebug.d("url=" + cardInfo.getImageUrl());
                PSDebug.d("file=" + imageFile.getPath());
                if (!ImageDownload.startSync(cardInfo.getImageUrl(), imageFile.getPath()))
                {
                    PSDebug.d("カード画像ダウンロード保存失敗");
                    return null;
                }
            }
            else
            {
                PSDebug.d("カード画像が既に存在するためスキップ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            PSDebug.d("画像ファイル処理失敗");
            return null;
        }

        PSDebug.d("cardInfo.getProductCode=" + cardInfo.getProductCode());
        PSDebug.d("cardInfo.getModelNumber=" + cardInfo.getModelNumber());
        PSDebug.d("cardInfo.getName=" + cardInfo.getName());
        PSDebug.d("cardInfo.getNameKana=" + cardInfo.getNameKana());
        PSDebug.d("cardInfo.getRarity=" + cardInfo.getRarity());

        PSDebug.d("cardInfo.getImagePath=" + cardInfo.getImageUrl());
        PSDebug.d("cardInfo.getIllust=" + cardInfo.getIllust());

        PSDebug.d("cardInfo.getKind=" + cardInfo.getKind());
        PSDebug.d("cardInfo.getType=" + cardInfo.getType());
        PSDebug.d("cardInfo.getColor=" + cardInfo.getColor());
        PSDebug.d("cardInfo.getLevel=" + cardInfo.getLevel());
        PSDebug.d("cardInfo.getGrowCost=" + cardInfo.getGrowCost());
        PSDebug.d("cardInfo.getCost=" + cardInfo.getCost());
        PSDebug.d("cardInfo.getLimit=" + cardInfo.getLimit());
        PSDebug.d("cardInfo.getPower=" + cardInfo.getPower());
        PSDebug.d("cardInfo.getLimitCondition=" + cardInfo.getLimitCondition());
        PSDebug.d("cardInfo.getGuard=" + cardInfo.getGuard());
        PSDebug.d("cardInfo.getRegular=" + cardInfo.getRegular());
        PSDebug.d("cardInfo.getArrival=" + cardInfo.getArrival());
        PSDebug.d("cardInfo.getStarting=" + cardInfo.getStarting());
        PSDebug.d("cardInfo.getLifeburst=" + cardInfo.getLifeburst());

        for (int i=0; i<cardInfo.getTextList().size(); i++)
        {
            PSDebug.d("cardInfo.getTextInfo[" + i + "]=" + cardInfo.getTextList().get(i));
        }
        for (CardFaqInfo faqInfo : cardInfo.getFaqList())
        {
            PSDebug.d("cardInfo.getFaqList Q=" + faqInfo.getQuestion() + " A=" + faqInfo.getAnswer());
        }

        return cardInfo;
    }

    /**
     * カード名の仮名文字を変換する
     * @param nameKana カード仮名文字部文字列
     * @return 変換した文字列
     */
    private static String getCardNameKana(String nameKana)
    {
        if (nameKana.contains("＜"))
        {
            nameKana = nameKana.replace("＜", "");
            nameKana = nameKana.replace("＞", "");
        }
        return KanamojiCharUtils.hankakuKatakanaToZenkakuKatakana(nameKana);
    }

    /**
     * イラストレーター名を取得する
     * @param illustName イラストレーター名部文字列
     * @return イラストレーター名
     */
    private static String getIllustName(String illustName)
    {
        illustName = illustName.trim();
        int pos = illustName.indexOf(" ");
        if (pos == -1)
        {
            return illustName;
        }

        return illustName.substring(pos+1);
    }

    /**
     * カードメッセージ文字列を取得する
     * @param skill メッセージ文字列
     * @return 変換後文字列
     */
    private static String getCardMessageText(Element skill)
    {
        PSDebug.d("call e=" + skill.toString());
        StringBuilder sb = new StringBuilder();

        for (Node node : skill.childNodes())
        {
            //nodeName()はタグ=タグ名、テキスト=#text
            if (node.nodeName().equals("img"))
            {
                sb.append("[");
                sb.append(node.attr("alt"));
                sb.append("]");
            }
            else if (node.nodeName().equals("br"))
            {
                sb.append("\n");
            }
            else if (node.nodeName().equals("#comment"))
            {
                //コメントは無視する
            }
            else
            {
                sb.append(replaceHtmlText(node.toString().trim()));
            }
        }
        return sb.toString().trim();
    }

    

    private static int getCountImageFile(Element skill, String imageFileName)
    {
        PSDebug.d("call e=" + skill.toString());
        int count = 0;
        for (Node node : skill.childNodes())
        {
            //nodeName()はタグ=タグ名、テキスト=#text
            if (node.nodeName().equals("img"))
            {
                if (node.attr("src").contains(imageFileName))
                {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * HTML特殊文字を変換する
     * @param text 文字列
     * @return 変換後の文字列
     */
    private static String replaceHtmlText(String text) {
        text = text.replaceAll("&times;", "×");

        return text;
    }

    /**
     * レコード文字列から整数にして返す
     * 整数ではなかった場合は-1を返す
     * @param table 数値部
     * @return 整数（変換失敗時は-1）
     */
    private static int getIntText(Element table) {
        String text = table.text();
        if (text.equals("") || text.equals("-"))
        {
            return -1;
        }
        return PSUtils.tryParseInt(text, -1);
    }
}
