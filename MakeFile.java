import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MakeFilee {

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	static ArrayList<String> hashes = new ArrayList<>();

	// handles writing to file
	static PrintWriter pw;
	static {
		try {
			pw = new PrintWriter(new FileWriter("pass.sh"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		input();
		for (String result : hashes) {
			// cuts the hash the way the api wants (range is the first 5 characters of the hash)
			// uses grep to find the hash if it's listed in testing_pwnedpasswords.txt i.e. if it's in the HaveIBeenPwned database
			pw.println("curl https://api.pwnedpasswords.com/range/" + result.substring(0,5) + " >> testing_pwnedpasswords.txt\ngrep " + result.substring(5) + " testing_pwnedpasswords.txt >> results_pwnedpasswords.txt");
		}
		
		// deletes garbage file
		pw.println("rm testing_pwnedpasswords.txt");
		pw.close();
	}

	public static void input() throws NoSuchAlgorithmException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Press q to quit anytime\n");
		String originalString = scan.nextLine();

		while (!originalString.equals("q")) {
				final MessageDigest digest = MessageDigest.getInstance("SHA1");
				final byte[] hashbytes = digest.digest(
				originalString.getBytes(StandardCharsets.UTF_8));
				String sha1Hex = bytesToHex(hashbytes);
				System.out.println(sha1Hex);
				hashes.add(sha1Hex);
				originalString = scan.nextLine();
		}
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

}
