import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


// Program Flow
public class WLPPGen {
	public static final void main(String args[]) {
		new WLPPGen().run();
	}

	public void run() {
		ParseTreeMaker maker = new ParseTreeMaker();
		ParseTree pt = maker.getParseTree();
		new Verifier(maker.getSymbols()).verify(pt);
		pt = new PTOptimizer(pt).getOptimizedPT();
		pt.print(0);
		new PTGenerator(maker.getSymbols()).genCode(maker.getParseTree()).print();
	}
}

// PT Def
class ParseTree {
	public String rule;
	public List<ParseTree> children = new ArrayList<ParseTree>();

	public void print(int offset) {
		for(int i = 0; i < offset; i++)
			System.err.print(" ");
		System.err.println(rule);
		for(ParseTree subT : children) {
			subT.print(offset+1);
		}
	}
}

// AST Def
class AST {

}

// Convert Input to PT
class ParseTreeMaker {
	public ParseTreeMaker() {
		parseTree = createTree("S");
	}

	public ParseTree getParseTree() {
		return parseTree.children.get(1);
	}

	public Map<String, String> getSymbols() {
		return symbols;
	}

	private Scanner in = new Scanner(System.in);

	private Set<String> terminals = new HashSet<String>(Arrays.asList("BOF", "BECOMES", "COMMA", "ELSE", "EOF", "EQ", "GE", "GT", "ID", "IF", "INT", "LBRACE", "LE", "LPAREN", "LT", "MINUS", "NE", "NUM", "PCT", "PLUS", "PRINTLN","RBRACE", "RETURN", "RPAREN", "SEMI", "SLASH", "STAR", "WAIN", "WHILE","AMP", "LBRACK", "RBRACK", "NEW", "DELETE", "NULL"));

	private ParseTree parseTree = new ParseTree();

	private Map<String, String> symbols = new LinkedHashMap<String, String>();
	private boolean beginSymbol = false;
	private String tempType;

	private ParseTree createTree(String lhs) {
		ParseTree ret = new ParseTree();
		ret.rule = in.nextLine();

		createSymbol(ret.rule);

		if(!terminals.contains(lhs)) {
			String[] arr = ret.rule.split(" ");
			for(int i = 1; i < arr.length; i++) {
				ret.children.add(createTree(arr[i]));
			}
		}
		return ret;
	}

	private void createSymbol(String rule) {
		if(rule.startsWith("dcl ")) {
			beginSymbol = true;
		} else if(beginSymbol) {
			if(rule.startsWith("type")) {
				tempType = rule.endsWith("STAR") ? "int*" : "int";
			} else if(rule.startsWith("ID")) {
				String tempName = rule.split(" ")[1];
				if(symbols.containsKey(tempName)) {
					error("variable " + tempName + " was declared multiple times.");
				}
				symbols.put(tempName, tempType);
				beginSymbol = false;
			}
		}
	}

	private void error(String err) {
		System.err.println("ERROR: " + err);
		System.exit(1);
	}
}

// Verify PT Validity
class Verifier {
	Map<String, String> symbols = new HashMap<String, String>();

	public Verifier(Map<String, String> symb) {
		symbols = symb;
	}

	public void verify(ParseTree parseTree) {
		checkValidity(parseTree);
	}

