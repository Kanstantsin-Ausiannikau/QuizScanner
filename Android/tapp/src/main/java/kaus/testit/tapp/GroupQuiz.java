package kaus.testit.tapp;

import java.util.ArrayList;
import java.util.List;

public class GroupQuiz {
    private int id;
    private  int groupId;
    private int quizId;

    private GroupQuiz(int id, int groupId, int quizId){
        this.id = id;
        this.groupId = groupId;
        this.quizId = quizId;
    }

    public int getId() { return this.id; }
    int getGroupId() { return  this.groupId; }
    int getQuizId() { return this.quizId; }

    static List<GroupQuiz> deserializeAll(String groupsQuizesMessage) {
        String [] items= groupsQuizesMessage.split("\\|");

        List<GroupQuiz> grQuizs = new ArrayList<>();

        for (String item : items) {
            grQuizs.add(deserialize(item));
        }

        return grQuizs;
    }

    private static GroupQuiz deserialize(String item) {
        String [] items = item.split(";");

        return new GroupQuiz(0,Integer.parseInt(items[0]),Integer.parseInt(items[1]));
    }
}
