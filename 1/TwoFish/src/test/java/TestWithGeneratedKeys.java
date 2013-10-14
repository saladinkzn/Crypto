import org.junit.Test;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;

/**
 * @author timur.shakurov
 */
public class TestWithGeneratedKeys {
    @Test
    public void testGenerator() throws NoSuchAlgorithmException {
        // Generate a 1024-bit Digital Signature Algorithm (DSA) key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        DSAPrivateKey privateKey = (DSAPrivateKey) keyPair.getPrivate();
        DSAPublicKey publicKey = (DSAPublicKey) keyPair.getPublic();
 /*
    */
        DSAParams dsaParams = privateKey.getParams();
        BigInteger p = dsaParams.getP();
        BigInteger q = dsaParams.getQ();
        BigInteger g = dsaParams.getG();
        BigInteger x = privateKey.getX();
        BigInteger y = publicKey.getY();
        System.out.println(p);
        System.out.println(q);
        System.out.println(g);
        assert g.modPow(q, p).equals(BigInteger.ONE);
        //
        String message = "Hello world, my name is Timur";
        byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
        final byte[] hash = Hasher.hash(bytes);
        final BigInteger[] sign = DiscreteLogarithm.sign(hash, p, g, q, x, y);
        System.out.println("Signed:");
        System.out.println("r = " + sign[0]);
        System.out.println("s = " + sign[1]);
        //
        System.out.println("Checking sign:");
        final boolean verify = DiscreteLogarithm.verify(hash, sign, p, g, q, y);
        System.out.println("Verified. Result = " + verify);

    }
}
