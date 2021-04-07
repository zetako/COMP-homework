package AgendaLogic;

import java.util.*;

public class AgendaLogic {
    private UserManager userMgr;
    private MeetingManager meetMgr;
    
    public enum Status {
        NORMAL,
        ERROR,
    
    }

    private Boolean validateAccount(String userName, String passwd)
    {
        return true;
    }

    public Map<String, Object> register(String userName, String passwd)
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        return resultMap;
    }
    
    public Status add(String userName, String passwd, String other, Date start, Date end, String title)
    {
        return Status.NORMAL;
    }

    public Map<String, Object> query(String userName, String passwd, Date start, Date end)
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        return resultMap;
    }

    public Status delete(String userName, String passwd, Integer meetingId)
    {
        return Status.NORMAL;
    }

    public Status clear(String userName, String passwd)
    {
        return Status.NORMAL;
    }

    public Status batch(String fileName)
    {
        return Status.NORMAL;
    }
}