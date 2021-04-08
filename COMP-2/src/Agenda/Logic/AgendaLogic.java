package Agenda.Logic;

import java.util.*;

public class AgendaLogic {
    private UserManager userMgr;
    private MeetingManager meetMgr;

    public AgendaLogic()
    {
        userMgr = new UserManager();
        meetMgr = new MeetingManager();
    }
    
    public enum Status {
        NORMAL,
        ERROR,
        USER_ALREADY_EXIST,
        USER_NOT_FOUND,
        LOGIN_FAILED,
        INVALID_DATE,
        MEETING_NOT_FOUND
    }

    private Status validateAccount(String userName, String passwd)
    {
        EnumMap<UserManager.GetUserReturn, Object> tmp = userMgr.getUser(userName, passwd);
        UserManager.Status result = (UserManager.Status)tmp.get(UserManager.GetUserReturn.status);
        switch (result) {
            case NORMAL:
                return Status.NORMAL;
            case NO_USER_BY_NAME:
                return Status.USER_NOT_FOUND;
            case PASSWORD_INVALID:
                return Status.LOGIN_FAILED;
            default:
                return Status.ERROR;
        }
    }

    public Status register(String userName, String passwd)
    {
        EnumMap<UserManager.AddUserReturn, Object> tmp = userMgr.addUser(userName, passwd);
        UserManager.Status result = (UserManager.Status)tmp.get(UserManager.AddUserReturn.status);
        switch (result) {
            case NORMAL:
                return Status.NORMAL;
            case USER_EXISTED:
                return Status.USER_ALREADY_EXIST;
            default:
                return Status.ERROR;
        }
    }
    
    enum AddReturn {
        status, id
    }
    public EnumMap<AddReturn, Object> add(String userName, String passwd, String other, Date start, Date end, String title)
    {
        EnumMap<AddReturn, Object> ret = new EnumMap<AddReturn, Object>(AddReturn.class);
        
        Status LoginStat = validateAccount(userName, passwd);
        if (LoginStat != Status.NORMAL) {
            ret.put(AddReturn.status, LoginStat);
            ret.put(AddReturn.id, null);
        }

        List<String> participants = new ArrayList<String>(Arrays.asList(userName,other));
        EnumMap<MeetingManager.AddMeetingReturn, Object> tmp = meetMgr.addMeeting(title, start, end, participants);
        MeetingManager.Status result = (MeetingManager.Status)tmp.get(MeetingManager.AddMeetingReturn.status);
        switch (result) {
            case NORMAL:
                ret.put(AddReturn.status, Status.NORMAL);
                ret.put(AddReturn.id, tmp.get(MeetingManager.AddMeetingReturn.id));
                break;
            case INVALID_DATE:
                ret.put(AddReturn.status, Status.INVALID_DATE);
                ret.put(AddReturn.id, tmp.get(MeetingManager.AddMeetingReturn.id));
                break;
            default:
                ret.put(AddReturn.status, Status.ERROR);
                ret.put(AddReturn.id, null);
                break;
        }
        return ret;
    }

    enum QueryReturn {
        status, list
    }
    public EnumMap<QueryReturn, Object> query(String userName, String passwd, Date start, Date end)
    {
        EnumMap<QueryReturn, Object> ret = new EnumMap<QueryReturn, Object>(QueryReturn.class);

        Status loginStat = validateAccount(userName, passwd);
        if (loginStat != Status.NORMAL) {
            ret.put(QueryReturn.status, loginStat);
            ret.put(QueryReturn.list, null);
            return ret;
        }

        EnumMap<MeetingManager.GetMeetingByDateReturn, Object> tmp = meetMgr.getMeetingByDate(start, end, userName);
        MeetingManager.Status result = (MeetingManager.Status) tmp.get(MeetingManager.GetMeetingByDateReturn.status);
        switch (result) {
            case NORMAL:
            case NO_MEETING_BY_DATE:
                ret.put(QueryReturn.status, Status.NORMAL);
                ret.put(QueryReturn.list, tmp.get(MeetingManager.GetMeetingByDateReturn.list));
                return ret;
            default:
                ret.put(QueryReturn.status, Status.ERROR);
                ret.put(QueryReturn.list, null);
                return ret;
        }
    }

    public Status delete(String userName, String passwd, Integer meetingId)
    {
        Status loginStat = validateAccount(userName, passwd);
        if (loginStat != Status.NORMAL) {
            return loginStat;
        }

        MeetingManager.Status result = meetMgr.delMeeting(meetingId);
        switch (result) {
            case NORMAL:
                return Status.NORMAL;
            case NO_MEETING_BY_ID:
                return Status.MEETING_NOT_FOUND;
            default:
                return Status.ERROR;
        }
    }

    public Status clear(String userName, String passwd)
    {
        Status loginStat = validateAccount(userName, passwd);
        if (loginStat != Status.NORMAL) {
            return loginStat;
        }

        EnumMap<MeetingManager.GetMeetingByUserReturn, Object> tmp = meetMgr.GetMeetingByUser(userName);
        MeetingManager.Status result = (MeetingManager.Status)tmp.get(MeetingManager.GetMeetingByUserReturn.status);
        
        switch (result) {
            case NORMAL:
                List<Meeting> meetList = (List<Meeting>)tmp.get(MeetingManager.GetMeetingByUserReturn.list);
                for (Meeting meet : meetList) {
                    meetMgr.delMeeting(meet.id);  
                }
                return Status.NORMAL;
            case NO_MEETING_BY_USER:
                return Status.NORMAL;
            default:
                return Status.ERROR;
        }
    }
}