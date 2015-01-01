package net.poringsoft.wixossbrowser.data;

/**
 * カードのFAQ情報を保持するクラス
 * Created by mry on 2014/04/29.
 */
public class CardFaqInfo {
    //フィールド
    //-------------------------------------------------------
    private String m_question;
    private String m_answer;

    /**
     * 質問内容を取得する
     */
    public String getQuestion() {
        return m_question;
    }

    /**
     * 質問内容を設定する
     */
    public void setQuestion(String question) {
        this.m_question = question;
    }

    /**
     * 回答内容を取得する
     */
    public String getAnswer() {
        return m_answer;
    }

    /**
     * 回答内容を設定する
     */
    public void setAnswer(String answer) {
        this.m_answer = answer;
    }


}
