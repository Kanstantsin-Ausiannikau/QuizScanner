package kaus.testit.tapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Mat;

public class Answer {

    private long id;
    private String date;
    private String quizNumber;
    private int[][] answers;
    private boolean isSync;
    private String respondentNumber;

    private Mat mImage;

    public Mat getImage() {
        return mImage;
    }

    Answer(long id, String quizNumber, String respondentNumber, int[][] answers, boolean isSync, String date, Mat image){
        this.id = id;
        this.quizNumber = quizNumber;
        this.respondentNumber = respondentNumber;
        this.answers = answers;
        this.isSync = isSync;
        this.date = date;
        this.mImage = image;
    }

    String serializeAnswer(){

        return String.format("%s;%s;%s;%s;%s;%s;",
                this.id,
                this.quizNumber,
                this.respondentNumber,
                serializeAnswers(this.answers),
                this.date,
                this.isSync
                );
    }


    static String serializeAnswers(int[][] answers) {

        String lineSeparator="";
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<answers.length;i++){
            String separator="";
            sb.append(lineSeparator);
            for(int j=0;j<answers[0].length;j++){
                sb.append(separator);
                sb.append(answers[i][j]);
                separator=",";
            }
            lineSeparator="%";
        }
        return sb.toString();
    }

    static  int [][] deserializeAnswer(String answerText){
        int [][] answers = new int[45][5];

        String []lines = answerText.split("%");
        for (int i =0;i<answers.length;i++){
            String [] items = lines[i].split(",");
            for(int j=0;j<answers[0].length;j++){
                answers[i][j] = Integer.parseInt(items[j]);
            }
        }
        return answers;
    }

    String getDate(){
        return this.date;
    }

    public long getId(){
        return this.id;
    }

    String getQuizNumber(){
        return this.quizNumber;
    }

    String getRespondentNumber(){
        return this.respondentNumber;
    }

    boolean getIsSync() {return  this.isSync;}

    public void setId(long id){
        this.id = id;
    }

    int[][] getAnswers(){
        return answers;
    }

    static double calculateAnswerPercentage(Context context, Answer answer){

        TappDataBaseHelper db = new TappDataBaseHelper(context);
        String quizNumber = answer.getQuizNumber();
        int [][] rightAnswers =  db.getAnswersByQuizNumber(quizNumber);
        int [][] answers = answer.getAnswers();

        int allBallsSum = 0;
        int currentSum = 0;
        if (rightAnswers!=null) {
            for (int i = 0; i < rightAnswers.length; i++) {
                if (rightAnswers[i] == null) {
                    break;
                }

                int difficultyBalls = 0;
                int countRightAnswers = 0;

                if(rightAnswers==null){
                    Log.d("Quiz", "rightAnswers - null");
                }

                if(answers==null){
                    Log.d("Quiz", "answers - null");
                }

                for(int j=0;j<5;j++){
                    Log.d("Quiz", String.valueOf(i));
                    Log.d("Quiz", String.valueOf(j));
                    Log.d("Quiz", String.valueOf(answers[i][j]));
                    Log.d("Quiz", String.valueOf(rightAnswers[i][j]));
                    if (isIdenticalAnswers(answers[i][j], rightAnswers[i][j])) {
                        countRightAnswers++;
                    }
                }
                difficultyBalls = getDifficultyAnswer(rightAnswers[i]);
                allBallsSum+= difficultyBalls;
                if (countRightAnswers==5){
                    currentSum += difficultyBalls;
                }
            }
            return (double)currentSum/allBallsSum;
        }
        return -1;
    }
    private static boolean isIdenticalAnswers(int answer, int rightAnswer){

       boolean isIdentical = true;

        if (rightAnswer>0){
            if (answer==0){
                isIdentical = false;
            }
        }
        else {
            if(answer>0){
                isIdentical = false;
            }
        }
        return isIdentical;
    }
    private static int getDifficultyAnswer(int [] line){
        int difficulty = 0;
        for(int i=0;i<line.length;i++){
            if (line[i]>0){
                difficulty = line[i];
                break;
            }
        }
        return difficulty;
    }
}
