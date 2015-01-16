import java.util.Scanner;

public class Galaxy {
	public static final void main(String[] args) {
		new Galaxy().run();
	}

	private void run() {
		Scanner in = new Scanner(System.in);

		// Skip grammar
		for(int i = 0; i < 19; i++) {
			in.nextLine();
		}

		String expression = "S BOF expr EOF";
		while(in.hasNextLine()) {
			String line = in.nextLine();

			int leading = 0;
			while(line.charAt(0) == ' ') {
				line = line.substring(1);
				leading++;
			}

			String arr[] = line.split(" ", 2);
			String from = arr[0];
			String to = arr[1];

			expression = expression.replaceFirst(from, to);
		}

		expression = expression.substring(6, expression.length() - 4);

		int value = getValue(expression.replace(" ",""));
		System.out.println(value);
	}

	private int getValue(String expr) {
		int value = 0;
		boolean add = true;
		while(true) {
			if(expr.length() <= 0)
				break;
			else {
				switch(expr.charAt(0)) {
					case '(':
						int index = 0;
						for(int i = 0; i < expr.length(); i++) {
							if(expr.charAt(i) == '(')
								index++;
							if(expr.charAt(i) == ')')
								index--;
							if(index == 0) {
								index = i;
								break;
							}
						}
						value = add ? value + getValue(expr.substring(1, index)) : value - getValue(expr.substring(1, index));
						add = true;
						expr = expr.substring(index+1);
						break;
					case 'i':
						value = add ? value + 42 : value - 42;
						add = true;
						expr = expr.substring(2);
						break;
					case '-':
						add = false;
						expr = expr.substring(1);
						break;
				}
			}
		}
		return value;
	}
}