import java.math.BigInteger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Asm {
	public static final void main(String[] args) {
		new Asm().run();
	}

	private Lexer lexer = new Lexer();

	private void run() {
		Scanner in = new Scanner(System.in);

		Map<String,Integer> labels = new LinkedHashMap<String,Integer>();
		List<String> output = new ArrayList<String>();
		int pc = 0;

		while(in.hasNextLine()) {
			String line = in.nextLine();

			// Scan the line into an array of tokens.
			Token[] tokens;
			tokens = lexer.scan(line);

			if(tokens.length == 0) continue;

			while(tokens.length > 0 && tokens[0].kind == Kind.LABEL) {
				String label = tokens[0].lexeme.substring(0, tokens[0].lexeme.length()-1);

				if(labels.containsKey(label)) {
					System.err.println("ERROR: Multiple declarations of label " + label + ".");
					System.exit(1);
				}

				labels.put(label, pc);

				Token[] newTokens = new Token[tokens.length - 1];
				System.arraycopy(tokens, 1, newTokens, 0, tokens.length-1);		// Remove label from token array
				tokens = newTokens;
			}

			if(tokens.length == 0) continue;

			if(tokens[0].kind == Kind.DOTWORD) {
				if(tokens.length != 2) {
					invalidArgumentNumber(tokens[0].lexeme);
				}

				if(tokens[1].kind == Kind.HEXINT || tokens[1].kind == Kind.INT) {
					output.add(""+tokens[1].toInt());
				} else if(tokens[1].kind == Kind.ID) {
					output.add(tokens[1].lexeme);
				} else {
					invalidArgument(tokens[0].lexeme, tokens[1].lexeme);
				}
			} else if(tokens[0].kind == Kind.ID) {
				if("jr".equals(tokens[0].lexeme)) {
					if(tokens.length != 2) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER) {
						String s = getRegisterAsBinary(tokens[1].lexeme);

						String instr = "000000" + s + "000000000000000001000";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme);
					}
				} else if("jalr".equals(tokens[0].lexeme)) {
					if(tokens.length != 2) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER) {
						String s = getRegisterAsBinary(tokens[1].lexeme);

						String instr = "000000" + s + "000000000000000001001";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme);
					}
				} else if("add".equals(tokens[0].lexeme)) {
					if(tokens.length != 6) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA || tokens[2].kind != Kind.COMMA) {
						invalidSeparators(tokens[2].lexeme, tokens[4].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[3].kind == Kind.REGISTER && tokens[5].kind == Kind.REGISTER) {
						String d = getRegisterAsBinary(tokens[1].lexeme);
						String s = getRegisterAsBinary(tokens[3].lexeme);
						String t = getRegisterAsBinary(tokens[5].lexeme);

						String instr = "000000" + s + t + d + "00000100000";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
					}
				} else if("sub".equals(tokens[0].lexeme)) {
					if(tokens.length != 6) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA || tokens[2].kind != Kind.COMMA) {
						invalidSeparators(tokens[2].lexeme, tokens[4].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[3].kind == Kind.REGISTER && tokens[5].kind == Kind.REGISTER) {
						String d = getRegisterAsBinary(tokens[1].lexeme);
						String s = getRegisterAsBinary(tokens[3].lexeme);
						String t = getRegisterAsBinary(tokens[5].lexeme);

						String instr = "000000" + s + t + d + "00000100010";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
					}
				} else if("slt".equals(tokens[0].lexeme)) {
					if(tokens.length != 6) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA || tokens[2].kind != Kind.COMMA) {
						invalidSeparators(tokens[2].lexeme, tokens[4].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[3].kind == Kind.REGISTER && tokens[5].kind == Kind.REGISTER) {
						String d = getRegisterAsBinary(tokens[1].lexeme);
						String s = getRegisterAsBinary(tokens[3].lexeme);
						String t = getRegisterAsBinary(tokens[5].lexeme);

						String instr = "000000" + s + t + d + "00000101010";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
					}
				} else if("sltu".equals(tokens[0].lexeme)) {
					if(tokens.length != 6) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA || tokens[2].kind != Kind.COMMA) {
						invalidSeparators(tokens[2].lexeme, tokens[4].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[3].kind == Kind.REGISTER && tokens[5].kind == Kind.REGISTER) {
						String d = getRegisterAsBinary(tokens[1].lexeme);
						String s = getRegisterAsBinary(tokens[3].lexeme);
						String t = getRegisterAsBinary(tokens[5].lexeme);

						String instr = "000000" + s + t + d + "00000101011";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
					}
				} else if("beq".equals(tokens[0].lexeme)) {
					if(tokens.length != 6) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA || tokens[4].kind != Kind.COMMA) {
						invalidSeparators(tokens[2].lexeme, tokens[4].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[3].kind == Kind.REGISTER) {
						if(tokens[5].kind == Kind.HEXINT || tokens[5].kind == Kind.INT) {
							String s = getRegisterAsBinary(tokens[1].lexeme);
							String t = getRegisterAsBinary(tokens[3].lexeme);

							int iDec = 0;
							if(tokens[5].kind == Kind.HEXINT) {
								String lex = tokens[5].lexeme;
								lex = lex.substring(2);

								while(lex.length() > 1 && lex.charAt(0) == '0') {
									lex = lex.substring(1);
								}

								if(lex.length() > 4) {
									invalidOffset("0x"+lex);
								}

								int iHex = Integer.parseInt(lex, 16);
								if(iHex > 32767) {
									iHex -= 32768*2;
								}
								iDec = iHex;
							} else {
								iDec = tokens[5].toInt();
							}

							if(iDec < -32768 || 32767 < iDec) {
								invalidOffset(""+iDec);
							}

							String iBin = iDec >= 0 ? Integer.toBinaryString(iDec) : Integer.toBinaryString(iDec).substring(16);

							for(int i = iBin.length(); i < 16; i++) {
								iBin = "0" + iBin;
							}

							String instr = "000100" + s + t + iBin;
							int dec = Integer.parseInt(instr, 2);

							output.add(""+dec);
						} else if(tokens[5].kind == Kind.ID) {
							String s = getRegisterAsBinary(tokens[1].lexeme);
							String t = getRegisterAsBinary(tokens[3].lexeme);
							String label = tokens[5].lexeme;

							String instr = ".000100" + s + t + label + "." + pc;

							output.add(instr);
						} else {
							invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
						}
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
					}
				} else if("bne".equals(tokens[0].lexeme)) {
					if(tokens.length != 6) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA || tokens[4].kind != Kind.COMMA) {
						invalidSeparators(tokens[2].lexeme, tokens[4].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[3].kind == Kind.REGISTER) {
						if(tokens[5].kind == Kind.HEXINT || tokens[5].kind == Kind.INT) {
							String s = getRegisterAsBinary(tokens[1].lexeme);
							String t = getRegisterAsBinary(tokens[3].lexeme);

							int iDec = 0;
							if(tokens[5].kind == Kind.HEXINT) {
								String lex = tokens[5].lexeme;
								lex = lex.substring(2);

								while(lex.length() > 1 && lex.charAt(0) == '0') {
									lex = lex.substring(1);
								}

								if(lex.length() > 4) {
									invalidOffset("0x"+lex);
								}

								int iHex = Integer.parseInt(lex, 16);
								if(iHex > 32767) {
									iHex -= 32768*2;
								}
								iDec = iHex;
							} else {
								iDec = tokens[5].toInt();
							}

							if(iDec < -32768 || 32767 < iDec) {
								invalidOffset(""+iDec);
							}

							String iBin = iDec >= 0 ? Integer.toBinaryString(iDec) : Integer.toBinaryString(iDec).substring(16);

							for(int i = iBin.length(); i < 16; i++) {
								iBin = "0" + iBin;
							}

							String instr = "000101" + s + t + iBin;
							int dec = Integer.parseInt(instr, 2);

							output.add(""+dec);
						} else if(tokens[5].kind == Kind.ID) {
							String s = getRegisterAsBinary(tokens[1].lexeme);
							String t = getRegisterAsBinary(tokens[3].lexeme);
							String label = tokens[5].lexeme;

							String instr = ".000101" + s + t + label + "." + pc;

							output.add(instr);
						} else {
							invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
						}
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
					}
				} else if("lis".equals(tokens[0].lexeme)) {
					if(tokens.length != 2) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER) {
						String s = getRegisterAsBinary(tokens[1].lexeme);

						String instr = "0000000000000000" + s + "00000010100";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme);
					}
				} else if("mflo".equals(tokens[0].lexeme)) {
					if(tokens.length != 2) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER) {
						String s = getRegisterAsBinary(tokens[1].lexeme);

						String instr = "0000000000000000" + s + "00000010010";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme);
					}
				} else if("mfhi".equals(tokens[0].lexeme)) {
					if(tokens.length != 2) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER) {
						String s = getRegisterAsBinary(tokens[1].lexeme);

						String instr = "0000000000000000" + s + "00000010000";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme);
					}
				} else if("mult".equals(tokens[0].lexeme)) {
					if(tokens.length != 4) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA) {
						invalidSeparator(tokens[2].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[3].kind == Kind.REGISTER) {
						String s = getRegisterAsBinary(tokens[1].lexeme);
						String t = getRegisterAsBinary(tokens[3].lexeme);

						String instr = "000000" + s + t + "0000000000011000";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme);
					}
				} else if("multu".equals(tokens[0].lexeme)) {
					if(tokens.length != 4) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA) {
						invalidSeparator(tokens[2].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[3].kind == Kind.REGISTER) {
						String s = getRegisterAsBinary(tokens[1].lexeme);
						String t = getRegisterAsBinary(tokens[3].lexeme);

						String instr = "000000" + s + t + "0000000000011001";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme);
					}
				} else if("div".equals(tokens[0].lexeme)) {
					if(tokens.length != 4) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA) {
						invalidSeparator(tokens[2].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[3].kind == Kind.REGISTER) {
						String s = getRegisterAsBinary(tokens[1].lexeme);
						String t = getRegisterAsBinary(tokens[3].lexeme);

						String instr = "000000" + s + t + "0000000000011010";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme);
					}
				} else if("divu".equals(tokens[0].lexeme)) {
					if(tokens.length != 4) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA) {
						invalidSeparator(tokens[2].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[3].kind == Kind.REGISTER) {
						String s = getRegisterAsBinary(tokens[1].lexeme);
						String t = getRegisterAsBinary(tokens[3].lexeme);

						String instr = "000000" + s + t + "0000000000011011";
						int dec = Integer.parseInt(instr, 2);

						output.add(""+dec);
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme);
					}
				} else if("lw".equals(tokens[0].lexeme)) {
					if(tokens.length != 7) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA || tokens[4].kind != Kind.LPAREN || tokens[6].kind != Kind.RPAREN) {
						invalidSeparators(tokens[2].lexeme, tokens[4].lexeme+tokens[5].lexeme+tokens[6].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[5].kind == Kind.REGISTER) {
						if(tokens[3].kind == Kind.HEXINT || tokens[3].kind == Kind.INT) {
							String t = getRegisterAsBinary(tokens[1].lexeme);
							String s = getRegisterAsBinary(tokens[5].lexeme);

							int iDec = 0;
							if(tokens[3].kind == Kind.HEXINT) {
								String lex = tokens[3].lexeme;
								lex = lex.substring(2);

								while(lex.length() > 1 && lex.charAt(0) == '0') {
									lex = lex.substring(1);
								}

								if(lex.length() > 4) {
									invalidOffset("0x"+lex);
								}

								int iHex = Integer.parseInt(lex, 16);
								if(iHex > 32767) {
									iHex -= 32768*2;
								}
								iDec = iHex;
							} else {
								iDec = tokens[3].toInt();
							}

							if(iDec < -32768 || 32767 < iDec) {
								invalidOffset(""+iDec);
							}

							String iBin = iDec >= 0 ? Integer.toBinaryString(iDec) : Integer.toBinaryString(iDec).substring(16);

							for(int i = iBin.length(); i < 16; i++) {
								iBin = "0" + iBin;
							}

							String instr = "100011" + s + t + iBin;
							int dec = new BigInteger(instr, 2).intValue();

							output.add(""+dec);
						} else {
							invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
						}
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
					}
				} else if("sw".equals(tokens[0].lexeme)) {
					if(tokens.length != 7) {
						invalidArgumentNumber(tokens[0].lexeme);
					}

					if(tokens[2].kind != Kind.COMMA || tokens[4].kind != Kind.LPAREN || tokens[6].kind != Kind.RPAREN) {
						invalidSeparators(tokens[2].lexeme, tokens[4].lexeme+tokens[5].lexeme+tokens[6].lexeme);
					}

					if(tokens[1].kind == Kind.REGISTER && tokens[5].kind == Kind.REGISTER) {
						if(tokens[3].kind == Kind.HEXINT || tokens[3].kind == Kind.INT) {
							String t = getRegisterAsBinary(tokens[1].lexeme);
							String s = getRegisterAsBinary(tokens[5].lexeme);

							int iDec = 0;
							if(tokens[3].kind == Kind.HEXINT) {
								String lex = tokens[3].lexeme;
								lex = lex.substring(2);

								while(lex.length() > 1 && lex.charAt(0) == '0') {
									lex = lex.substring(1);
								}

								if(lex.length() > 4) {
									invalidOffset("0x"+lex);
								}

								int iHex = Integer.parseInt(lex, 16);
								if(iHex > 32767) {
									iHex -= 32768*2;
								}
								iDec = iHex;
							} else {
								iDec = tokens[3].toInt();
							}

							if(iDec < -32768 || 32767 < iDec) {
								invalidOffset(""+iDec);
							}

							String iBin = iDec >= 0 ? Integer.toBinaryString(iDec) : Integer.toBinaryString(iDec).substring(16);

							for(int i = iBin.length(); i < 16; i++) {
								iBin = "0" + iBin;
							}

							String instr = "101011" + s + t + iBin;
							int dec = new BigInteger(instr, 2).intValue();

							output.add(""+dec);
						} else {
							invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
						}
					} else {
						invalidArgument(tokens[0].lexeme, tokens[1].lexeme+tokens[2].lexeme+" "+tokens[3].lexeme+tokens[4].lexeme+" "+tokens[5].lexeme);
					}
				} else {
					System.err.println("ERROR: " + tokens[0].lexeme + " is not a supported instruction.");
					System.exit(1);
				}
			} else {
				System.err.println("ERROR: A line cannot begin with a " + tokens[0].kind + ".");
				System.exit(1);
			}

			pc += 4;
		}

		for(String s : output) {
			int dec = 0;

			try {
				dec = Integer.parseInt(s);
			} catch (Exception e) {
				if(s.startsWith(".")) {
					String sPC = s.substring(s.lastIndexOf(".")+1);
					int iPC = Integer.parseInt(sPC);
					s = s.replace("."+sPC,"").replace(".","");
					System.out.println("Stripping label " + s);
					String label = s.replaceAll("\\d+","");

					if(!labels.containsKey(label)) {
						invalidLabel(label);
					}

					int decOffset = ((labels.get(label) - iPC) / 4) - 1;

					if(decOffset < -32768 || 32767 < decOffset) {
						invalidOffset(""+decOffset);
					}

					String binOffset = decOffset >= 0 ? Integer.toBinaryString(decOffset) : Integer.toBinaryString(decOffset).substring(16);

					for(int i = binOffset.length(); i < 16; i++) {
						binOffset = "0" + binOffset;
					}

					String sBin = s.replace(label, binOffset);

					dec = Integer.parseInt(sBin, 2);
				} else {
					if(!labels.containsKey(s)) {
						invalidLabel(s);
					}

					dec = labels.get(s);
				}
			}

			for(int i = 0; i < 4; i++) {
				System.out.write(getByte(dec, i));
			}
		}

		System.out.flush();

		// Print label table to System.err
		/*
		for(Map.Entry<String, Integer> entry : labels.entrySet()) {
			String name = entry.getKey();
			Integer location = entry.getValue();
			System.err.println(name + " " + location);
		}
		*/
	}

	int getByte(int in, int index) {
		String sHex = Integer.toHexString(in);

		for(int i = sHex.length(); i < 8; i++) {
			sHex = "0" + sHex;
		}

		String sByte = sHex.substring(index*2,index*2+2);
		return Integer.parseInt(sByte, 16);
	}

	String getRegisterAsBinary(String register) {
		register = register.substring(1, register.length());		// Remove $ in register lexeme
		int dec = Integer.parseInt(register);

		if(dec < 0 || dec > 31) {
			System.err.println("ERROR: Register is out of bounds ($" + dec + " is not a valid register value).");
			System.exit(1);
		}

		String sBin = Integer.toBinaryString(dec);

		for(int i = sBin.length(); i < 5; i++) {
			sBin = "0" + sBin;
		}

		return sBin;
	}

	void invalidArgumentNumber(String instr) {
		System.err.println("ERROR: " + instr + " instruction has invalid number of arguments.");
		System.exit(1);
	}

	void invalidArgument(String instr, String arg) {
		System.err.println("ERROR: " + instr + " not followed by valid argument (followed by " + arg + ").");
		System.exit(1);
	}

	void invalidOffset(String offset) {
		System.err.println("ERROR: " + offset + " is not a valid offset (out of range).");
		System.exit(1);
	}

	void invalidLabel(String label) {
		System.err.println("ERROR: Tried to reference a non-existent label (" + label + ").");
		System.exit(1);
	}

	void invalidSeparator(String sep) {
		System.err.println("ERROR: Parameters not comma-separated (separated by " + sep + ").");
		System.exit(1);
	}

	void invalidSeparators(String sep1, String sep2) {
		System.err.println("ERROR: Parameters not comma-separated (separated by " + sep1 + " and " + sep2 + ").");
		System.exit(1);
	}
}

