import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * @author timur.shakurov
 */
public class GenerateKeysTest {
    @Test
    public void testKeyGenerator() {
        final BigInteger[] pqgyx = DiscreteLogarithm.getPQGYX();
        BigInteger p = pqgyx[0];
        BigInteger q = pqgyx[1];
        BigInteger g = pqgyx[2];
        BigInteger y = pqgyx[3];
        BigInteger x = pqgyx[4];
        Assert.assertEquals(BigInteger.ONE, g.modPow(q, p));
    }
}
