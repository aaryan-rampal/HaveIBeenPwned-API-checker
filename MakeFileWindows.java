import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MakeFile {

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	static ArrayList<String> hashes = new ArrayList<>();
	static ArrayList<String> words = new ArrayList<>();

	// handles writing to file
	static PrintWriter pw;
	static {
		try {
			pw = new PrintWriter(new FileWriter("pass.bat"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		input();
		for (String result : hashes) {
			// cuts the hash the way the api wants (range is the first 5 characters of the hash)
			// uses grep to find the hash if it's listed in testing_pwnedpasswords.txt i.e. if it's in the HaveIBeenPwned database
			pw.println("curl https://api.pwnedpasswords.com/range/" + result.substring(0,5) + "| findstr " + result.substring(5) + " >> results_pwnedpasswords.txt");
		}
		
		pw.println("del pass.bat");
		pw.close();
	}

	public static void input() throws NoSuchAlgorithmException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Press q to quit anytime");
		String originalString = scan.nextLine();

		while (!originalString.equals("q")) {
			// adds input so it can be arranged later
			words.add(originalString);

			final MessageDigest digest = MessageDigest.getInstance("SHA1");
			final byte[] hashbytes = digest.digest(
			originalString.getBytes(StandardCharsets.UTF_8));
			String sha1Hex = bytesToHex(hashbytes);

			// you can uncomment the line, however, I feel like it clutters up the screen too much and is of no use as this program's functionality is not to be a SHA-1 hash generator
			//System.out.println(sha1Hex);
			hashes.add(sha1Hex);
			originalString = scan.nextLine();
		}

		// prints out the hash without the first 5 characters which is how the HaveIBeenPwned API deals with the hashes
		System.out.println("\nWhat to look for:");
		for (int i = 0; i < words.size(); i++) {
			System.out.println(words.get(i) + ": " + hashes.get(i).substring(5));
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
