package cs241.asm;

import java.util.*;
import java.math.*;

/** A sample main class demonstrating the use of the Lexer.
 * This main class just outputs each line in the input, followed by
 * the tokens returned by the lexer for that line.
 */
public class Asm {
    public static final void go(boolean a, Scanner in, boolean b, boolean relocatable, boolean linkable) {
        Main.go(a, in, b, relocatable, linkable);
    }
    public static final void go(boolean a, Scanner in, boolean b, boolean relocatable, boolean linkable, boolean c) {
        Main.go(a, in, b, relocatable, linkable, c);
    }

    private Lexer lexer = new Lexer();

    private void run() {
        Scanner in = new Scanner(System.in);
        while(in.hasNextLine()) {
            String line = in.nextLine();
            processLine( line );
        }
        System.out.flush();
    }

    private void processLine(String line) {
        System.out.println( "Input line: "+line );
        List tokens;
        try {
            tokens = lexer.scan(line);
        } catch(ParseError e) {
            System.err.println("ERROR in scanning line "+line+":\n  "+e);
            System.exit(1);
            return;
        }
        for( int i = 0; i < tokens.size(); i++ ) {
            System.out.println( "  Token: "+tokens.get(i));
        }
    }
}

/** The different kinds of tokens. */
enum Sym {
    ID,         // Opcode or identifier (use of a label)
    INT,        // Decimal integer
    HEXINT,     // Hexadecimal integer
    REGISTER,   // Register number
    COMMA,      // Comma
    LPAREN,     // (
    RPAREN,     // )
    LABEL,      // Declaration of a label (with a colon)
    DIRECTIVE,  // Assembler directive
    WHITESPACE; // Whitespace
}

/** Representation of a token. */
class Token {
    public Sym sym;       // The kind of token.
    public String string; // String representation of the actual token in the
                          // source code.

    public Token(Sym sym, String string) {
        this.sym = sym;
        this.string = string;
    }
    public String toString() {
        return sym+" {"+string+"}";
    }
    /** Returns an integer representation of the token. For tokens of kind
     * INT (decimal integer constant) and HEXINT (hexadecimal integer
     * constant), returns the integer constant. For tokens of kind
     * REGISTER, returns the register number.
     */
    public int toInt() {
        if(sym == Sym.INT) return parseLiteral(string, 10, 32);
        if(sym == Sym.HEXINT) return parseLiteral(string.substring(2), 16, 32);
        if(sym == Sym.REGISTER) return parseLiteral(string.substring(1), 10, 5);
        throw new RuntimeException("internal error");
    }
    public boolean isInt() {
        return (sym == Sym.INT) || (sym == Sym.HEXINT);
    }
    private int parseLiteral(String s, int base, int bits) {
        BigInteger x = new BigInteger(s, base);
        if(x.signum() > 0) {
            if(x.bitLength() > bits) throw new ParseError("Constant out of range: "+s);
        } else if(x.signum() < 0) {
            if(x.negate().bitLength() > bits-1
            && x.negate().subtract(new BigInteger("1")).bitLength() > bits-1)
                throw new ParseError("Constant out of range: "+s);
        }
        return (int) (x.longValue() & ((1L << bits) - 1));
    }
}

