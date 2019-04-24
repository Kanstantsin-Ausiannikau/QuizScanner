package kaus.testit.tapp;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by home on 31.03.2016.
 */
public class Server {

    //private static final  String URL = "http://192.168.1.7/tit/Android/";
    //private static final  String URL = "http://192.168.1.103/tit/Android/";
    private static final  String URL = "http://test.budny.by/Android/";


    private static final String POST_COMMAND = "GetRequest";
    private static final String HELLO_COMMAND = "HelloRequest";

    private static final String GET_QUIZES_COMMAND = "GetQuizesRequest";
    private static final String SEND_QUIZES_CONFIRMATION_COMMAND = "GetQuizesConfirmationRequest";

    private static final String GET_GROUPS_QUIZES_COMMAND = "GetGroupsQuizesRequest";
    private static final String SEND_GROUPS_QUIZES_CONFIRMATION_COMMAND = "GetGroupsQuizesConfirmationRequest";


    private static final String GET_RESPONDENTS_COMMAND = "GetRespondentsRequest";
    private static final String SEND_RESPONDENTS_CONFIRMATION_COMMAND = "GetRespondentsConfirmationRequest";

    private static final String GET_GROUPS_COMMAND = "GetGroupsRequest";
    private static final String SEND_GROUPS_CONFIRMATION_COMMAND = "GetGroupsConfirmationRequest";

    private static final String SEND_ANSWERS_COMMAND = "SetAnswersRequest";


    private  TappDataBaseHelper db;

    private  Context mContext;

    public Server(Context context) {

        mContext = context;

        db = new TappDataBaseHelper(mContext);
    }
    

    
    private  boolean mIsConnected = false;
    
