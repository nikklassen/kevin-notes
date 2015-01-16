import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class LR {
	public static final void main(String[] args) {
		new LR().run();
	}

	private List<String> rules;
	private List<String> derivations;
	private List<String> state;
	private List<String> symbol;
	private List<String> input;

	private String start;

	private List<String> rightDeriv;

	private void run() {
		rules = new ArrayList<String>();
		derivations = new ArrayList<String>();
		state = new ArrayList<String>();
		symbol = new ArrayList<String>();
		input = new ArrayList<String>();

		rightDeriv = new ArrayList<String>();
		readInput();

		state.add("0");

		int counter = 0;
		for(String token : input) {
			String derivation = getDerivation(token);
			if("NULL".equals(derivation)) {
				if(counter > 50000)
					counter++;
				System.err.println("ERROR at " + counter);
				System.exit(1);
			}

			if(derivation.contains("shift")) {
				shift(token, derivation.replaceAll(".*shift ",""));
			} else if(derivation.contains("reduce")) {
				reduce(rules.get(Integer.parseInt(derivation.replaceAll(".*reduce ",""))));
				while(true) {
					String newDeriv = getDerivation(token);
					if(newDeriv.contains("reduce")) {
						reduce(rules.get(Integer.parseInt(newDeriv.replaceAll(".*reduce ",""))));
					} else {
						shift(token, newDeriv.replaceAll(".*shift ",""));
						break;
					}
				}
			}
			counter++;
		}
		for(String s : rules) {
			if(s.startsWith(start + " "))
				reduce(s);
		}

		for(String s : rightDeriv) {
			System.out.println(s);
		}
	}

	private String getDerivation(String token) {
		for(String derivation : derivations) {
			if(derivation.startsWith(state.get(state.size()-1) + " " + token))
				return derivation;
		}
		return "NULL";
	}

	private void shift(String newSymbol, String newState) {
		symbol.add(newSymbol);
		state.add(newState);
	}

	private void reduce(String rule) {
		rightDeriv.add(rule);
		String[] ruleArray = rule.split(" ");
		for(int i = 0; i < ruleArray.length - 1; i++) {
			state.remove(state.size()-1);
		}
		for(int i = 0; i < ruleArray.length - 1; i++) {
			symbol.remove(state.size()-1);
		}
		String derivation = getDerivation(ruleArray[0]);
		shift(ruleArray[0], derivation.replaceAll(".*shift ",""));
	}

	private void readInput() {
		Scanner in = new Scanner(System.in);

		int num = Integer.parseInt(in.nextLine());
		for(int i = 0; i < num; i++) {
			in.nextLine();
		}
		num = Integer.parseInt(in.nextLine());
		for(int i = 0; i < num; i++) {
			in.nextLine();
		}
		start = in.nextLine();
		num = Integer.parseInt(in.nextLine());
		for(int i = 0; i < num; i++) {
			rules.add(in.nextLine());
		}
		in.nextLine();
		num = Integer.parseInt(in.nextLine());
		for(int i = 0; i < num; i++) {
			derivations.add(in.nextLine());
		}
		StringBuilder sb = new StringBuilder();
		while(in.hasNextLine()) {
			sb.append(in.nextLine());
			sb.append(" ");
		}
		String[] inArray = sb.toString().replaceAll(" +"," ").split(" ");
		for(String s : inArray) {
			input.add(s);
		}
	}

	private void printState(String in, String token) {
		System.out.println("Action: " + in);
		System.out.println("Input: " + token);
		for(String s : state) {
			System.out.println("State: " + s);
		}
		for(String s : symbol) {
			System.out.println("Symbol: " + s);
		}
	}
}