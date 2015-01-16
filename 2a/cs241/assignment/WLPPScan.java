import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class WLPPScan {
	public static final void main(String[] args) {
		new WLPPScan().run();
	}

	private Lexer lexer = new Lexer();

	private void run() {
		Scanner in = new Scanner(System.in);
		while(in.hasNextLine()) {
			String line = in.nextLine();

			Token[] tokens = lexer.scan(line);

			for(int i = 0; i < tokens.length; i++) {
				tokens[i].print();
			}
		}
	}
}

enum Kind {
	ID,
	NUM,
	LPAREN,
	RPAREN,
	LBRACE,
	RBRACE,
	RETURN,
	IF,
	ELSE,
	WHILE,
	PRINTLN,
	WAIN,
	BECOMES,
	INT,
	EQ,
	NE,
	LT,
	GT,
	LE,
	GE,
	PLUS,
	MINUS,
	STAR,
	SLASH,
	PCT,
	COMMA,
	SEMI,
	NEW,
	DELETE,
	LBRACK,
	RBRACK,
	AMP,
	NULL,
	WHITESPACE;
}

class Token {
	public Kind kind;
	public String lexeme;

	public Token(Kind kind, String lexeme) {
		this.kind = kind;
		this.lexeme = lexeme;
	}

	public void print() {
		System.out.println(kind + " " + lexeme);
	}
}