	private String checkValidity(ParseTree t) {
		if(t.rule.startsWith("ID")) {
			return getSymbolType(t);
		} else if(t.rule.startsWith("procedure")) {
			String lparam = checkValidity(t.children.get(3));
			String rparam = checkValidity(t.children.get(5));
			if(rparam.equals("int*")) {
				error("second parameter of wain() must be an integer.");
			}
			String dcls = checkValidity(t.children.get(8));
			String statements = checkValidity(t.children.get(9));
			String ret = checkValidity(t.children.get(11));
			if(ret.equals("int*")) {
				error("int wain() must return an integer value.");
			}
		} else if(t.rule.startsWith("dcls ")) {
			String dcls = checkValidity(t.children.get(0));
			if(t.rule.contains("NUM")) {
				String dcl = checkValidity(t.children.get(1));
				if(dcl.equals("int*")) {
					error("cannot assign an integer value to a pointer.");
				}
			} else {
				String dcl = checkValidity(t.children.get(1));
				if(dcl.equals("int")) {
					error("cannot assign NULL to an integer.");
				}
			}
		} else if(t.rule.startsWith("type INT")) {
			return t.rule.endsWith("STAR") ? "int*" : "int";
		} else if(t.rule.startsWith("statements ")) {
			String states = checkValidity(t.children.get(0));
			String state = checkValidity(t.children.get(1));
			return state;
		} else if(t.rule.equals("statement lvalue BECOMES expr SEMI")) {
			String lvalue = checkValidity(t.children.get(0));
			String expr = checkValidity(t.children.get(2));
			if(!lvalue.equals(expr)) {
				error("attempted to assign value with incorrect type.");
			}
		} else if(t.rule.equals("statement PRINTLN LPAREN expr RPAREN SEMI")) {
			String expr = checkValidity(t.children.get(2));
			if(expr.equals("int*")) {
				error("println only accepts integer parameters.");
			}
		} else if(t.rule.equals("statement DELETE LBRACK RBRACK expr SEMI")) {
			String expr = checkValidity(t.children.get(3));
			if(expr.equals("int")) {
				error("attempted to delete an integer.");
			}
		} else if(t.rule.equals("factor NEW INT LBRACK expr RBRACK")) {
			String expr = checkValidity(t.children.get(3));
			if(expr.equals("int*")) {
				error("cannot create integer array with a pointer size.");
			}
			return "int*";
		} else if(t.rule.equals("statement IF LPAREN test RPAREN LBRACE statements RBRACE ELSE LBRACE statements RBRACE")) {
			String test = checkValidity(t.children.get(2));
			String lstate = checkValidity(t.children.get(5));
			String rstate = checkValidity(t.children.get(9));
		} else if(t.rule.equals("statement WHILE LPAREN test RPAREN LBRACE statements RBRACE")) {
			String test = checkValidity(t.children.get(2));
			String lstate = checkValidity(t.children.get(5));
		} else if(t.rule.startsWith("test")) {
			String lhs = checkValidity(t.children.get(0));
			String rhs = checkValidity(t.children.get(2));
			if(!lhs.equals(rhs)) {
				error("cannot compare non-similar types.");
			}
		} else if(t.rule.equals("expr expr PLUS term")) {
			String lhs = checkValidity(t.children.get(0));
			String rhs = checkValidity(t.children.get(2));
			if(lhs.equals("int*") && rhs.equals("int*")) {
				error("attempted to perform pointer addition.");
			} else if(lhs.equals(rhs)) {
				return "int";
			} else {
				return "int*";
			}
		} else if(t.rule.equals("expr expr MINUS term")) {
			String lhs = checkValidity(t.children.get(0));
			String rhs = checkValidity(t.children.get(2));
			if(lhs.equals("int") && rhs.equals("int*")) {
				error("attempted to subtract a pointer from an integer.");
			} else if(lhs.equals(rhs)) {
				return "int";
			} else {
				return "int*";
			}
		} else if(t.rule.equals("term term STAR factor")) {
			String lhs = checkValidity(t.children.get(0));
			String rhs = checkValidity(t.children.get(2));
			if(lhs.equals("int*") || rhs.equals("int*")) {
				error("attempted to multiply a pointer.");
			} else {
				return "int";
			}
		} else if(t.rule.equals("term term SLASH factor")) {
			String lhs = checkValidity(t.children.get(0));
			String rhs = checkValidity(t.children.get(2));
			if(lhs.equals("int*") || rhs.equals("int*")) {
				error("attempted to divide a pointer.");
			} else {
				return "int";
			}
		} else if(t.rule.equals("term term PCT factor")) {
			String lhs = checkValidity(t.children.get(0));
			String rhs = checkValidity(t.children.get(2));
			if(lhs.equals("int*") || rhs.equals("int*")) {
				error("attempted to modulus a pointer.");
			} else {
				return "int";
			}
		} else if(t.rule.equals("factor NUM")) {
			return "int";
		} else if(t.rule.equals("factor NULL")) {
			return "int*";
		} else if(t.rule.equals("factor AMP lvalue")) {
			String lvalue = checkValidity(t.children.get(1));
			if(lvalue.equals("int*")) {
				error("attempted to reference a pointer (i.e. &pointer).");
			}
			return "int*";
		} else if(t.rule.endsWith("STAR factor")) {
			String factor = checkValidity(t.children.get(1));
			if(factor.equals("int")) {
				error("attempted to dereference an integer (i.e. *integer).");
			}
			return "int";
		} else if(t.rule.equals("factor LPAREN expr RPAREN") || t.rule.equals("lvalue LPAREN lvalue RPAREN")) {
			return checkValidity(t.children.get(1));
		} else if(t.children.size() == 1 || t.children.size() == 2) {
			return checkValidity(t.children.get(0));
		}

		return "VALID";
	}

	private String getSymbolType(ParseTree t) {
		String name = t.rule.split(" ")[1];
		if(!symbols.containsKey(name)) {
			error("variable " + name + " was used but not declared.");
		}
		return symbols.get(name);
	}

	private void error(String err) {
		System.err.println("ERROR: " + err);
		System.exit(1);
	}
}

