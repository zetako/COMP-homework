package parser;

import zetako.Table;
import java.util.*;

import exceptions.*;
import parser.Token.TokenType;

import java.io.*;

/**
 * Parse list of token from {@link Scanner}, using OPP
 * @author zetako
 * @version 1.1
 */
public class Parser {
    /**
     * OPP table, using {@link zetako.Table} <br>
     * using 2 token OPPTag(String) to locate a action
     */
    private Table<String, String, String> OPPTable;
    /**
     * Read OPP table formatted in .csv by path
     * @param csvPath file's path
     * @return return true when read success
     */
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

    /**
     * OPP stack, shifted tokens, implement as ArrayList
     */
    private List<Token> stack;
    /**
     * OPP input stack, unshifted tokens, implement as ArrayList <br>
     * it init using token stream provided by scanner
     */
    private List<Token> input;
    /**
     * Debug oriented function for print stack and input
     */
    private void printStacks() {
        for (Token token : stack) {
            System.out.print(token.getToken() + " ");
        }
        System.out.print("|---| ");
        for (Token token : input) {
            System.out.print(token.getToken() + " ");
        }
        System.out.println(" ");
    }
    /**
     * Get the 1st terminal at top of stack, decimal and boolean is treated as nonterminal
     * @return return the terminal token
     * @throws LexicalException throw by {@link Token#Token(String, TokenType)}
     */
    private Token getLastTerminal() throws LexicalException {
        if (stack == null || stack.isEmpty()) {
            return new Token("$", TokenType.end);
        }
        Integer tmp = stack.size() - 1;
        while (stack.get(tmp).getType() == TokenType.oprend_dec || stack.get(tmp).getType() == TokenType.oprend_bool) {
            tmp--;
            if (tmp < 0) {
                return new Token("$", TokenType.end);
            }
        }
        return stack.get(tmp);
    }
    /**
     * Get the next token of input
     * @return the next token
     * @throws LexicalException throw by {@link Token#Token(String, TokenType)}
     */
    private Token getNextInput() throws LexicalException {
        if (input == null || input.isEmpty()) {
            return new Token("$", TokenType.end);
        }
        return input.get(0);
    }
    /**
     * Pop n token at top of stacks, pop failed is not catch
     * @param n pop number
     */
    private void popStack(Integer n) {
        for (int i = 0; i < n; i++) {
            stack.remove(stack.size() - 1);
        }
    }

    /**
     * Action shift, move a token from input to stack
     */
    private void shift() {
        stack.add(input.get(0));
        input.remove(0);
    }

