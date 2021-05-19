package parser;

import java.util.*;
import exceptions.*;

public class Token {
    private String token;
    public enum TokenType {
        oprend_dec, oprend_bool, operator, function
    };
    private TokenType type;

    public Boolean OPPTagDefined;
    public String OPPTag;

    private static final String operatorStr = "(,),-,^,*,/,+,=,<>,<,<=,>,>=,!,&,|,?,:";
    public static final Set<String> operatorSet = new HashSet<String>(Arrays.asList(operatorStr.split(",")));

    public Token(String _token, TokenType _type) throws LexicalException{
        token = _token;
        type = _type;

<<<<<<< HEAD
        OPPTagDefined = false;

=======
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
            default:
                typeStr = "unknown";
            break;
        }
        // System.out.println("%s | %s".formatted(_token, typeStr));
>>>>>>> f6fe0416dfb52df90fd3d1582285daa5d8d054e0
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
            case function:
                return true;
            default:
                throw new LexicalException();
        }
    }

    
}