// Convert PT to AST
class Converter {

}

// Optimize AST
class ASTOptimizer {

}

// Generate Code from AST
class ASTGenerator {

}

// Optimize PT
class PTOptimizer {
	private ParseTree parseTree = new ParseTree();

	public PTOptimizer(ParseTree t) {
		parseTree = t;

		optimize();
	}

	public ParseTree getOptimizedPT() {
		return parseTree;
	}

	private void optimize() {
		initVars(parseTree.children.get(3));
		initVars(parseTree.children.get(5));
		initVars(parseTree.children.get(8));

		propogate(parseTree.children.get(9));
	}

	private Map<String, String> symbolVals = new HashMap<String, String>();

	private void initVars(ParseTree t) {
		if(t.rule.startsWith("dcls ")) {
			initVars(t.children.get(0));
		} else if(t.rule.equals("dcls")) {
			return;
		}

		String name;
		String value;

		if(t.rule.endsWith("NUM SEMI")) {
			name = t.children.get(1).children.get(1).rule.split(" ")[1];
			value = t.children.get(3).rule.split(" ")[1];
		} else if(t.rule.endsWith("NULL SEMI")) {
			name = t.children.get(1).children.get(1).rule.split(" ")[1];
			value = "NULL";
		} else {
			name = t.children.get(1).rule.split(" ")[1];
			value = "UNKNOWN";
		}

		symbolVals.put(name, value);
	}

	private void propogate(ParseTree t) {
		if(t.rule.startsWith("statements ")) {
			propogate(t.children.get(0));
			propogate(t.children.get(1));
		} else if(t.rule.equals("statements")) {
			return;
		}

		if(t.rule.startsWith("statement lvalue")) {
			String lhs = getLVal(t.children.get(0));
			if(lhs.equals("UNSUPPORTED")) {
				return;
			}
			try {
				String rhs = getRVal(t.children.get(2));
				if(rhs.equals("NULL")) {
					fold(t.children.get(2), "NULL");
					symbolVals.put(lhs, "NULL");
				} else {
					int irhs = Integer.parseInt(rhs);
					fold(t.children.get(2), ""+irhs);
					symbolVals.put(lhs, ""+irhs);
				}
			} catch(Exception e) {
				symbolVals.put(lhs, "UNKNOWN");
				return;
			}
		}

		System.err.println(t.rule);
	}

	private Set<String> mathRules = new HashSet<String>(Arrays.asList("expr expr PLUS term", "expr expr MINUS term", "term term STAR factor", "term term SLASH factor", "term term PCT factor"));

	private String getLVal(ParseTree t) {
		if(t.rule.equals("lvalue ID")) {
			return t.children.get(0).rule.split(" ")[1];
		} else if(t.rule.equals("lvalue LPAREN lvalue RPAREN")) {
			return getLVal(t.children.get(1));
		} else {
			return "UNSUPPORTED";
		}
	}

	private String getRVal(ParseTree t) {
		// TODO: bandaid solution for below
		if(t.rule.equals("factor AMP lvalue") && t.children.get(1).rule.equals("lvalue STAR factor")) {
			return getRVal(t.children.get(1).children.get(1));
		}

		if(t.rule.equals("factor ID") || t.rule.equals("lvalue ID")) {
			return symbolVals.get(t.children.get(0).rule.split(" ")[1]);
		} else if(t.rule.equals("factor NUM")) {
			return t.children.get(0).rule.split(" ")[1];
		} else if(t.rule.equals("factor NULL")) {
			return "NULL";
		} else if(t.rule.equals("expr term") || t.rule.equals("term factor")) {
			return getRVal(t.children.get(0));
		} else if(t.rule.equals("factor LPAREN expr RPAREN") || t.rule.equals("lvalue LPAREN lvalue RPAREN")) {
			return getRVal(t.children.get(1));
		} else if(t.rule.equals("factor AMP lvalue")) {
			// TODO: how the fuck do I do this? value -> key? but multi
		} else if(t.rule.equals("factor STAR factor") || t.rule.equals("lvalue STAR factor")) {
			return symbolVals.get(getRVal(t.children.get(1)));
		} else if(t.rule.equals("factor NEW INT LBRACK expr RBRACK")) {
			return "UNKNOWN";
		}

		// MATH

		System.err.println("getRVal ERROR: No rule for " + t.rule);
		return "FAIL";
	}

	private void fold(ParseTree t, String val) {
		ParseTree term = new ParseTree();
		term.rule = "term factor";
		ParseTree factor = new ParseTree();
		ParseTree deepest = new ParseTree();
		t.rule = "expr term";
		t.children.clear();
		t.children.add(term);
		term.children.add(factor);

		if(val.equals("NULL")) {
			factor.rule = "factor NULL";
			deepest.rule = "NULL null";
		} else {
			factor.rule = "factor NUM";
			deepest.rule = "NUM " + val;
		}

		factor.children.add(deepest);
	}
}

