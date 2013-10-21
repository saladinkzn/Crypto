import elliptic.EllipticCurve;
import elliptic.EllipticPoint;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * @author sala
 */
public class EllipticCurveSigningTest {
    @Test
    public void testSigning() throws UnsupportedEncodingException {
        BigInteger p = new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564821041", 10);
        BigInteger a = BigInteger.valueOf(7);
        BigInteger b = new BigInteger("5FBFF498AA938CE739B8E022FBAFEF40563F6E6A3472FC2A514C0CE9DAE23B7E", 16);
        BigInteger m = new BigInteger("8000000000000000000000000000000150FE8A1892976154C59CFC193ACCF5B3", 16);
        BigInteger q = new BigInteger("8000000000000000000000000000000150FE8A1892976154C59CFC193ACCF5B3", 16);
        BigInteger xP = BigInteger.valueOf(2);
        BigInteger yP = new BigInteger("8E2A8A0E65147D4BD6316030E16D19C85C97F0A9CA267122B96ABBCEA7E8FC8", 16);
        BigInteger d = new BigInteger("7A929ADE789BB9BE10ED359DD39A72C11B60961F49397EEE1D19CE9891EC3B28", 16);
        //
        BigInteger xQ = new BigInteger("7F2B49E270DB6D90D8595BEC458B50C58585BA1D4E9B788F6689DbD8E56FD80B", 16);
        BigInteger yQ = new BigInteger("26F1B489D6701DD185C8413A977B3CBBAF64D1C593D26627DFFB101A87FF77DA", 16);
        //
        EllipticCurveSigning signing = new EllipticCurveSigning();
        String hello = "Hello, world";
        final byte[] hash = Hasher.hash(hello.getBytes("UTF-8"));
        EllipticCurve E = new EllipticCurve(a, b, p);
        final EllipticPoint P = new EllipticPoint(xP, yP);
        final BigInteger[] sign = signing.sign(hash, p, q, m, E, d, P);
        System.out.println("r = " + sign[0].toString());
        System.out.println("r = " + sign[0].toString(16));
        System.out.println("s = " + sign[1].toString());
        System.out.println("s = " + sign[1].toString(16));
        //
        EllipticPoint Q = new EllipticPoint(xQ, yQ);
        final boolean verify = signing.verify(sign, hash, p, q, E, P, Q);
        System.out.println(verify);
    }
}
