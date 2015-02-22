package Encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.function.Function;

public class EncryptedReader extends BufferedReader {
	Function<String, String> encryptIt;

	public EncryptedReader(Reader in) {
		super(in);
		encryptIt = e -> Encryption.superEncrypt(e);

	}

	public EncryptedReader(Reader in, Function<String, String> decryptIt) {
		super(in);
		this.encryptIt = decryptIt;
	}

	@Override
	public String readLine() throws IOException {
		Optional<String> result = Optional.ofNullable(super.readLine());
		return result.map(e->this.encryptIt.apply(e)).orElse(null);
	}
}