// Generate Code from PT
class PTGenerator {
	Map<String, String> symbols = new HashMap<String, String>();

	public PTGenerator(Map<String, String> symb) {
		symbols = symb;
	}

	private List<String> mips = new ArrayList<String>();

	public PTGenerator genCode(ParseTree t) {
		String arg0 = t.children.get(3).children.get(1).rule.split(" ")[1];
		mips.add("lis $3");
		mips.add(".word " + arg0);
		sw(1,3,0);

		String arg1 = t.children.get(5).children.get(1).rule.split(" ")[1];
		mips.add("lis $3");
		mips.add(".word " + arg1);
		sw(2,3,0);

		generateDcls(t.children.get(8));
		generateStatements(t.children.get(9));
		generateExpression(t.children.get(11));

		mips.add("jr $31");

		generateSymbols();
		generateImports(t);

		return this;
	}

	private void generateDcls(ParseTree t) {
		if(t.children.size() == 0)
			return;

		if(t.rule.endsWith("NUM SEMI")) {
			generateDcls(t.children.get(0));

			mips.add("lis $1");
			mips.add(".word " + t.children.get(3).rule.split(" ")[1]);

			mips.add("lis $3");
			mips.add(".word " + t.children.get(1).children.get(1).rule.split(" ")[1]);

			sw(1,3,0);
		} else if(t.rule.endsWith("NULL SEMI")) {
			generateDcls(t.children.get(0));

			mips.add("lis $3");
			mips.add(".word " + t.children.get(1).children.get(1).rule.split(" ")[1]);

			sw(0,3,0);
		}
	}

	private void generateStatements(ParseTree t) {
		if(t.children.size() == 0)
			return;

		if(t.rule.startsWith("statements ")) {
			generateStatements(t.children.get(0));
			generateStatements(t.children.get(1));
		} else if(t.rule.startsWith("statement lvalue")) {
			generateExpression(t.children.get(2));
			mips.add("add $8, $0, $3");
			generateAddress(t.children.get(0));
			sw(8,3,0);
		} else if(t.rule.startsWith("statement PRINTLN")) {
			generateExpression(t.children.get(2));
			mips.add("add $1, $0, $3");
			call("print");
		} else if(t.rule.startsWith("statement DELETE")) {
			generateExpression(t.children.get(3));
			mips.add("add $1, $0, $3");
			call("delete");
		} else if(t.rule.startsWith("statement WHILE")) {
			String startLabel = generateLabel("whileStart");
			String endLabel = generateLabel("whileEnd");

			mips.add(startLabel + ":");
			generateTest(t.children.get(2), endLabel);
			generateStatements(t.children.get(5));
			mips.add("beq $0, $0, " + startLabel);
			mips.add(endLabel + ":");
		} else if(t.rule.startsWith("statement IF")) {
			String elseLabel = generateLabel("ifElse");
			String endLabel = generateLabel("ifEnd");

			generateTest(t.children.get(2), elseLabel);
			generateStatements(t.children.get(5));
			mips.add("beq $0, $0, " + endLabel);
			mips.add(elseLabel + ":");
			generateStatements(t.children.get(9));
			mips.add(endLabel + ":");
		}
	}

	private void generateTest(ParseTree t, String skipLabel) {
		generateExpression(t.children.get(0));
		mips.add("add $9, $0, $3");
		generateExpression(t.children.get(2));

		if (t.rule.endsWith("expr EQ expr")) {
			mips.add("bne $9, $3, " + skipLabel);
		} else if (t.rule.endsWith("expr NE expr")) {
			mips.add("beq $9, $3, " + skipLabel);
		} else if(t.rule.endsWith("expr LT expr")) {
			mips.add("slt $1, $9, $3");
			mips.add("beq $1, $0, " + skipLabel);
		} else if (t.rule.endsWith("expr LE expr")) {
			mips.add("slt $1, $3, $9");
			mips.add("bne $1, $0, " + skipLabel);
		} else if (t.rule.endsWith("expr GT expr")) {
			mips.add("slt $1, $3, $9");
			mips.add("beq $1, $0, " + skipLabel);
		} else if (t.rule.endsWith("expr GE expr")) {
			mips.add("slt $1, $9, $3");
			mips.add("bne $1, $0, " + skipLabel);
		}
	}

