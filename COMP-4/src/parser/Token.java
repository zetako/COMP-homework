package parser;

import java.util.*;
import exceptions.*;

public class Token {
    private String token;
    public enum TokenType {
        oprend_dec, oprend_bool, operator, function
    };
    private TokenType type;

    private static final String operatorStr = "(,),-,^,*,/,+,=,<>,<,<=,>,>=,!,&,|,?,:";
    public static final Set<String> operatorSet = new HashSet<String>(Arrays.asList(operatorStr.split(",")));

    public Token(String _token, TokenType _type) throws LexicalException{
        token = _token;
        type = _type;

        validate();
    }

    public String getToken() {
        return token;
    }
    public TokenType getType() {
        return type;
    }
    public Double getDoubleValue() throws Exception {
        if (type != TokenType.oprend_dec) {
            throw new Exception();
        }
        return Double.parseDouble(token);
    }
    public Boolean getBooleanValue() throws Exception {
        if (type != TokenType.oprend_bool) {
            throw new Exception();
        }
        switch (token) {
            case "true":
                return true;
            case "false":
                return false; 
            default:
                throw new Exception();
        }
    }
    public Boolean equals(Token otherToken) {
        return (token.equals(otherToken.token)) && (type == otherToken.type);
    }

    public Boolean validate() throws LexicalException {
        switch (type) {
            case oprend_dec:
                try {
                    Double.parseDouble(token);
                    return true;
                } catch (NumberFormatException e) {
                    throw new IllegalDecimalException("Invalid Oprend (Number) : " + token);
                }
            case oprend_bool:
                if (token.equals("true") || token.equals("false")) {
                    throw new IllegalIdentifierException("Invalid Oprend (Boolean) : " + token);
                }
                return true;
            case operator:
                if (!operatorSet.contains(token)) {
                    throw new IllegalSymbolException("Invalid Operator : " + token);
                }
                return true;
            default:
                throw new LexicalException();
        }
    }

    
}