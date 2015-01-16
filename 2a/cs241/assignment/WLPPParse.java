import java.util.Scanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;


public class WLPPParse {
	public static final void main(String[] args) {
		new WLPPParse().run();
	}

	private void run() {
		init();
		generateWLPPGrammar();

		getTokens();

		state.push(0);

		for(int i = 0; i < input.size(); i++) {
			Transition t;
			while(true) {
				t = findTransition(input.get(i));
				if(t == null) {
					System.err.println("ERROR: Syntax error at token " + i);
					System.exit(0);
				} else if("SHIFT".equals(t.type)) {
					break;
				} else {
					reduce(t, i);
				}
			}
			shift(input.get(i), t.to);
		}

		output.add(rules[state.get(0)]);

		Tree parsetree = lrdo();
		traverse(parsetree);
	}

	private Set<String> terminals;
	private Set<String> nonterminals;
	private String[] rules;
	private Transition[] transitions;

	private Stack<Token> symbol;
	private Stack<Integer> state;

	private List<Token> input;
	private List<String> output;

	private String start;

	private void init() {
		symbol = new Stack<Token>();
		state = new Stack<Integer>();

		input = new ArrayList<Token>();
		output = new ArrayList<String>();
	}

	private void generateWLPPGrammar() {
		start = "S";

		terminals = new HashSet<String>(Arrays.asList("AMP", "BOF", "BECOMES", "COMMA", "DELETE", "ELSE", "EOF", "EQ", "GE", "GT", "ID", "IF", "INT", "LBRACE", "LBRACK", "LE", "LPAREN", "LT", "MINUS", "NE", "NEW", "NULL", "NUM", "PCT", "PLUS", "PRINTLN", "RBRACE", "RBRACK", "RETURN", "RPAREN", "SEMI", "SLASH", "STAR", "WAIN", "WHILE"));

		nonterminals = new HashSet<String>(Arrays.asList("dcl", "dcls", "expr", "factor", "lvalue", "procedure", "statement", "statements", "term", "test", "type"));

		rules = new String[] { "S BOF procedure EOF", "procedure INT WAIN LPAREN dcl COMMA dcl RPAREN LBRACE dcls statements RETURN expr SEMI RBRACE", "type INT", "type INT STAR", "dcls", "dcls dcls dcl BECOMES NUM SEMI", "dcls dcls dcl BECOMES NULL SEMI", "dcl type ID", "statements", "statements statements statement", "statement lvalue BECOMES expr SEMI", "statement IF LPAREN test RPAREN LBRACE statements RBRACE ELSE LBRACE statements RBRACE", "statement WHILE LPAREN test RPAREN LBRACE statements RBRACE", "statement PRINTLN LPAREN expr RPAREN SEMI", "statement DELETE LBRACK RBRACK expr SEMI", "test expr EQ expr", "test expr NE expr", "test expr LT expr", "test expr LE expr", "test expr GE expr", "test expr GT expr", "expr term", "expr expr PLUS term", "expr expr MINUS term", "term factor", "term term STAR factor", "term term SLASH factor", "term term PCT factor", "factor ID", "factor NUM", "factor NULL", "factor LPAREN expr RPAREN", "factor AMP lvalue", "factor STAR factor", "factor NEW INT LBRACK expr RBRACK", "lvalue ID", "lvalue STAR factor", "lvalue LPAREN lvalue RPAREN" };

		transitions = new Transition[] { new Transition(1, "BECOMES", 29, "REDUCE"), new Transition(1, "EQ", 29, "REDUCE"), new Transition(1, "GE", 29, "REDUCE"), new Transition(1, "GT", 29, "REDUCE"), new Transition(1, "LE", 29, "REDUCE"), new Transition(1, "LT", 29, "REDUCE"), new Transition(1, "MINUS", 29, "REDUCE"), new Transition(1, "NE", 29, "REDUCE"), new Transition(1, "PCT", 29, "REDUCE"), new Transition(1, "PLUS", 29, "REDUCE"), new Transition(1, "RBRACK", 29, "REDUCE"), new Transition(1, "RPAREN", 29, "REDUCE"), new Transition(1, "SEMI", 29, "REDUCE"), new Transition(1, "SLASH", 29, "REDUCE"), new Transition(1, "STAR", 29, "REDUCE"), new Transition(10, "DELETE", 11, "REDUCE"), new Transition(10, "ID", 11, "REDUCE"), new Transition(10, "IF", 11, "REDUCE"), new Transition(10, "LPAREN", 11, "REDUCE"), new Transition(10, "PRINTLN", 11, "REDUCE"), new Transition(10, "RBRACE", 11, "REDUCE"), new Transition(10, "RETURN", 11, "REDUCE"), new Transition(10, "STAR", 11, "REDUCE"), new Transition(10, "WHILE", 11, "REDUCE"), new Transition(100, "DELETE", 8, "REDUCE"), new Transition(100, "ID", 8, "REDUCE"), new Transition(100, "IF", 8, "REDUCE"), new Transition(100, "LPAREN", 8, "REDUCE"), new Transition(100, "PRINTLN", 8, "REDUCE"), new Transition(100, "RBRACE", 8, "REDUCE"), new Transition(100, "RETURN", 8, "REDUCE"), new Transition(100, "STAR", 8, "REDUCE"), new Transition(100, "WHILE", 8, "REDUCE"), new Transition(101, "DELETE", 8, "REDUCE"), new Transition(101, "ID", 8, "REDUCE"), new Transition(101, "IF", 8, "REDUCE"), new Transition(101, "LPAREN", 8, "REDUCE"), new Transition(101, "PRINTLN", 8, "REDUCE"), new Transition(101, "RBRACE", 8, "REDUCE"), new Transition(101, "RETURN", 8, "REDUCE"), new Transition(101, "STAR", 8, "REDUCE"), new Transition(101, "WHILE", 8, "REDUCE"), new Transition(103, "DELETE", 8, "REDUCE"), new Transition(103, "ID", 8, "REDUCE"), new Transition(103, "IF", 8, "REDUCE"), new Transition(103, "LPAREN", 8, "REDUCE"), new Transition(103, "PRINTLN", 8, "REDUCE"), new Transition(103, "RBRACE", 8, "REDUCE"), new Transition(103, "RETURN", 8, "REDUCE"), new Transition(103, "STAR", 8, "REDUCE"), new Transition(103, "WHILE", 8, "REDUCE"), new Transition(105, "DELETE", 10, "REDUCE"), new Transition(105, "ID", 10, "REDUCE"), new Transition(105, "IF", 10, "REDUCE"), new Transition(105, "LPAREN", 10, "REDUCE"), new Transition(105, "PRINTLN", 10, "REDUCE"), new Transition(105, "RBRACE", 10, "REDUCE"), new Transition(105, "RETURN", 10, "REDUCE"), new Transition(105, "STAR", 10, "REDUCE"), new Transition(105, "WHILE", 10, "REDUCE"), new Transition(106, "DELETE", 14, "REDUCE"), new Transition(106, "ID", 14, "REDUCE"), new Transition(106, "IF", 14, "REDUCE"), new Transition(106, "LPAREN", 14, "REDUCE"), new Transition(106, "PRINTLN", 14, "REDUCE"), new Transition(106, "RBRACE", 14, "REDUCE"), new Transition(106, "RETURN", 14, "REDUCE"), new Transition(106, "STAR", 14, "REDUCE"), new Transition(106, "WHILE", 14, "REDUCE"), new Transition(11, "BECOMES", 30, "REDUCE"), new Transition(11, "EQ", 30, "REDUCE"), new Transition(11, "GE", 30, "REDUCE"), new Transition(11, "GT", 30, "REDUCE"), new Transition(11, "LE", 30, "REDUCE"), new Transition(11, "LT", 30, "REDUCE"), new Transition(11, "MINUS", 30, "REDUCE"), new Transition(11, "NE", 30, "REDUCE"), new Transition(11, "PCT", 30, "REDUCE"), new Transition(11, "PLUS", 30, "REDUCE"), new Transition(11, "RBRACK", 30, "REDUCE"), new Transition(11, "RPAREN", 30, "REDUCE"), new Transition(11, "SEMI", 30, "REDUCE"), new Transition(11, "SLASH", 30, "REDUCE"), new Transition(11, "STAR", 30, "REDUCE"), new Transition(12, "BECOMES", 28, "REDUCE"), new Transition(12, "EQ", 28, "REDUCE"), new Transition(12, "GE", 28, "REDUCE"), new Transition(12, "GT", 28, "REDUCE"), new Transition(12, "LE", 28, "REDUCE"), new Transition(12, "LT", 28, "REDUCE"), new Transition(12, "MINUS", 28, "REDUCE"), new Transition(12, "NE", 28, "REDUCE"), new Transition(12, "PCT", 28, "REDUCE"), new Transition(12, "PLUS", 28, "REDUCE"), new Transition(12, "RBRACK", 28, "REDUCE"), new Transition(12, "RPAREN", 28, "REDUCE"), new Transition(12, "SEMI", 28, "REDUCE"), new Transition(12, "SLASH", 28, "REDUCE"), new Transition(12, "STAR", 28, "REDUCE"), new Transition(15, "EQ", 24, "REDUCE"), new Transition(15, "GE", 24, "REDUCE"), new Transition(15, "GT", 24, "REDUCE"), new Transition(15, "LE", 24, "REDUCE"), new Transition(15, "LT", 24, "REDUCE"), new Transition(15, "MINUS", 24, "REDUCE"), new Transition(15, "NE", 24, "REDUCE"), new Transition(15, "PCT", 24, "REDUCE"), new Transition(15, "PLUS", 24, "REDUCE"), new Transition(15, "RBRACK", 24, "REDUCE"), new Transition(15, "RPAREN", 24, "REDUCE"), new Transition(15, "SEMI", 24, "REDUCE"), new Transition(15, "SLASH", 24, "REDUCE"), new Transition(15, "STAR", 24, "REDUCE"), new Transition(19, "EQ", 21, "REDUCE"), new Transition(19, "GE", 21, "REDUCE"), new Transition(19, "GT", 21, "REDUCE"), new Transition(19, "LE", 21, "REDUCE"), new Transition(19, "LT", 21, "REDUCE"), new Transition(19, "MINUS", 21, "REDUCE"), new Transition(19, "NE", 21, "REDUCE"), new Transition(19, "PLUS", 21, "REDUCE"), new Transition(19, "RBRACK", 21, "REDUCE"), new Transition(19, "RPAREN", 21, "REDUCE"), new Transition(19, "SEMI", 21, "REDUCE"), new Transition(29, "ID", 3, "REDUCE"), new Transition(31, "EOF", 1, "REDUCE"), new Transition(36, "BECOMES", 31, "REDUCE"), new Transition(36, "EQ", 31, "REDUCE"), new Transition(36, "GE", 31, "REDUCE"), new Transition(36, "GT", 31, "REDUCE"), new Transition(36, "LE", 31, "REDUCE"), new Transition(36, "LT", 31, "REDUCE"), new Transition(36, "MINUS", 31, "REDUCE"), new Transition(36, "NE", 31, "REDUCE"), new Transition(36, "PCT", 31, "REDUCE"), new Transition(36, "PLUS", 31, "REDUCE"), new Transition(36, "RBRACK", 31, "REDUCE"), new Transition(36, "RPAREN", 31, "REDUCE"), new Transition(36, "SEMI", 31, "REDUCE"), new Transition(36, "SLASH", 31, "REDUCE"), new Transition(36, "STAR", 31, "REDUCE"), new Transition(39, "ID", 2, "REDUCE"), new Transition(4, "DELETE", 6, "REDUCE"), new Transition(4, "ID", 6, "REDUCE"), new Transition(4, "IF", 6, "REDUCE"), new Transition(4, "INT", 6, "REDUCE"), new Transition(4, "LPAREN", 6, "REDUCE"), new Transition(4, "PRINTLN", 6, "REDUCE"), new Transition(4, "RETURN", 6, "REDUCE"), new Transition(4, "STAR", 6, "REDUCE"), new Transition(4, "WHILE", 6, "REDUCE"), new Transition(40, "EQ", 25, "REDUCE"), new Transition(40, "GE", 25, "REDUCE"), new Transition(40, "GT", 25, "REDUCE"), new Transition(40, "LE", 25, "REDUCE"), new Transition(40, "LT", 25, "REDUCE"), new Transition(40, "MINUS", 25, "REDUCE"), new Transition(40, "NE", 25, "REDUCE"), new Transition(40, "PCT", 25, "REDUCE"), new Transition(40, "PLUS", 25, "REDUCE"), new Transition(40, "RBRACK", 25, "REDUCE"), new Transition(40, "RPAREN", 25, "REDUCE"), new Transition(40, "SEMI", 25, "REDUCE"), new Transition(40, "SLASH", 25, "REDUCE"), new Transition(40, "STAR", 25, "REDUCE"), new Transition(41, "EQ", 26, "REDUCE"), new Transition(41, "GE", 26, "REDUCE"), new Transition(41, "GT", 26, "REDUCE"), new Transition(41, "LE", 26, "REDUCE"), new Transition(41, "LT", 26, "REDUCE"), new Transition(41, "MINUS", 26, "REDUCE"), new Transition(41, "NE", 26, "REDUCE"), new Transition(41, "PCT", 26, "REDUCE"), new Transition(41, "PLUS", 26, "REDUCE"), new Transition(41, "RBRACK", 26, "REDUCE"), new Transition(41, "RPAREN", 26, "REDUCE"), new Transition(41, "SEMI", 26, "REDUCE"), new Transition(41, "SLASH", 26, "REDUCE"), new Transition(41, "STAR", 26, "REDUCE"), new Transition(42, "EQ", 27, "REDUCE"), new Transition(42, "GE", 27, "REDUCE"), new Transition(42, "GT", 27, "REDUCE"), new Transition(42, "LE", 27, "REDUCE"), new Transition(42, "LT", 27, "REDUCE"), new Transition(42, "MINUS", 27, "REDUCE"), new Transition(42, "NE", 27, "REDUCE"), new Transition(42, "PCT", 27, "REDUCE"), new Transition(42, "PLUS", 27, "REDUCE"), new Transition(42, "RBRACK", 27, "REDUCE"), new Transition(42, "RPAREN", 27, "REDUCE"), new Transition(42, "SEMI", 27, "REDUCE"), new Transition(42, "SLASH", 27, "REDUCE"), new Transition(42, "STAR", 27, "REDUCE"), new Transition(45, "BECOMES", 37, "REDUCE"), new Transition(45, "EQ", 37, "REDUCE"), new Transition(45, "GE", 37, "REDUCE"), new Transition(45, "GT", 37, "REDUCE"), new Transition(45, "LE", 37, "REDUCE"), new Transition(45, "LT", 37, "REDUCE"), new Transition(45, "MINUS", 37, "REDUCE"), new Transition(45, "NE", 37, "REDUCE"), new Transition(45, "PCT", 37, "REDUCE"), new Transition(45, "PLUS", 37, "REDUCE"), new Transition(45, "RBRACK", 37, "REDUCE"), new Transition(45, "RPAREN", 37, "REDUCE"), new Transition(45, "SEMI", 37, "REDUCE"), new Transition(45, "SLASH", 37, "REDUCE"), new Transition(45, "STAR", 37, "REDUCE"), new Transition(5, "DELETE", 5, "REDUCE"), new Transition(5, "ID", 5, "REDUCE"), new Transition(5, "IF", 5, "REDUCE"), new Transition(5, "INT", 5, "REDUCE"), new Transition(5, "LPAREN", 5, "REDUCE"), new Transition(5, "PRINTLN", 5, "REDUCE"), new Transition(5, "RETURN", 5, "REDUCE"), new Transition(5, "STAR", 5, "REDUCE"), new Transition(5, "WHILE", 5, "REDUCE"), new Transition(53, "BECOMES", 35, "REDUCE"), new Transition(53, "EQ", 35, "REDUCE"), new Transition(53, "GE", 35, "REDUCE"), new Transition(53, "GT", 35, "REDUCE"), new Transition(53, "LE", 35, "REDUCE"), new Transition(53, "LT", 35, "REDUCE"), new Transition(53, "MINUS", 35, "REDUCE"), new Transition(53, "NE", 35, "REDUCE"), new Transition(53, "PCT", 35, "REDUCE"), new Transition(53, "PLUS", 35, "REDUCE"), new Transition(53, "RBRACK", 35, "REDUCE"), new Transition(53, "RPAREN", 35, "REDUCE"), new Transition(53, "SEMI", 35, "REDUCE"), new Transition(53, "SLASH", 35, "REDUCE"), new Transition(53, "STAR", 35, "REDUCE"), new Transition(59, "BECOMES", 36, "REDUCE"), new Transition(59, "EQ", 36, "REDUCE"), new Transition(59, "GE", 36, "REDUCE"), new Transition(59, "GT", 36, "REDUCE"), new Transition(59, "LE", 36, "REDUCE"), new Transition(59, "LT", 36, "REDUCE"), new Transition(59, "MINUS", 36, "REDUCE"), new Transition(59, "NE", 36, "REDUCE"), new Transition(59, "PCT", 36, "REDUCE"), new Transition(59, "PLUS", 36, "REDUCE"), new Transition(59, "RBRACK", 36, "REDUCE"), new Transition(59, "RPAREN", 36, "REDUCE"), new Transition(59, "SEMI", 36, "REDUCE"), new Transition(59, "SLASH", 36, "REDUCE"), new Transition(59, "STAR", 36, "REDUCE"), new Transition(6, "BECOMES", 33, "REDUCE"), new Transition(6, "EQ", 33, "REDUCE"), new Transition(6, "GE", 33, "REDUCE"), new Transition(6, "GT", 33, "REDUCE"), new Transition(6, "LE", 33, "REDUCE"), new Transition(6, "LT", 33, "REDUCE"), new Transition(6, "MINUS", 33, "REDUCE"), new Transition(6, "NE", 33, "REDUCE"), new Transition(6, "PCT", 33, "REDUCE"), new Transition(6, "PLUS", 33, "REDUCE"), new Transition(6, "RBRACK", 33, "REDUCE"), new Transition(6, "RPAREN", 33, "REDUCE"), new Transition(6, "SEMI", 33, "REDUCE"), new Transition(6, "SLASH", 33, "REDUCE"), new Transition(6, "STAR", 33, "REDUCE"), new Transition(66, "DELETE", 9, "REDUCE"), new Transition(66, "ID", 9, "REDUCE"), new Transition(66, "IF", 9, "REDUCE"), new Transition(66, "LPAREN", 9, "REDUCE"), new Transition(66, "PRINTLN", 9, "REDUCE"), new Transition(66, "RBRACE", 9, "REDUCE"), new Transition(66, "RETURN", 9, "REDUCE"), new Transition(66, "STAR", 9, "REDUCE"), new Transition(66, "WHILE", 9, "REDUCE"), new Transition(67, "DELETE", 8, "REDUCE"), new Transition(67, "ID", 8, "REDUCE"), new Transition(67, "IF", 8, "REDUCE"), new Transition(67, "LPAREN", 8, "REDUCE"), new Transition(67, "PRINTLN", 8, "REDUCE"), new Transition(67, "RBRACE", 8, "REDUCE"), new Transition(67, "RETURN", 8, "REDUCE"), new Transition(67, "STAR", 8, "REDUCE"), new Transition(67, "WHILE", 8, "REDUCE"), new Transition(74, "DELETE", 13, "REDUCE"), new Transition(74, "ID", 13, "REDUCE"), new Transition(74, "IF", 13, "REDUCE"), new Transition(74, "LPAREN", 13, "REDUCE"), new Transition(74, "PRINTLN", 13, "REDUCE"), new Transition(74, "RBRACE", 13, "REDUCE"), new Transition(74, "RETURN", 13, "REDUCE"), new Transition(74, "STAR", 13, "REDUCE"), new Transition(74, "WHILE", 13, "REDUCE"), new Transition(78, "EQ", 22, "REDUCE"), new Transition(78, "GE", 22, "REDUCE"), new Transition(78, "GT", 22, "REDUCE"), new Transition(78, "LE", 22, "REDUCE"), new Transition(78, "LT", 22, "REDUCE"), new Transition(78, "MINUS", 22, "REDUCE"), new Transition(78, "NE", 22, "REDUCE"), new Transition(78, "PLUS", 22, "REDUCE"), new Transition(78, "RBRACK", 22, "REDUCE"), new Transition(78, "RPAREN", 22, "REDUCE"), new Transition(78, "SEMI", 22, "REDUCE"), new Transition(79, "EQ", 23, "REDUCE"), new Transition(79, "GE", 23, "REDUCE"), new Transition(79, "GT", 23, "REDUCE"), new Transition(79, "LE", 23, "REDUCE"), new Transition(79, "LT", 23, "REDUCE"), new Transition(79, "MINUS", 23, "REDUCE"), new Transition(79, "NE", 23, "REDUCE"), new Transition(79, "PLUS", 23, "REDUCE"), new Transition(79, "RBRACK", 23, "REDUCE"), new Transition(79, "RPAREN", 23, "REDUCE"), new Transition(79, "SEMI", 23, "REDUCE"), new Transition(80, "BECOMES", 34, "REDUCE"), new Transition(80, "EQ", 34, "REDUCE"), new Transition(80, "GE", 34, "REDUCE"), new Transition(80, "GT", 34, "REDUCE"), new Transition(80, "LE", 34, "REDUCE"), new Transition(80, "LT", 34, "REDUCE"), new Transition(80, "MINUS", 34, "REDUCE"), new Transition(80, "NE", 34, "REDUCE"), new Transition(80, "PCT", 34, "REDUCE"), new Transition(80, "PLUS", 34, "REDUCE"), new Transition(80, "RBRACK", 34, "REDUCE"), new Transition(80, "RPAREN", 34, "REDUCE"), new Transition(80, "SEMI", 34, "REDUCE"), new Transition(80, "SLASH", 34, "REDUCE"), new Transition(80, "STAR", 34, "REDUCE"), new Transition(81, "BECOMES", 7, "REDUCE"), new Transition(81, "COMMA", 7, "REDUCE"), new Transition(81, "RPAREN", 7, "REDUCE"), new Transition(85, "DELETE", 4, "REDUCE"), new Transition(85, "ID", 4, "REDUCE"), new Transition(85, "IF", 4, "REDUCE"), new Transition(85, "INT", 4, "REDUCE"), new Transition(85, "LPAREN", 4, "REDUCE"), new Transition(85, "PRINTLN", 4, "REDUCE"), new Transition(85, "RETURN", 4, "REDUCE"), new Transition(85, "STAR", 4, "REDUCE"), new Transition(85, "WHILE", 4, "REDUCE"), new Transition(9, "DELETE", 12, "REDUCE"), new Transition(9, "ID", 12, "REDUCE"), new Transition(9, "IF", 12, "REDUCE"), new Transition(9, "LPAREN", 12, "REDUCE"), new Transition(9, "PRINTLN", 12, "REDUCE"), new Transition(9, "RBRACE", 12, "REDUCE"), new Transition(9, "RETURN", 12, "REDUCE"), new Transition(9, "STAR", 12, "REDUCE"), new Transition(9, "WHILE", 12, "REDUCE"), new Transition(90, "BECOMES", 32, "REDUCE"), new Transition(90, "EQ", 32, "REDUCE"), new Transition(90, "GE", 32, "REDUCE"), new Transition(90, "GT", 32, "REDUCE"), new Transition(90, "LE", 32, "REDUCE"), new Transition(90, "LT", 32, "REDUCE"), new Transition(90, "MINUS", 32, "REDUCE"), new Transition(90, "NE", 32, "REDUCE"), new Transition(90, "PCT", 32, "REDUCE"), new Transition(90, "PLUS", 32, "REDUCE"), new Transition(90, "RBRACK", 32, "REDUCE"), new Transition(90, "RPAREN", 32, "REDUCE"), new Transition(90, "SEMI", 32, "REDUCE"), new Transition(90, "SLASH", 32, "REDUCE"), new Transition(90, "STAR", 32, "REDUCE"), new Transition(91, "RPAREN", 20, "REDUCE"), new Transition(92, "RPAREN", 19, "REDUCE"), new Transition(93, "RPAREN", 18, "REDUCE"), new Transition(94, "RPAREN", 17, "REDUCE"), new Transition(95, "RPAREN", 16, "REDUCE"), new Transition(96, "RPAREN", 15, "REDUCE"), 	new Transition(0, "BOF", 99, "SHIFT"), new Transition(100, "statements", 72, "SHIFT"), new Transition(101, "statements", 71, "SHIFT"), new Transition(103, "statements", 73, "SHIFT"), new Transition(104, "RBRACK", 27, "SHIFT"), new Transition(13, "AMP", 26, "SHIFT"), new Transition(13, "expr", 89, "SHIFT"), new Transition(13, "factor", 15, "SHIFT"), new Transition(13, "ID", 12, "SHIFT"), new Transition(13, "LPAREN", 13, "SHIFT"), new Transition(13, "NEW", 3, "SHIFT"), new Transition(13, "NULL", 11, "SHIFT"), new Transition(13, "NUM", 1, "SHIFT"), new Transition(13, "STAR", 14, "SHIFT"), new Transition(13, "term", 19, "SHIFT"), new Transition(14, "AMP", 26, "SHIFT"), new Transition(14, "factor", 6, "SHIFT"), new Transition(14, "ID", 12, "SHIFT"), new Transition(14, "LPAREN", 13, "SHIFT"), new Transition(14, "NEW", 3, "SHIFT"), new Transition(14, "NULL", 11, "SHIFT"), new Transition(14, "NUM", 1, "SHIFT"), new Transition(14, "STAR", 14, "SHIFT"), new Transition(16, "AMP", 26, "SHIFT"), new Transition(16, "expr", 92, "SHIFT"), new Transition(16, "factor", 15, "SHIFT"), new Transition(16, "ID", 12, "SHIFT"), new Transition(16, "LPAREN", 13, "SHIFT"), new Transition(16, "NEW", 3, "SHIFT"), new Transition(16, "NULL", 11, "SHIFT"), new Transition(16, "NUM", 1, "SHIFT"), new Transition(16, "STAR", 14, "SHIFT"), new Transition(16, "term", 19, "SHIFT"), new Transition(17, "AMP", 26, "SHIFT"), new Transition(17, "expr", 96, "SHIFT"), new Transition(17, "factor", 15, "SHIFT"), new Transition(17, "ID", 12, "SHIFT"), new Transition(17, "LPAREN", 13, "SHIFT"), new Transition(17, "NEW", 3, "SHIFT"), new Transition(17, "NULL", 11, "SHIFT"), new Transition(17, "NUM", 1, "SHIFT"), new Transition(17, "STAR", 14, "SHIFT"), new Transition(17, "term", 19, "SHIFT"), new Transition(18, "AMP", 26, "SHIFT"), new Transition(18, "expr", 57, "SHIFT"), new Transition(18, "factor", 15, "SHIFT"), new Transition(18, "ID", 12, "SHIFT"), new Transition(18, "LPAREN", 13, "SHIFT"), new Transition(18, "NEW", 3, "SHIFT"), new Transition(18, "NULL", 11, "SHIFT"), new Transition(18, "NUM", 1, "SHIFT"), new Transition(18, "STAR", 14, "SHIFT"), new Transition(18, "term", 19, "SHIFT"), new Transition(19, "PCT", 32, "SHIFT"), new Transition(19, "SLASH", 54, "SHIFT"), new Transition(19, "STAR", 46, "SHIFT"), new Transition(2, "AMP", 26, "SHIFT"), new Transition(2, "factor", 59, "SHIFT"), new Transition(2, "ID", 12, "SHIFT"), new Transition(2, "LPAREN", 13, "SHIFT"), new Transition(2, "NEW", 3, "SHIFT"), new Transition(2, "NULL", 11, "SHIFT"), new Transition(2, "NUM", 1, "SHIFT"), new Transition(2, "STAR", 14, "SHIFT"), new Transition(20, "SEMI", 74, "SHIFT"), new Transition(21, "AMP", 26, "SHIFT"), new Transition(21, "expr", 94, "SHIFT"), new Transition(21, "factor", 15, "SHIFT"), new Transition(21, "ID", 12, "SHIFT"), new Transition(21, "LPAREN", 13, "SHIFT"), new Transition(21, "NEW", 3, "SHIFT"), new Transition(21, "NULL", 11, "SHIFT"), new Transition(21, "NUM", 1, "SHIFT"), new Transition(21, "STAR", 14, "SHIFT"), new Transition(21, "term", 19, "SHIFT"), new Transition(22, "AMP", 26, "SHIFT"), new Transition(22, "factor", 15, "SHIFT"), new Transition(22, "ID", 12, "SHIFT"), new Transition(22, "LPAREN", 13, "SHIFT"), new Transition(22, "NEW", 3, "SHIFT"), new Transition(22, "NULL", 11, "SHIFT"), new Transition(22, "NUM", 1, "SHIFT"), new Transition(22, "STAR", 14, "SHIFT"), new Transition(22, "term", 78, "SHIFT"), new Transition(23, "EQ", 17, "SHIFT"), new Transition(23, "GE", 16, "SHIFT"), new Transition(23, "GT", 7, "SHIFT"), new Transition(23, "LE", 24, "SHIFT"), new Transition(23, "LT", 21, "SHIFT"), new Transition(23, "MINUS", 25, "SHIFT"), new Transition(23, "NE", 37, "SHIFT"), new Transition(23, "PLUS", 22, "SHIFT"), new Transition(24, "AMP", 26, "SHIFT"), new Transition(24, "expr", 93, "SHIFT"), new Transition(24, "factor", 15, "SHIFT"), new Transition(24, "ID", 12, "SHIFT"), new Transition(24, "LPAREN", 13, "SHIFT"), new Transition(24, "NEW", 3, "SHIFT"), new Transition(24, "NULL", 11, "SHIFT"), new Transition(24, "NUM", 1, "SHIFT"), new Transition(24, "STAR", 14, "SHIFT"), new Transition(24, "term", 19, "SHIFT"), new Transition(25, "AMP", 26, "SHIFT"), new Transition(25, "factor", 15, "SHIFT"), new Transition(25, "ID", 12, "SHIFT"), new Transition(25, "LPAREN", 13, "SHIFT"), new Transition(25, "NEW", 3, "SHIFT"), new Transition(25, "NULL", 11, "SHIFT"), new Transition(25, "NUM", 1, "SHIFT"), new Transition(25, "STAR", 14, "SHIFT"), new Transition(25, "term", 79, "SHIFT"), new Transition(26, "ID", 53, "SHIFT"), new Transition(26, "LPAREN", 30, "SHIFT"), new Transition(26, "lvalue", 90, "SHIFT"), new Transition(26, "STAR", 2, "SHIFT"), new Transition(27, "AMP", 26, "SHIFT"), new Transition(27, "expr", 55, "SHIFT"), new Transition(27, "factor", 15, "SHIFT"), new Transition(27, "ID", 12, "SHIFT"), new Transition(27, "LPAREN", 13, "SHIFT"), new Transition(27, "NEW", 3, "SHIFT"), new Transition(27, "NULL", 11, "SHIFT"), new Transition(27, "NUM", 1, "SHIFT"), new Transition(27, "STAR", 14, "SHIFT"), new Transition(27, "term", 19, "SHIFT"), new Transition(28, "RBRACE", 31, "SHIFT"), new Transition(3, "INT", 33, "SHIFT"), new Transition(30, "ID", 53, "SHIFT"), new Transition(30, "LPAREN", 30, "SHIFT"), new Transition(30, "lvalue", 69, "SHIFT"), new Transition(30, "STAR", 2, "SHIFT"), new Transition(32, "AMP", 26, "SHIFT"), new Transition(32, "factor", 42, "SHIFT"), new Transition(32, "ID", 12, "SHIFT"), new Transition(32, "LPAREN", 13, "SHIFT"), new Transition(32, "NEW", 3, "SHIFT"), new Transition(32, "NULL", 11, "SHIFT"), new Transition(32, "NUM", 1, "SHIFT"), new Transition(32, "STAR", 14, "SHIFT"), new Transition(33, "LBRACK", 83, "SHIFT"), new Transition(34, "SEMI", 4, "SHIFT"), new Transition(35, "LBRACE", 103, "SHIFT"), new Transition(37, "AMP", 26, "SHIFT"), new Transition(37, "expr", 95, "SHIFT"), new Transition(37, "factor", 15, "SHIFT"), new Transition(37, "ID", 12, "SHIFT"), new Transition(37, "LPAREN", 13, "SHIFT"), new Transition(37, "NEW", 3, "SHIFT"), new Transition(37, "NULL", 11, "SHIFT"), new Transition(37, "NUM", 1, "SHIFT"), new Transition(37, "STAR", 14, "SHIFT"), new Transition(37, "term", 19, "SHIFT"), new Transition(38, "LPAREN", 48, "SHIFT"), new Transition(39, "STAR", 29, "SHIFT"), new Transition(43, "NULL", 34, "SHIFT"), new Transition(43, "NUM", 51, "SHIFT"), new Transition(44, "WAIN", 75, "SHIFT"), new Transition(46, "AMP", 26, "SHIFT"), new Transition(46, "factor", 40, "SHIFT"), new Transition(46, "ID", 12, "SHIFT"), new Transition(46, "LPAREN", 13, "SHIFT"), new Transition(46, "NEW", 3, "SHIFT"), new Transition(46, "NULL", 11, "SHIFT"), new Transition(46, "NUM", 1, "SHIFT"), new Transition(46, "STAR", 14, "SHIFT"), new Transition(47, "AMP", 26, "SHIFT"), new Transition(47, "expr", 23, "SHIFT"), new Transition(47, "factor", 15, "SHIFT"), new Transition(47, "ID", 12, "SHIFT"), new Transition(47, "LPAREN", 13, "SHIFT"), new Transition(47, "NEW", 3, "SHIFT"), new Transition(47, "NULL", 11, "SHIFT"), new Transition(47, "NUM", 1, "SHIFT"), new Transition(47, "STAR", 14, "SHIFT"), new Transition(47, "term", 19, "SHIFT"), new Transition(47, "test", 76, "SHIFT"), new Transition(48, "AMP", 26, "SHIFT"), new Transition(48, "expr", 23, "SHIFT"), new Transition(48, "factor", 15, "SHIFT"), new Transition(48, "ID", 12, "SHIFT"), new Transition(48, "LPAREN", 13, "SHIFT"), new Transition(48, "NEW", 3, "SHIFT"), new Transition(48, "NULL", 11, "SHIFT"), new Transition(48, "NUM", 1, "SHIFT"), new Transition(48, "STAR", 14, "SHIFT"), new Transition(48, "term", 19, "SHIFT"), new Transition(48, "test", 77, "SHIFT"), new Transition(49, "AMP", 26, "SHIFT"), new Transition(49, "expr", 56, "SHIFT"), new Transition(49, "factor", 15, "SHIFT"), new Transition(49, "ID", 12, "SHIFT"), new Transition(49, "LPAREN", 13, "SHIFT"), new Transition(49, "NEW", 3, "SHIFT"), new Transition(49, "NULL", 11, "SHIFT"), new Transition(49, "NUM", 1, "SHIFT"), new Transition(49, "STAR", 14, "SHIFT"), new Transition(49, "term", 19, "SHIFT"), new Transition(50, "EOF", 102, "SHIFT"), new Transition(51, "SEMI", 5, "SHIFT"), new Transition(52, "LPAREN", 47, "SHIFT"), new Transition(54, "AMP", 26, "SHIFT"), new Transition(54, "factor", 41, "SHIFT"), new Transition(54, "ID", 12, "SHIFT"), new Transition(54, "LPAREN", 13, "SHIFT"), new Transition(54, "NEW", 3, "SHIFT"), new Transition(54, "NULL", 11, "SHIFT"), new Transition(54, "NUM", 1, "SHIFT"), new Transition(54, "STAR", 14, "SHIFT"), new Transition(55, "MINUS", 25, "SHIFT"), new Transition(55, "PLUS", 22, "SHIFT"), new Transition(55, "SEMI", 106, "SHIFT"), new Transition(56, "MINUS", 25, "SHIFT"), new Transition(56, "PLUS", 22, "SHIFT"), new Transition(56, "RPAREN", 20, "SHIFT"), new Transition(57, "MINUS", 25, "SHIFT"), new Transition(57, "PLUS", 22, "SHIFT"), new Transition(57, "SEMI", 105, "SHIFT"), new Transition(58, "BECOMES", 43, "SHIFT"), new Transition(60, "RPAREN", 65, "SHIFT"), new Transition(61, "LBRACK", 104, "SHIFT"), new Transition(62, "COMMA", 98, "SHIFT"), new Transition(63, "ID", 81, "SHIFT"), new Transition(64, "MINUS", 25, "SHIFT"), new Transition(64, "PLUS", 22, "SHIFT"), new Transition(64, "SEMI", 28, "SHIFT"), new Transition(65, "LBRACE", 85, "SHIFT"), new Transition(67, "dcl", 58, "SHIFT"), new Transition(67, "INT", 39, "SHIFT"), new Transition(67, "statements", 97, "SHIFT"), new Transition(67, "type", 63, "SHIFT"), new Transition(68, "BECOMES", 18, "SHIFT"), new Transition(69, "RPAREN", 45, "SHIFT"), new Transition(7, "AMP", 26, "SHIFT"), new Transition(7, "expr", 91, "SHIFT"), new Transition(7, "factor", 15, "SHIFT"), new Transition(7, "ID", 12, "SHIFT"), new Transition(7, "LPAREN", 13, "SHIFT"), new Transition(7, "NEW", 3, "SHIFT"), new Transition(7, "NULL", 11, "SHIFT"), new Transition(7, "NUM", 1, "SHIFT"), new Transition(7, "STAR", 14, "SHIFT"), new Transition(7, "term", 19, "SHIFT"), new Transition(70, "dcl", 62, "SHIFT"), new Transition(70, "INT", 39, "SHIFT"), new Transition(70, "type", 63, "SHIFT"), new Transition(71, "DELETE", 61, "SHIFT"), new Transition(71, "ID", 53, "SHIFT"), new Transition(71, "IF", 52, "SHIFT"), new Transition(71, "LPAREN", 30, "SHIFT"), new Transition(71, "lvalue", 68, "SHIFT"), new Transition(71, "PRINTLN", 82, "SHIFT"), new Transition(71, "RBRACE", 8, "SHIFT"), new Transition(71, "STAR", 2, "SHIFT"), new Transition(71, "statement", 66, "SHIFT"), new Transition(71, "WHILE", 38, "SHIFT"), new Transition(72, "DELETE", 61, "SHIFT"), new Transition(72, "ID", 53, "SHIFT"), new Transition(72, "IF", 52, "SHIFT"), new Transition(72, "LPAREN", 30, "SHIFT"), new Transition(72, "lvalue", 68, "SHIFT"), new Transition(72, "PRINTLN", 82, "SHIFT"), new Transition(72, "RBRACE", 9, "SHIFT"), new Transition(72, "STAR", 2, "SHIFT"), new Transition(72, "statement", 66, "SHIFT"), new Transition(72, "WHILE", 38, "SHIFT"), new Transition(73, "DELETE", 61, "SHIFT"), new Transition(73, "ID", 53, "SHIFT"), new Transition(73, "IF", 52, "SHIFT"), new Transition(73, "LPAREN", 30, "SHIFT"), new Transition(73, "lvalue", 68, "SHIFT"), new Transition(73, "PRINTLN", 82, "SHIFT"), new Transition(73, "RBRACE", 10, "SHIFT"), new Transition(73, "STAR", 2, "SHIFT"), new Transition(73, "statement", 66, "SHIFT"), new Transition(73, "WHILE", 38, "SHIFT"), new Transition(75, "LPAREN", 70, "SHIFT"), new Transition(76, "RPAREN", 86, "SHIFT"), new Transition(77, "RPAREN", 87, "SHIFT"), new Transition(78, "PCT", 32, "SHIFT"), new Transition(78, "SLASH", 54, "SHIFT"), new Transition(78, "STAR", 46, "SHIFT"), new Transition(79, "PCT", 32, "SHIFT"), new Transition(79, "SLASH", 54, "SHIFT"), new Transition(79, "STAR", 46, "SHIFT"), new Transition(8, "ELSE", 35, "SHIFT"), new Transition(82, "LPAREN", 49, "SHIFT"), new Transition(83, "AMP", 26, "SHIFT"), new Transition(83, "expr", 88, "SHIFT"), new Transition(83, "factor", 15, "SHIFT"), new Transition(83, "ID", 12, "SHIFT"), new Transition(83, "LPAREN", 13, "SHIFT"), new Transition(83, "NEW", 3, "SHIFT"), new Transition(83, "NULL", 11, "SHIFT"), new Transition(83, "NUM", 1, "SHIFT"), new Transition(83, "STAR", 14, "SHIFT"), new Transition(83, "term", 19, "SHIFT"), new Transition(84, "AMP", 26, "SHIFT"), new Transition(84, "expr", 64, "SHIFT"), new Transition(84, "factor", 15, "SHIFT"), new Transition(84, "ID", 12, "SHIFT"), new Transition(84, "LPAREN", 13, "SHIFT"), new Transition(84, "NEW", 3, "SHIFT"), new Transition(84, "NULL", 11, "SHIFT"), new Transition(84, "NUM", 1, "SHIFT"), new Transition(84, "STAR", 14, "SHIFT"), new Transition(84, "term", 19, "SHIFT"), new Transition(85, "dcls", 67, "SHIFT"), new Transition(86, "LBRACE", 101, "SHIFT"), new Transition(87, "LBRACE", 100, "SHIFT"), new Transition(88, "MINUS", 25, "SHIFT"), new Transition(88, "PLUS", 22, "SHIFT"), new Transition(88, "RBRACK", 80, "SHIFT"), new Transition(89, "MINUS", 25, "SHIFT"), new Transition(89, "PLUS", 22, "SHIFT"), new Transition(89, "RPAREN", 36, "SHIFT"), new Transition(91, "MINUS", 25, "SHIFT"), new Transition(91, "PLUS", 22, "SHIFT"), new Transition(92, "MINUS", 25, "SHIFT"), new Transition(92, "PLUS", 22, "SHIFT"), new Transition(93, "MINUS", 25, "SHIFT"), new Transition(93, "PLUS", 22, "SHIFT"), new Transition(94, "MINUS", 25, "SHIFT"), new Transition(94, "PLUS", 22, "SHIFT"), new Transition(95, "MINUS", 25, "SHIFT"), new Transition(95, "PLUS", 22, "SHIFT"), new Transition(96, "MINUS", 25, "SHIFT"), new Transition(96, "PLUS", 22, "SHIFT"), new Transition(97, "DELETE", 61, "SHIFT"), new Transition(97, "ID", 53, "SHIFT"), new Transition(97, "IF", 52, "SHIFT"), new Transition(97, "LPAREN", 30, "SHIFT"), new Transition(97, "lvalue", 68, "SHIFT"), new Transition(97, "PRINTLN", 82, "SHIFT"), new Transition(97, "RETURN", 84, "SHIFT"), new Transition(97, "STAR", 2, "SHIFT"), new Transition(97, "statement", 66, "SHIFT"), new Transition(97, "WHILE", 38, "SHIFT"), new Transition(98, "dcl", 60, "SHIFT"), new Transition(98, "INT", 39, "SHIFT"), new Transition(98, "type", 63, "SHIFT"), new Transition(99, "INT", 44, "SHIFT"), new Transition(99, "procedure", 50, "SHIFT") };
	}