	private void generateExpression(ParseTree t) {
		if(t.rule.endsWith("expr PLUS term")) {
			generateExpression(t.children.get(0));

			if(getType(t.children.get(2)).equals("int*")) {
				mips.add("lis $4");
				mips.add(".word 4");
				mips.add("mult $3, $4");
				mips.add("mflo $3");
			}

			push(3);
			generateTerm(t.children.get(2));
			pop(1);

			if(getType(t.children.get(0)).equals("int*")) {
				mips.add("lis $4");
				mips.add(".word 4");
				mips.add("mult $3, $4");
				mips.add("mflo $3");
			}

			mips.add("add $3, $1, $3");
		} else if(t.rule.endsWith("expr MINUS term")) {
			generateExpression(t.children.get(0));
			
			push(3);
			generateTerm(t.children.get(2));
			pop(1);

			if(getType(t.children.get(0)).equals("int*") && getType(t.children.get(2)).equals("int")) {
				mips.add("lis $4");
				mips.add(".word 4");
				mips.add("mult $3, $4");
				mips.add("mflo $3");
			}

			mips.add("sub $3, $1, $3");

			if(getType(t.children.get(0)).equals("int*") && getType(t.children.get(2)).equals("int*")) {
				mips.add("lis $4");
				mips.add(".word 4");
				mips.add("div $3, $4");
				mips.add("mflo $3");
			}
		} else {
			// expr -> term
			generateTerm(t.children.get(0));
		}
	}

	private void generateTerm(ParseTree t) {
		if(t.rule.startsWith("term term")) {
			generateTerm(t.children.get(0));
			push(3);
			generateFactor(t.children.get(2));
			pop(1);
		}

		if(t.rule.endsWith("term STAR factor")) {
			mips.add("mult $1, $3");
			mips.add("mflo $3");
		} else if(t.rule.endsWith("term SLASH factor")) {
			mips.add("div $1, $3");
			mips.add("mflo $3");
		} else if(t.rule.endsWith("term PCT factor")) {
			mips.add("div $1, $3");
			mips.add("mfhi $3");
		} else {
			// term -> factor
			generateFactor(t.children.get(0));
		}
	}

	private void generateFactor(ParseTree t) {
		if(t.rule.startsWith("factor NEW INT")) {
			generateExpression(t.children.get(3));
			mips.add("add $1, $0, $3");
			call("new");
		} else if(t.rule.equals("factor STAR factor")) {
			generateFactor(t.children.get(1));
			lw(3,3,0);
		} else if(t.rule.equals("factor AMP lvalue")) {
			generateAddress(t.children.get(1));
		} else if(t.rule.endsWith("NULL")) {
			mips.add("add $3, $0, $0");
		} else if(t.rule.endsWith("NUM")) {
			mips.add("lis $3");
			mips.add(".word " + t.children.get(0).rule.split(" ")[1]);
		} else if(t.rule.endsWith("LPAREN expr RPAREN")) {
			generateExpression(t.children.get(1));
		} else {
			// factor -> ID;;;
			mips.add("lis $1");
			mips.add(".word " + t.children.get(0).rule.split(" ")[1]);
			lw(3,1,0);
		}
	}

	private void generateAddress(ParseTree t) {
		if(t.rule.endsWith("LPAREN lvalue RPAREN")) {
			generateAddress(t.children.get(1));
		} else if(t.rule.endsWith("STAR factor")) {
			generateFactor(t.children.get(1));
		} else {
			// lvalue -> ID
			mips.add("lis $3");
			mips.add(".word " + t.children.get(0).rule.split(" ")[1]);
		}
	}

	private String getType(ParseTree t) {
		if(t.rule.startsWith("ID")) {
			return symbols.get(t.rule.split(" ")[1]);
		} else if(t.rule.startsWith("type INT")) {
			return t.rule.endsWith("STAR") ? "int*" : "int";
		} else if(t.rule.equals("expr expr PLUS term") || t.rule.equals("expr expr MINUS term")) {
			String lhs = getType(t.children.get(0));
			String rhs = getType(t.children.get(2));
			if(lhs.equals(rhs)) {
				return "int";
			} else {
				return "int*";
			}
		} else if(t.rule.equals("factor NEW INT LBRACK expr RBRACK") || t.rule.equals("factor NULL") || t.rule.equals("factor AMP lvalue")) {
			return "int*";
		} else if(t.rule.equals("term term STAR factor") || t.rule.equals("term term SLASH factor") || t.rule.equals("term term PCT factor") || t.rule.equals("factor NUM") || t.rule.endsWith("STAR factor")) {
			return "int";
		} else if(t.rule.equals("factor LPAREN expr RPAREN") || t.rule.equals("lvalue LPAREN lvalue RPAREN")) {
			return getType(t.children.get(1));
		} else if(t.children.size() == 1 || t.children.size() == 2) {
			return getType(t.children.get(0));
		} else {
			return "UNTYPED";
		}
	}

