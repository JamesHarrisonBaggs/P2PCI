package Server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

public class SecretKey {
	
	protected String theKey = "yx31nyb071dj98s";
	
	protected static SecretKeySpec secretKey;

	protected static byte[] key;
	
	//example from http://aesencryption.net/
	public SecretKey() {
		try {
			key = MessageDigest.getInstance("SHA-1").digest(theKey.getBytes("UTF-8"));
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public SecretKeySpec getKey() {
		return secretKey;
	}
}
