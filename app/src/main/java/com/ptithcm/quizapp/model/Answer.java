package com.ptithcm.quizapp.model;

public class Answer {
    private String answerID;
    private String answerContent;

    public Answer(String answerID, String answerContent) {
        this.answerID = answerID;
        this.answerContent = answerContent;
    }

    public Answer() {
    }

    public String getAnswerID() {
        return answerID;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerID(String answerID) {
        this.answerID = answerID;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "answerID='" + answerID + '\'' +
                ", answerContent='" + answerContent + '\'' +
                '}';
    }
}
