package org.sakaiproject.util.impl;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.util.api.ValueEncryptionService;

/**
 * This class provides encryption/decryption services. Service is thread safe.
 */
public class ValueEncryptionServiceImpl implements ValueEncryptionService {

    private static Log M_log = LogFactory.getLog(ValueEncryptionServiceImpl.class);

    // The encryption key.
    private String key;

    public void setKey(String key) {
        this.key = key;
    }
    
    public void init(){}

    /* (non-Javadoc)
     * @see org.sakaiproject.util.impl.ValueEncryptionService#encrypt(java.lang.String)
     */
    public String encrypt(String value) {
    	try {
	    	//generate a new random SALT
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[16];
			random.nextBytes(salt);
			//new secret with given key and salt
			SecretKey secret = getSecret(key, salt);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();
			//get IV from cipher parameters
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] ciphertext = cipher.doFinal(value.getBytes());
			//create final array (in bytes) : IV + SALT + TEXT
			byte[] finalCiphertext = new byte[ciphertext.length+2*16];
			System.arraycopy(iv, 0, finalCiphertext, 0, 16);
			System.arraycopy(salt, 0, finalCiphertext, 16, 16);
			System.arraycopy(ciphertext, 0, finalCiphertext, 32, ciphertext.length);
			//encode all bytes in a Base64 string
			return Base64.getEncoder().encodeToString(finalCiphertext);
    	} catch(Exception e){
    		M_log.error("Error while encrypting value " + value + " : " + e);
    		return null;
    	}
    }

    /* (non-Javadoc)
	 * @see org.sakaiproject.util.impl.ValueEncryptionService#decrypt(java.lang.String)
	 */
	public String decrypt(String encrypted) {
    	try {
	    	//decode the whole string -> result : IV + SALT + TEXT
			byte[] finalCipherBytes = Base64.getDecoder().decode(encrypted.getBytes("UTF-8"));
			//0 - 16 : IV
			byte[] ivBytes = new byte[16];
			System.arraycopy(finalCipherBytes, 0, ivBytes, 0, 16);
			//16 - 32 : SALT
			byte[] saltBytes = new byte[16];
			System.arraycopy(finalCipherBytes, 16, saltBytes, 0, 16);
			//32 - TOEND : TEXT
			byte[] ciphertext = new byte[finalCipherBytes.length-2*16];
			System.arraycopy(finalCipherBytes, 32, ciphertext, 0, ciphertext.length);
			//generate secret with same key and salt as in encryption process
			SecretKey secret = getSecret(key, saltBytes);
			// Decrypt the message, given derived key and initialization vector.
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
			String plaintext = new String(cipher.doFinal(ciphertext), "UTF-8");
			return plaintext;
    	} catch(Exception e) {
    		M_log.error("Error while decrypting value " + encrypted + " : " + e);
    		return null;
    	}
    }
    
    private SecretKey getSecret(String strKey, byte[] salt) throws Exception{
		//Derive the key, given base key and salt.
		KeySpec spec = new PBEKeySpec(strKey.toCharArray(), salt, 65536, 256);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		return secret;
	}
}
