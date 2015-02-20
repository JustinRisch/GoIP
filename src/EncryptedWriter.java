import java.io.OutputStream;
import java.io.PrintWriter;

public class EncryptedWriter extends PrintWriter {

	public EncryptedWriter(OutputStream out, boolean truth) {
		super(out, truth);
	}

	@Override
	public void print(Object obj) {
		print(obj.toString());
	}

	@Override
	public void println(Object obj) {
		print(obj.toString());
	}

	@Override
	public void println(String x) {
		super.println(Encryption.superEncrypt(x));
		System.out.println(x);
		System.out.println(Encryption.superEncrypt(x));
	}

	@Override
	public void print(String s) {
		super.print(Encryption.superEncrypt(s));
		System.out.println(s);
		System.out.println(Encryption.superEncrypt(s));
	}

}
