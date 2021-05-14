package Agenda.Shell;

import java.lang.reflect.Constructor;
import java.util.*;
import java.io.*;

import Agenda.Logic.AgendaLogic;

/**
 * Shell(CLI) of Agenda System
 * @author zetako
 * @version 0.0.1
 */
public class AgendaShell {
    /**
     * main function, entry point
     * @param args never used
     */
    public static void main(String[] args) {
        shell();
    }
    
    /**
     * the real function of the shell
     */
    public static void shell() {
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
    /**
     * run a line of Command
     * @param commandLine a line of command
     * @param logic the AgendaLogic Instance
     * @return continue or not (false = continue)
     */
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
            try {
                FileInputStream inputStream = new FileInputStream(tmpList.get(0));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    
                String str = null;
                while((str = bufferedReader.readLine()) != null)
                {
                    System.out.println("(batch) $ "+str);
                    runCommand(str, logic);
                }
                    
                inputStream.close();
                bufferedReader.close();
            } catch (IOException e) {
                System.out.println("Error when reading file");
            }
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
    /**
     * a private method for upper first letter of string
     * @param src source string
     * @return  string that changed
     */
    private static String upper(String src) {
        return src.substring(0,1).toUpperCase()+src.substring(1);
    }
}