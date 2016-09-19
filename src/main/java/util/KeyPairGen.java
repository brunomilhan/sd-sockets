package util;

import javax.crypto.Cipher;
import java.security.*;

/**
 * Created by Bruno on 13/09/2016.
 */
public class KeyPairGen {
    /*SecureRandom random = null;
    KeyPairGenerator keyGen = null;*/

    private PublicKey publicKey;
    private PrivateKey privateKey;


    /*public KeyPairGen(){
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public void generateKeyPair(){
        keyGen.initialize(512);
        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
    }*/

    public void generateKeyPair() {
        KeyPair keyPair = null;
        try {
            keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        publicKey = keyPair.getPublic();  // add this
        privateKey = keyPair.getPrivate();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }


    public static byte[] encrypt(byte[] inpBytes, PublicKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }

    public static byte[] decrypt(byte[] inpBytes, PrivateKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }
}
