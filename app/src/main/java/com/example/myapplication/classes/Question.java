package com.example.myapplication.classes;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String Response;
    private String Question;
    private ArrayList<String> Incorrect;

    public Question(String response ,ArrayList<String> Incorrect, String question) {
        Response = response;
        Question = question;
        this.Incorrect = Incorrect;

    }

    public Question() {
    }

    public ArrayList<String> getIncorrect() {
        return Incorrect;
    }

    public void setIncorrect(ArrayList<String> incorrect) {
        Incorrect = incorrect;
    }

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }
}
