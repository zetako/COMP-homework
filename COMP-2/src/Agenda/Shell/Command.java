package Agenda.Shell;

import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import Agenda.Logic.AgendaLogic;
import Agenda.Logic.AgendaLogic.Status;

interface Command {
    public Boolean parse(List<String> args);
    public void exec(AgendaLogic logic);
}

class Register implements Command {
    private String userName;
    private String passwd;
    public Boolean parse(List<String> args) {
        if (args.size() != 2) return false;
        
        userName = args.get(0);
        passwd = args.get(1);
        return true;
    }
    public void exec(AgendaLogic logic) {
        Status result = logic.register(userName, passwd);
        switch (result) {
            case NORMAL:
                System.out.println("User "+userName+" register done!");
                break;
            case USER_ALREADY_EXIST:
                System.out.println("User "+userName+" is already exist!");
            default:
                System.out.println("Unknown ERROR occured");
                break;
        }
    }
}

class Add implements Command {
    private String userName;
    private String passwd;
    private String other;
    private Date start;
    private Date end;
    private String title;

    public Boolean parse(List<String> args){
        if (args.size() != 6) return false;
        
        userName = args.get(0);
        passwd = args.get(1);
        other = args.get(2);
        title = args.get(5);

        SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-hh:mm");
        try {
            start = fmt.parse(args.get(3));
        } catch (ParseException e) {

        }


        return true;
    }

    public void exec(AgendaLogic logic) {

    }
}