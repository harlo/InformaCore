package org.witness.informacam.crypto;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.App.Crypto;

import android.util.Base64;
import android.util.Log;

public class AesUtility {
	public final static String LOG = Crypto.LOG;
	
	public static String DecryptWithPassword(String password, byte[] iv, byte[] message) {
		return DecryptWithPassword(password, iv, message, true);
	}
	
	public static String DecryptWithPassword(String password, byte[] iv, byte[] message, boolean isBase64) {
		String new_message = null;
		
		if(isBase64) {
			iv = Base64.decode(iv, Base64.DEFAULT);
			message = Base64.decode(message, Base64.DEFAULT);
		}
		
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), Crypto.PASSWORD_SALT, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret_key = new SecretKeySpec(tmp.getEncoded(), "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret_key, new IvParameterSpec(iv));
			
			new_message = new String(cipher.doFinal(message));
			
		} catch (IllegalBlockSizeException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (BadPaddingException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		}
		
		return new_message;
	}
	
	public static String EncryptWithPassword(String password, String message) {		
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), Crypto.PASSWORD_SALT, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret_key = new SecretKeySpec(tmp.getEncoded(), "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret_key);
			
			AlgorithmParameters params = cipher.getParameters();
			String iv = Base64.encodeToString(params.getParameterSpec(IvParameterSpec.class).getIV(), Base64.DEFAULT);
			String new_message = Base64.encodeToString(cipher.doFinal(message.getBytes("UTF-8")), Base64.DEFAULT);
			
			JSONObject pack = new JSONObject();
			pack.put(Codes.Keys.IV, iv);
			pack.put(Codes.Keys.VALUE, new_message);
			
			return pack.toString();
			
		} catch (IllegalBlockSizeException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (BadPaddingException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (InvalidParameterSpecException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		}
		
		
		return null;
	}
}
