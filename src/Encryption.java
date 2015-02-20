import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Encryption {
	/*
	 * Note: this encryption is *not* meant to uphold to any real scrutiny.
	 * Rather, it's meant to deter passive searching. Most DnD campaigns talk
	 * about doing illicit, graphic, or gore intensive activities. This is meant
	 * to prevent a "passerby" from "overhearing" this *fictional* conversation,
	 * mistaking it for a real threat, and acting on it. Other than that, it was
	 * a fun method to write.
	 */
	private static final char[] alphabet = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, '!',
			'@', '#', '$', '%', '^', '&', '*', '(', ')', 'a', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	public static String superEncrypt(String start){
		start = scramble(start, 7);
		start = keyEncrypt(7, start);
		return start;
	}
	public static String superDecrypt(String start){
		start = keyDecrypt(7, start);
		start = descramble(start,7);
		return start;
	}
	public static String scramble(String start, int count) {
		for (int i = 0; i < count; i++)
			start = scramble(start);
		return start;
	}

	public static String descramble(String start, int count) {
		for (int i = 0; i < count; i++)
			start = descramble(start);
		return start;
	}

	public static String scramble(final String start) {
		StringBuilder result = new StringBuilder();
		char[] temp = start.toCharArray();
		for (int i = 0; i < start.length(); i += 2) {
			result.append(temp[i]);
		}
		for (int i = 1; i < start.length(); i += 2) {
			result.append(temp[i]);
		}
		return result.toString();
	}

	public static String descramble(final String start) {
		StringBuilder result = new StringBuilder();
		char[] decrypter = start.toCharArray();
		
		for (int i = 0; i < ((decrypter.length) / 2); i++) {
			result.append(decrypter[i]);
			if (decrypter.length % 2 == 0) {
				result.append(decrypter[i + (decrypter.length) / 2]);
			} else {
				result.append(decrypter[i + decrypter.length / 2 + 1]);
				if (i == (decrypter.length / 2) - 1)
					result.append(decrypter[(int) Math
							.floor(decrypter.length / 2)]);
			}
		}
		return result.toString();
	}

	public static String keyEncrypt(final String key, final String start) {

		AtomicInteger ai = new AtomicInteger(0);
		char[] keyArray = key.toCharArray();
		if (keyArray.length < 1)
			return start;
		StringBuilder result = new StringBuilder("");
		Arrays.stream(start.split(""))
				.map(e -> e.toCharArray())
				.forEachOrdered(
						e -> {
							char c = (char) (e[0] + Character
									.getNumericValue(keyArray[ai
											.incrementAndGet()
											% keyArray.length]));
							result.append(c);
						});

		return result.toString();
	}

	public static String keyEncrypt(final int key, final String start) {
		AtomicInteger ai = new AtomicInteger(0);
		StringBuilder result = new StringBuilder("");
		Arrays.stream(start.split(""))
				.map(e -> e.toCharArray())
				.forEach(
						e -> {
							char c = (char) (e[0] + Character
									.getNumericValue(ai.incrementAndGet() % key));
							result.append(c);
						});

		return result.toString();
	}

	public static String keyDecrypt(final int key, String start) {
		AtomicInteger ai = new AtomicInteger(0);

		StringBuilder result = new StringBuilder("");
		Arrays.stream(start.split(""))
				.map(e -> e.toCharArray())
				.forEach(
						e -> {
							char c = (char) (e[0] - Character
									.getNumericValue(ai.incrementAndGet() % key));
							result.append(c);
						});
		return result.toString();
	}

	public static String keyDecrypt(String key, String start) {
		AtomicInteger ai = new AtomicInteger(0);
		char[] keyArray = key.toCharArray();
		if (keyArray.length < 1)
			return start;
		StringBuilder result = new StringBuilder("");
		Arrays.stream(start.split(""))
				.map(e -> e.toCharArray())
				.forEach(
						e -> {
							char c = (char) (e[0] - Character
									.getNumericValue(keyArray[ai
											.incrementAndGet()
											% keyArray.length]));
							result.append(c);
						});
		return result.toString();
	}

	// in this version, the key is hidden inside the message itself.
	// Unfortunately, this doubles the length of the message.
	public static String keyEncrypt(String start) {
		StringBuilder result = new StringBuilder("");
		start = start.replace("\n", " ").trim();
		final char[] temp = start.toCharArray();
		for (int i = 0; i < temp.length; i += 2) {
			char key = alphabet[(int) Math.floor(Math.random()
					* alphabet.length)];
			while ((temp[i] + Character.getNumericValue(key)) < 32
					|| (temp[i] + Character.getNumericValue(key)) > 126)
				key = alphabet[(int) Math
						.floor(Math.random() * alphabet.length)];
			result.append(Character.toString((char) (temp[i] + Character
					.getNumericValue(key))));
			result.append(key);
		}
		for (int i = 1; i < temp.length; i += 2) {
			char key = alphabet[(int) Math.floor(Math.random()
					* alphabet.length)];
			while ((temp[i] + Character.getNumericValue(key)) < 32
					|| (temp[i] + Character.getNumericValue(key)) > 125)
				key = alphabet[(int) Math
						.floor(Math.random() * alphabet.length)];
			result.append(Character.toString((char) (temp[i] + Character
					.getNumericValue(key))));
			result.append(key);
		}
		return result.toString();
	}

	public static String keyDecrypt(String start) {
		StringBuilder result = new StringBuilder("");
		char[] decrypter = start.toCharArray();
		if (decrypter.length < 1)
			return start;

		StringBuilder temp = new StringBuilder("");
		for (int i = 0; i < decrypter.length - 1; i += 2) {
			temp.append(Character.toString((char) (decrypter[i] - Character
					.getNumericValue(decrypter[i + 1]))));
		}
		decrypter = temp.toString().toCharArray();

		for (int i = 0; i < ((decrypter.length) / 2); i++) {
			result.append(decrypter[i]);
			if (decrypter.length % 2 == 0) {
				result.append(decrypter[i + (decrypter.length) / 2]);
			} else {
				result.append(decrypter[i + decrypter.length / 2 + 1]);
				if (i == (decrypter.length / 2) - 1)
					result.append(decrypter[(int) Math
							.floor(decrypter.length / 2)]);
			}
		}

		return result.toString();
	}
}
