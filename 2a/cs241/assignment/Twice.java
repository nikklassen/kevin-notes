import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


public class Twice {
	public static void main (String[] args) {
		List<String> list = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String input;

			while ((input = br.readLine()) != null) {
				list.add(input);
				System.out.println(input);
			}

			for (String s : list) {
				System.out.println(s);
			}
		} catch (IOException ioe) {
			System.out.println("ERROR");
		}
	}
}