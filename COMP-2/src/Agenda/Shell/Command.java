package Agenda.Shell;

import java.util.*;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import Agenda.Logic.AgendaLogic;
import Agenda.Logic.AgendaLogic.Status;
import Agenda.Logic.Meeting;

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
                break;
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

    public Boolean parse(List<String> args) {
        if (args.size() != 6) return false;
        
        userName = args.get(0);
        passwd = args.get(1);
        other = args.get(2);
        title = args.get(5);

        SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-hh:mm");
        try {
            start = fmt.parse(args.get(3));
            end = fmt.parse(args.get(4));
        } catch (ParseException e) {
            System.out.println("Invalid Date format, should be MM-dd-hh:mm");
            return false;
        }

        return true;
    }

    public void exec(AgendaLogic logic) {
        EnumMap<AgendaLogic.AddReturn, Object> result = logic.add(userName, passwd, other, start, end, title);
        Status stat = (Status)result.get(AgendaLogic.AddReturn.status);
        switch (stat) {
            case NORMAL:
                Integer id = (Integer)result.get(AgendaLogic.AddReturn.id);
                System.out.println("Add Meeting successful with id "+id);
                break;
            case USER_NOT_FOUND:
                System.out.println("Login failed: NO such user");
                break;
            case LOGIN_FAILED:
                System.out.println("Login failed: wrong password");
                break;
            case INVALID_DATE:
                System.out.println("Add meeting failed: date is invalid");
                break;
            default:
                System.out.println("Unknown ERROR occured");
                break;
        }
    }
}

class Query implements Command {
    private String userName;
    private String passwd;
    private Date start;
    private Date end;

    public Boolean parse(List<String> args) {
        if (args.size() != 4) return false;
        
        userName = args.get(0);
        passwd = args.get(1);

        SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-hh:mm");
        try {
            start = fmt.parse(args.get(2));
            end = fmt.parse(args.get(3));
        } catch (ParseException e) {
            System.out.println("Invalid Date format: should be MM-dd-hh:mm");
            return false;
        }

        return true;
    }

    public void exec(AgendaLogic logic) {
        EnumMap<AgendaLogic.QueryReturn, Object> result = logic.query(userName, passwd, start, end);
        Status stat = (Status)result.get(AgendaLogic.QueryReturn.status);
        switch (stat) {
            case NORMAL:
                List<Meeting> list = (List<Meeting>)result.get(AgendaLogic.QueryReturn.list);
                System.out.println(list.size()+" records found");

                SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-hh:mm");
                for (Integer i=0;i<list.size();i++) {
                    Meeting tmp = list.get(i);
                    System.out.printf("\t%d. %d%s\t%s~%s\t%s,%s\n",
                            i+1, tmp.id, tmp.title, fmt.format(start), fmt.format(end),
                            tmp.participants.get(0), tmp.participants.get(1));
                }
                break;
            case USER_NOT_FOUND:
                System.out.println("Login failed: NO such user");
                break;
            case LOGIN_FAILED:
                System.out.println("Login failed: wrong password");
                break;
            default:
                System.out.println("Unknown ERROR occured");
                break;
        }
    }
}

class Delete implements Command {
    private String userName;
    private String passwd;
    private Integer meetingId;

    public Boolean parse(List<String> args) {
        if (args.size() != 3) return false;
        
        userName = args.get(0);
        passwd = args.get(1);
        meetingId = Integer.parseInt(args.get(2));

        return true;
    }

    public void exec(AgendaLogic logic) {
        Status stat = logic.delete(userName, passwd, meetingId);
        switch (stat) {
            case NORMAL:
                System.out.println("Delete meeting "+meetingId+" successful");
                break;
            case USER_NOT_FOUND:
                System.out.println("Login failed: NO such user");
                break;
            case LOGIN_FAILED:
                System.out.println("Login failed: wrong password");
                break;
            case MEETING_NOT_FOUND:
                System.out.println("Delete meeting failed: NO such meeting");
                break;
            default:
                System.out.println("Unknown ERROR occured");
                break;
        }
    }
}

class Clear implements Command {
    private String userName;
    private String passwd;

    public Boolean parse(List<String> args) {
        if (args.size() != 2) return false;
        
        userName = args.get(0);
        passwd = args.get(1);

        return true;
    }

    public void exec(AgendaLogic logic) {
        Status stat = logic.clear(userName, passwd);
        switch (stat) {
            case NORMAL:
                System.out.println("Delete all meetings of user "+userName+" successful");
                break;
            case USER_NOT_FOUND:
                System.out.println("Login failed: NO such user");
                break;
            case LOGIN_FAILED:
                System.out.println("Login failed: wrong password");
                break;
            default:
                System.out.println("Unknown ERROR occured");
                break;
        }
    }
}