    /**
     * Reduce for operators that cal 2 decimal to 1 decimal, like D->D+D
     * @param toReduce token to reduce, the operator
     * @throws MissingOperatorException 2 or more decimal at top of operator is not allowed
     * @throws MissingOperatorException must be decimal before/after operator
     * @throws DividedByZeroException 0 after operator "/"
     * @throws SyntacticException should never throw, an unknown operator
     */
    private void twoDecimalReduce(Token toReduce) throws ExpressionException {
        Double tmp1, tmp2;
        Double result = 0.0;
        Token tmpToken;
        String tmpStr = toReduce.getToken();
        if (!toReduce.equals(stack.get(stack.size() - 2))) {
            throw new MissingOperatorException("1+ oprend after \"%s\"".formatted(tmpStr));
        }
        tmpToken = stack.get(stack.size() - 3);
        if (tmpToken.getType() != TokenType.oprend_dec) {
            throw new MissingOperandException("Decimal expected before \"%s\"".formatted(tmpStr));
        }
        tmp1 = tmpToken.getDoubleValue();
        tmpToken = stack.get(stack.size() - 1);
        if (tmpToken.getType() != TokenType.oprend_dec) {
            throw new MissingOperandException("Decimal expected after \"%s\"".formatted(tmpStr));
        }
        tmp2 = tmpToken.getDoubleValue();
        popStack(3);

        switch (tmpStr) {
            case "^":
                result = Math.pow(tmp1, tmp2);
                break;
            case "*":
                result = tmp1 * tmp2;
                break;
            case "/":
                if (tmp2 == 0) {
                    throw new DividedByZeroException();
                }
                result = tmp1 / tmp2;
                break;
            case "+":
                result = tmp1 + tmp2;
                break;
            case "-":
                result = tmp1 - tmp2;
                break;
            default:
                throw new SyntacticException("Unknown Exception");
        }

        stack.add(new Token(result.toString(), TokenType.oprend_dec));
    }
    /**
     * Reduce for operators that cal 2 decimal to 1 boolean, like D->D=D
     * @param toReduce token to reduce, the operator
     * @throws MissingOperatorException 2 or more boolean at top of operator is not allowed
     * @throws MissingOperatorException must be decimal before/after operator
     * @throws SyntacticException should never throw, an unknown operator
     */
    private void compareReduce(Token toReduce) throws ExpressionException {
        Double tmp1, tmp2;
        Boolean result = true;
        Token tmpToken;
        String tmpStr = toReduce.getToken();
        if (!toReduce.equals(stack.get(stack.size() - 2))) {
            throw new MissingOperatorException("1+ oprend after \"%s\"".formatted(tmpStr));
        }
        tmpToken = stack.get(stack.size() - 3);
        if (tmpToken.getType() != TokenType.oprend_dec) {
            throw new MissingOperandException("Decimal expected before \"%s\"".formatted(tmpStr));
        }
        tmp1 = tmpToken.getDoubleValue();
        tmpToken = stack.get(stack.size() - 1);
        if (tmpToken.getType() != TokenType.oprend_dec) {
            throw new MissingOperandException("Decimal expected after \"%s\"".formatted(tmpStr));
        }
        tmp2 = tmpToken.getDoubleValue();
        popStack(3);

        switch (tmpStr) {
            case "=":
                result = (tmp1 == tmp2);
                break;
            case "<>":
                result = (tmp1 != tmp2);
                break;
            case "<":
                result = (tmp1 < tmp2);
                break;
            case "<=":
                result = (tmp1 <= tmp2);
                break;
            case ">":
                result = (tmp1 > tmp2);
                break;
            case ">=":
                result = (tmp1 >= tmp2);
                break;
            default:
                throw new SyntacticException("Unknown Exception");
        }

        stack.add(new Token(result.toString(), TokenType.oprend_bool));
    }
    /**
     * 
     * Reduce for operators that cal 2 boolean to 1 boolean, like D->D|D
     * @param toReduce token to reduce, the operator
     * @throws MissingOperatorException 2 or more boolean at top of operator is not allowed
     * @throws MissingOperatorException must be boolean before/after operator
     * @throws SyntacticException should never throw, an unknown operator
     */
    private void twoBooleanReduce(Token toReduce) throws ExpressionException {
        Boolean tmp1, tmp2;
        Boolean result;
        Token tmpToken;
        String tmpStr = toReduce.getToken();
        if (!toReduce.equals(stack.get(stack.size() - 2))) {
            throw new MissingOperatorException("1+ oprend after \"%s\"".formatted(tmpStr));
        }
        tmpToken = stack.get(stack.size() - 3);
        if (tmpToken.getType() != TokenType.oprend_bool) {
            throw new MissingOperandException("Boolean expected before \"%s\"".formatted(tmpStr));
        }
        tmp1 = tmpToken.getBooleanValue();
        tmpToken = stack.get(stack.size() - 1);
        if (tmpToken.getType() != TokenType.oprend_bool) {
            throw new MissingOperandException("Boolean expected after \"%s\"".formatted(tmpStr));
        }
        tmp2 = tmpToken.getBooleanValue();
        popStack(3);

        switch (tmpStr) {
            case "&":
                result = tmp1 && tmp2;
                break;
            case "|":
                result = tmp1 || tmp2;
                break;
            default:
                throw new SyntacticException("Unknown Exception");
        }

        stack.add(new Token(result.toString(), TokenType.oprend_bool));
    }
    /**
     * Reduce opreator, invoke detail reduce function or directly reduce
     * @param toReduce token to reduce, the operator
     * @throws ExpressionException should never throw, toReduce is not an operator
     * @throws MissingOperatorException any token after ")" is not allowed
     * @throws MissingleftParenthesisException "(" not found
     * @throws TypeMismatchedException no decimal/boolean inside "()"
     * 
     * @throws MissingOperatorException 2 or more tokens after -(minus)/!
     * @throws MissingOperendException not matching tokens for -(minus)/!
     * 
     * @throws MissingOperatorException 2 or more tokens after ":"
     * @throws TrinaryOperationException "?" not found
     * @throws MissingOperandException "A?B:C" A, B or C not match
     * @see Parser#twoDecimalReduce(Token)
     * @see Parser#compareReduce(Token)
     * @see Parser#twoBooleanReduce(Token)
     */
	private void reduceOperator(Token toReduce) throws ExpressionException {
		if (toReduce.getType() != TokenType.operator) {
            throw new ExpressionException("Failed to reduce operator");
        }
        Double tmp;
        Token tmpToken;
        Boolean tmpBool;
    	switch (toReduce.getToken()) {
            case ")":
                if (!toReduce.equals(stack.get(stack.size() - 1))) {
                    throw new MissingOperatorException("NO oprend should after \")\"");
                }
                if (!stack.get(stack.size() - 3).getToken().equals("(")) {
                    throw new MissingLeftParenthesisException();
                }
                tmpToken = stack.get(stack.size() - 2);
                if  (!(tmpToken.getType() == TokenType.oprend_bool || tmpToken.getType() == TokenType.oprend_dec)) {
                    throw new TypeMismatchedException("Boolean or Decimal excepted between \"(\" and \")\"");
                }
                popStack(3);
                stack.add(tmpToken);
                break;
            case "-":
                if (OPPTag(toReduce).equals("minus")) {
                    if (!toReduce.equals(stack.get(stack.size() - 2))) {
                        throw new MissingOperatorException("1+ oprend after minus");
                    }
                    tmpToken = stack.get(stack.size() - 1);
                    if (tmpToken.getType() != TokenType.oprend_dec) {
                        throw new MissingOperandException("Decimal expected after minus");
                    }
                    tmp = -tmpToken.getDoubleValue();
                    popStack(2);
                    stack.add(new Token(tmp.toString(), TokenType.oprend_dec));
                } else {
                    twoDecimalReduce(toReduce);
                }
                break;
            case "^":
            case "*":
            case "/":
            case "+":
                twoDecimalReduce(toReduce);
                break;
            case "=":
            case "<>":
            case "<":
            case "<=":
            case ">":
            case ">=":
                compareReduce(toReduce);
                break;
            case "!":
                if (!toReduce.equals(stack.get(stack.size() - 2))) {
                    throw new MissingOperatorException("1+ oprend after \"!\"");
                }
                tmpToken = stack.get(stack.size() - 1);
                if (tmpToken.getType() != TokenType.oprend_bool) {
                    throw new MissingOperandException("Boolean expected after \"!\"");
                }
                tmpBool = !tmpToken.getBooleanValue();
                popStack(2);
                stack.add(new Token(tmpBool.toString(), TokenType.oprend_bool));
                break;
            case "&":
            case "|":
                twoBooleanReduce(toReduce);
                break;
            case ":":
                if (!toReduce.equals(stack.get(stack.size() - 2))) {
                    throw new MissingOperatorException("1+ oprend after \":\"");
                }
                if (!stack.get(stack.size() - 4).getToken().equals("?")) {
                    throw new TrinaryOperationException("\"?\" except when reduce \":\"");
                }
                
                tmpToken = stack.get(stack.size() - 5);
                if (tmpToken.getType() != TokenType.oprend_bool) {
                    throw new MissingOperandException("Boolean expected before \"?\"");
                }
                tmpBool = tmpToken.getBooleanValue();
                tmpToken = stack.get(stack.size() - 1);
                if (tmpToken.getType() != TokenType.oprend_dec) {
                    throw new MissingOperandException("Decimal expected after \":\"");
                }
                tmp = tmpToken.getDoubleValue();
                tmpToken = stack.get(stack.size() - 3);
                if (tmpToken.getType() != TokenType.oprend_dec) {
                    throw new MissingOperandException("Decimal expected before \":\"");
                }
                if (tmpBool) {
                    tmp = tmpToken.getDoubleValue();
                }

                popStack(5);
                stack.add(new Token(tmp.toString(), TokenType.oprend_dec));
            default:
                break;
        }
    }

