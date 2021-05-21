package parser;

import java.util.*;
import exceptions.*;

/**
 * Represent a token scanned
 * @author zetako
 * @version 1.2
 */
public class Token {
    /**
     * Token's content
     */
    private String token;
    /**
     * Token's type's enum
     */
    public enum TokenType {
        oprend_dec, oprend_bool, operator, function, end
    };
    /**
     * Token's type
     */
    private TokenType type;

    /**
     * OPPTag for OPP table looking, modify by outer function; this represents defined or not
     */
    public Boolean OPPTagDefined;
    /**
     * OPPTag for OPP table looking, modify by outer function
     */
    public String OPPTag;

    /**
     * A string to init operatorSet
     */
    private static final String operatorStr = "(,),-,^,*,/,+,=,<>,<,<=,>,>=,!,&,|,?,:";
    /**
     * A set for matching operator
     */
    public static final Set<String> operatorSet = new HashSet<String>(Arrays.asList(operatorStr.split(",")));

    /**
     * Constructor, init members and validation
     * @param _token token content string
     * @param _type token type
     * @throws LexicalException exception throw by validation
     * @see Token#validate()
     */
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

    /**
     * Simple function to get token
     * @return token's content
     */
    public String getToken() {
        return token;
    }
    /**
     * Simple function to get token type
     * @return token's type
     */
    public TokenType getType() {
        return type;
    }
    /**
     * Function to get token's value
     * @return if token is decimal, it returns its value
     * @throws LexicalException if token is not decimal, this exception throws
     */
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
    /**
     * Function to get token's value
     * @return if token is boolean, it returns its value
     * @throws LexicalException if token is not boolean, this exception throws
     */
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
    /**
     * Equals function, it's not an override of Object's equals
     * @param otherToken other token
     * @return if two token has same type and content, they are equal
     */
    public Boolean equals(Token otherToken) {
        return (token.equals(otherToken.token)) && (type == otherToken.type);
    }

    /**
     * Validation for token's member
     * @return normally, it return true representing a good match
     * @throws IllegalDecimalException content not match type decimal
     * @throws IllegalIdentifierException content not match type boolean
     * @throws IllegalSymbolException content not match type operator
     * @throws LexicalException unknown type, normally it should not occur
     */
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