	//
	// Helpers
	//

	int counter = 0;
	private String generateLabel(String base) {
		String label = base + counter;
		counter++;
		while(symbols.containsKey(label)) {
			label = base + counter;
			counter++;
		}
		return label;
	}

	private void push(int push) {
		mips.add("lis $4");
		mips.add(".word 4");
		sw(push,30,-4);
		mips.add("sub $30, $30, $4");
	}

	private void pop(int pop) {
		mips.add("lis $4");
		mips.add(".word 4");
		mips.add("add $30, $30, $4");
		lw(pop,30,-4);
	}

	private void sw(int from, int to, int offset) {
		mips.add("sw $" + from + ", " + offset + "($" + to + ")");
	}

	private void lw(int to, int from, int offset) {
		mips.add("lw $" + to + ", " + offset + "($" + from + ")");
	}

	private boolean genNew = false;
	private boolean genDelete = false;
	private boolean genPrint = false;

	private void call(String function) {
		push(31);
		if(function.equals("print")) {
			if(genPrint == false) {
				genPrint = true;
				mips.add("lis $5");
				mips.add(".word print");
			}
			mips.add("jalr $5");
		} else if(function.equals("new")) {
			if(genNew == false) {
				genNew = true;
				mips.add("lis $6");
				mips.add(".word new");
			}
			mips.add("jalr $6");
		} else if(function.equals("delete")) {
			if(genDelete == false) {
				genDelete = true;
				mips.add("lis $7");
				mips.add(".word delete");
			}
			mips.add("jalr $7");
		}
		pop(31);
	}

	private void generateImports(ParseTree t) {
		if(genNew || genDelete) {
			mips.add(6, "lw $31, -4($30)");
			mips.add(6, "add $30, $30, $4");
			mips.add(6, "jalr $3");
			mips.add(6, ".word init");
			mips.add(6, "lis $3");
			mips.add(6, "sub $30, $30, $4");
			mips.add(6, "sw $31, -4($30)");
			mips.add(6, ".word 4");
			mips.add(6, "lis $4");

			if(symbols.get(t.children.get(3).children.get(1).rule.split(" ")[1]).equals("int")) {
				mips.add(6, ".word 0");
				mips.add(6, "lis $2");
			}
		}
		if(genPrint)
			mips.add(0, ".import print");
		if(genDelete)
			mips.add(0, ".import delete");
		if(genNew)
			mips.add(0, ".import new");
		if(genNew || genDelete) {
			mips.add(0, ".import init");
		}
	}

	private void generateSymbols() {
		for(Map.Entry<String,String> entry : symbols.entrySet()) {
			mips.add(entry.getKey() + ": .word 0");
		}
	}

	public void print() {
		for(String instruction : mips) {
			System.out.println(instruction);
		}
	}
}


// private void optimize() {
// 	//
// 	// Optimization
// 	//

// 	private void optimizeTree(Tree t) {
// 		initVars(t.children.get(3));
// 		initVars(t.children.get(5));
// 		initVars(t.children.get(8));

// 		propogate(t.children.get(9));
// 		partiallyfold(t.children.get(9));
// 		streamlineLoops(t.children.get(9));
// 		Tree deadless = removeDead(t.children.get(9));
// 		t.children.remove(9);
// 		t.children.add(9, deadless);

// 		propogate(t.children.get(11));
// 		partiallyfold(t.children.get(11));

// 		//t.print(0);
// 	}

// 	private void propogate(Tree t) {
// 		if(t.rule.startsWith("statement WHILE")) {
// 			nullify(t.children.get(2));
// 		}

// 		for(Tree child : t.children) {
// 			propogate(child);
// 		}

// 		if(t.rule.equals("factor ID")) {
// 			try {
// 				int newVal = Integer.parseInt(symbolVals.get(t.children.get(0).rule.split(" ")[1]));
// 				t.rule = "factor NUM";
// 				t.children.get(0).rule = "NUM " + newVal;
// 			} catch (Exception e) {
// 				return;
// 			}
// 		} else if(t.rule.startsWith("statement lvalue")) {
// 			try {
// 				int val = Integer.parseInt(getNum(t.children.get(2)));
// 				fold(t.children.get(2), val);

// 				symbolVals.put(t.children.get(0).children.get(0).rule.split(" ")[1], ""+val);
// 			} catch (Exception e) {
// 				symbolVals.put(t.children.get(0).children.get(0).rule.split(" ")[1], "NULL");
// 				return;
// 			}
// 		}
// 	}

// 	private void nullify(Tree t) {
// 		for(Tree child : t.children) {
// 			nullify(child);
// 		}

