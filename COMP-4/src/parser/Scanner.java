package parser;

import java.util.*;
import exceptions.*;
import parser.Token.TokenType;


/**
 * Scan a raw string (expression from frontend) to list of token
 * @author zetako
 * @version 2.3
 */
public class Scanner {
    /**
     * Scanning result, list of token; an ArrayList
     */
    public List<Token> tokenStream;

    /**
     * Index for lookahead, point to lookahead character
     */
    private Integer lookaheadIndex;
    /**
     * Index for substring, modified by private funcions like {@link Scanner#decState()}
     */
    private Integer start, end;
    /**
     * State code for DFAs, it's share by each sub-DFA
     */
    private Integer stateCode;
    /**
     * Input raw string (expression get by frontend)
     */
    private String rawString;

    /**
     * A set contain tokens that match a right bracket, including functions and left bracket
     */
    private Set<Token> leftBracket;

    /**
     * Constructor, init members
     * @param raw input raw string
     */
    public Scanner(String raw) {
        rawString = raw;
        stateCode = 0;
        lookaheadIndex = 0;

        tokenStream = new ArrayList<Token>();
        leftBracket = new HashSet<Token>();
        try {
            leftBracket.add(new Token("max(", TokenType.function));
            leftBracket.add(new Token("min(", TokenType.function));
            leftBracket.add(new Token("sin(", TokenType.function));
            leftBracket.add(new Token("cos(", TokenType.function));
            leftBracket.add(new Token("(", TokenType.operator));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A debug oriented function to print token stream
     */
    public void printStream() {
        // System.out.println("Stream length: %d".formatted(tokenStream.size()));
        for (Token token : tokenStream) {
            String typeStr;
            switch (token.getType()) {
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
            System.out.println("%s | %s".formatted(token.getToken(), typeStr));
        }
    }

    /**
     * Get lookahead
     * @param n length to get
     * @return lookahead
     */
    private String lookahead(Integer n) {
        if (lookaheadIndex + n - 1 >= rawString.length()) {
            return "$";
        } else {
            return rawString.substring(lookaheadIndex, lookaheadIndex + n);
        }
    }

    /**
     * Conresponding a DFA state of func DFA
     * @return return false means DFA terminates
     * @throws IllegalIdentifierException no function name match
     * @throws FunctionCallException variable function with no comma
     * @throws FunctionCallException bracket not close
     * @throws ExpressionException at a state that should never reach
     */
    private Boolean funcState() throws ExpressionException {
        String tmp = lookahead(1);
        Character tmpChar = tmp.charAt(0);
        Integer bracketCounter = 0;
        Boolean flag = true;
        switch (stateCode) {
            case 0:
                switch (tmpChar) {
                    case 'm':
                        stateCode = 1;
                        lookaheadIndex++;
                        break;
                    case 's':
                        stateCode = 7;
                        lookaheadIndex++;
                        break;
                    case 'c':
                        stateCode = 10;
                        lookaheadIndex++;
                        break;
                    default:
                        throw new IllegalIdentifierException("Unknown function identifier at pos %d".formatted(lookaheadIndex));
                }
                break;
            case 1:
                tmp = lookahead(3);
                if (tmp.equals("ax(") || tmp.equals("in(")) {
                    tmp = "m" + tmp;
                    Token tmpToken= new Token(tmp, TokenType.function);
                    tokenStream.add(tmpToken);
                    lookaheadIndex += 3;
                    stateCode = 2;
                } else {
                    throw new IllegalIdentifierException("Unknown function identifier at pos %d".formatted(lookaheadIndex));
                }
                break;
            case 2:
                stateCode = 0;
                flag = true;
                while (flag && lookaheadIndex != rawString.length() && !lookahead(1).equals(",")) {
                    flag = exprScan();
                }
                stateCode = 3;
                break;
            case 3:
                tmp = lookahead(1);
                if (tmp.equals(",")) {
                    lookaheadIndex++;
                    stateCode = 4;
                    Token tmpToken = new Token(",", TokenType.function);
                    tokenStream.add(tmpToken);
                } else {
                    throw new FunctionCallException("Exception in Function Scanning, except \",\" but get %s at pos %d".formatted(tmp, lookaheadIndex));
                }
                break;
            case 4:
                tmp = lookahead(1);
                if (tmp.equals(" ")) {
                    stateCode = 4;
                    lookaheadIndex++;
                } else {
                    flag = true;
                    bracketCounter = 0;
                    stateCode = 0;
                    while (flag && lookaheadIndex != rawString.length()) {
                        // Token tmpToken = tokenStream.get(tokenStream.size() - 1);
                        if (lookahead(1).equals("(")) {
                            bracketCounter++;
                        } else if (lookahead(1).equals(")")) {
                            bracketCounter--;
                        } else if (lookahead(1).equals(",")) {
                            // System.out.println(stateCode);
                            // if (!(stateCode == 1 || stateCode ==7)) {
                            //     throw new FunctionCallException("Unknown Exception occur in function scanning at pos %d".formatted(lookaheadIndex));
                            // } else {
                            //     stateCode = 4;
                            //     break;
                            // }
                            tokenStream.add(new Token(",", TokenType.function));
                            lookaheadIndex++;
                            stateCode = 4;
                            break;
                        }
                        if (bracketCounter < 0) {
                            stateCode = 5;
                            break;
                        }
                        exprScan();
                    }
                }
                break;
            case 5:
                if (tmp.equals(")")) {
                    lookaheadIndex++;
                    tokenStream.add(new Token(tmp, TokenType.function));
                } else {
                    throw new FunctionCallException("Except \")\" in pos %d but get %s".formatted(lookaheadIndex, tmp));
                }
                return false;
            case 6:
                throw new ExpressionException("Unknown Status in Function Scanning");
            case 7:
                tmp = lookahead(3);
                if (tmp.equals("in(")) {
                    tmp = "s" + tmp;
                    Token tmpToken= new Token(tmp, TokenType.function);
                    tokenStream.add(tmpToken);
                    lookaheadIndex += 3;
                    stateCode = 8;
                } else {
                    throw new IllegalIdentifierException("Unknown function identifier at pos %d".formatted(lookaheadIndex));
                }
                break;
            case 8:
                tmp = lookahead(1);
                if (tmp.equals(" ")) {
                    stateCode = 8;
                    lookaheadIndex++;
                } else {
                    flag = true;
                    bracketCounter = 0;
                    stateCode = 0;
                    while (flag && lookaheadIndex != rawString.length()) {
                        // Token tmpToken = tokenStream.get(tokenStream.size() - 1);
                        if (lookahead(1).equals("(")) {
                            bracketCounter++;
                        } else if (lookahead(1).equals(")")) {
                            bracketCounter--;
                        }
                        if (bracketCounter < 0) {
                            stateCode = 9;
                            break;
                        }
                        exprScan();
                    }
                }
                break;
            case 9:
                if (tmp.equals(")")) {
                    lookaheadIndex++;
                    tokenStream.add(new Token(tmp, TokenType.function));
                } else {
                    throw new FunctionCallException("Except \")\" in pos %d but get %s".formatted(lookaheadIndex, tmp));
                }
                return false;
            case 10:
                tmp = lookahead(3);
                if (tmp.equals("os(")) {
                    tmp = "c" + tmp;
                    Token tmpToken= new Token(tmp, TokenType.function);
                    tokenStream.add(tmpToken);
                    lookaheadIndex += 3;
                    stateCode = 8;
                } else {
                    throw new IllegalIdentifierException("Unknown function identifier at pos %d".formatted(lookaheadIndex));
                }
                break;
            default:
                throw new ExpressionException("Unknown Exception in Function scaning at pos %d".formatted(lookaheadIndex));
        }
        return true;
    }

    /**
     * Main function for scanning func
     * @return normally it returns true
     * @throws ExpressionException throw by {@link Scanner#funcState()}
     */
    private Boolean funcScan() throws ExpressionException {
        Boolean flag = true;
        stateCode = 0;
        while (flag && lookaheadIndex != rawString.length()) {
            // System.out.println("Func Scan state %d".formatted(stateCode));
            flag = funcState();
        }
        return true;
    }

    /**
     * Conresponding a DFA state of decimal DFA
     * @return return false means DFA terminates
     * @throws IllegalDecimalException decimal not start with digit
     * @throws IllegalDecimalException no digit after "."
     * @throws IllegalDecimalException no +/-/digit after E/e
     * @throws IllegalDecimalException no digit after +/-
     * @throws LexicalException normally no throws
     */
    private Boolean decState() throws LexicalException{
        String tmp = lookahead(1);
        // System.out.println("This lookahead \"%s\"".formatted(tmp));
        Character tmpChar = tmp.charAt(0);
        switch (stateCode) {
            case 0:
                start = lookaheadIndex;
                end = lookaheadIndex;

                if (Character.isDigit(tmpChar)) {
                    stateCode = 1;
                    end += 1;
                    lookaheadIndex += 1;
                } else {
                    throw new IllegalDecimalException("Illegal char \"%s\" when parsing decimal at pos %d".formatted(tmp, lookaheadIndex));
                }
                break;
            case 1:
                if (Character.isDigit(tmpChar)) {
                    stateCode = 1;
                    end += 1;
                    lookaheadIndex += 1;
                } else if (tmpChar == 'E' || tmpChar == 'e') {
                    stateCode = 4;
                    end += 1;
                    lookaheadIndex += 1;
                } else if (tmpChar == '.') {
                    stateCode = 2;
                    end += 1;
                    lookaheadIndex += 1;
                } else {
                    tmp = rawString.substring(start, end);
                    tokenStream.add(new Token(tmp, TokenType.oprend_dec));
                    return false;
                }
                break;
            case 2:
                if (Character.isDigit(tmpChar)) {
                    stateCode = 3;
                    end += 1;
                    lookaheadIndex += 1;
                } else {
                    throw new IllegalDecimalException("Illegal char \"%s\" when parsing decimal at pos %d, expected [0-9]".formatted(tmp, lookaheadIndex));
                }
                break;
            case 3:
                if (Character.isDigit(tmpChar)) {
                    stateCode = 3;
                    end += 1;
                    lookaheadIndex += 1;
                } else if (tmpChar == 'E' || tmpChar == 'e') {
                    stateCode = 4;
                    end += 1;
                    lookaheadIndex += 1;
                } else {
                    tmp = rawString.substring(start, end);
                    tokenStream.add(new Token(tmp, TokenType.oprend_dec));
                    return false;
                    // throw new IllegalDecimalException("Illegal char \"%s\" when parsing decimal at pos %d, expected [0-9]/E/e".formatted(tmp, lookaheadIndex));
                }
                break;
            case 4:
                if (Character.isDigit(tmpChar)) {
                    stateCode = 6;
                    end += 1;
                    lookaheadIndex += 1;
                } else if (tmpChar == '+' || tmpChar == '-') {
                    stateCode = 5;
                    end += 1;
                    lookaheadIndex += 1;
                } else {
                    throw new IllegalDecimalException("Illegal char \"%s\" when parsing decimal at pos %d, expected [0-9]/+/-".formatted(tmp, lookaheadIndex));
                }
                break;
            case 5:
                if (Character.isDigit(tmpChar)) {
                    stateCode = 6;
                    end += 1;
                    lookaheadIndex += 1;
                } else {
                    throw new IllegalDecimalException("Illegal char \"%s\" when parsing decimal at pos %d, expected [0-9]".formatted(tmp, lookaheadIndex));
                }
                break;
            case 6:
                if (Character.isDigit(tmpChar)) {
                    stateCode = 6;
                    end += 1;
                    lookaheadIndex += 1;
                } else {
                    stateCode = 7;
                }
                break;
            case 7:
                Token tmpToken = new Token(rawString.substring(start, end), TokenType.oprend_dec);
                tokenStream.add(tmpToken);
                end += 1;
                start = end;
                // lookaheadIndex += 1;
                return false;
            default:
                throw new IllegalDecimalException("Unknown Exception in Decimal Scanning at pos %d".formatted(lookaheadIndex));
        }
        return true;
    }
    /**
     * Main function for scanning decimal
     * @return normally it returns true
     * @throws ExpressionException throw by {@link Scanner#decState()}
     */
    private Boolean decScan() throws ExpressionException {
        Boolean flag = true;
        stateCode = 0;
        while (flag) {
            // System.out.println("Decimal scan state %d".formatted(stateCode));
            flag = decState();
        }
        return true;
    }

    /**
     * Scan boolean
     * @return normally it returns true
     * @throws IlleaglSymbolException not match true/false
     */
    private Boolean boolScan() throws LexicalException {
        String tmp = lookahead(4).toLowerCase();
        if (tmp.equals("true")) {
            lookaheadIndex += 4;
            Token tmpToken = new Token(tmp, TokenType.oprend_bool);
            tokenStream.add(tmpToken);
        } else {
            tmp = lookahead(5).toLowerCase();
            if (tmp.equals("false")) {
                lookaheadIndex += 4;
            Token tmpToken = new Token(tmp, TokenType.oprend_bool);
            tokenStream.add(tmpToken);
            } else {
                throw new IllegalSymbolException("Illegal symbol \"%s\" at position %d".formatted(tmp, lookaheadIndex));
            }
        }
        return true;
    }

    /**
     * Scan operator
     * @return normally it returns true
     * @throws IlleaglSymbolException no matching operator
     */
    private Boolean opScan() throws LexicalException {
        String tmp = lookahead(2);
        if (Token.operatorSet.contains(tmp)) {
            lookaheadIndex += 2;
            Token tmpToken = new Token(tmp, TokenType.operator);
            tokenStream.add(tmpToken);
        } else {
            tmp = lookahead(1);
            if (Token.operatorSet.contains(tmp)) {
                lookaheadIndex += 1;
                Token tmpToken = new Token(tmp, TokenType.operator);
                tokenStream.add(tmpToken);
            } else {
                throw new IllegalSymbolException("Illegal symbol \"%s\" at position %d".formatted(tmp, lookaheadIndex));
            }
        }
        return true;
    }

    /**
     * Main function to invoke sub-DFA
     * @return return false means DFA terminates
     * @throws IllegalSymbolException no matching lexical rule
     * @see Scanner#boolScan()
     * @see Scanner#funcScan()
     * @see Scanner#decScan()
     * @see Scanner#opScan()
     */
    private Boolean exprScan() throws ExpressionException {
        Boolean flag=true;
        switch (lookahead(1)) {
            case " ":
                // System.out.println("Skip space");
                lookaheadIndex++;
                break;
            case "t":
            case "T":
            case "f":
            case "F":
                // System.out.println("start boolean scan");
                flag = boolScan();
                break;
            case "m":
            case "s":
            case "c":
                stateCode = 0;
                // System.out.println("start function scan");
                flag = funcScan();
                break;
            default:
                if (Token.operatorSet.contains(lookahead(1))) {
                    // System.out.println("start operator scan");
                    flag = opScan();
                } else if (Character.isDigit(lookahead(1).charAt(0))) {
                    stateCode = 0;
                    // System.out.println("start decimal scan");
                    flag = decScan();
                } else if (lookahead(1).equals("$")){
                    flag = false;
                } else {
                    throw new IllegalSymbolException("Illegal symbol \"%s\" at position %d".formatted(lookahead(1), lookaheadIndex));
                }
                break;
        }
        return flag;
    }

    /**
     * Function exposed for calling
     * @return normally it returns true
     * @throws ExpressionException throw by {@link Scanner#exprScan()}
     */
    public Boolean scan() throws ExpressionException {
        Boolean flag = true;
        while (flag && lookaheadIndex != rawString.length()) {
            // System.out.println("Expr state: %d".formatted(stateCode));
            flag = exprScan();
        }
        return true;
    }
}