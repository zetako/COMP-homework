package parser;

import java.util.*;
import exceptions.*;

public class Token {
    private String token;
    public enum TokenType {
        oprend_dec, oprend_bool, operator, function, end
    };
    private TokenType type;

    public Boolean OPPTagDefined;
    public String OPPTag;

    private static final String operatorStr = "(,),-,^,*,/,+,=,<>,<,<=,>,>=,!,&,|,?,:";
    public static final Set<String> operatorSet = new HashSet<String>(Arrays.asList(operatorStr.split(",")));

    public Token(String _token, TokenType _type) throws LexicalException{
        token = _token;
        type = _type;

        OPPTagDefined = false;

        String typeStr;
        switch (_type) {
            case operator:
                typeStr = "operator";
                break;
            case oprend_bool:
                typeStr = "boolean";
                break;
            case oprend_dec:
                typeStr = "decimal";
                break;
            case function:
                typeStr = "function";
                break;
            case end:
                typeStr = "$";
                break;
            default:
                typeStr = "unknown";
                break;
        }
        // System.out.println("%s | %s".formatted(_token, typeStr));
        validate();
    }

    public String getToken() {
        return token;
    }
    public TokenType getType() {
        return type;
    }
    public Double getDoubleValue() throws LexicalException {
        if (type != TokenType.oprend_dec) {
            throw new LexicalException("Get Double Value Failed");
        }
        Double ret = 0.0;
        try {
            ret = Double.parseDouble(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    public Boolean getBooleanValue() throws LexicalException {
        if (type != TokenType.oprend_bool) {
            throw new LexicalException("Get Boolean Value Failed");
        }
        switch (token) {
            case "true":
                return true;
            case "false":
                return false; 
            default:
                throw new LexicalException("Get Boolean Value Failed");
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
                if (!(token.equals("true") || token.equals("false"))) {
                    throw new IllegalIdentifierException("Invalid Oprend (Boolean) : " + token);
                }
                return true;
            case operator:
                if (!operatorSet.contains(token)) {
                    throw new IllegalSymbolException("Invalid Operator : " + token);
                }
                return true;
            case function:
                return true;
            case end:
                if (!token.equals("$")) {
                    throw new LexicalException("Internal Error");
                }
                return true;
            default:
                throw new LexicalException();
        }
    }

    
}
