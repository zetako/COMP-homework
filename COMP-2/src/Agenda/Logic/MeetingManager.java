package Agenda.Logic;

import java.util.*;

public class MeetingManager {
    private Map<Integer, Meeting> meetList;
    private Integer curId;

    public MeetingManager()
    {
        meetList = new HashMap<Integer, Meeting>();
        curId = 0;
    }

    public enum Status {
        NORMAL,
        ERROR,
        NO_MEETING_BY_ID,
        NO_MEETING_BY_DATE,
        NO_MEETING_BY_USER,
        INVALID_DATE,
    }
    
    enum GetMeetingByIdReturn {
        status, meeting
    }
    public EnumMap<GetMeetingByIdReturn, Object> getMeetingById(Integer meetingId)
    {
        EnumMap<GetMeetingByIdReturn, Object> ret = new EnumMap<GetMeetingByIdReturn, Object>(GetMeetingByIdReturn.class);

        if (!meetList.containsKey(meetingId))
        {
            ret.put(GetMeetingByIdReturn.status, Status.NO_MEETING_BY_ID);
            ret.put(GetMeetingByIdReturn.meeting, null);
            return ret;
        }
        
        ret.put(GetMeetingByIdReturn.status, Status.NORMAL);
        ret.put(GetMeetingByIdReturn.meeting, meetList.get(meetingId));
        return ret;
    }

    enum GetMeetingByDateReturn {
        status,list
    }
    public EnumMap<GetMeetingByDateReturn, Object> getMeetingByDate(Date start, Date end, String userName)
    {
        EnumMap<GetMeetingByDateReturn, Object> ret = new EnumMap<GetMeetingByDateReturn, Object>(GetMeetingByDateReturn.class);
        List<Meeting> list = new ArrayList<Meeting>();

        for (Meeting meet : meetList.values()) {
            if (meet.start.after(start)&&meet.end.before(end)) {
                if (meet.participants.contains(userName)) {
                    list.add(meet);
                }
            }
        }

        if (list.size()==0) {
            ret.put(GetMeetingByDateReturn.status, Status.NO_MEETING_BY_DATE);
        } else {
            ret.put(GetMeetingByDateReturn.status, Status.NORMAL);
        }
        ret.put(GetMeetingByDateReturn.list, list);
        return ret;
    }

    enum GetMeetingByUserReturn {
        status, list
    }

    public EnumMap<GetMeetingByUserReturn, Object> GetMeetingByUser(String userName)
    {
        EnumMap<GetMeetingByUserReturn, Object> ret = new EnumMap<GetMeetingByUserReturn, Object>(GetMeetingByUserReturn.class);
        List<Meeting> list = new ArrayList<Meeting>();

        for (Meeting meet : meetList.values()) {
            if (meet.participants.contains(userName)) {
                list.add(meet);
            }
        }

        if (list.size()==0) {
            ret.put(GetMeetingByUserReturn.status, Status.NO_MEETING_BY_USER);
        } else {
            ret.put(GetMeetingByUserReturn.status, Status.NORMAL);
        }
        ret.put(GetMeetingByUserReturn.list, list);
        return ret;
    }

    enum AddMeetingReturn {
        status, id
    }
    public EnumMap<AddMeetingReturn, Object> addMeeting(String meetTitle, Date start, Date end, List<String> participants)
    {
        EnumMap<AddMeetingReturn, Object> ret = new EnumMap<AddMeetingReturn, Object>(AddMeetingReturn.class);
        
        Meeting curMeeting = new Meeting(curId, meetTitle, start, end, participants);
        meetList.put(curId,curMeeting);
        ret.put(AddMeetingReturn.status, Status.NORMAL);
        ret.put(AddMeetingReturn.id, curId);
        curId++;

        return ret;
    }
    
    public Status delMeeting(Integer meetingId)
    {
        if (!meetList.containsKey(meetingId)) {
            return Status.NO_MEETING_BY_ID;
        }
        meetList.remove(meetingId);
        return Status.NORMAL;
    }
}
