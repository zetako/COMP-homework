import java.util.ArrayList;
import org.javatuples.*;

public class App {
    taxTable table;
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
    }
}

class taxTable
{
    ArrayList<taxRow> rows;

    public double calculate(int incoming)
    {
        double tax=0;
        int index=0;
        
        while (incoming>0&&index<rows.size())
        {
            Pair<Double,Double> tmp=rows.get(index).cal(incoming);
            incoming-=tmp.getValue0();
            tax+=tmp.getValue1();
            index++;
        }
        return tax;
    }

    public void printTable()
    {

    }
}

class taxRow
{
    double tierLen;
    double rate;
    double maxTax;

    public taxRow(double _len,double _rate)
    {
        tierLen=_len;
        rate=_rate;
        maxTax=_len*_rate;
    }
    public void setRow(double _len,double _rate)
    {
        tierLen=_len;
        rate=_rate;
        maxTax=_len*_rate;
    }
    public Pair<Double,Double> getRow()
    {
        return Pair.with(tierLen,rate);
    }

    public Pair<Double,Double> cal(double income)
    {
        double rest=income,tax=maxTax;

        if (tierLen<0||tierLen>income)// the last tier or end at this tier
        {
            rest=0;
            tax=income*rate;
        }
        else// pass this tier
        {
            rest-=tierLen;
        }

        return Pair.with(rest,tax);
    }
}