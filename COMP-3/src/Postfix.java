import java.io.*;

/**
 * Logic of Postfix parser
 * @author zetako
 * @version 0.0.1
 */
class Parser {
/**
 * lookahead for all parser
 */
	static int lookahead;
/**
 * the position of now parsing token
 */
	int pos;
/**
 * constror of Parser, init lookahead
 * @throws IOException io error is no catch by parser
 */
	public Parser() throws IOException {
		lookahead = System.in.read();
		pos = 0;
	}

/**
 * loop version of expr, start point of parsing
 * @throws IOException io error is no catch by parser
 */
	void expr() throws IOException {
		try {
			term();
			restLoop();
		} catch (Error e) {
			System.out.write('\n');
			System.out.println("Error when parse");
			System.out.print(e.getMessage());
		}
	}

/**
 * recursion version of expr, start point of parsing
 * @throws IOException io error is no catch by parser
 */
	void exprRecursion() throws IOException {
		try {
			term();
			rest();
		} catch (Error e) {
			System.out.write('\n');
			System.out.println("Error when parse: ");
			System.out.print(e.getMessage());
		}
	}

/**
 * resursion version of rest
 * @throws IOException io error is no catch by parser
 * @throws Error error catch is not done in non-loop version
 */
	void rest() throws IOException,Error {
		if (lookahead == '+') {
			match('+');
			term();
			System.out.write('+');
			rest();
		} else if (lookahead == '-') {
			match('-');
			term();
			System.out.write('-');
			rest();
		} else {
			// do nothing with the input
		}
	}

/**
 * loop version of rest, parse operator part of sentence
 * @throws IOException io error is no catch by parser
 * @throws Error throw Syntax Error at op or Invalid Token at op
 */
	void restLoop() throws IOException,Error {
		while(lookahead == '+' || lookahead == '-') {
			if (lookahead == '+') {
				match('+');
				term();
				System.out.write('+');
			} else {
				match('-');
				term();
				System.out.write('-');
			}
		}
		if ( Character.isDigit((char)lookahead) ) {
			throw new Error(String.format("Syntax error at position %d(\"%c\"), expect operator(+/-)", pos, lookahead));
		} else if (lookahead != -1 && lookahead != 10 && lookahead != 13){
			throw new Error(String.format("Invalid token \"%c\"(%d) at position %d", lookahead, lookahead, pos));
		}
	}

/**
 * term, parse operand part of sentence
 * @throws IOException io error is no catch by parser
 * @throws Error throw Syntax Error at op or Invalid Token at operand(digit)
 */
	void term() throws IOException,Error {
		if (Character.isDigit((char)lookahead)) {
			System.out.write((char)lookahead);
			match(lookahead);
		} else if (lookahead != '+' && lookahead != '-') {
			throw new Error(String.format("Invalid token \"%c\"(%d) at position %d", lookahead, lookahead, pos));
		} else {
			throw new Error(String.format("Syntax error at position %d(\"%c\"), expect operand(0-9)", pos, lookahead));
		}
	}

/**
 * match step, check if it's right token
 * @param t the char to match (integer)
 * @throws IOException io error is no catch by parser
 * @throws Error throw a dismatch, normally never used
 */
	void match(int t) throws IOException,Error {
		if (lookahead == t) {
			lookahead = System.in.read();
			pos++;
		}
		else throw new Error(String.format("Syntax error at %d(\"%c\"), expect \"%c\"", pos, lookahead, t));
	}
}

/**
 * Surface of Postfix parser
 * @author zetako
 * @version 0.0.1
 */
public class Postfix {
/**
 * main function as entry point
 * @param args cmd args
 * @throws IOException directly throw out when io error occures
 */
	public static void main(String[] args) throws IOException {
		Boolean timeFlag = false;
		Boolean recursionFlag = false;
		Boolean recordFlag = false;
		if (args.length >= 1) {
			for (Integer i=0; i<args.length; i++) {
				switch (args[i]) {
					case "time":
						timeFlag = true;
						break;
					case "recursion":
						recursionFlag = true;
						break;
					case "loop":
						recursionFlag = false;
						break;
					case "record":
						recordFlag = true;
						break;
					default:
						System.out.println("Wrong Args at pos "+Integer.toString(i)+": "+args[i]);
						break;
				}
			}
		}
		System.out.println("Input an infix expression and output its postfix notation:");
		try {
			Long start = System.currentTimeMillis();
			if (recursionFlag) {
				new Parser().exprRecursion();
			} else {
				new Parser().expr();
			}
			Long end = System.currentTimeMillis();
			Long timeDur = end - start;
			if (timeFlag) {
				OutputStream fos = new FileOutputStream("result.log",true);
				OutputStreamWriter fosWriter = new OutputStreamWriter(fos, "UTF-8");
				fosWriter.append((recursionFlag ? "recursion" : "  loop   ") + " method: (average) " + Double.toString(timeDur) + "ms\n");
				fosWriter.close();
				fos.close();
			}
			if (recordFlag) {
				String recordName = recursionFlag ? "recursion-record.log" : "loop-record.log";
				System.out.println(recordName);
				OutputStream recordOS = new FileOutputStream(recordName,true);
				OutputStreamWriter recordOSWriter = new OutputStreamWriter(recordOS, "UTF-8");
				recordOSWriter.append(Long.toString(timeDur)+" ");
				recordOSWriter.close();
				recordOS.close();
			}
		} catch (IOException e) {
			System.out.println("Error when reading IO");
			System.out.println(e.getMessage());
		}
		System.out.println("\nEnd of program.");
	}
}
