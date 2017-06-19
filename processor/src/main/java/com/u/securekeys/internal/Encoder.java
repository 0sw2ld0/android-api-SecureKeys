package com.u.securekeys.internal;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * Class to encode strings with a given key
 * Created by saguilera on 3/3/17.
 */
public class Encoder {

    private static String hash = "SHA-256";

    //Charset
    private static String utf8 = "UTF-8";

    //Radix for the hash
    private static final int STRING_RADIX_REPRESENTATION = 16;

    // Initial vector for AES cipher
    private static final byte initialVectorBytes[] = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04,
        0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

    // Key used for AES cypher
    private static final byte keyBytes[] = new byte[] { 0x60, 0x3d, (byte) 0xeb,
        0x10, 0x15, (byte) 0xca, 0x71, (byte) 0xbe, 0x2b, 0x73,
        (byte) 0xae, (byte) 0xf0, (byte) 0x85, 0x7d, 0x77, (byte) 0x81,
        0x1f, 0x35, 0x2c, 0x07, 0x3b, 0x61, 0x08, (byte) 0xd7, 0x2d,
        (byte) 0x98, 0x10, (byte) 0xa3, 0x09, 0x14, (byte) 0xdf,
        (byte) 0xf4 };

    public Encoder() {}

    /**
     * hash a string using hash mode.
     * @param name string to hash
     * @return string with the hashed name
     */
    private static String hash(String name) {
        try {
            MessageDigest m = MessageDigest.getInstance(hash);
            m.update(name.getBytes(Charset.forName(utf8)));
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            return bigInt.toString(STRING_RADIX_REPRESENTATION);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String encode(String what) {
        try {
            return DatatypeConverter.printBase64Binary(aes(what.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldnt encode value: " + what, e);
        }
    }

    byte[] aes(byte[] content) {
        try {
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final IvParameterSpec iv = new IvParameterSpec(initialVectorBytes);

            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return cipher.doFinal(content);
        } catch (InvalidKeyException e) {
            System.out.println("Please install JCE's Unlimited Strength Policies for next compilation");
            if (Restrictions.remove())
                return aes(content);
            else throw new RuntimeException("No JCE's policies installed + couldnt bypass them", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unknown exception while trying to encript with aes", e);
        }
    }

}
