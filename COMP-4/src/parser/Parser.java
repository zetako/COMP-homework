package parser;

import zetako.Pair;
import zetako.Table;
import java.util.*;

import exceptions.*;
import parser.Token.TokenType;

import java.io.*;

public class Parser {
    private Table<String, String, String> OPPTable;
	public Boolean readOPPTable(String csvPath) {
        File csv = new File(csvPath);
        try {
            // init io
            BufferedReader csvReader = new BufferedReader(new FileReader(csv));
            String line;
            // read first line, get index -> String
            line = csvReader.readLine();
            String OPPTag[] = line.split(",");
            // read each line, put into table
            Integer i = 1;
			while((line = csvReader.readLine()) != null) {
				String tmp[] = line.split(",");
                for (int j = 0; j < tmp.length; j++) {
					OPPTable.put(OPPTag[i], OPPTag[j], tmp[j]);
                }
                i++;
            }
            // close io
            csvReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("No such file : " + csvPath);
        } catch (IOException e) {
            System.out.println("Error when reading csv file");
        }
        return true;
    }

    private List<Token> stack;
    private List<Token> input;
    private Token getLastTerminal() throws LexicalException {
        if (stack == null || stack.isEmpty()) {
            return new Token("$", TokenType.end);
        }
        Integer tmp = stack.size() - 1;
        while (stack.get(tmp).getType() == TokenType.oprend_dec) {
            tmp--;
            if (tmp < 0) {
                return new Token("$", TokenType.end);
            }
        }
        return stack.get(tmp);
    }
    private Token getNextInput() throws LexicalException {
        if (input == null || input.isEmpty()) {
            return new Token("$", TokenType.end);
        }
        return input.get(0);
    }
    private void shift() {
        stack.add(input.get(0));
        input.remove(0);
    }
	private void reduceOperator(Token toReduce) throws ExpressionException {
		if (toReduce.getType() != TokenType.operator) {
            throw new ExpressionException("Failed to reduce operator");
        }
    	switch (toReduce.getToken()) {
            case "":
                
                break;
        
            default:
                break;
        }
    }
    private void reduceFunction(Token toReduce) {

    }

    private void reduce(Token toReduce) throws ExpressionException {
		switch (toReduce.getType()) {
            case operator:
                reduceOperator(toReduce);
            	break;
        	case function:
            	reduceFunction(toReduce);
            	break;
            default:
            	throw new ExpressionException("Failed to reduce");
        }
    }
    private Double accept() throws MissingOperatorException {
		if (stack.size() != 1 || stack.get(0).getType() != TokenType.oprend_dec) {
            throw new MissingOperatorException("Parse finish but 1+ oprand in stack");
        }
        Double ret = 0.0;
        try {
            ret = stack.get(0).getDoubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    private void error(String OPPTag) throws ExpressionException {
		switch (OPPTag) {
            case "error1":
                throw new MissingOperatorException();
        	case "error2":
            	throw new MissingOperandException();
            case "error3":
            	throw new MissingLeftParenthesisException();
            case "error4":
            	throw new MissingRightParenthesisException();
            case "error5":
            	throw new FunctionCallException();
            case "error6":
            	throw new TrinaryOperationException();
            case "error7":
            	throw new TypeMismatchedException();
            default:
                throw new SyntacticException();
        }
    }

    private String OPPTag(Token token) {
        if (!token.OPPTagDefined) {
            token.OPPTagDefined = true;
            switch (token.getType()) {
                case operator:
                    switch (token.getToken()) {
                        case "(":
                        case ")":
                        case "^":
                        case "!":
                        case "&":
                        case "|":
                        case "?":
                        case ":":
                            token.OPPTag = token.getToken();
                            break;
                        case "<":
                        case "<=":
                        case ">":
                        case ">=":
                        case "=":
                        case "<>":
                            token.OPPTag = "compare";
                            break;
                        case "*":
                        case "/":
                            token.OPPTag = "*/";
                            break;
                        case "+":
                        case "-":
                            if (stack.get(stack.size() - 1).getType() != TokenType.oprend_dec && token.getToken().equals("-")) {
                                token.OPPTag = "minus";
                            } else {
                                token.OPPTag = "+-";
                            }
                            break;
                        default:
                            token.OPPTag = "unknown";
                            break;
                    }
                    break;
                case function:
                    switch (token.getToken()) {
                        case "sin(":
                        case "cos(":
                        case "min(":
                        case "max(":
                            token.OPPTag = "functionLeft";
                            break;
                        case ",":
                            token.OPPTag = "comma";
                            break;
                        case ")":
                            token.OPPTag = "functionRight";
                            break;
                        default:
                            token.OPPTag = "unknown";
                            break;
                    }
                    break;
                case oprend_dec:
                    token.OPPTag = "decimal";
                    break;
                case oprend_bool:
                    token.OPPTag = "boolean";
                    break;
                case end:
                    token.OPPTag = "$";
                    break;
                default:
                    token.OPPTag = "unknown";
                    break;
            }
        }
        return token.OPPTag;
    }

    public Parser(List<Token> tokenStream) {
        input = tokenStream;
        stack = new ArrayList<Token>();
        OPPTable = new Table<String, String, String>();

        readOPPTable("../OPPTable.csv");
    }

    public Double parse() throws ExpressionException {
        Token stackToken, inputToken;
        String stackTokenTag, inputTokenTag;
        String OPPOperation;
		while(true) {
			stackToken = getLastTerminal();
            inputToken = getNextInput();
            stackTokenTag = OPPTag(stackToken);
            inputTokenTag = OPPTag(inputToken);

            OPPOperation = OPPTable.get(stackTokenTag, inputTokenTag);
            switch (OPPOperation) {
                case "reduce":
                    reduce(stackToken);
                    break;
            	case "shift":
                	shift();
                    break;
                case "accept":
                	return accept();
                default:
                	error(OPPOperation);
                    break;
            }
        }
    }
}