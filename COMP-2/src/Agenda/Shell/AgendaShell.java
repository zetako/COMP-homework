package Agenda.Shell;

import java.lang.reflect.Constructor;
import java.util.*;

import Agenda.Logic.AgendaLogic;

public class AgendaShell {
    public static void main(String[] args) {
        System.out.println("Agenda - Meeting Managing System");
        AgendaLogic logic = new AgendaLogic();
        Scanner scan = new Scanner(System.in);
        Boolean exitFlag = false;
        while (!exitFlag) {
            System.out.print("$ ");
            String tmp = scan.nextLine();
            exitFlag = runCommand(tmp, logic);
        }
        scan.close();
    }
    private static Boolean runCommand(String commandLine, AgendaLogic logic) {
        List<String> tmpList = new ArrayList<String>(Arrays.asList(commandLine.split(" ")));
        while (tmpList.get(0).equals("")) {
            tmpList.remove(0);
        }
        String tmp = upper(tmpList.get(0));
        tmpList.remove(0);

        if (tmp.equals("Exit")) {
            return true;
        } else if (tmp.equals("Batch")) {
            //todo
            return false;
        }

        try {
            Class cmd = Class.forName("Agenda.Shell."+tmp);
            Constructor cons = cmd.getDeclaredConstructor();
            Command cmdIns = (Command)cons.newInstance();
            if (cmdIns.parse(tmpList)) {
                //System.out.println("Parse Done");
                cmdIns.exec(logic);
            } else {
                System.out.println("Command Parse falied! May be wrong args number?");
            }
        } catch (Exception e) {
            System.out.println("Invalid Command: "+tmp);
            //System.out.println(e.getMessage());
        }

        return false;
    }
    private static String upper(String src) {
        return src.substring(0,1).toUpperCase()+src.substring(1);
    }
}