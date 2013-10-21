package elliptic;

import java.math.BigInteger;

/**
 * @author sala
 */
public class EllipticCurve {
    public final BigInteger a;
    public final BigInteger b;
    public final BigInteger p;

    public EllipticCurve(BigInteger a, BigInteger b, BigInteger p) {
        this.a = a;
        this.b = b;
        this.p = p;
    }

    public EllipticPoint add(EllipticPoint q1, EllipticPoint q2) {
        final BigInteger x1 = q1.x;
        final BigInteger y1 = q1.y;
        final BigInteger x2 = q2.x;
        final BigInteger y2 = q2.y;
        //
        final BigInteger x3;
        final BigInteger y3;
        if(!x1.equals(x2)) {
            BigInteger lambda = y2.subtract(y1).multiply(x2.subtract(x1).modPow(p.subtract(BigInteger.valueOf(2)), p)).mod(p);
            x3 = lambda.modPow(BigInteger.valueOf(2), p).subtract(x1).subtract(x2).mod(p);
            y3 = lambda.multiply(x1.subtract(x3)).subtract(y1).mod(p);
        } else {
            BigInteger lambda = x1.modPow(BigInteger.valueOf(2L), p).multiply(BigInteger.valueOf(3)).add(a).multiply(y1.multiply(BigInteger.valueOf(2)).modPow(p.subtract(BigInteger.valueOf(2L)), p)).mod(p);
            x3 = lambda.modPow(BigInteger.valueOf(2), p).subtract(x1.multiply(BigInteger.valueOf(2))).mod(p);
            y3 = lambda.multiply(x1.subtract(x3)).subtract(y1).mod(p);
        }
        return new EllipticPoint(x3, y3);
    }

    public EllipticPoint multiply(BigInteger k, EllipticPoint P) {
        if(k.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("k cannot be negative");
        }
        EllipticPoint sum = P;
        k = k.subtract(BigInteger.ONE);
        while (k.compareTo(BigInteger.ZERO) > 0) {
            if(k.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
                k = k.divide(BigInteger.valueOf(2));
                P = add(P, P);
            } else {
                k = k.subtract(BigInteger.ONE);
                sum = add(sum, P);
            }
        }
        return sum;
    }


}
