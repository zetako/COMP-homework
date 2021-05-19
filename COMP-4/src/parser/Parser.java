package parser;

import zetako.Pair;
import zetako.Table;
import java.util.*;

import exceptions.ExpressionException;
import exceptions.FunctionCallException;
import exceptions.MissingLeftParenthesisException;
import exceptions.MissingOperandException;
import exceptions.MissingOperatorException;
import exceptions.MissingRightParenthesisException;
import exceptions.SyntacticException;
import exceptions.TrinaryOperationException;
import exceptions.TypeMismatchedException;
import parser.Token.TokenType;
import sun.jvm.hotspot.code.ExceptionBlob;

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
    private Token getLastTerminal() {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        Integer tmp = stack.size() - 1;
        while (stack.get(tmp).getType() == TokenType.oprend_dec) {
            tmp--;
            if (tmp < 0) {
                return null;
            }
        }
        return stack.get(tmp);
    }
    private Token getNextInput() {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return input.get(0);
    }
    private void shift() {
        stack.add(input.get(0));
        input.remove(0);
    }
    private void reduce() {

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

    private OPPTag(Token token) {
        if (token.OPPTagDefined) {
            return token.OPPTag;
        }
        switch (token.getType()) {
            case 
        }
    }

    public Parser(List<Token> tokenStream) {
        input = tokenStream;
        stack = new ArrayList<Token>();
        OPPTable = new Table<String, String, String>();

        readOPPTable("../OPPTable.csv");
    }

    public Double parse() throws ExpressionException {
        Token stackToken, inputToken;
		while(true) {
			stackToken = getLastTerminal();
            inputToken = getNextInput();
            if (stackToken == null && inputToken != null) { // stack is empty, directly shift
                shift();
            } else if (stackToken != null && inputToken == null) {
                reduce();
            } else if (stackToken == null && inputToken == null) {
                if (stack.size() != 1) { // error : if still 1+ decimal in stack, error
                    throw new MissingOperatorException("Parse finish but still 1+ decimal in stack");
                } else if (stack.get(0).getType() != TokenType.oprend_dec) {
                    throw new SyntacticException("Unknown Excption when accepting");
                } else {
                    try {
                        Double ret = stack.get(0).getDoubleValue();
                        return ret;
                    } catch (Exception e) {
                        throw new SyntacticException("Get wrong content of decimal token when accepting");
                    }
                }
            } else {
	
            }
        }
    }
}