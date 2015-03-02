package Encryption;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.Function;

public final class DecryptedWriter extends PrintWriter implements AutoCloseable {
	private Function<String, String> decryptMethod;

	
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
		//accept a string
		Optional<String> y = Optional.ofNullable(x);
		//if present, decrypt it. Otherwise set it back to null. 
		x =y.map(decryptMethod::apply).orElse(null);
		
		super.print(x);
	}

}
