import java.math.BigInteger;
import java.util.Random;

/**
 * @author sala
 */
public class DiscreteLogarithm {
    private static final Random R = new Random();
    public static final int BIT_LENGTH = 128;

    public static BigInteger[] getPQGYX() {
        BigInteger q = BigInteger.probablePrime(BIT_LENGTH, R);
        final BigInteger multiply = q.multiply(new BigInteger(512 - BIT_LENGTH, R));
        BigInteger p = multiply.add(BigInteger.ONE);
        do {
            p = p.add(q);
        } while (!p.isProbablePrime(20) || !(p.subtract(BigInteger.ONE)).remainder(q).equals(BigInteger.ZERO));
        BigInteger phi;
        do {
            phi = new BigInteger(p.bitLength(), R);
        } while (phi.compareTo(p) >= 0);
        //
        assert p.subtract(BigInteger.ONE).mod(q).equals(BigInteger.ZERO);
        //
        BigInteger g = phi.modPow(p.subtract(BigInteger.ONE).divide(q), p);
        BigInteger x;
        do {
            x = new BigInteger(q.bitLength(), R);
        } while (x.compareTo(q.subtract(BigInteger.ONE)) > 0 || x.equals(BigInteger.ZERO) || x.equals(BigInteger.ONE));
        BigInteger y = g.modPow(x, p);
        System.out.println("p = " + p);
        System.out.println("q = " + q);
        System.out.println("g = " + g);
        System.out.println("y = " + y);
        System.out.println("x = " + x);
        System.out.println("--------------------------------------------");
        return new BigInteger[] { p,q,g,y,x};
    }

    public static BigInteger[] sign(byte[] hash, BigInteger p, BigInteger g, BigInteger q, BigInteger x, BigInteger y) {
        assert y.equals(g.modPow(x, p));
        //
        BigInteger h = new BigInteger(hash);
        System.out.println("h.bigLength() " + h.bitLength());
        //
        BigInteger k;
        do {
            k = new BigInteger(q.bitLength(), R);
        // k >=q
        } while (k.compareTo(q) >= 0 || k.compareTo(BigInteger.ZERO) <= 0);
        System.out.println("k = " + k);
        System.out.println();
        final BigInteger r = g.modPow(k, p).mod(q);
        final BigInteger rho = r.mod(q);
        final BigInteger s = k.subtract(rho.multiply(h).multiply(x)).mod(q);
        //
        return new BigInteger[] {r, s};
    }

    public static boolean verify(byte[] hash, BigInteger[] sign, BigInteger p, BigInteger g, BigInteger q, BigInteger y) {
        BigInteger h = new BigInteger(hash);
        //
        BigInteger r = sign[0];
        BigInteger s = sign[1];
        BigInteger rho = r.mod(q);
        System.out.println(r);
        final BigInteger test = g.modPow(s, p).multiply(y.modPow(rho.multiply(h), p)).mod(p);
        System.out.println(test.mod(q));
        return r.mod(q).equals(test.mod(q));
    }
}
