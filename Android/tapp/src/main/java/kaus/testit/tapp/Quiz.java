package kaus.testit.tapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ausiannikau on 17.11.2017.
 */

public class Quiz {

    private int id;
    private int serverId;
    private int parentQuizId;
    private String quizNumber;
    private int quizVersion;
    private String title;
    private String answerText;


    //private int [][] answers;

    public Quiz(int id, int serverId, int parentQuizId, String quizNumber, String title, int quizVersion, String answerText){
        this.id = id;
        this.serverId = serverId;
        this.parentQuizId = parentQuizId;
        this.quizNumber = quizNumber;
        //this.answers = answers;
        this.title = title;
        this.quizVersion = quizVersion;
        this.answerText = answerText;
    }

    public int[][] getAnswers(){
        String [] answers = this.answerText.split("%");

        int [][] ans = new int[answers.length][];
        for(int i=0;i<ans.length;i++){
            String [] items = this.answerText.split(",");

            ans[i] = new int[5];

            ans[i][0] = Integer.parseInt(items[0]);
            ans[i][1] = Integer.parseInt(items[1]);
            ans[i][2] = Integer.parseInt(items[2]);
            ans[i][3] = Integer.parseInt(items[3]);
            ans[i][4] = Integer.parseInt(items[4]);
        }

        return ans;
    }

    int getServerId() { return  this.serverId; }

    int getParentQuizId() {return this.parentQuizId; }

    int getQuizId(){
        return  this.id;
    }

    String getQuizNumber(){
        return this.quizNumber;
    }

    int getQuizVersion(){
        return  quizVersion;
    }

    String getAnswerText() {return this.answerText; }

    public void setId(int id) { this.id = id; }


    String serialize(){
        return String.format("%s;%s;%s;%s;%s;%s;%s",
                this.id,
                this.serverId,
                this.parentQuizId,
                this.quizNumber,
                this.title,
                this.quizVersion,
                this.answerText);
    }

    String getTitle(){
        return title;
    }

    private static Quiz deserialize(String data){
        String [] items = data.split(";");

        return new Quiz(
               Integer.parseInt(items[0]),
               Integer.parseInt(items[1]),
               Integer.parseInt(items[2]),
               items[3],
               items[4],
               Integer.parseInt(items[5]),
               items[6]
               );
    }

    static List<Quiz> deserializeAllQuizes(String data){
        String [] strQuizes = data.split("\\|");

        List<Quiz> quizes = new ArrayList<>();

        for (String strQuize : strQuizes) {
            quizes.add(deserialize(strQuize));
        }

        return quizes;
    }

}
