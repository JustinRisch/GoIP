package Encryption;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.Function;

public class DecryptedWriter extends PrintWriter implements AutoCloseable {
	Function<String, String> decryptMethod;

	public DecryptedWriter(OutputStream out, boolean truth) {
		super(out, truth);
		this.decryptMethod = Encryption::superDecrypt;
	}

	public DecryptedWriter(OutputStream out, boolean truth,
			Function<String, String> encryptMethod) {
		super(out, truth);
		this.decryptMethod = encryptMethod;
	}

	@Override
	public void print(String x) {
		Optional<String> y = Optional.ofNullable(x);
		y.map(decryptMethod::apply).ifPresent(super::print);
	}

}
