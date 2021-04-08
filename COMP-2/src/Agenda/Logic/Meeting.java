package Agenda.Logic;

import java.util.*;

/**
 * Representing a single meeting
 * @author zetako
 * @version 0.0.1
 */
public class Meeting {
    public Integer id;
    public String title;
    public Date start;
    public Date end;
    public List<String> participants;

    public Meeting(Integer _id, String _title, Date _start, Date _end, List<String> _participants)
    {
        id=_id;
        title = _title;
        start = _start;
        end = _end;
        participants = _participants;
    }

}
