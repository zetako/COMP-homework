import java.util.*;
import org.javatuples.*;
import com.alibaba.fastjson.*;
import com.alibaba.fastjson.annotation.JSONField;

public class TaxTable
{
    private ArrayList<TaxRow> rows;

    public TaxTable()
    {
        rows=new ArrayList<TaxRow>();
    }

    public void saveToJSON(String fileName)
    {

    }
    public void readFromJSON(String fileName)
    {
        rows.clear();
        String JSONString=FileIO.readToString(fileName);
    }

    public double calculate(double incoming)
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

    public ArrayList<Pair<Double,Double>> getTable()
    {
        ArrayList<Pair<Double,Double>> ret=new ArrayList<Pair<Double,Double>>();
        for (int i=0;i<rows.size();i++)
        {
            ret.set(i,rows.get(i).getRow());
        }
        return ret;
    }
}

class TaxRow
{
    double tierLen;
    double rate;
    double maxTax;

    public TaxRow(double _len,double _rate)
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

class TaxRowBean
{
    @JSONField(name="TierLen")
    private double TierLen;

    @JSONField(name="TierRate")
    private double TierRate;

    public TaxRowBean(double len,double rate)
    {
        TierLen=len;
        TierRate=rate;
    }
}