    /**
     * Reduce functions with single argument(sin(), cos())
     * @param toReduce token that invoke reduce, should be ")"
     * @throws MissingOperatorException ")" should at top of the stack
     * @throws FunctionCallException no matching function name
     * @throws FunctionCallException not matching type for function argument
     */
    private void unaryFunctionReduce(Token toReduce) throws ExpressionException {
        Token tmpToken;
        if (!toReduce.equals(stack.get(stack.size() - 1))) {
            throw new MissingOperatorException("NO oprend should after \")\"");
        }
        String funcName = stack.get(stack.size() - 3).getToken();
        if (!(funcName.equals("sin(") || funcName.equals("cos("))) {
            System.out.println(funcName);
            throw new FunctionCallException();
        }
        tmpToken = stack.get(stack.size() - 2);
        if  (tmpToken.getType() != TokenType.oprend_dec) {
            throw new FunctionCallException("Decimal excepted for function %s )".formatted(funcName));
        }
        Double result = tmpToken.getDoubleValue();
        switch (funcName) {
            case "sin(":
                result = Math.sin(result);
                break;
            case "cos(":
                result = Math.cos(result);
                break;
            case ",":
                throw new FunctionCallException("Too much args");
            default:    
                throw new MissingOperatorException("NO match function");
        }
        popStack(3);
        stack.add(new Token(result.toString(), TokenType.oprend_dec));
    }
    /**
     * Reduce functions with multiple arguments(max(), min())
     * @param toReduce token that invoke reduce, should be ")"
     * @param funcName function name
     * @throws MissingOperatorException ")" should at top of the stack
     * @throws TypeMismatchedException arguments type wrong
     * @throws FunctionCallException read arguments failed because can't find "," or reach bottom of stack
     * @throws IllegalIndetifierException should never throw, no matching function name
     * @throws ExpressionException
     */
    private void VariFunctionReduce(Token toReduce, String funcName) throws ExpressionException {
        if (!toReduce.equals(stack.get(stack.size() - 1))) {
            // printStacks();
            // System.out.println("%s != %s".formatted(toReduce.getToken(), stack.get(stack.size() - 1).getToken()));
            throw new MissingOperatorException("NO oprend should after \")\"");
        }
        Token tmpToken;
        Integer index = stack.size() - 2;
        List<Double> argList = new ArrayList<Double>();
        
        tmpToken = stack.get(index);
        if (tmpToken.getType() != TokenType.oprend_dec) {
            throw new TypeMismatchedException("Decimal excepted in function %s".formatted(funcName));
        }
        // System.out.println("argList add " + tmpToken.getToken());
        argList.add(tmpToken.getDoubleValue());
        index--;
        while (true) {
            tmpToken = stack.get(index);
            if (tmpToken.getType() == TokenType.function && tmpToken.getToken().equals(funcName)) {
                break;
            }
            if ((index - 1) < 0) {
                throw new ExpressionException("Unknown Exception");
            }
            if (tmpToken.getType() != TokenType.function || !tmpToken.getToken().equals(",")) {
                throw new FunctionCallException("Read arg list for %s failed".formatted(funcName));
            }
            tmpToken = stack.get(index - 1);
            if (tmpToken.getType() != TokenType.oprend_dec) {
                throw new TypeMismatchedException("Decimal excepted as function args");
            }
            // System.out.println("argList add " + tmpToken.getToken());
            argList.add(tmpToken.getDoubleValue());
            index -= 2;
        }

        Double result;
        Integer popNum = argList.size() * 2 + 1;
        switch (funcName) {
            case "max(":
                result = Collections.max(argList);
                break;
            case "min(":
                result = Collections.min(argList);
                break;
            default:
                throw new IllegalIdentifierException("Unknown function name");
        }
        popStack(popNum);
        stack.add(new Token(result.toString(), TokenType.oprend_dec));
    }
    private void reduceFunction(Token toReduce) throws ExpressionException {
        Integer index = stack.size() - 2;
        Token tmpToken;
        Boolean flag = true;
        while (flag) {
            if (index < 0) {
                throw new FunctionCallException("No Function Name Found");
            }
            tmpToken = stack.get(index);
            if (tmpToken.getType() != TokenType.function) {
                index--;
                continue;
            }
            switch (tmpToken.getToken()) {
                case ")":
                    throw new FunctionCallException("Duplicate \")\"");
                case "sin(":
                case "cos(":
                    unaryFunctionReduce(toReduce);
                    flag = false;
                    break;
                case ",":
                    index--;
                    continue;
                case "max(":
                case "min(":
                    VariFunctionReduce(toReduce, tmpToken.getToken());
                    flag = false;
                    break;
            }
        }
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
            printStacks();
            inputToken = getNextInput();
            while (inputToken.getType() == TokenType.oprend_dec || inputToken.getType() == TokenType.oprend_bool) {
                shift();
                printStacks();
                inputToken = getNextInput();
            }
            inputTokenTag = OPPTag(inputToken);
            stackToken = getLastTerminal();
            stackTokenTag = OPPTag(stackToken);
            
            OPPOperation = OPPTable.get(stackTokenTag, inputTokenTag);
            switch (OPPOperation) {
                case "reduce":
                    reduce(stackToken);
                    break;
            	case "shift":
                case "equal":
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