// 		if(t.rule.startsWith("ID ")) {
// 			symbolVals.put(t.rule.split(" ")[1], "NULL");
// 		}
// 	}

// 	private void fold(Tree t, int val) {
// 		Tree term = new Tree();
// 		term.rule = "term facto`r";
// 		Tree factor = new Tree();
// 		factor.rule = "factor NUM";
// 		Tree num = new Tree();
// 		num.rule = "NUM " + val;

// 		t.rule = "expr term";
// 		t.children.clear();
// 		t.children.add(term);
// 		term.children.add(factor);
// 		factor.children.add(num);
// 	}

// 	private Set<String> mathRules = new HashSet<String>(Arrays.asList("expr expr PLUS term", "expr expr MINUS term", "term term STAR factor", "term term SLASH factor", "term term PCT factor"));

// 	private String getNum(Tree t) {
// 		if(t.rule.equals("factor AMP lvalue") && t.children.get(1).rule.equals("lvalue STAR factor") || t.rule.equals("lvalue STAR factor") && t.children.get(1).rule.equals("factor AMP lvalue")) {
// 			return getNum(t.children.get(1).children.get(1));
// 		}

// 		if(t.rule.startsWith("NUM")) {
// 			return t.rule.split(" ")[1];
// 		} else if(t.rule.startsWith("ID")) {
// 			return symbolVals.get(t.rule.split(" ")[1]);
// 		} else if(mathRules.contains(t.rule)) {
// 			try {
// 				int lvalue = Integer.parseInt(getNum(t.children.get(0)));
// 				int rvalue = Integer.parseInt(getNum(t.children.get(2)));
				
// 				if(t.rule.contains("PLUS")) {
// 					return ""+(lvalue + rvalue);
// 				} else if(t.rule.contains("MINUS")) {
// 					return ""+(lvalue - rvalue);
// 				} else if(t.rule.contains("STAR")) {
// 					return ""+(lvalue * rvalue);
// 				} else if(t.rule.contains("SLASH")) {
// 					return ""+(lvalue / rvalue);
// 				} else {
// 					return ""+(lvalue % rvalue);
// 				}
// 			} catch (Exception e) {
// 				return "NULL";
// 			}
// 		} else if(t.rule.equals("expr term") || t.rule.equals("term factor") || t.rule.equals("factor NUM") || t.rule.equals("factor ID")) {
// 			return getNum(t.children.get(0));
// 		} else if(t.rule.equals("factor LPAREN expr RPAREN") || t.rule.equals("factor STAR factor")) {
// 			return getNum(t.children.get(1));
// 		} else {
// 			return "NULL";
// 		}
// 	}

// 	private void partiallyfold(Tree t) {
// 		for(Tree child : t.children) {
// 			partiallyfold(child);
// 		}

// 		if(mathRules.contains(t.rule)) {
// 			int newVal = 0;
// 			int lvalue = 0;
// 			int rvalue = 0;

// 			try {
// 				rvalue = Integer.parseInt(getNum(t.children.get(2)));
// 				if(rvalue == 0) {
// 					if(t.rule.contains("PLUS") || t.rule.contains("MINUS")) {
// 						Tree temp = t.children.get(0);
// 						t.rule = temp.rule;
// 						t.children.clear();
// 						t.children.add(temp.children.get(0));
// 					} else if(t.rule.contains("STAR")) {
// 						fold(t, 0);
// 					} else {
// 						return;
// 					}
// 				}
// 				lvalue = Integer.parseInt(getNum(t.children.get(0)));

// 				if(t.rule.contains("PLUS")) {
// 					newVal = lvalue + rvalue;
// 				} else if(t.rule.contains("MINUS")) {
// 					newVal = lvalue - rvalue;
// 				} else if(t.rule.contains("STAR")) {
// 					newVal = lvalue * rvalue;
// 				} else if(t.rule.contains("SLASH")) {
// 					newVal = lvalue / rvalue;
// 				} else {
// 					newVal = lvalue % rvalue;
// 				}
// 			} catch(Exception e) {
// 				return;
// 			}

// 			Tree factor = new Tree();

// 			if(t.rule.contains("PLUS") || t.rule.contains("MINUS")) {
// 				fold(t, newVal);
// 			} else {
// 				t.rule = "term factor";
// 				t.children.clear();
// 				t.children.add(factor);
// 			}

// 			factor.rule = "factor NUM";
// 			Tree num = new Tree();
// 			num.rule = "NUM " + newVal;
// 			factor.children.add(num);
// 		} else if(t.rule.equals("expr term")) {
// 			try {
// 				int val = Integer.parseInt(getNum(t.children.get(0)));

// 				fold(t, val);
// 			} catch (Exception e) {
// 				return;
// 			}
// 		}
// 	}

