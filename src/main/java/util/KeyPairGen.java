package util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Bruno on 13/09/2016.
 */
public class KeyPairGen {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public void generateKeyPair() {
        KeyPair keyPair = null;
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512);
            keyPair = keyPairGenerator.generateKeyPair();
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

    public static String encrypt(String string, PrivateKey key) throws Exception {
        byte[] str2Bytes = string.getBytes();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytesFinal = cipher.doFinal(str2Bytes);
        return Base64.encode(bytesFinal);
    }

    public static String decrypt(String string, String key) throws Exception {
        byte[] bytesDecoded = Base64.decode(string);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, str2PubKey(key));
        return new String(cipher.doFinal(bytesDecoded));
    }

    public String getPubKey() {
        String key = Base64.encode(publicKey.getEncoded());
        System.out.println(key);
        return key;
    }

    private static PublicKey str2PubKey(String key) {
        KeyFactory kf = null;
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(key));
        PublicKey pubKey = null;
        try {
            kf = KeyFactory.getInstance("RSA");
            pubKey = kf.generatePublic(spec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return pubKey;
    }

}
