package elliptic;

import java.math.BigInteger;

/**
 * @author sala
 */
public class EllipticPoint {
    public final BigInteger x;
    public final BigInteger y;

    public EllipticPoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }
}
