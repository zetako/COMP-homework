package parser;

import java.util.*;
import exceptions.*;
import jdk.internal.org.jline.reader.impl.DefaultParser.Bracket;
import parser.Token.TokenType;

public class Scanner {
    public List<Token> tokenStream;

    private Integer lookaheadIndex;
    private Integer start, end;
    private Integer stateCode;
    private String rawString;

    public Scanner(String raw) {
        rawString = raw;
        stateCode = 0;
        lookaheadIndex = 0;
    }

    private String lookhead(Integer n) {
        return rawString.substring(lookaheadIndex, lookaheadIndex + n);
    }

    private Boolean funcState() throws ExpressionException {
        String tmp = lookhead(1);
        Character tmpChar = tmp.charAt(0);
        Integer bracketCounter = 0;
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
                        stateCode = 11;
                        lookaheadIndex++;
                        break;
                    default:
                        throw new IllegalIdentifierException("Unknown function identifier at pos %d".formatted(lookaheadIndex));
                }
                break;
            case 1:
                tmp = lookhead(3);
                if (tmp.equals("ax(") || tmp.equals("in(")) {
                    tmp = "m" + tmp;
                    Token tmpToken= new Token(tmp, TokenType.function);
                    tokenStream.add(tmpToken);
                    lookaheadIndex += 3;
                } else {
                    throw new IllegalIdentifierException("Unknown function identifier at pos %d".formatted(lookaheadIndex));
                }
            case 2:
                
            default:
            throw new IllegalSymbolException("Unknown Exception in Function scaning at pos %d".formatted(lookaheadIndex));
        }
    }

    private Boolean funcScan() throws ExpressionException {
        Boolean flag = true;
        stateCode = 0;
        while (flag && lookaheadIndex != rawString.length()) {
            flag = funcState();
        }
        return true;
    }
    private Boolean decState() throws ExpressionException{
        String tmp = lookhead(1);
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
                    throw new IllegalDecimalException("Illegal char \"%s\" when parsing decimal at pos %d, expected [0-9]/E/e/.".formatted(tmp, lookaheadIndex));
                }
                break;
            case 2:
                if (Character.isDigit(tmpChar)) {
                    stateCode = 2;
                    end += 1;
                    lookaheadIndex += 1;
                } else {
                    throw new IllegalDecimalException("Illegal char \"%s\" when parsing decimal at pos %d, expected [0-9]".formatted(tmp, lookaheadIndex));
                }
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
                    throw new IllegalDecimalException("Illegal char \"%s\" when parsing decimal at pos %d, expected [0-9]/E/e".formatted(tmp, lookaheadIndex));
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
                lookaheadIndex += 1;
                break;
            default:
                throw new IllegalDecimalException("Unknown Exception in Decimal Scanning at pos %d".formatted(lookaheadIndex));
        }
        return true;
    }
    private Boolean decScan() throws ExpressionException {
        Boolean flag = true;
        stateCode = 0;
        while (flag && lookaheadIndex != rawString.length()) {
            flag = decState();
        }
        return true;
    }

    private Boolean boolScan() throws ExpressionException {
        String tmp = lookhead(4).toLowerCase();
        if (tmp.equals("true")) {
            lookaheadIndex += 4;
            Token tmpToken = new Token(tmp, TokenType.oprend_bool);
            tokenStream.add(tmpToken);
        } else {
            tmp = lookhead(5).toLowerCase();
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

    private Boolean opScan() throws ExpressionException {
        String tmp = lookhead(2);
        if (Token.operatorSet.contains(tmp)) {
            lookaheadIndex += 2;
            Token tmpToken = new Token(tmp, TokenType.operator);
            tokenStream.add(tmpToken);
        } else {
            tmp = lookhead(1);
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

    private Boolean exprScan() throws ExpressionException {
        Boolean flag=false;
        switch (lookhead(1)) {
            case " ":
                break;
            case "t":
            case "T":
            case "f":
            case "F":
                flag = boolScan();
                break;
            case "m":
            case "s":
            case "c":
                stateCode = 0;
                flag = funcScan();
                break;
            default:
                if (Token.operatorSet.contains(lookhead(1))) {
                    flag = opScan();
                } else if (Character.isDigit(lookhead(1).charAt(0))) {
                    stateCode = 0;
                    flag = decScan();
                } else {
                    throw new IllegalSymbolException("Illegal symbol \"%s\" at position %d".formatted(lookhead(1), lookaheadIndex));
                }
                break;
        }
        return flag;
    }



    

    public Boolean scan() throws ExpressionException {
        Boolean flag = true;
        while (flag && lookaheadIndex != rawString.length()) {
            flag = exprScan();
        }
        return true;
    }
}