/** The various kinds of tokens. */
enum Kind {
	ID,         // Opcode or identifier (use of a label)
	INT,        // Decimal integer
	HEXINT,     // Hexadecimal integer
	REGISTER,   // Register number
	COMMA,      // Comma
	LPAREN,     // (
	RPAREN,     // )
	LABEL,      // Declaration of a label (with a colon)
	DOTWORD,    // .word directive
	WHITESPACE; // Whitespace
}

/** Representation of a token. */
class Token {
	public Kind kind;     // The kind of token.
	public String lexeme; // String representation of the actual token in the
						  // source code.

	public Token(Kind kind, String lexeme) {
		this.kind = kind;
		this.lexeme = lexeme;
	}
	public String toString() {
		return kind+" {"+lexeme+"}";
	}
	/** Returns an integer representation of the token. For tokens of kind
	 * INT (decimal integer constant) and HEXINT (hexadecimal integer
	 * constant), returns the integer constant. For tokens of kind
	 * REGISTER, returns the register number.
	 */
	public int toInt() {
		if(kind == Kind.INT) return parseLiteral(lexeme, 10, 32);
		else if(kind == Kind.HEXINT) return parseLiteral(lexeme.substring(2), 16, 32);
		else if(kind == Kind.REGISTER) return parseLiteral(lexeme.substring(1), 10, 5);
		else {
			System.err.println("ERROR in to-int conversion.");
			System.exit(1);
			return 0;
		}
	}
	private int parseLiteral(String s, int base, int bits) {
		BigInteger x = new BigInteger(s, base);
		if(x.signum() > 0) {
			if(x.bitLength() > bits) {
				System.err.println("ERROR in parsing: constant out of range: "+s);
				System.exit(1);
			}
		} else if(x.signum() < 0) {
			if(x.negate().bitLength() > bits-1
			&& x.negate().subtract(new BigInteger("1")).bitLength() > bits-1) {
				System.err.println("ERROR in parsing: constant out of range: "+s);
				System.exit(1);
			}
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
				new Transition(State.DOT, new Chars("w"), State.DOTW),
				new Transition(State.DOTW, new Chars("o"), State.DOTWO),
				new Transition(State.DOTWO, new Chars("r"), State.DOTWOR),
				new Transition(State.DOTWOR, new Chars("d"), State.DOTWORD),
				new Transition(State.COMMENT, all, State.COMMENT)
		};
	}
	/** Partitions the line passed in as input into an array of tokens.
	 * The array of tokens is returned.
	 */
	public Token[] scan( String input ) {
		List<Token> ret = new ArrayList<Token>();
		if(input.length() == 0) return new Token[0];
		int i = 0;
		int startIndex = 0;
		State state = State.START;
		while(true) {
			Transition t = null;
			if(i < input.length()) t = findTransition(state, input.charAt(i));
			if(t == null) {
				// no more transitions possible
				if(!state.isFinal()) {
					System.err.println("ERROR in lexing after reading "+input.substring(0, i));
					System.exit(1);
				}
				if( state.kind != Kind.WHITESPACE ) {
					ret.add(new Token(state.kind,
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
		return ret.toArray(new Token[ret.size()]);
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
		REGISTER(Kind.REGISTER),
		INT(Kind.INT),
		ID(Kind.ID),
		LABEL(Kind.LABEL),
		COMMA(Kind.COMMA),
		LPAREN(Kind.LPAREN),
		RPAREN(Kind.RPAREN),
		ZERO(Kind.INT),
		ZEROX(null),
		HEXINT(Kind.HEXINT),
		COMMENT(Kind.WHITESPACE),
		DOT(null),
		DOTW(null),
		DOTWO(null),
		DOTWOR(null),
		DOTWORD(Kind.DOTWORD),
		WHITESPACE(Kind.WHITESPACE);
		State(Kind kind) {
			this.kind = kind;
		}
		Kind kind;
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