	private void getTokens() {
		Scanner in = new Scanner(System.in);

		input.add(new Token("BOF","BOF"));
		while(in.hasNextLine()) {
			String[] inCurr = in.nextLine().split(" ");
			input.add(new Token(inCurr[0], inCurr[1]));
		}
		input.add(new Token("EOF","EOF"));
	}

	private void shift(Token newSymbol, int newState) {
		symbol.push(newSymbol);
		state.push(newState);
	}

	private void reduce(Transition t, int index) {
		String[] arr = rules[t.to].split(" ");
		for(int i = 0; i < arr.length - 1; i++) {
			symbol.pop();
			state.pop();
		}
		symbol.push(new Token(arr[0], "non-terminal"));
		Transition next = findTransition(symbol.peek());
		if(next == null) {
			System.out.println("ERROR: Syntax error at token " + index);
			System.exit(0);
		} else {
			output.add(rules[t.to]);
		}
		state.push(next.to);
	}

	private Tree lrdo() {
		Stack<Tree> stack = new Stack<Tree>();
		String l;
		int index = 0;
		do {
			String f = output.get(index);
			List<String> r = new ArrayList<String>();

			String[] tokens = f.split(" ");
			l = tokens[0];
			for(int i = 1; i < tokens.length; ) {
				String checkNon = tokens[i++];
				if(nonterminals.contains(checkNon))
					r.add(checkNon);
			}
			popper(stack, r, f);
			index++;
		} while(!start.equals(l));
		return stack.peek();
	}

