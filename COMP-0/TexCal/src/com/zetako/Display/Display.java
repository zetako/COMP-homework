package com.zetako.Display;

import java.util.*;

public class Display {
    public static void main(String[] args) throws Exception
    {
        String[] opt=new String[3];
        opt[0]="happy";
        opt[1]="every";
        opt[2]="day";
        int ret=displayOption(opt,"Option Test");
        System.out.println("You Choose "+Integer.toString(ret)+" "+opt[ret]);
        String[][] table=new String[3][3];
        for (int i=0;i<3;i++)
        {
            for (int j=0;j<3;j++) table[i][j]=Integer.toString(i)+" "+Integer.toString(j);
        }
        displayTable(table, "Test Table");
    }
    private static String genRepeatString(int n,String s)
    {
        return String.join("",Collections.nCopies(n, s));
    }
    public static int displayOption(String[] options,String name) throws Exception
    {
        System.out.println(name);
        for (int i=0;i<options.length;i++)
        {
            System.out.println(Integer.toString(i)+"\t"+options[i]);
        }
        
        char optChar=(char) System.in.read();
        int opt=Integer.parseInt(String.valueOf(optChar));

        return opt;
    }

    public static void displayTable(String[][] content,String Name)
    {
        int row=content.length;
        int col=content[0].length;

        // find best gridWidth
        int[] gridWidth=new int[col];
        for (int i=0;i<col;i++)
        {
            int tmp=0;
            for (int j=0;j<row;j++)
            {
                if (content[j][i].length()>tmp) tmp=content[j][i].length();
            }
            gridWidth[i]=tmp+2;
        }

        //print table
        String border="+";
        for (int i=0;i<col;i++)
        {
            border=border+genRepeatString(gridWidth[i],"-")+"+";
        }
        System.out.println("\t"+Name);
        System.out.println(border);
        for (int i=0;i<row;i++)
        {
            System.out.print("|");
            for (int j=0;j<col;j++)
            {
                System.out.print(" "+content[i][j]+" |");
            }
            System.out.print("\n");
            System.out.println(border);
        }
        
        return;
    }
}
