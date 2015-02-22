package Encryption;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.function.Function;

public class DecryptedWriter extends PrintWriter implements AutoCloseable {
	Function<String, String> decryptMethod;

	public DecryptedWriter(OutputStream out, boolean truth) {
		super(out, truth);
		this.decryptMethod = e -> Encryption.superDecrypt(e);
	}

	public DecryptedWriter(OutputStream out, boolean truth,
			Function<String, String> encryptMethod) {
		super(out, truth);
		this.decryptMethod = encryptMethod;
	}

	@Override
	public void print(String x) {
		x = this.decryptMethod.apply(x);
		super.print(x);
	}

}