	private void popper(Stack<Tree> stack, List<String> rhs, String rule) {
		Tree n = new Tree();
		n.rule = rule;
		for(String s : rhs) {
			n.children.addFirst(stack.pop());
		}
		stack.push(n);
	}

	private void traverse(Tree t) {
		System.out.println(t.rule);
		String[] ruleArr = t.rule.split(" ");
		for(int i = 1; i < ruleArr.length; i++) {
			if(terminals.contains(ruleArr[i])) {
				System.out.println(ruleArr[i] + " " + input.get(0).lexeme);
				input.remove(0);
			} else {
				traverse(t.children.getFirst());
				t.children.remove();
			}
		}
	}

	private Transition findTransition(Token tLookahead) {
		String lookahead = tLookahead.kind;
		for(int i = 0; i < transitions.length; i++) {
			Transition t = transitions[i];
			if(t.from == state.peek() && t.lookahead.equals(lookahead))
				return t;
		}
		return null;
	}
}

class Token {
	public String kind;
	public String lexeme;

	Token(String kind, String lexeme) {
		this.kind = kind;
		this.lexeme = lexeme;
	}
}

class Transition {
	public int from;
	public int to;
	public String lookahead;
	public String type;

	Transition(int from, String lookahead, int to, String type) {
		this.from = from;
		this.lookahead = lookahead;
		this.to = to;
		this.type = type;
	}
}

class Tree {
	public String rule;
	public LinkedList<Tree> children = new LinkedList<Tree>();
}