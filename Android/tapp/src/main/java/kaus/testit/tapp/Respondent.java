package kaus.testit.tapp;

import java.util.ArrayList;
import java.util.List;

public class Respondent {

    private int id;
    private  String firstName;
    private  String lastName;
    private String respondentNumber;
    private  int serverId;
    private int groupServerId;

    Respondent(int id, int groupServerId, int serverId, String respondentNumber, String firstName, String lastName){
        this.id = id;
        this.groupServerId = groupServerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.serverId = serverId;
        this.respondentNumber = respondentNumber;
    }

    int getServerGroupId() {
        return groupServerId;
    }

    public int getId(){
        return this.id;
    }

    String getFirstName(){
        return this.firstName;
    }

    String getLastName(){
        return this.lastName;
    }

    int getServerId(){
        return this.serverId;
    }

    String getRespondentNumber() {return this.respondentNumber; }

    String serialize(){
        return  String.format("%s;%s;%s;%s;%s;%s",
                this.id,
                this.groupServerId,
                this.serverId,
                this.respondentNumber,
                this.firstName,
                this.lastName);
    }

    private static Respondent deserialize(String data){
        String [] items = data.split(";");

        return new Respondent(0, Integer.parseInt(items[1]),Integer.parseInt(items[0]), items[2],items[3],items[4]);
    }

    static List<Respondent> deserializeAllRespondents(String data){
        String [] items= data.split("\\|");

        List<Respondent> respondents = new ArrayList<>();
        for (String item : items) {
            respondents.add(deserialize(item));
        }
        return respondents;
    }


    public void setId(int id) {
        this.id = id;
    }
}
