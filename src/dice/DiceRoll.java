package dice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class DiceRoll {
	public final static String statroll() {
		String result = "";
		int[] rolls = new int[4];
		Arrays.setAll(rolls, e -> (int) (Math.random() * 6) + 1);
		Arrays.sort(rolls);
		result = "Total=" + (rolls[3] + rolls[2] + rolls[1]) + "; [" + rolls[3]
				+ "] " + "[" + rolls[2] + "] " + "[" + rolls[1] + "] " + "["
				+ rolls[0] + "]";
		return result;
	}

	public static void main(String[] args) {
		System.out.println(roll("roll +1 -2 2d6 d8 1d ", "DM"));

	}
	
	public final static String roll(String banana, String name) {
		return name + ": " + java8roll(banana);
	}
	public final static String roll(String[] banana, String name) {
		return name + ": " + java8roll(String.join(" ", banana));
	}

	private final static String java8roll(String start) {
		start = start.toLowerCase().replace("roll", "").replace("r", "").trim();
		ArrayList<String> banana = new ArrayList<String>();
		Arrays.stream(start.split(" ")).forEach(e -> banana.add(e));
		StringBuilder result = new StringBuilder("");
		AtomicInteger sum = new AtomicInteger(0);
		AtomicInteger adder = new AtomicInteger(0);
		try {
			// flat modifiers
			banana.stream().filter(t -> t.startsWith("+") || t.startsWith("-"))
					.forEach(temp -> adder.addAndGet(Integer.parseInt(temp)));
			sum.addAndGet(adder.get());

			// each dice begats it's own kind
			banana.stream().filter(t -> t.contains("d")).forEach(t -> {
				String[] temp = t.split("d");
				int dnum, dsides;
				if (temp[0] != null && !temp[0].equals(""))
					dnum = Integer.parseInt(temp[0]);
				else
					dnum = 1;

				if (temp.length > 2)
					dsides = Integer.parseInt(temp[1]);
				else
					dsides = 20;
				while (dnum > 0) {
					int roll = (int) Math.floor(Math.random() * dsides + 1);
					sum.addAndGet(roll);
					result.append(roll + "/" + dsides + " ");
					dnum--;
				}

			});

			// formatting
			if (adder.get() > 0)
				return sum + " - " + result + "(+" + adder + ").";
			else if (adder.get() < 0)
				return sum + " - " + result + "(" + adder + ").";
			else
				return sum + " - " + result;

		} catch (Exception e) {
			return " got an Error - " + e.getMessage(); // System.out.println(Encryption.encrypt(Message)e.getMessage());
		}
	}

}
