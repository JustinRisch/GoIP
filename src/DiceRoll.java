import java.util.Arrays;


public class DiceRoll {
	public static String statroll() {
		String result = "";
		int[] rolls = new int[4];
		for (int i = 0; i < 4; i++)
			rolls[i] = (int) (Math.random() * 6) + 1;
		Arrays.sort(rolls);
		result = "Total=" + (rolls[3] + rolls[2] + rolls[1]) + "; [" + rolls[3]
				+ "] " + "[" + rolls[2] + "] " + "[" + rolls[1] + "] " + "["
				+ rolls[0] + "]\n";
		return result;
	}

	public static String roll(String[] banana) {
		try {
			String results = "";
			int sum = 0;
			int D = 20;
			int dnum = 0;
			Integer adder = 0;
			for (int x = 1; x < banana.length; x++) {
				if (banana[x].contains("+") || banana[x].contains("-")) {
					// continue;
					adder += new Integer(banana[x]);
					continue;
				}
				if (banana[x].contains("d")) {
					if (banana[x].split("d")[0] != null
							&& !banana[x].split("d")[0].equals(""))
						dnum = Integer.parseInt(banana[x].split("d")[0]);
					else
						dnum = 0;
					try {
						D = Integer.parseInt(banana[x].split("d")[1]);
					} catch (Exception e) {
						D = 20;
					}

				} else {
					if (banana[x] != null && !banana[x].equals(""))
						dnum = Integer.parseInt(banana[x]);
					if (banana[x + 1] != null && !banana[x + 1].equals(""))
						D = Integer.parseInt(banana[x + 1]);
					x++;
				}
				for (int i = 0; i < dnum; i++) {
					int roll = (int) Math.floor(Math.random() * D + 1);
					sum += roll;
					results += roll;
					results += "(" + D + ") ";
				}

			}
			if (banana.length < 2) {
				int roll = (int) Math.floor(Math.random() * D + 1);
				sum += roll;
				results += roll;
				results += "[" + D + "] ";
			}
			sum += adder;
			return " rolled a " + sum + "(+" + adder + "): " + results;
		} catch (Exception e) {
			return " got an Error - " + e.getMessage(); // System.out.println(Encryption.encrypt(Message)e.getMessage());
		}
	}
}