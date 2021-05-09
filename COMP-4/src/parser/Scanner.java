package parser;

import java.util.*;
import exceptions.*;

public class Scanner {
    private Integer count;
    private String rawString;
    private List<Token> tokenStream;

    public Scanner(String raw) {
        count = 0;
        rawString = raw;
        tokenStream = new ArrayList<Token>();
    }

    public Boolean scan() throws LexicalException {
        
        return true;
    }
}
