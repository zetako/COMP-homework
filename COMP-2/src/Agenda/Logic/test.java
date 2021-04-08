package Agenda.Logic;

import java.util.*;

public class test {
    public static void main(String[] args) {
        AgendaLogic logicTest = new AgendaLogic();

        logicTest.register("zetako", "123456");
        logicTest.register("wynn", "123456");
        logicTest.add("zetako", "123456", "wynn", new Date(100), new Date(1000), "test1");
        logicTest.add("zetako", "123456", "wynn", new Date(200), new Date(2000), "test1");
        EnumMap<AgendaLogic.QueryReturn, Object> tmp = logicTest.query("zetako", "123456", new Date(90), new Date(3000));
        List<Meeting> list = (List<Meeting>)tmp.get(AgendaLogic.QueryReturn.list);
        print(list);
        Integer meetingId = list.get(0).id;
        logicTest.delete("zetako", "123456", meetingId);
        tmp = logicTest.query("zetako", "123456", new Date(90), new Date(3000));
        list = (List<Meeting>)tmp.get(AgendaLogic.QueryReturn.list);
        logicTest.clear("zetako", "123456");
        print(list);
        tmp = logicTest.query("wynn", "123456", new Date(90), new Date(3000));
        list = (List<Meeting>)tmp.get(AgendaLogic.QueryReturn.list);
        print(list);
    }

    static void print(List<Meeting> list) {
        for (Meeting meet : list) {
            System.out.println(meet);
        }
    }
}
