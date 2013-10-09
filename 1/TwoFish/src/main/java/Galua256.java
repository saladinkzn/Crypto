/**
 * @author sala
 */
public class Galua256 {
    private final byte mask;
    public Galua256(byte mask) {
        this.mask = mask;
    }

    public byte add(byte a, byte b) {
        return (byte)(a ^ b);
    }

    public byte add(byte a, byte... b) {
        byte sum = a;
        for (byte aB : b) {
            sum = add(sum, aB);
        }
        return sum;
    }

    public byte multiply(byte a, byte b) {
        int p = 0;

        for(int i = 0; i < 8; i++) {
            if((b & 1) == 1) {
                p ^= a;
            }
            byte carry = (byte)(a & 0x80);
            a <<= 1;
            if(carry != 0) {
                a ^= mask;
            }
            b >>= 1;
        }
        return (byte)p;
    }
}