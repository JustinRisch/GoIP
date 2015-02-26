package Encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.function.Function;

public class EncryptedReader extends BufferedReader {
	private Function<String, String> encryptIt;

	public EncryptedReader(Reader in) {
		super(in);
		encryptIt = Encryption::superEncrypt;
	}

	public EncryptedReader(Reader in, Function<String, String> decryptIt) {
		super(in);
		this.encryptIt = decryptIt;
	}

	@Override
	public String readLine() throws IOException {
		// read the input
		Optional<String> result = Optional.ofNullable(super.readLine());
		// if it wasn't null, encrypt it.
		return result.map(e->e.replace("\n", "")).map(encryptIt::apply).orElse(null);
	}
}
