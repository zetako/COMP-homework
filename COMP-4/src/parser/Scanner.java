package parser;

import java.util.*;
import exceptions.*;

public class Scanner {
    public List<Token> tokenStream;

    private Integer start, end;
    private Integer stateCode;
    private String rawString;

    private static final String singleOpStr = "(,),-,^,*,/,+,=,<,>,!,&,|,?,:";
    public static final Set<String> singleOpSet = new HashSet<String>(Arrays.asList(singleOpStr.split(",")));
    private static final String multiOpStr = "sin,cos,max,min,<>,<=,>=";
    public static final Set<String> MultiOpSet = new HashSet<String>(Arrays.asList(multiOpStr.split(",")));


    public Scanner(String raw) {
        start = 0;
        end = 0;
        stateCode = 1;
        rawString = raw;
        tokenStream = new ArrayList<Token>();
    }

    private Boolean multiOpProcess() throws LexicalException {
        
    }

    private Boolean stateProcess() throws LexicalException {
        switch (stateCode) {
            case 1:
                Character tmp = rawString.charAt(end);
                if (tmp == 't') {
                    end++;
                    stateCode = 2;
                } else if (tmp == 'f') {
                    end++;
                    stateCode = 4;
                } else if (Character.isDigit(tmp)) {
                    end++;
                    stateCode = 6;
                } else if (singleOpSet.contains(tmp.toString())) {
                    end++;
                    stateCode = 10;
                } else {
                    Boolean flag = true;
                    while (flag) {
                        flag = multiOpProcess();
                    }
                }

                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
        
            default:
                throw new LexicalException();
        }
        return true;
    }

    public Boolean scan() throws LexicalException {
        Boolean flag = true;
        while (flag) {
            flag = stateProcess();
        }
        return true;
    }
}