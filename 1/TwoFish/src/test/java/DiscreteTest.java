import org.junit.Test;

import java.math.BigInteger;
import java.nio.charset.Charset;

/**
 * @author sala
 */
public class DiscreteTest {
    @Test
    public void testDiscrette() {
        final BigInteger[] pqgyx = DiscreteLogarithm.getPQGYX();
        BigInteger p = pqgyx[0];
        BigInteger q = pqgyx[1];
        BigInteger g = pqgyx[2];
        BigInteger y = pqgyx[3];
        BigInteger x = pqgyx[4];
        System.out.println("p = " + p);
        System.out.println("q = " + q);
        System.out.println("g = " + g);
        System.out.println("y = " + y);
        System.out.println("x = " + x);
        System.out.println("--------------------------------------------");
        //
        assert y.equals(g.modPow(x, p));
        assert x.compareTo(BigInteger.valueOf(2L)) >= 0 && x.compareTo(q.subtract(BigInteger.ONE)) <= 0;
        assert BigInteger.ZERO.equals(p.subtract(BigInteger.ONE).remainder(q));
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
