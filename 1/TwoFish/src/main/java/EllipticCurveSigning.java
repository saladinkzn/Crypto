import elliptic.EllipticCurve;
import elliptic.EllipticPoint;

import java.math.BigInteger;
import java.util.Random;

/**
 * @author sala
 */
public class EllipticCurveSigning {
    private static final Random R = new Random();


    public BigInteger[] sign(byte[] hash, BigInteger p, BigInteger q, BigInteger m, EllipticCurve E, BigInteger d, EllipticPoint P) {
        final BigInteger a = new BigInteger(hash);
        BigInteger e = a.mod(q);
//        BigInteger e = new BigInteger("2DFBC1B372D89A1188C09C52E0EEC61FCE52032AB1022E8E67ECE6672B043EE5", 16);
        System.out.println("e = " + e.toString());
        System.out.println("e = " + e.toString(16));
        if(BigInteger.ZERO.equals(e)) {
            e = BigInteger.ONE;
        }
        BigInteger k;
        do {
            k = new BigInteger(q.bitLength(), R);
        } while (k.compareTo(q) >= 0 || k.compareTo(BigInteger.ZERO) <= 0);
//        BigInteger k = new BigInteger("77105C9B20BCD3122823C8CF6FCC7B956DE33814E95B7FE64FED924594DCEAB3", 16);
        System.out.println("k = " + k.toString());
        System.out.println("k = " + k.toString(16));
        //
        EllipticPoint C = E.multiply(k, P);
        System.out.println("x_C = " + C.x);
        System.out.println("x_C = " + C.x.toString(16));
        System.out.println("y_C = " + C.y);
        System.out.println("y_C = " + C.y.toString(16));
        BigInteger r = C.x.mod(q);
        BigInteger s = (r.multiply(d)).add(k.multiply(e)).mod(q);
        if(BigInteger.ZERO.equals(s)) {
            throw new IllegalStateException("TODO : implement cycle");
        }
        return new BigInteger[] {r,s};
    }

    public boolean verify(BigInteger[] sign, byte[] hash, BigInteger p, BigInteger q, EllipticCurve E, EllipticPoint P, EllipticPoint Q) {
        final BigInteger r = sign[0];
        final BigInteger s = sign[1];
        //
        BigInteger h = new BigInteger(hash);
        BigInteger e = h.mod(q);
//        BigInteger e = new BigInteger("2DFBC1B372D89A1188C09C52E0EEC61FCE52032AB1022E8E67ECE6672B043EE5", 16);
        BigInteger v = e.modPow(q.subtract(BigInteger.valueOf(2)), q);
        System.out.println("v = " + v.toString());

        BigInteger z1 = s.multiply(v).mod(q);
        System.out.println("z1 = " + z1);
        BigInteger z2 = q.subtract(r.multiply(v).mod(q)).mod(q);
        System.out.println("z2 = " + z2);
        //
        EllipticPoint C = E.add(E.multiply(z1, P), E.multiply(z2, Q));
        BigInteger R = C.x.mod(q);
        return R.equals(r);
    }
}