/** Lexer -- reads an input line, and partitions it into a list of tokens. */
class Lexer {
    public Lexer() {
        CharSet whitespace = new Chars("\t\n\r ");
        CharSet letters = new Chars(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        CharSet lettersDigits = new Chars(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        CharSet digits = new Chars("0123456789");
        CharSet hexDigits = new Chars("0123456789ABCDEFabcdef");
        CharSet oneToNine = new Chars("123456789");
        CharSet all = new AllChars(); 

        table = new Transition[] {
                new Transition(State.START, whitespace, State.WHITESPACE),
                new Transition(State.START, letters, State.ID),
                new Transition(State.ID, lettersDigits, State.ID),
                new Transition(State.START, oneToNine, State.INT),
                new Transition(State.INT, digits, State.INT),
                new Transition(State.START, new Chars("-"), State.MINUS),
                new Transition(State.MINUS, digits, State.INT),
                new Transition(State.START, new Chars(","), State.COMMA),
                new Transition(State.START, new Chars("("), State.LPAREN),
                new Transition(State.START, new Chars(")"), State.RPAREN),
                new Transition(State.START, new Chars("$"), State.DOLLAR),
                new Transition(State.DOLLAR, digits, State.REGISTER),
                new Transition(State.REGISTER, digits, State.REGISTER),
                new Transition(State.START, new Chars("0"), State.ZERO),
                new Transition(State.ZERO, new Chars("x"), State.ZEROX),
                new Transition(State.ZERO, digits, State.INT),
                new Transition(State.ZEROX, hexDigits, State.HEXINT),
                new Transition(State.HEXINT, hexDigits, State.HEXINT),
                new Transition(State.ID, new Chars(":"), State.LABEL),
                new Transition(State.START, new Chars(";"), State.COMMENT),
                new Transition(State.START, new Chars("."), State.DOT),
                new Transition(State.DOT, letters, State.DIRECTIVE),
                new Transition(State.DIRECTIVE, letters, State.DIRECTIVE),
                new Transition(State.COMMENT, all, State.COMMENT)
        };
    }
    /** Partitions the line passed in as input into a list of tokens.
     * The list of tokens is returned.
     */
    public List<Token> scan( String input ) {
        List<Token> ret = new ArrayList<Token>();
        if(input.length() == 0) return ret;
        int i = 0;
        int startIndex = 0;
        State state = State.START;
        while(true) {
            Transition t = null;
            if(i < input.length()) t = findTransition(state, input.charAt(i));
            if(t == null) {
                // no more transitions possible
                if(!state.isFinal()) {
                    throw new ParseError(
                        "Lexer error after reading "+input.substring(0, i));
                }
                if( state.sym != Sym.WHITESPACE ) {
                    ret.add(new Token(state.sym,
                                input.substring(startIndex, i)));
                }
                startIndex = i;
                state = State.START;
                if(i >= input.length()) break;
            } else {
                state = t.toState;
                i++;
            }
        }
        return ret;
    }

    ///////////////////////////////////////////////////////////////
    // END OF PUBLIC METHODS
    ///////////////////////////////////////////////////////////////

    private Transition findTransition(State state, char c) {
        for( int j = 0; j < table.length; j++ ) {
            Transition t = table[j];
            if(t.fromState == state && t.chars.contains(c)) {
                return t;
            }
        }
        return null;
    }

    private static enum State {
        START(null),
        DOLLAR(null),
        MINUS(null),
        REGISTER(Sym.REGISTER),
        INT(Sym.INT),
        ID(Sym.ID),
        LABEL(Sym.LABEL),
        COMMA(Sym.COMMA),
        LPAREN(Sym.LPAREN),
        RPAREN(Sym.RPAREN),
        ZERO(Sym.INT),
        ZEROX(null),
        HEXINT(Sym.HEXINT),
        COMMENT(Sym.WHITESPACE),
        DOT(null),
        DIRECTIVE(Sym.DIRECTIVE),
        WHITESPACE(Sym.WHITESPACE);
        State(Sym sym) {
            this.sym = sym;
        }
        Sym sym;
        boolean isFinal() {
            return sym != null;
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

class ParseError extends RuntimeException {
    public ParseError(String s) {
        super(s);
    }
}


class Args {
    public Args(int rd, int rs, int rt, Token imm) {
        checkReg(rd); checkReg(rs); checkReg(rt);
        this.rd = rd; this.rs = rs; this.rt = rt;
        this.imm = imm;
    }
    private void checkReg(int r) {
        if(r<0 || r > 31) throw new ParseError("Invalid register number "+r);
    }
    public int rd, rs, rt, immValue;
    public Token imm;
    public void resolve(LabelResolver resolver,
            Map<String, Integer> symbolTable, Set<String> impSet, int pc) {
        if(resolver != null) {
            immValue = resolver.resolve(symbolTable, impSet, pc, imm);
        }
    }
}
abstract class ArgParser {
    abstract Args parse(Iterator<Token> it);
    protected void skipToken(Iterator<Token> it, Sym sym) {
        if(!it.hasNext())
            throw new ParseError("Expecting "+sym+" but got end of line.");
        Token t = it.next();
        if(t.sym != sym)
            throw new ParseError("Expecting "+sym+" but got "+t);
    }
    protected int eatReg(Iterator<Token> it) {
        if(!it.hasNext())
            throw new ParseError("Expecting register but got end of line.");
        Token t = it.next();
        if(t.sym != Sym.REGISTER)
            throw new ParseError("Expecting register but got "+t);
        return t.toInt();
    }
    protected Token eatImm(Iterator<Token> it) {
        if(!it.hasNext())
            throw new ParseError("Expecting immediate but got end of line.");
        Token t = it.next();
        if(t.sym != Sym.INT && t.sym != Sym.HEXINT && t.sym != Sym.ID)
            throw new ParseError("Expecting immediate but got "+t);
        return t;
    }
    protected String eatId(Iterator<Token> it) {
        if(!it.hasNext())
            throw new ParseError("Expecting identifier but got end of line.");
        Token t = it.next();
        if(t.sym != Sym.ID)
            throw new ParseError("Expecting identifier but got "+t);
        return t.string;
    }
    protected void expectEnd(Iterator<Token> it) {
        if(it.hasNext()) throw new ParseError("Expecting end of line, but there's more stuff.");
    }
}
class NoArgs extends ArgParser {
    Args parse(Iterator<Token> it) { 
        expectEnd(it);
        return new Args(0,0,0,null); }
}
class Rs extends ArgParser {
    Args parse(Iterator<Token> it) {
        int rs = eatReg(it);
        expectEnd(it);
        return new Args(0, rs, 0, null);
    }
}
class R extends ArgParser {
    Args parse(Iterator<Token> it) {
        int rd = eatReg(it);
        expectEnd(it);
        return new Args(rd, 0, 0, null);
    }
}
class RR extends ArgParser {
    Args parse(Iterator<Token> it) {
        int rs = eatReg(it);
        skipToken(it, Sym.COMMA);
        int rt = eatReg(it);
        expectEnd(it);
        return new Args(0, rs, rt, null);
    }
}
class RRR extends ArgParser {
    Args parse(Iterator<Token> it) {
        int rd = eatReg(it);
        skipToken(it, Sym.COMMA);
        int rs = eatReg(it);
        skipToken(it, Sym.COMMA);
        int rt = eatReg(it);
        expectEnd(it);
        return new Args(rd, rs, rt, null);
    }
}
class RRRdts extends ArgParser {
    Args parse(Iterator<Token> it) {
        int rd = eatReg(it);
        skipToken(it, Sym.COMMA);
        int rt = eatReg(it);
        skipToken(it, Sym.COMMA);
        int rs = eatReg(it);
        expectEnd(it);
        return new Args(rd, rs, rt, null);
    }
}
class RI extends ArgParser {
    Args parse(Iterator<Token> it) {
        int rt = eatReg(it);
        skipToken(it, Sym.COMMA);
        Token imm = eatImm(it);
        expectEnd(it);
        return new Args(0, 0, rt, imm);
    }
}
class RIs extends ArgParser {
    Args parse(Iterator<Token> it) {
        int rs = eatReg(it);
        skipToken(it, Sym.COMMA);
        Token imm = eatImm(it);
        expectEnd(it);
        return new Args(0, rs, 0, imm);
    }
}
/** Used for shift instructions. */
class RRIdt extends ArgParser {
    Args parse(Iterator<Token> it) {
        int rd = eatReg(it);
        skipToken(it, Sym.COMMA);
        int rt = eatReg(it);
        skipToken(it, Sym.COMMA);
        Token imm = eatImm(it);
        if(!imm.isInt())
            throw new ParseError("Shift offset is not a constant");
        if(imm.toInt() < 0 || imm.toInt() > 31)
            throw new ParseError("Shift offset out of range");
        expectEnd(it);
        return new Args(rd, 0, rt, imm);
    }
}
class RRIst extends ArgParser {
    Args parse(Iterator<Token> it) {
        int rs = eatReg(it);
        skipToken(it, Sym.COMMA);
        int rt = eatReg(it);
        skipToken(it, Sym.COMMA);
        Token imm = eatImm(it);
        expectEnd(it);
        return new Args(0, rs, rt, imm);
    }
}
class RRIts extends ArgParser {
    Args parse(Iterator<Token> it) {
        int rt = eatReg(it);
        skipToken(it, Sym.COMMA);
        int rs = eatReg(it);
        skipToken(it, Sym.COMMA);
        Token imm = eatImm(it);
        expectEnd(it);
        return new Args(0, rs, rt, imm);
    }
}
/** used for j and jal */
class I extends ArgParser {
    Args parse(Iterator<Token> it) {
        Token imm = eatImm(it);
        expectEnd(it);
        return new Args(0, 0, 0, imm);
    }
}
class LS extends ArgParser {
    Args parse(Iterator<Token> it) {
        int rt = eatReg(it);
        skipToken(it, Sym.COMMA);
        Token imm = eatImm(it);
        skipToken(it, Sym.LPAREN);
        int rs = eatReg(it);
        skipToken(it, Sym.RPAREN);
        expectEnd(it);
        return new Args(0, rs, rt, imm);
    }
}
abstract class Encoding {
    abstract int encode(Opcode opcode, Args args);
}
class REncoding extends Encoding {
    int encode(Opcode opcode, Args args) {
        return (opcode.code<<26) | (args.rs<<21) | (args.rt<<16) |
            (args.rd<<11) | opcode.funct;
    }
}
class IEncoding extends Encoding {
    int encode(Opcode opcode, Args args) {
        if(args.imm.sym == Sym.INT && (args.immValue < -32768 || args.immValue > 32767))
            throw new ParseError("Branch offset out of range");
        if(args.imm.sym == Sym.HEXINT && (args.immValue < 0 || args.immValue > 65535))
            throw new ParseError("Branch offset out of range");
        return (opcode.code<<26) | (args.rs<<21) | (args.rt<<16) |
            (args.immValue & 0xffff);
    }
}
class SEncoding extends Encoding {
    int encode(Opcode opcode, Args args) {
        return (opcode.code<<26) | (args.rs<<21) | (args.rt<<16) |
            (args.rd<<11) | ((args.immValue & 0x3f)<<6) | opcode.funct;
    }
}
class JEncoding extends Encoding {
    int encode(Opcode opcode, Args args) {
        return (opcode.code<<26) | (args.immValue & 0x03ffffff);
    }
}

enum Opcode {
    add    (0x00, 0x20, new RRR(),    new REncoding(), null),
    sub    (0x00, 0x22, new RRR(),    new REncoding(), null),
    //addi   (0x08, 0x00, new RRIts(),  new IEncoding(), new Absolute()),
    //and    (0x00, 0x24, new RRR(),    new REncoding(), null),
    //andi   (0x0c, 0x00, new RRIts(),  new IEncoding(), new Absolute()),
    //or     (0x00, 0x25, new RRR(),    new REncoding(), null),
    //ori    (0x0d, 0x00, new RRIts(),  new IEncoding(), new Absolute()),
    //sll    (0x00, 0x00, new RRIdt(),  new SEncoding(), new Absolute()),
    slt    (0x00, 0x2a, new RRR(),    new REncoding(), null),
    sw     (0x2b, 0x00, new LS(),     new IEncoding(), new Literal()),
    lw     (0x23, 0x00, new LS(),     new IEncoding(), new Literal()),
    mult   (0x00, 0x18, new RR(),     new REncoding(), null),
    div    (0x00, 0x1a, new RR(),     new REncoding(), null),
    mflo   (0x00, 0x12, new R(),      new REncoding(), null),
    mfhi   (0x00, 0x10, new R(),      new REncoding(), null),
    beq    (0x04, 0x00, new RRIst(),  new IEncoding(), new Branch()),
    bne    (0x05, 0x00, new RRIst(),  new IEncoding(), new Branch()),
    //lui    (0x0f, 0x00, new RI(),     new IEncoding(), new HighWord()),
    lis    (0x00, 0x14, new R(),      new REncoding(), null),
    //j      (0x02, 0x00, new I(),      new JEncoding(), new Jump()),
    jr     (0x00, 0x08, new Rs(),     new REncoding(), null),

    //addu   (0x00, 0x21, new RRR(),    new REncoding(), null),
    //addiu  (0x09, 0x00, new RRIts(),  new IEncoding(), new Absolute()),
    divu   (0x00, 0x1b, new RR(),     new REncoding(), null),
    multu  (0x00, 0x19, new RR(),     new REncoding(), null),
    //nor    (0x00, 0x27, new RRR(),    new REncoding(), null),
    //sllv   (0x00, 0x04, new RRRdts(), new REncoding(), null),
    //sra    (0x00, 0x03, new RRIdt(),  new SEncoding(), new Absolute()),
    //srav   (0x00, 0x07, new RRRdts(), new REncoding(), null),
    //srl    (0x00, 0x02, new RRIdt(),  new SEncoding(), new Absolute()),
    //srlv   (0x00, 0x06, new RRRdts(), new REncoding(), null),
    //subu   (0x00, 0x23, new RRR(),    new REncoding(), null),
    //xor    (0x00, 0x26, new RRR(),    new REncoding(), null),
    //xori   (0x0e, 0x00, new RRIts(),  new IEncoding(), new Absolute()),
    sltu   (0x00, 0x2b, new RRR(),    new REncoding(), null),
    //slti   (0x0a, 0x00, new RRIts(),  new IEncoding(), new Absolute()),
    //sltiu  (0x0b, 0x00, new RRIts(),  new IEncoding(), new Absolute()),
    //bgtz   (0x07, 0x00, new RIs(),    new IEncoding(), new Branch()),
    //blez   (0x06, 0x00, new RIs(),    new IEncoding(), new Branch()),
    //jal    (0x03, 0x00, new I(),      new JEncoding(), new Jump()),
    jalr   (0x00, 0x09, new Rs(),     new REncoding(), null),
    //lb     (0x20, 0x00, new LS(),     new IEncoding(), new Absolute()),
    //lh     (0x21, 0x00, new LS(),     new IEncoding(), new Absolute()),
    //lbu    (0x24, 0x00, new LS(),     new IEncoding(), new Absolute()),
    //lhu    (0x25, 0x00, new LS(),     new IEncoding(), new Absolute()),
    //sb     (0x28, 0x00, new LS(),     new IEncoding(), new Absolute()),
    //sh     (0x29, 0x00, new LS(),     new IEncoding(), new Absolute()),
    //mtlo   (0x00, 0x13, new Rs(),     new REncoding(), null),
    //mthi   (0x00, 0x11, new Rs(),     new REncoding(), null),

    syscall(0x00, 0x0c, new NoArgs(), new REncoding(), null);

    /*
    // UW extensions supported for compatibility
    lhi    (0x19, 0x00, new RI(),     new IEncoding(), new HighWord()),
    llo    (0x18, 0x00, new RI(),     new IEncoding(), new Absolute()),
    trap   (0x00, 0x0c, new I(),      new JEncoding(), new Absolute());
    */

    int code;
    int funct;
    ArgParser parser;
    Encoding encoder;
    LabelResolver resolver;
    Opcode(int code, int funct, ArgParser parser, Encoding encoder,
            LabelResolver resolver ) {
        this.code = code;
        this.funct = funct;
        this.parser = parser;
        this.encoder = encoder;
        this.resolver = resolver;
    }
    static Opcode v(String name) {
        try {
            return Enum.valueOf(Opcode.class, name);
        } catch(IllegalArgumentException e) {
            throw new ParseError("Illegal opcode "+name);
        }
    }
}

abstract class LabelResolver {
    public int resolve(Map<String, Integer> symbolTable, Set<String> impSet, int pc, Token imm) {
        if(imm.sym == Sym.INT || imm.sym == Sym.HEXINT) return imm.toInt();
        Integer target = symbolTable.get(imm.string);
        if(target == null) {
            if (impSet.contains(imm.string)) return doExternal();
            throw new ParseError("No such label: "+imm.string);
        }
        return doResolve(pc, target);
    }
    public abstract int doResolve(int pc, int target);
    public abstract int doExternal();
}

class Literal extends LabelResolver {
    public int resolve(Map<String, Integer> symbolTable, Set<String> impSet, int pc, Token imm) {
        if(imm.sym == Sym.INT || imm.sym == Sym.HEXINT) return imm.toInt();
        throw new ParseError("Label not allowed in sw or lw.");
    }
    public int doResolve(int pc, int target) {
        throw new RuntimeException("Internal Error");
    }

    public int doExternal() {
        throw new RuntimeException("Internal Error");
    }
}
class Absolute extends LabelResolver {
    public int doResolve(int pc, int target) { return target; }
    public int doExternal() {return 0;}
}
class Branch extends LabelResolver {
    public int doResolve(int pc, int target) {
        int ret = target-(pc+4);
        if((ret%4) != 0) throw new ParseError("Branch offset not word-aligned");
        ret = ret>>2;
        if((ret & 0xffff8000) != 0 &&
           (ret & 0xffff8000) != 0xffff8000)
            throw new ParseError("Branch target too far");
        return ret;
    }
    public int doExternal() {
        throw new ParseError("Can't branch to an external address.");
    }
}
class HighWord extends LabelResolver {
    public int doResolve(int pc, int target) { return target>>16; }
    public int doExternal() {return 0;}
}
class Jump extends LabelResolver {
    public int resolve(Map<String, Integer> symbolTable, Set<String> impSet, int pc, Token addr) {
        int ret = super.resolve(symbolTable, impSet, pc, addr);
        if((ret%4) != 0) throw new ParseError("Jump target not word-aligned");
        if((ret & 0xf0000000) != (pc &0xf0000000))
            throw new ParseError("Jump target too far from current pc.");
        return (ret&0x0fffffff)>>2;
    }
    public int doResolve(int pc, int target) { return target; }
    public int doExternal() { return 0; }
}

class Main {
    public static final boolean DEBUG = false;
    public static final boolean DEBUG_LEXER = false;
    private static String pad(String s) {
        StringBuffer ret = new StringBuffer();
        for(int i = 0; i+s.length() < 8; i++) ret.append('0');
        ret.append(s);
        return ret.toString();
    }
    private static String chopColon(String in) {
        return in.substring(0, in.length()-1);
    }
    public static int INITIAL_ADDRESS = 0;
    public static final void go(boolean outputHex, Scanner in, boolean wordonly, boolean relocatable, boolean linkable) {
        go(outputHex, in, wordonly, relocatable, linkable, false);
    }
    public static final void go(boolean outputHex, Scanner in, boolean wordonly, boolean relocatable, boolean linkable, boolean dumpDymbolTable) {
        if (relocatable) INITIAL_ADDRESS = 12;
        Lexer lexer = new Lexer();

        List<List<Token>> lines = new ArrayList<List<Token>>();
        Map<String, Integer> symbolTable = new HashMap<String, Integer>();
        Map<Integer, String> imports = new HashMap<Integer, String>();
        Map<String, Integer> exports = new HashMap<String, Integer>();
        Set<Integer> relocates = new HashSet<Integer>();
        Set<String> impSet = new HashSet<String>();
        Set<String> refSet = new HashSet<String>();

        int pc;

        // Pass 1:
        pc = INITIAL_ADDRESS;
line:
        while(in.hasNextLine()) {
            String line = in.nextLine();
            List<Token> tokens;
            try {
                tokens = lexer.scan(line);
                List<Token> newTokens = new ArrayList<Token>();
                for( Token t : tokens ) {
                    if(t.sym == Sym.WHITESPACE) continue;
                    if(DEBUG_LEXER) System.err.println(t);
                    newTokens.add(t);
                }
                tokens = newTokens;
                if(tokens.isEmpty()) continue; // blank line

                Iterator<Token> tokIt = tokens.iterator();
                Token firstToken = tokIt.next();
                if(!wordonly) while(firstToken.sym == Sym.LABEL) {
                    String id = chopColon(firstToken.string);
                    if(symbolTable.containsKey(id))
                        throw new ParseError("Duplicate symbol: "+id);
                    if(impSet.contains(id))
                        throw new ParseError("Symbol " + id + " imported and defined.");
                    if(exports.containsKey(id))
                        exports.put(id, pc);

                    symbolTable.put(id, pc);
                    if(tokIt.hasNext()) {
                        firstToken = tokIt.next();
                    } else {
                        continue line;
                    }
                }
                if(!wordonly && firstToken.sym == Sym.ID) {
                    pc += 4;
                } else if(firstToken.sym == Sym.DIRECTIVE) {
                    String dir = firstToken.string;
                    if(dir.equals(".word")) {
                        if(!tokIt.hasNext()) throw new ParseError(
                                "Need an integer after directive "+dir);
                        Token secondToken = tokIt.next();
                        if(secondToken.sym != Sym.INT 
                        && secondToken.sym != Sym.HEXINT
                        && (secondToken.sym != Sym.ID || !dir.equals(".word")))
                            throw new ParseError(
                                "Need an integer after directive "+dir);
                        if (secondToken.sym == Sym.ID && relocatable) {
                           if (!impSet.contains(secondToken.string)) {
                              relocates.add(pc);
                              refSet.add(secondToken.string);
                           }
                           else if (linkable) imports.put(pc, secondToken.string);
                        }
                        if(dir.equals(".word")) pc += 4;
                        if(tokIt.hasNext())
                            throw new ParseError("Expecting end of line, but there's more stuff.");
                    } else if(!wordonly && linkable && relocatable && dir.equals(".import")) {
                        if(!tokIt.hasNext()) throw new ParseError(
                                "Need a label after directive "+dir);
                        Token secondToken = tokIt.next();
                        if(secondToken.sym != Sym.ID)
                            throw new ParseError(
                                "Need a label after directive "+dir);
                        if(tokIt.hasNext())
                            throw new ParseError("Expecting end of line, but there's more stuff.");
                        if (impSet.contains(secondToken.string))
                            throw new ParseError("Label " + secondToken.string + " imported more than once.");
                        if (symbolTable.containsKey(secondToken.string))
                            throw new ParseError("Label " + secondToken.string + " imported and defined.");
                        if (refSet.contains(secondToken.string))
                            throw new ParseError("Label " + secondToken.string + " referenced before imported.");
                        impSet.add(secondToken.string);
                    } else if(!wordonly && relocatable && linkable && dir.equals(".export")) {
                        if(!tokIt.hasNext()) throw new ParseError(
                                "Need a label after directive "+dir);
                        Token secondToken = tokIt.next();
                        if(secondToken.sym != Sym.ID)
                            throw new ParseError(
                                "Need a label after directive "+dir);
                        if(tokIt.hasNext())
                            throw new ParseError("Expecting end of line, but there's more stuff.");
                        if (exports.containsKey(secondToken.string))
                            throw new ParseError("Label " + secondToken.string + " exported more than once.");
                        if (symbolTable.containsKey(secondToken.string))
                            throw new ParseError("Label " + secondToken.string + " defined before exported.");
                        exports.put(secondToken.string, pc);
                    } else {
                        throw new ParseError("Unknown directive "+dir);
                    }
                } else {
                    if(wordonly) throw new ParseError("Expecting .word, but got "+firstToken);
                    else throw new ParseError(
                            "Expecting opcode, label, or directive, but got "+firstToken);
                }
            } catch( ParseError e ) {
                System.err.println("ERROR: Parse error in line: "+line);
                System.err.println(e.toString());
                System.exit(1);
                return;
            }
            lines.add(tokens);
        }

        if(dumpDymbolTable) {
            for(String sym : symbolTable.keySet()) {
                System.err.println(sym+" "+symbolTable.get(sym));
            }
        }

        int merlHeader, codeLen, relocateSize, esrSize=0, esrKey, esdSize=0, merlLen;
        Set<Integer> esrKeys = null;
        Set<String> esdKeys = null;
        String esdKey;

        if (relocatable) {
            merlHeader = 0x10000002;
            codeLen = pc;

            relocateSize = relocates.size() * 8;
            esrSize = 0;

            if (linkable) {
                esrKeys = imports.keySet();
                for (Iterator<Integer> iter = esrKeys.iterator(); iter.hasNext();) {
                    esrKey = iter.next();
                    esrSize += 12 + 4 * imports.get(esrKey).length();
                }

                esdSize = 0;
                esdKeys = exports.keySet();
                for (Iterator<String> iter = esdKeys.iterator(); iter.hasNext();) {
                    esdKey = iter.next();
                    esdSize += 12 + 4 * esdKey.length();
                }
            }
            merlLen = codeLen + relocateSize + esrSize + esdSize;

            writeWord(merlHeader);
            writeWord(merlLen);
            writeWord(codeLen);
         }

        // Pass 2:
        pc = INITIAL_ADDRESS;
        Iterator<List<Token>> linesIt = lines.iterator();
line2:
        while(linesIt.hasNext()) {
            List<Token> tokens = linesIt.next();
            try {
                Iterator<Token> it = tokens.iterator();
                Token opcodeToken = it.next();
                while(opcodeToken.sym == Sym.LABEL) {
                    if(it.hasNext()) {
                        opcodeToken = it.next();
                    } else {
                        if(outputHex) {
                            System.out.println("; "+formatLine(tokens));
                        }
                        continue line2;
                    }
                }
                if(opcodeToken.sym == Sym.DIRECTIVE) {
                    if(opcodeToken.string.equals(".word")) {
                        Token tok = it.next();
                        int arg = new Absolute().resolve(symbolTable, impSet, pc, tok);
                        if(outputHex)
                            System.out.println(pad(Integer.toHexString(arg)));
                        else
                            writeWord(arg);
                        pc += 4;
                    }
                    else if(relocatable && linkable && opcodeToken.string.equals(".import")) {
                    }
                    else if(relocatable && linkable && opcodeToken.string.equals(".export")) {
                        Token tok = it.next();
                        if (!symbolTable.containsKey(tok.string))
                            throw new ParseError("Label " + tok.string + " exported but not defined.");
                    }
                }
                if(opcodeToken.sym == Sym.ID) {
                    Opcode opcode = Opcode.v(opcodeToken.string);
                    Args args = opcode.parser.parse(it);
                    args.resolve(opcode.resolver, symbolTable, impSet, pc);
                    int encoding = opcode.encoder.encode(opcode, args);
                    if(outputHex) {
                        System.out.print(pad(Integer.toHexString(encoding)));
                        System.out.print(" ; "+pad(Integer.toHexString(pc)));
                        System.out.println(formatLine(tokens));
                    } else {
                        writeWord(encoding);
                    }
                    pc += 4;
                }
            } catch( ParseError e ) {
                System.err.print("ERROR: Parse error in line:");
                System.err.println(formatLine(tokens));
                System.err.println(e.toString());
                System.exit(1);
                return;
            }
        }

        if (relocatable) {
            for (int r : relocates) {
               writeWord(1);
               writeWord(r);
            }

            if (linkable) {
                for (Iterator<Integer> iter = esrKeys.iterator(); iter.hasNext();) {
                    esrKey = iter.next();
                    writeWord(0x11);
                    writeWord(esrKey);
                    String lbl = imports.get(esrKey);
                    writeWord(lbl.length());
                    for (int i=0; i < lbl.length(); i++) {
                       writeWord(lbl.charAt(i));
                    }
                }
                for (Iterator<String> iter = esdKeys.iterator(); iter.hasNext();) {
                    esdKey = iter.next();
                    writeWord(5);
                    writeWord(exports.get(esdKey));
                    writeWord(esdKey.length());
                    for (int i=0; i < esdKey.length(); i++) {
                       writeWord(esdKey.charAt(i));
                    }
                }
           }
        }
        System.out.flush();
    }
    public static void writeWord(int word) {
        System.out.write((word>>24)&0xff);
        System.out.write((word>>16)&0xff);
        System.out.write((word>>8)&0xff);
        System.out.write((word)&0xff);
    }
    private static String formatLine(List<Token> tokens) {
        StringBuffer ret = new StringBuffer();
        for(Token t : tokens) {
            ret.append(" ");
            ret.append(t.string);
        }
        return ret.toString();
    }
}