// 	private void streamlineLoops(Tree t) {
// 		for(Tree child : t.children) {
// 			streamlineLoops(child);
// 		}

// 		if(t.rule.startsWith("statement IF")) {
// 			String bool = getTruth(t.children.get(2));
// 			if(bool.equals("NULL")) {
// 				return;
// 			}

// 			Tree temp = new Tree();
// 			if(bool.equals("TRUE")) {
// 				temp = t.children.get(5);
// 			} else {
// 				temp = t.children.get(9);
// 			}

// 			t.rule = "statements statements statement";
// 			t.children.clear();
// 			for(Tree child : temp.children) {
// 				t.children.add(child);
// 			}
// 		} else if(t.rule.startsWith("statement WHILE")) {
// 			String bool = getTruth(t.children.get(2));
// 			if(bool.equals("FALSE")) {
// 				t.rule = "NULL";
// 				t.children.clear();
// 			}
// 		}
// 	}

// 	private String getTruth(Tree t) {
// 		try {
// 			int lvalue = Integer.parseInt(getNum(t.children.get(0)));
// 			int rvalue = Integer.parseInt(getNum(t.children.get(2)));

// 			if(t.children.get(1).rule.startsWith("LT")) {
// 				return lvalue < rvalue ? "TRUE" : "FALSE";
// 			} else if(t.children.get(1).rule.startsWith("LE")) {
// 				return lvalue <= rvalue ? "TRUE" : "FALSE";
// 			} else if(t.children.get(1).rule.startsWith("GT")) {
// 				return lvalue > rvalue ? "TRUE" : "FALSE";
// 			} else if(t.children.get(1).rule.startsWith("GE")) {
// 				return lvalue >= rvalue ? "TRUE" : "FALSE";
// 			} else if(t.children.get(1).rule.startsWith("EQ")) {
// 				return lvalue == rvalue ? "TRUE" : "FALSE";
// 			} else {
// 				return lvalue != rvalue ? "TRUE" : "FALSE";
// 			}
// 		} catch (Exception e) {
// 			return "NULL";
// 		}
// 	}

// 	private Tree removeDead(Tree t) {
// 		Tree temp = t;
// 		Tree statementNexus = buildNexus(temp, new Tree());

// 		statementNexus.rule = "nexus";

// 		for(int i = 0; i < statementNexus.children.size(); i++) {
// 			if(statementNexus.children.get(i).rule.startsWith("statement lvalue") && statementNexus.children.get(i+1).rule.startsWith("statement lvalue")) {
// 				try {
// 					String l1 = statementNexus.children.get(i).children.get(0).children.get(0).rule.split(" ")[1];
// 					String l2 = statementNexus.children.get(i+1).children.get(0).children.get(0).rule.split(" ")[1];
// 					if(l1.equals(l2)) {
// 						statementNexus.children.remove(i);
// 						i--;
// 					}
// 				} catch(Exception e) {
// 					continue;
// 				}
// 			}
// 		}

// 		// for(int i = 0; i < statementNexus.children.size(); i++) {
// 		// 	System.err.println(statementNexus.children.get(i).rule);
// 		// }

// 		return statementNexus;
// 	}

// 	private Tree buildNexus(Tree pointer, Tree build) {
// 		while(pointer.rule.equals("statements statements statement")) {
// 			if(pointer.children.get(1).rule.equals("statements statements statement")) {
// 				Tree temp = pointer.children.get(1);
// 				build = buildNexus(temp, build);
// 			} else if(!pointer.children.get(1).rule.equals("NULL")) {
// 				build.children.add(0, pointer.children.get(1));
// 			}
// 			pointer = pointer.children.get(0);
// 		}
// 		return build;
// 	}

// 	private void printSymbolVals() {
// 		for(Map.Entry<String,String> entry : symbolVals.entrySet()) {
// 			System.err.println(entry.getKey() + " " + entry.getValue());
// 		}
// 		System.err.println("---");
// 	}

// 	private void optimizeMips() {
// 		for(int i = 0; i < mips.size() - 1; i++) {
// 			if(mips.get(i).startsWith("lw") || mips.get(i).startsWith("sw")) {
// 				if(mips.get(i).substring(1).equals(mips.get(i+1).substring(1))) {
// 					mips.remove(i);
// 					mips.remove(i);
// 					i -= 2;
// 				}
// 			} else if(mips.get(i).startsWith("add") || mips.get(i).startsWith("sub")) {
// 				if(mips.get(i).substring(3).equals(mips.get(i+1).substring(3))) {
// 					mips.remove(i);
// 					mips.remove(i);
// 					i -= 2;
// 				}
// 			}
// 		}
// 	}
// }
