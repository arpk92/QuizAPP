package com.snowhillapps.brainspire.model;

import java.util.ArrayList;

public class Question {
    public int id;
    private String question, note, level,trueAns,image,ansOption,selectedAns,langId;
    public boolean isAttended;
    private ArrayList<String> options = new ArrayList<String>();

    public Question() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Question(String question) {
        super();
        this.question = question;
    }

    public String getLangId() {
        return langId;
    }

    public void setLangId(String langId) {
        this.langId = langId;
    }

    public String getSelectedAns() {
        return selectedAns;
    }

    public void setSelectedAns(String selectedAns) {
        this.selectedAns = selectedAns;
    }

    public boolean isAttended() {
        return isAttended;
    }

    public void setAttended(boolean attended) {
        isAttended = attended;
    }

    public String getAnsOption() {
        return ansOption;
    }

    public void setAnsOption(String ansOption) {
        this.ansOption = ansOption;
    }

    public String getQuestion() {
        return question;
    }

    public boolean addOption(String option) {
        return this.options.add(option);
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public String getTrueAns() {
        return trueAns;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTrueAns(String trueAns) {
        this.trueAns = trueAns;
    }


}
