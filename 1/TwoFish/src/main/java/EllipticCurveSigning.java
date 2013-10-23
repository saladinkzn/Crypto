import elliptic.EllipticCurve;
import elliptic.EllipticPoint;

import java.math.BigInteger;
import java.util.Random;

/**
 * @author sala
 */
public class EllipticCurveSigning {
    private static final Random R = new Random();


    public static BigInteger[] sign(byte[] hash, BigInteger p, BigInteger q, BigInteger m, EllipticCurve E, BigInteger d, EllipticPoint P) {
        final BigInteger a = new BigInteger(hash);
        BigInteger e = a.mod(q);
        if(BigInteger.ZERO.equals(e)) {
            e = BigInteger.ONE;
        }
        BigInteger k;
        do {
            k = new BigInteger(q.bitLength(), R);
        } while (k.compareTo(q) >= 0 || k.compareTo(BigInteger.ZERO) <= 0);
        //
        EllipticPoint C = E.multiply(k, P);
        BigInteger r = C.x.mod(q);
        BigInteger s = (r.multiply(d)).add(k.multiply(e)).mod(q);
        if(BigInteger.ZERO.equals(s)) {
            throw new IllegalStateException("TODO : implement cycle");
        }
        return new BigInteger[] {r,s};
    }

    public static boolean verify(BigInteger[] sign, byte[] hash, BigInteger p, BigInteger q, EllipticCurve E, EllipticPoint P, EllipticPoint Q) {
        final BigInteger r = sign[0];
        final BigInteger s = sign[1];
        //
        BigInteger h = new BigInteger(hash);
        BigInteger e = h.mod(q);
        BigInteger v = e.modPow(q.subtract(BigInteger.valueOf(2)), q);

        BigInteger z1 = s.multiply(v).mod(q);
        BigInteger z2 = q.subtract(r.multiply(v).mod(q)).mod(q);
        //
        EllipticPoint C = E.add(E.multiply(z1, P), E.multiply(z2, Q));
        BigInteger R = C.x.mod(q);
        return R.equals(r);
    }
}
