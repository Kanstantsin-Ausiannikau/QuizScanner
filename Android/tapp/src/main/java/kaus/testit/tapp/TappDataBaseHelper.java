package kaus.testit.tapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by home on 31.03.2016.
 */

public class TappDataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TappData";

    private static final int DATABASE_VERSION = 18;

    private static final String CREATE_TABLE_ANSWERS = "CREATE TABLE Answers (Id integer primary key autoincrement, "
            + "Date text not null, QuizID integer, AnswerText text not null, IsSync integer, RespondentID integer);";

    private static final String CREATE_TABLE_RESPONDENTS = "CREATE TABLE Respondents (Id integer primary key autoincrement, "
            + "GroupId INTEGER, ServerId INTEGER, RespondentNumber text, FirstName text, LastName text);";

    private static final String CREATE_TABLE_QUIZES = "CREATE TABLE \"Quizes\" ( `Id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `ServerId` INTEGER,"
            +" `Title` TEXT, `AnswerText` TEXT, `QuizNumber` TEXT, `QuizVersion` INTEGER, `ParentQuizId` INTEGER )";

    private static final String CREATE_TABLE_GROUPS_QUIZES = "CREATE TABLE \"GroupsQuizes\" ( `Id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            +"`GroupsId` INTEGER, `QuizesId` INTEGER, FOREIGN KEY(`QuizesId`) REFERENCES `Quizes`(`Id`), FOREIGN KEY(`GroupsId`) REFERENCES `Groups`(`Id`) );";

    private static final String CREATE_TABLE_GROUPS = "CREATE TABLE \"Groups\" ( `Id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + "`GroupName` TEXT NOT NULL, `ServerId` INTEGER );";

    private static final String CREATE_TABLE_SETTINGS = "CREATE TABLE Settings (Id integer primary key autoincrement, "
            + "LastSyncDate timestamp text not null, UserID int);";

    public TappDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ANSWERS);
        db.execSQL(CREATE_TABLE_QUIZES);
        db.execSQL(CREATE_TABLE_RESPONDENTS);
        db.execSQL(CREATE_TABLE_GROUPS);
        db.execSQL(CREATE_TABLE_GROUPS_QUIZES);
        db.execSQL(CREATE_TABLE_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.w(TappDataBaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS Answers");
        database.execSQL("DROP TABLE IF EXISTS Respondents");
        database.execSQL("DROP TABLE IF EXISTS Quizes");
        database.execSQL("DROP TABLE IF EXISTS Groups");
        database.execSQL("DROP TABLE IF EXISTS GroupsQuizes");
        database.execSQL("DROP TABLE IF EXISTS Settings");
        onCreate(database);
    }

    public  SQLiteDatabase getDbInstance(){
        return this.getReadableDatabase();
    }

    private int getQuizID(String quizNumber){
        String Query = "Select id from Quizes where QuizNumber LIKE '" + String.valueOf(quizNumber)+"'";
        Cursor cursor = getReadableDatabase().rawQuery(Query, null);

        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            return Integer.valueOf(cursor.getString(0));
        }
        return -1;
    }

    private String getQuizNumber(String quizId){


        String Query = "Select QuizNumber from Quizes where id =" + quizId;
        Cursor cursor = getReadableDatabase().rawQuery(Query, null);

        //Cursor cursor = getReadableDatabase().query("Quizes",new String[]{"id"},"QuizNumber=?",new String[]{quizNumber},null,null,null);

        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            return cursor.getString(0);
        }
        return null;
    }

    private String getAnswerText(int [] data){
        String str="";

        for(int i=0;i<data.length;i++){
            str=str+String.valueOf(data[i])+";";
        }

        return str;
    }

    private boolean isQuizExist(int quizesID) {
        return true;
    }

    private int getRespondentID(String respondentNumber) {

        Cursor cursor;
        cursor = getReadableDatabase().query("Respondents",new String[]{"id"},"RespondentNumber=?",new String[]{respondentNumber},null,null,null);

        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            return Integer.valueOf(cursor.getString(0));
        }
        return  -1;
    }

    public void insertQuizes(List<Quiz> quizes) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from Quizes");

        if (quizes.size()==0){
            return;
        }

            for (int i=0;i<quizes.size();i++){
                ContentValues values = new ContentValues();

                values.put("ServerId",  quizes.get(i).getServerId());
                values.put("ParentQuizId",quizes.get(i).getParentQuizId());
                values.put("QuizNumber", quizes.get(i).getQuizNumber());
                values.put("Title", quizes.get(i).getTitle());
                values.put("QuizVersion",quizes.get(i).getQuizVersion());
                values.put("AnswerText", quizes.get(i).getAnswerText());

                int id = (int)db.insert("Quizes", null,values);
                quizes.get(i).setId(id);
            }
    }

    public void insertRespondents(List<Respondent> respondents) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from Respondents");

        if (respondents.size()==0){
            return;
        }

        for (int i=0;i<respondents.size();i++){
            ContentValues values = new ContentValues();

            values.put("ServerId", respondents.get(i).getServerId());
            values.put("GroupId", getGroupIdFromServerId(respondents.get(i).getServerGroupId()));
            values.put("RespondentNumber", respondents.get(i).getRespondentNumber());
            values.put("FirstName", respondents.get(i).getFirstName());
            values.put("LastName", respondents.get(i).getLastName());

            int id = (int)db.insert("Respondents", null,values);

            respondents.get(i).setId(id);
        }
    }

    public List<Answer> getNewAnswers() {
        List<Answer> answers = new ArrayList<Answer>();

        Cursor cursor = getReadableDatabase().rawQuery("select * from Answers ORDER BY id DESC LIMIT 50",null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            answers.add(new Answer(Long.parseLong(cursor.getString(0)),
                    getQuizNumber(cursor.getString(2)),
                    getRespondentNumber(cursor.getString(5)),
                    Answer.deserializeAnswer(cursor.getString(3)),
                    Boolean.getBoolean(cursor.getString(4)),
                    cursor.getString(1),
                    null
                    ));

            cursor.moveToNext();
        }
        return  answers;
    }

    private String getRespondentNumber(String respondentID) {
        if (respondentID.equals("-1")){
            return "";
        }

        String Query = "Select RespondentNumber from Respondents where Id LIKE '" + String.valueOf(respondentID)+"'";
        Cursor cursor = getReadableDatabase().rawQuery(Query, null);

        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            return cursor.getString(0);
        }
        return "";
    }

    public int[][] getAnswersByQuizNumber(String quizNumber){
        SQLiteDatabase db = this.getReadableDatabase();

        String Query = "Select AnswerText from Quizes where QuizNumber LIKE '" + String.valueOf(quizNumber)+"'";
        Cursor cursor = db.rawQuery(Query, null);

        int[][] answers = null;
        if (cursor.getCount()>0) {
            cursor.moveToFirst();

            answers = new int[45][];

            String[] rightAnswers = cursor.getString(0).split("%");

            for (int i = 0; i < rightAnswers.length; i++) {
                String[] items = rightAnswers[i].split(",");
                answers[i] = new int[5];
                for (int j = 0; j < 5; j++) {
                    answers[i][j] = Integer.parseInt(items[j]);
                }

            }
        }

        return answers;
    }

    public long insertAnswer(Answer answer) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //values.put("Id",  answer.getId());
        values.put("Date", answer.getDate());

        String quizNumber = answer.getQuizNumber();
        int quizId = getQuizID(quizNumber);
        if (quizId>0) {
            values.put("QuizID", quizId);
        }
        else {
            return  -1;
        }
        values.put("AnswerText", Answer.serializeAnswers(answer.getAnswers()));
        values.put("isSync",answer.getIsSync());

        int respondentId = getRespondentID(answer.getRespondentNumber());
        values.put("RespondentID",respondentId);

        long id = db.insert("Answers", null,values);

        return id;
    }

    public void insertGroups(List<Group> groups) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from Groups");

        if (groups.size()==0){
            return;
        }

        for (int i=0;i<groups.size();i++){

            ContentValues values = new ContentValues();

            values.put("GroupName", groups.get(i).getGroupName());

            if (isGroupExist(groups.get((i)))) {
                db.update(
                        "Groups",
                        values,
                        "ServerId = ?",
                        new String[] {String.valueOf(groups.get(i).getServerId())});
            }
            else {
                values.put("ServerId", groups.get(i).getServerId());

                groups.get(i).setId((int) db.insert("Groups", null, values));
            }
        }
    }

    private boolean isGroupExist(Group group) {
        SQLiteDatabase db = this.getReadableDatabase();

        String Query = "Select * from Groups where ServerId = " + group.getServerId();
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void insertGroupsQuizes(List<GroupQuiz> groupsQuizes) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from GroupsQuizes");

        if (groupsQuizes.size()==0){
            return;
        }

        for (int i=0;i<groupsQuizes.size();i++){

            int quizServerId = getQuizIdFromServerId(groupsQuizes.get(i).getQuizId());
            int groupServerId = getGroupIdFromServerId(groupsQuizes.get(i).getGroupId());

            if (quizServerId>0&&groupServerId>0){
                ContentValues values = new ContentValues();
                values.put("GroupsId", quizServerId);
                values.put("QuizesId", groupServerId);

                db.insert("GroupsQuizes", null,values);
            }
        }
    }

    public int getGroupIdFromServerId(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String Query = "Select Id from Groups where ServerId = " + groupId;
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        else{
            return  -1;
        }

    }

    private int getQuizIdFromServerId(int quizId) {
        //SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = getReadableDatabase().query("Quizes",new String[]{"id"},"ServerId=?",new String[]{String.valueOf(quizId)},null,null,null);

        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        else{
            return  -1;
        }
    }

    public List<Group> getGroups() {
        List<Group> groups = new ArrayList<Group>();

        Cursor cursor = getReadableDatabase().rawQuery("select * from Groups",null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            groups.add(
                    new Group(
                            Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(2)),
                            cursor.getString(1)
                            ));

            cursor.moveToNext();
        }
        return  groups;
    }

    public List<Respondent> getRespondents(int groupId) {
        List<Respondent> respondents = new ArrayList<Respondent>();

        Cursor cursor = getReadableDatabase().rawQuery("select * from Respondents where GroupId=?",new String[]{Integer.toString(groupId)});// where GroupId=?",new String[]{Integer.toString(groupId)});

        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            respondents.add(
                    new Respondent(
                            Integer.parseInt(cursor.getString(0)),
                            getServerGroupIdFromGroupId(Integer.parseInt(cursor.getString(1))),
                            Integer.parseInt(cursor.getString(2)),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5)
                    ));

            cursor.moveToNext();
        }
        return  respondents;
     }

    private int getServerGroupIdFromGroupId(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String Query = "Select ServerId from Groups where Id = " + groupId;
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        else{
            return  -1;
        }
    }

    public List<Quiz> getQuizs() {
        List<Quiz> quizs = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery("select * from Quizes",null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            quizs.add(
                    new Quiz(
                            Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(1)),
                            Integer.parseInt(cursor.getString(6)),
                            cursor.getString(4),
                            cursor.getString(2),
                            Integer.parseInt(cursor.getString(5)),
                            cursor.getString(3)
                    ));

            cursor.moveToNext();
        }
        return  quizs;
    }

    public String GetRespondentNameByRespondentNumber(String respondentNumber) {
        String Query = "Select FirstName, LastName from Respondents where RespondentNumber LIKE '" + respondentNumber +"'";
        Cursor cursor = getReadableDatabase().rawQuery(Query, null);

        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            return cursor.getString(0)+" "+cursor.getString(1);
        }
        return "";
    }
}