class Lexer {
	public Lexer() {
		CharSet whitespace = new Chars("\t\n\r ");
		CharSet letters = new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
		CharSet nonzero = new Chars("123456789");
		CharSet digits = new Chars("0123456789");
		CharSet lettersdigits = new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
		CharSet safeletters = new Chars("ABCDEFGHIJKLMOPQRSTUVWXYZabcfghjklmoqstuvxyz");
		CharSet all = new AllChars();

		table = new Transition[] {
				new Transition(State.START, whitespace, State.WHITESPACE),
				new Transition(State.START, safeletters, State.ID),
				new Transition(State.START, nonzero, State.NUM),
				new Transition(State.START, new Chars("0"), State.ZERO),
				new Transition(State.START, new Chars("("), State.LPAREN),
				new Transition(State.START, new Chars(")"), State.RPAREN),
				new Transition(State.START, new Chars("{"), State.LBRACE),
				new Transition(State.START, new Chars("}"), State.RBRACE),
				new Transition(State.START, new Chars("["), State.LBRACK),
				new Transition(State.START, new Chars("]"), State.RBRACK),
				new Transition(State.START, new Chars("="), State.BECOMES),
				new Transition(State.START, new Chars("!"), State.NOT),
				new Transition(State.START, new Chars("<"), State.LT),
				new Transition(State.START, new Chars(">"), State.GT),
				new Transition(State.START, new Chars("+"), State.PLUS),
				new Transition(State.START, new Chars("-"), State.MINUS),
				new Transition(State.START, new Chars("*"), State.STAR),
				new Transition(State.START, new Chars("/"), State.SLASH),
				new Transition(State.START, new Chars("%"), State.PCT),
				new Transition(State.START, new Chars(","), State.COMMA),
				new Transition(State.START, new Chars(";"), State.SEMI),
				new Transition(State.START, new Chars("&"), State.AMP),
				new Transition(State.START, new Chars("N"), State.N),
				new Transition(State.START, new Chars("d"), State.d),
				new Transition(State.START, new Chars("e"), State.e),
				new Transition(State.START, new Chars("i"), State.i),
				new Transition(State.START, new Chars("n"), State.n),
				new Transition(State.START, new Chars("p"), State.p),
				new Transition(State.START, new Chars("r"), State.r),
				new Transition(State.START, new Chars("w"), State.w),
				new Transition(State.ID, lettersdigits, State.ID),
				new Transition(State.NUM, digits, State.NUM),
				new Transition(State.NUM, letters, State.ERROR),
				new Transition(State.ZERO, lettersdigits, State.ERROR),
				new Transition(State.BECOMES, new Chars("="), State.EQ),
				new Transition(State.LT, new Chars("="), State.LE),
				new Transition(State.GT, new Chars("="), State.GE),
				new Transition(State.NOT, new Chars("="), State.NE),
				new Transition(State.SLASH, new Chars("/"), State.COMMENT),
				new Transition(State.COMMENT, all, State.COMMENT),
				new Transition(State.N, new Chars("U"), State.NU),
				new Transition(State.N, new Chars("ABCDEFGHIJKLMNOPQRSTVWXYZabcdefghijklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.NU, new Chars("L"), State.NUL),
				new Transition(State.NU, new Chars("ABCDEFGHIJKMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.NUL, new Chars("L"), State.NULL),
				new Transition(State.NUL, new Chars("ABCDEFGHIJKMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.NULL, lettersdigits, State.ID),
				new Transition(State.d, new Chars("e"), State.de),
				new Transition(State.d, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdfghijklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.de, new Chars("l"), State.del),
				new Transition(State.de, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.del, new Chars("e"), State.dele),
				new Transition(State.del, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdfghijklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.dele, new Chars("t"), State.delet),
				new Transition(State.dele, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrsuvwxyz0123456789"), State.ID),
				new Transition(State.delet, new Chars("e"), State.DELETE),
				new Transition(State.delet, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdfghijklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.DELETE, lettersdigits, State.ID),
				new Transition(State.e, new Chars("l"), State.el),
				new Transition(State.e, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.el, new Chars("s"), State.els),
				new Transition(State.el, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrtuvwxyz0123456789"), State.ID),
				new Transition(State.els, new Chars("e"), State.ELSE),
				new Transition(State.els, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdfghijklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.ELSE, lettersdigits, State.ID),
				new Transition(State.i, new Chars("f"), State.IF),
				new Transition(State.i, new Chars("n"), State.in),
				new Transition(State.i, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdeghijklmopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.IF, lettersdigits, State.ID),
				new Transition(State.in, new Chars("t"), State.INT),
				new Transition(State.in, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrsuvwxyz0123456789"), State.ID),
				new Transition(State.INT, lettersdigits, State.ID),
				new Transition(State.n, new Chars("e"), State.ne),
				new Transition(State.n, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdfghijklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.ne, new Chars("w"), State.NEW),
				new Transition(State.ne, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789"), State.ID),
				new Transition(State.NEW, lettersdigits, State.ID),
				new Transition(State.p, new Chars("r"), State.pr),
				new Transition(State.p, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqstuvwxyz0123456789"), State.ID),
				new Transition(State.pr, new Chars("i"), State.pri),
				new Transition(State.pr, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghjklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.pri, new Chars("n"), State.prin),
				new Transition(State.pri, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.prin, new Chars("t"), State.print),
				new Transition(State.prin, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrsuvwxyz0123456789"), State.ID),
				new Transition(State.print, new Chars("l"), State.printl),
				new Transition(State.print, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.printl, new Chars("n"), State.PRINTLN),
				new Transition(State.printl, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.PRINTLN, lettersdigits, State.ID),
				new Transition(State.r, new Chars("e"), State.re),
				new Transition(State.r, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdfghijklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.re, new Chars("t"), State.ret),
				new Transition(State.re, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrsuvwxyz0123456789"), State.ID),
				new Transition(State.ret, new Chars("u"), State.retu),
				new Transition(State.ret, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstvwxyz0123456789"), State.ID),
				new Transition(State.retu, new Chars("r"), State.retur),
				new Transition(State.retu, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqstuvwxyz0123456789"), State.ID),
				new Transition(State.retur, new Chars("n"), State.RETURN),
				new Transition(State.retur, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.RETURN, lettersdigits, State.ID),
				new Transition(State.w, new Chars("a"), State.wa),
				new Transition(State.w, new Chars("h"), State.wh),
				new Transition(State.w, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZbcdefgijklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.wa, new Chars("i"), State.wai),
				new Transition(State.wa, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghjklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.wai, new Chars("n"), State.WAIN),
				new Transition(State.wai, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.WAIN, lettersdigits, State.ID),
				new Transition(State.wh, new Chars("i"), State.whi),
				new Transition(State.wh, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghjklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.whi, new Chars("l"), State.whil),
				new Transition(State.whi, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.whil, new Chars("e"), State.WHILE),
				new Transition(State.whil, new Chars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdfghijklmnopqrstuvwxyz0123456789"), State.ID),
				new Transition(State.WHILE, lettersdigits, State.ID)
		};
	}

	public Token[] scan(String input) {
		List<Token> ret = new ArrayList<Token>();
		if(input.length() == 0)
			return new Token[0];
		int i = 0;
		int startIndex = 0;
		State state = State.START;
		while(true) {
			Transition t = null;
			if(i < input.length())
				t = findTransition(state, input.charAt(i));
			if(t == null) {
				if(!state.isFinal()) {
					System.err.println("ERROR: Lexer error on line '" + input + "'");
					System.exit(1);
				}
				if(state.kind != Kind.WHITESPACE) {
					ret.add(new Token(state.kind, input.substring(startIndex, i)));
				}
				startIndex = i;
				state = State.START;
				if(i >= input.length())
					break;
			} else {
				state = t.toState;
				i++;
			}
		}
		return ret.toArray(new Token[ret.size()]);
	}

	private Transition findTransition(State state, char c) {
		for(int j = 0; j < table.length; j++) {
			Transition t = table[j];
			if(t.fromState == state && t.chars.contains(c)) {
				return t;
			}
		}
		return null;
	}

	private static enum State {
		START(null),
		ERROR(null),
		WHITESPACE(Kind.WHITESPACE),
		ID(Kind.ID),
		NUM(Kind.NUM),
		ZERO(Kind.NUM),
		LPAREN(Kind.LPAREN),
		RPAREN(Kind.RPAREN),
		LBRACE(Kind.LBRACE),
		RBRACE(Kind.RBRACE),
		LBRACK(Kind.LBRACK),
		RBRACK(Kind.RBRACK),
		BECOMES(Kind.BECOMES),
		EQ(Kind.EQ),
		NOT(null),
		NE(Kind.NE),
		LT(Kind.LT),
		LE(Kind.LE),
		GT(Kind.GT),
		GE(Kind.GE),
		PLUS(Kind.PLUS),
		MINUS(Kind.MINUS),
		STAR(Kind.STAR),
		SLASH(Kind.SLASH),
		COMMENT(Kind.WHITESPACE),
		PCT(Kind.PCT),
		COMMA(Kind.COMMA),
		SEMI(Kind.SEMI),
		AMP(Kind.AMP),
		N(Kind.ID),
		NU(Kind.ID),
		NUL(Kind.ID),
		NULL(Kind.NULL),
		d(Kind.ID),
		de(Kind.ID),
		del(Kind.ID),
		dele(Kind.ID),
		delet(Kind.ID),
		DELETE(Kind.DELETE),
		e(Kind.ID),
		el(Kind.ID),
		els(Kind.ID),
		ELSE(Kind.ELSE),
		i(Kind.ID),
		IF(Kind.IF),
		in(Kind.ID),
		INT(Kind.INT),
		n(Kind.ID),
		ne(Kind.ID),
		NEW(Kind.NEW),
		p(Kind.ID),
		pr(Kind.ID),
		pri(Kind.ID),
		prin(Kind.ID),
		print(Kind.ID),
		printl(Kind.ID),
		PRINTLN(Kind.PRINTLN),
		r(Kind.ID),
		re(Kind.ID),
		ret(Kind.ID),
		retu(Kind.ID),
		retur(Kind.ID),
		RETURN(Kind.RETURN),
		w(Kind.ID),
		wa(Kind.ID),
		wai(Kind.ID),
		WAIN(Kind.WAIN),
		wh(Kind.ID),
		whi(Kind.ID),
		whil(Kind.ID),
		WHILE(Kind.WHILE);

		Kind kind;

		State(Kind kind) {
			this.kind = kind;
		}

		boolean isFinal() {
			return kind != null;
		}
	}

	private interface CharSet {
		public boolean contains(char newC);
	}

	private class Chars implements CharSet {
		private String chars;
		public Chars(String chars) { this.chars = chars; }
		public boolean contains(char newC) {
			return chars.indexOf(newC) >= 0;
		}
	}

	private class AllChars implements CharSet {
		public boolean contains(char newC) {
			return true;
		}
	}

	private class Transition {
		State fromState;
		CharSet chars;
		State toState;
		Transition(State fromState, CharSet chars, State toState) {
			this.fromState = fromState;
			this.chars = chars;
			this.toState = toState;
		}
	}

	private Transition[] table;
}