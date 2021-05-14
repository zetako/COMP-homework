import java.text.DecimalFormat;
import java.util.*;
import org.javatuples.*;

public class Interface{
    TaxTable table;
    public void run() throws Exception
    {
        init();
        String[] mainMenu={ "Calculate Tax",
                            "Check Tax Table",
                            "Read Tax Table (.json)",
                            "Exit"};
        int ret=Display.displayOption(mainMenu,"TexCal - Tax Calculator");
        boolean exitFlag=false;
        while (!exitFlag)
        {
            switch (ret) {
                case 0:
                    cal();
                    break;
                case 1:
                    checkTable();
                    break;
                case 2:
                    readJSON();
                    break;
                case 3:
                    saveJSON();
                    exitFlag=true;
                    break;
            
                default:
                    System.out.println("Wrong Option!");
                    break;
            }
        }
    }

    private void init()
    {
        table.readFromJSON("table.json");
    }
    private void cal()
    {
        System.out.print("Input the income: ");
        Scanner scan=new Scanner(System.in);
        if (scan.hasNextDouble())
        {
            double result=table.calculate(scan.nextDouble());
            System.out.println("Your Tax is: "+Double.toString(result));
        }
        scan.close();
    }
    private void checkTable()
    {
        ArrayList<Pair<Double,Double>> rawTable=table.getTable();
        String[][] contentTable=new String[rawTable.size()+1][3];
        contentTable[0][0]="Tier";
        contentTable[0][1]="Section";
        contentTable[0][2]="Rate";

        double base=0;
        DecimalFormat moneyForm=new DecimalFormat("0.00");
        DecimalFormat rateForm=new DecimalFormat("0.00%");
        for (int i=0;i<rawTable.size();i++)
        {
            contentTable[i+1][0]=Integer.toString(i);
            contentTable[0][1]=moneyForm.format(base)+"~"+moneyForm.format(base+rawTable.get(i).getValue0());
            contentTable[0][2]=rateForm.format(rawTable.get(i).getValue1());
        }

    }
    private void saveJSON()
    {
        System.out.print("Input the income: ");
        Scanner scan=new Scanner(System.in);
        if (scan.hasNextLine())
        {
            String filename=scan.nextLine();
            table.readFromJSON(filename);
        }
        scan.close();
    }
    private void readJSON()
    {
        table.saveToJSON("table.json");
    }
}
