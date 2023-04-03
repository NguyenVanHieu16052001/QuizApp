package com.ptithcm.quizapp.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Question implements Serializable {
    public String questionID;
    public String questionContent;
    public String questionType;
    public String questionLevel;
    public String exactAnswer;
    public Bitmap questionImage;

    public Question(String questionID, String questionContent, String questionType, String questionLevel, String exactAnswer, Bitmap questionImage) {
        this.questionID = questionID;
        this.questionContent = questionContent;
        this.questionType = questionType;
        this.questionLevel = questionLevel;
        this.exactAnswer = exactAnswer;
        this.questionImage = questionImage;
    }

    public Question() {
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getQuestionLevel() {
        return questionLevel;
    }

    public String getExactAnswer() {
        return exactAnswer;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public void setQuestionLevel(String questionLevel) {
        this.questionLevel = questionLevel;
    }

    public void setExactAnswer(String exactAnswer) {
        this.exactAnswer = exactAnswer;
    }

    public void setQuestionImage(Bitmap questionImage) {
        this.questionImage = questionImage;
    }

    public Bitmap getQuestionImage() {
        return questionImage;
    }

//    @Override
//    public int compareTo(Question o) {
//        return this.getQuestionID().compareTo(o.getQuestionID());
//    }

    @Override
    public String toString() {
        return "Question{" +
                "questionID='" + questionID + '\'' +
                ", questionContent='" + questionContent + '\'' +
                ", questionType='" + questionType + '\'' +
                ", questionLevel='" + questionLevel + '\'' +
                ", exactAnswer='" + exactAnswer + '\'' +
                ", questionImage=" + questionImage +
                '}';
    }
}