    public void Connect(String userName, String password){

        mIsConnected = true;


        /*
        mIsConnected = false;

        int _sessionId = 0;

        if (isOnline()) {
            PostDataSender sender = new PostDataSender();

            sender.execute(URL + HELLO_COMMAND, "user", userName, "password", password);

            try {
                String result = sender.get();
                if (result!=null) {
                    _sessionId = Integer.parseInt(result);
                }
                else {
                    _sessionId = 0;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (_sessionId>0){
            mIsConnected = true;
        }

        */
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

   /* public  boolean isConnected() {

        return mIsConnected;
    }

    */

    public boolean SyncServer(String token){

        boolean isSuccessSync = false;

        if (this.isOnline()) {
            String message;
            List<Group> groups = getNewGroupsFromServer(token);
            db.insertGroups(groups);
        }
/*
                message = sendAddGroupsConfirmation(groups, userName, password);
                if (message==null){
                    isSuccessSync = true;
                }
                else {
                    isSuccessSync = message.equals("Ok");
                }

                if (!isSuccessSync) {
                    return isSuccessSync;
                }


            List<Quiz> quizes = getNewQuizesFromServer(userName, password);

                db.insertQuizes(quizes);
                message = sendAddQuizesConfirmation(quizes, userName, password);
            if (message==null){
                isSuccessSync = true;
            }
            else {
                isSuccessSync = message.equals("Ok");
            }
                if(!isSuccessSync){
                    return isSuccessSync;
                }


            List<GroupQuiz> groupsQuizes = getAllFromGroupQuizFromServer(userName, password);

                db.insertGroupsQuizes(groupsQuizes);
                message = sendAddGroupsQuizesConfirmation(groupsQuizes, userName, password);
            if (message==null){
                isSuccessSync = true;
            }
            else {
                isSuccessSync = message.equals("Ok");
            }
                if(!isSuccessSync){
                    return isSuccessSync;
                }

            
            List<Respondent> respondents = getNewRespondentsFromServer(userName, password);

                db.insertRespondents(respondents);
                message = sendAddRespondentsConfirmation(respondents, userName, password);
            if (message==null){
                isSuccessSync = true;
            }
            else {
                isSuccessSync = message.equals("Ok");
            }
                if(!isSuccessSync) {
                    return isSuccessSync;
                }

            List<Answer> answers = db.getNewAnswers();
            message = sendNewAnswers(answers);
            if (message==null){
                isSuccessSync = true;
            }
            else {
                isSuccessSync = message.equals("Ok");
            }
            if(!isSuccessSync) {
                return isSuccessSync;
            }
        }

        */
        return isSuccessSync;
    }

    private String sendAddRespondentsConfirmation(List<Respondent> respondents, String userName, String password) {

        StringBuilder sb = new StringBuilder();

        String separator = "";
        for(int i = 0;i<respondents.size();i++){
            sb.append(separator);
            sb.append(respondents.get(i).serialize());
            separator="|";
        }

        PostDataSender sender = new PostDataSender();
        String sendMessage = null;

        sender.execute(
                URL + SEND_RESPONDENTS_CONFIRMATION_COMMAND,
                "user", userName,
                "password", password,
                "respondents", sb.toString());

        try {
            sendMessage = sender.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return  sendMessage;
    }

    private String sendAddGroupsQuizesConfirmation(List<GroupQuiz> groupsQuizes, String userName, String password) {

        PostDataSender sender = new PostDataSender();
        String sendMessage = null;


            sender.execute(
                    URL + SEND_GROUPS_QUIZES_CONFIRMATION_COMMAND,
                    "user", userName,
                    "password", password,
                    "groupsquizes", String.valueOf(groupsQuizes.size()));

            try {
                sendMessage = sender.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        return  sendMessage;
    }

    private List<GroupQuiz> getAllFromGroupQuizFromServer(String userName, String password) {
        PostDataSender sender = new PostDataSender();
        sender.execute(URL+GET_GROUPS_QUIZES_COMMAND, "user", userName, "password", password);
        String groupsQuizesMessage = null;

        try {
            groupsQuizesMessage = sender.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        List<GroupQuiz> groupsQuizes = new ArrayList<GroupQuiz>();

        if (!(groupsQuizesMessage.isEmpty()||groupsQuizesMessage==null)){
            groupsQuizes = GroupQuiz.deserializeAll(groupsQuizesMessage);
        }

        return  groupsQuizes;
    }

    private String sendAddQuizesConfirmation(List<Quiz> quizes, String userName, String password) {
        StringBuilder sb = new StringBuilder();

        String separator = "";
        for(int i = 0;i<quizes.size();i++){
            sb.append(separator);
            sb.append(quizes.get(i).serialize());
            separator="|";
        }

        PostDataSender sender = new PostDataSender();
        String sendData = sb.toString();
        String sendMessage = null;

        if (sendData.length()!=0) {
            sender.execute(URL + SEND_QUIZES_CONFIRMATION_COMMAND,"user", userName, "password", password,  "quizs", sendData);

            try {
                sendMessage = sender.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return  sendMessage;
    }

    private String sendAddGroupsConfirmation(List<Group> groups, String userName, String password) {
        StringBuilder sb = new StringBuilder();

        String separator = "";
        for(int i = 0;i<groups.size();i++){
            sb.append(separator);
            sb.append(groups.get(i).serialize());
            separator="|";
        }

        PostDataSender sender = new PostDataSender();
        String sendData = sb.toString();
        String sendMessage = null;

        if (sendData.length()!=0) {
            sender.execute(URL + SEND_GROUPS_CONFIRMATION_COMMAND,"user", userName, "password", password,  "groups", sendData);

            try {
                sendMessage = sender.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return  sendMessage;
    }

    private List<Group> getNewGroupsFromServer(String token) {
        PostDataSender sender = new PostDataSender();
        //sender.execute(URL+GET_GROUPS_COMMAND, "token", token);
        sender.execute("http://api.budny.by/api/groups", token);

        String groupsMessage = null;

        try {
            groupsMessage = sender.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        List<Group> groups = new ArrayList<Group>();

        if (!(groupsMessage.isEmpty()||groupsMessage==null)){
            groups = Group.deserializeAllGroups(groupsMessage);
        }

        return  groups;
    }

    private List<Quiz> getNewQuizesFromServer(String userName, String password) {
        PostDataSender sender = new PostDataSender();
        sender.execute(URL+GET_QUIZES_COMMAND, "user", userName, "password", password);
        String quizesMessage = null;

        try {
            quizesMessage = sender.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<Quiz> quizes = new ArrayList<Quiz>();
        if (!(quizesMessage.isEmpty()||quizesMessage==null)) {
            quizes = Quiz.deserializeAllQuizes(quizesMessage);
        }

        return  quizes;
    }

    private List<Respondent> getNewRespondentsFromServer(String userName, String password) {
        PostDataSender sender = new PostDataSender();
        sender.execute(URL+GET_RESPONDENTS_COMMAND, "user", userName, "password", password);
        String respondentsMessage = null;

        try {
            respondentsMessage = sender.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<Respondent> respondents = new ArrayList<Respondent>();
        if (!(respondentsMessage.isEmpty()||respondentsMessage==null)){
            respondents = Respondent.deserializeAllRespondents(respondentsMessage);
        }
        return respondents;
    }

    private  String sendNewAnswers(List<Answer> answers){
        StringBuilder sb = new StringBuilder();

        String separator = "";
        for(int i = 0;i<answers.size();i++){
            sb.append(separator);
            sb.append(answers.get(i).serializeAnswer());
            separator="|";
        }

        PostDataSender sender = new PostDataSender();
        String sendData = sb.toString();
        String sendMessage = null;

        if (sendData.length()!=0) {
            sender.execute(URL + SEND_ANSWERS_COMMAND, "answers", sendData.substring(0, sendData.length() - 1));


            try {
                sendMessage = sender.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return  sendMessage;
    }
}

