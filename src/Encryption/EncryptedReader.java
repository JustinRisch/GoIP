package Encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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
		String result = super.readLine();
		result = this.encryptIt.apply(result);
		return result;
	}
}
