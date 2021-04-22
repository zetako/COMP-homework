import java.io.*;

class Parser {
	static int lookahead;
	int pos;

	public Parser() throws IOException {
		lookahead = System.in.read();
		pos = 0;
	}

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

	void exprrecursion() throws IOException {
		try {
			term();
			rest();
		} catch (Error e) {
			System.out.write('\n');
			System.out.println("Error when parse: " + e.toString());
			System.out.print(e.getMessage());
		}
	}

	void rest() throws IOException {
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

	void restLoop() throws IOException {
		if (lookahead != '+' && lookahead != '-') {
			throw new Error(String.format("Syntax error at position %d(\"%c\"), expect operator(+/-)", pos, lookahead));
		}
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
	}
	void term() throws IOException {
		if (Character.isDigit((char)lookahead)) {
			System.out.write((char)lookahead);
			match(lookahead);
		} else throw new Error(String.format("Syntax error at position %d(\"%c\"), expect operand(0-9)", pos, lookahead));
	}

	void match(int t) throws IOException {
		if (lookahead == t) {
			lookahead = System.in.read();
			pos++;
		}
		else throw new Error(String.format("Syntax error at %d(\"%c\"), expect \"%c\"", pos, lookahead, t));
	}
}

public class Postfix {
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
				new Parser().exprrecursion();
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
