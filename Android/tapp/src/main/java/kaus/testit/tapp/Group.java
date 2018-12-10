package kaus.testit.tapp;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private int id;
    private String groupName;
    private int serverId;

    public int getId(){ return this.id; }

    public void setId(int id) {this.id=id;}

    String getGroupName() {return  this.groupName; }

    int getServerId() {return  this.serverId;}

    Group(int id, int serverId, String groupName) {
        this.id = id;
        this.serverId = serverId;
        this.groupName = groupName;
    }

    String serialize(){
        return  String.format("%s;%s;%s",this.id, this.serverId, this.groupName);
    }

    static List<Group> deserializeAllGroups(String data) {
        String [] items = data.split("\\|");

        List<Group> groups = new ArrayList<>();

        for (String item : items) {
            Group group = deserialize(item);
            groups.add(group);

        }
        return groups;
    }

    private static Group deserialize(String item) {
        String [] items = item.split(";");

        return new Group(Integer.parseInt(items[0]),
                Integer.parseInt(items[1]),
                items[2]
        );
    }
}
