/**
 * @author sala
 */
public class Program {
    public static void main(String[] args) {
        int[] plainText = new int[] {0x01234567,0x90ABCDEF, 0x01234567,0x09ABCDEF };
        int[] key = new int[] {0x01234567,0x90ABCDEF, 0x01234567,0x09ABCDEF };
        //
        int[] whitened = encrypt(plainText, key);
        System.out.println(whitened);
    }

    private static int[] encrypt(int[] plainText, int[] key) {
        final int[] roundKey01 = roundKeys(key, 0);
        final int[] roundKey23 = roundKeys(key, 1);
        final int[] roundKey45 = roundKeys(key, 2);
        final int[] roundKey67 = roundKeys(key, 3);
        // whitening
        int[] whitened = whitening(plainText, key, roundKey01[0], roundKey01[1], roundKey23[0], roundKey23[1]);
        //
        for(int i = 0; i < 16; i++) {
            whitened = encryptionRound(whitened, key, i);
        }
        whitened = whitening(whitened, key, roundKey45[0], roundKey45[1], roundKey67[0], roundKey67[1]);
        return whitened;
    }

    public static int[] decrypt(int[] cypheredText, int[] key) {
        final int[] roundKey01 = roundKeys(key, 0);
        final int[] roundKey23 = roundKeys(key, 1);
        final int[] roundKey45 = roundKeys(key, 2);
        final int[] roundKey67 = roundKeys(key, 3);
        // whitening
        int[] whitened = whitening(cypheredText, key, roundKey45[0], roundKey45[1], roundKey67[0], roundKey67[1]);
        //
        for(int i = 16; i < 0; i++) {
            whitened = decryptionRound(whitened, key, i);
        }
        whitened = whitening(whitened, key, roundKey01[0], roundKey01[1], roundKey23[0], roundKey23[1]);
        return whitened;

    }

    public static int[] whitening(int[] plainText, int[] key, int k0, int k1, int k2, int k3) {
        if(plainText.length != 4) {
            throw new IllegalArgumentException("plainText cannot have length != 4");
        }
        if(key.length != 4) {
            throw new IllegalArgumentException("key cannot have length != 4");
        }

        return new int[] {
                plainText[0] ^ k0,
                plainText[1] ^ k1,
                plainText[2] ^ k2,
                plainText[3] ^ k3
        };
    }

    public static int[] encryptionRound(int[] input, int[] key, int round) {
        final int[] s = getS(key);
        int p0 = h(input[0], s[0], s[1]);
        int p1 = h(ROL(input[1], 8), s[0], s[1]);
        int[] pPht = pht(p0, p1);
        final int[] roundKeys2r_8_2r_9 = roundKeys(key, round + 4);
        //
        final int f0 = pPht[0] ^ roundKeys2r_8_2r_9[0];
        final int f1 = pPht[1] ^ roundKeys2r_8_2r_9[1];
        //
        int c2 = (f0 ^ input[2]) >>> 1;
        int c3 = (f1 ^ ROL(input[3], 1));
        //
        return new int[] {c2, c3, f0, f1};
    }

    public static int[] decryptionRound(int[] input, int[] key, int round) {
        throw new UnsupportedOperationException();
    }

    private static int ROL(int value, int positions) {
        return value >>> (32 - positions);
    }

    public static int[] pht(int a, int b) {
        int a1 = (int)((long)a +(long)b);
        int b1 = (int)((long)a + 2*(long)b);
        return new int[] {a1, b1};
    }

    public static int h(int input, int l0, int l1) {
        Galua256 galua256 = new Galua256((byte)0b01101001);
        final byte[] input2 = asBytes(input);
        final byte[] input3 = new byte[] { q0(input2[0]), q1(input2[1]), q0(input2[2]), q1(input2[3])};
        final int input4 = fromBytes(input3);
        final int input5 = input4 ^ l1;
        final byte[] input6 = asBytes(input5);
        final byte[] input7 = new byte[] { q0(input6[0]), q0(input6[1]), q1(input6[2]), q1(input6[3])};
        final int input8 = fromBytes(input7);
        final int input9 = input8 ^ l0;
        final byte[] input10 = asBytes(input9);
        final byte[] input11 = new byte[] { q1(input10[0]), q0(input10[1]), q1(input10[2]), q0(input10[3]) };
        final byte[] input12 = multiply(galua256, MDS, input11);
        return fromBytes(input12);
    }

    private static final byte[] t00 = { 0x8, 0x1, 0x7, 0xD, 0x6, 0xF, 0x3, 0x2, 0x0, 0xB, 0x5, 0x9, 0xE, 0xC, 0xA, 0x4};
    private static final byte[] t01 = { 0xE, 0xC, 0xB, 0x8, 0x1, 0x2, 0x3, 0x5, 0xF, 0x4, 0xA, 0x6, 0x7, 0x0, 0x9, 0xD};
    private static final byte[] t02 = { 0xB, 0xA, 0x5, 0xE, 0x6, 0xD, 0x9, 0x0, 0xC, 0x8, 0xF, 0x3, 0x2, 0x4, 0x7, 0x1};
    private static final byte[] t03 = { 0xD, 0x7, 0xF, 0x4, 0x1, 0x2, 0x6, 0xE, 0x9, 0xB, 0x3, 0x0, 0x8, 0x5, 0xC, 0xA};
    //
    private static final byte[] t10 = { 0x2, 0x8, 0xB, 0xD, 0xF, 0x7, 0x6, 0xE, 0x3, 0x1, 0x9, 0x4, 0x0, 0xA, 0xC, 0x5};
    private static final byte[] t11 = { 0x1, 0xE, 0x2, 0xB, 0x4, 0xC, 0x3, 0x7, 0x6, 0xD, 0xA, 0x5, 0xF, 0x9, 0x0, 0x8};
    private static final byte[] t12 = { 0x4, 0xC, 0x7, 0x5, 0x1, 0x6, 0x9, 0xA, 0x0, 0xE, 0xD, 0x8, 0x2, 0xB, 0x3, 0xF};
    private static final byte[] t13 = { 0xB, 0x9, 0x5, 0x1, 0xC, 0x3, 0xD, 0xE, 0x6, 0x4, 0x7, 0xF, 0x2, 0x0, 0x8, 0xA};

    public static final byte[][] RS = new byte[][] {
            new byte[] { 0x01, 0x04, 0x55, (byte)0x87, 0x5A, 0x58, (byte)0xDB, (byte)0x9E},
            new byte[] { (byte)0xA4, 0x56, (byte)0x82, (byte)0xF3, 0x1E, (byte)0xC6, (byte)0x68, (byte)0xE5},
            new byte[] { 0x02, (byte)0xA1, (byte)0xFC, (byte)0xC1, 0x47, (byte)0xAE, (byte)0x3D, (byte)0x19},
            new byte[] { (byte)0xA4, 0x55, (byte)0x87, (byte)0x5A, 0x58, (byte)0xDB, (byte)0x9E, 0x03}
    };

    public static final byte[][] MDS = new byte[][] {
            new byte[] { 0x01, (byte)0xEF, 0x5B, 0x5B},
            new byte[] { 0x5B, (byte)0xEF, (byte)0xEF, 0x01},
            new byte[] { (byte)0xEF, 0x5B, 0x01, (byte)0xEF},
            new byte[] { (byte)0xEF, 0x01, (byte)0xEF, 0x5B},
    };

    public static byte q0(byte input) {
        byte a0 = (byte)(input >> 4);
        byte b0 = (byte)(input & 0xF);
        byte a1 = (byte)(a0 ^ b0);
        byte b1 = (byte)(a0 ^ (b0 >>> 4) ^ 8*(a0 & 0xF));
        byte a2 = t00[a1];
        byte b2 = t01[b1];
        byte a3 = (byte)(a2 ^ b2);
        byte b3 = (byte)(a2 ^ (b2 >>> 4) ^ 8*(a2 & 0xF));
        byte a4 = t02[a3];
        byte b4 = t03[b3];
        return (byte)(16*b4+a4);
    }

    public static byte q1(byte input) {
        byte a0 = (byte)(input >> 4);
        byte b0 = (byte)(input & 0xF);
        byte a1 = (byte)(a0 ^ b0);
        byte b1 = (byte)(a0 ^ (b0 >>> 4) ^ 8*(a0 & 0xF));
        byte a2 = t10[a1];
        byte b2 = t11[b1];
        byte a3 = (byte)(a2 ^ b2);
        byte b3 = (byte)(a2 ^ (b2 >>> 4) ^ 8*(a2 & 0xF));
        byte a4 = t12[a3];
        byte b4 = t13[b3];
        return (byte)(16*b4+a4);
    }

    public static int[] getS(int[] key) {
        final int m0 = key[0];
        final int m1 = key[1];
        final int m2 = key[2];
        final int m3 = key[3];
        final int S0 = RS(m0, m1);
        final int S1 = RS(m2, m3);
        return new int[] {S0, S1};
    }

    private static int RS(int X, int Y) {
        byte[] x = asBytes(X);
        byte[] y = asBytes(Y);
        byte[] XY = new byte[8];
        // Merging x and y
        System.arraycopy(x, 0, XY, 0, 4);
        System.arraycopy(y, 0, XY, 4, 4);
        //
        final byte[][] matrix = RS;
        Galua256 galua = new Galua256((byte)0b01001101);
        //
        byte[] S = multiply(galua, matrix, XY);
        int S0 = 0;
        for(int i = 0; i < 4; i++) { S0 |= (S[i] << i * 8); }
        return S0;
    }

    private static byte[] multiply(Galua256 galua, byte[][] matrix, byte[] vector) {
        byte[] S = new byte[vector.length];
        for(int i = 0; i < matrix.length; i++) {
            final byte[] RSrow = matrix[i];
            S[i] = galua.multiply(RSrow[0], vector[0]);
            for(int j = 1; j < RSrow.length; i++) {
                S[i] = galua.add(S[i], galua.multiply(RSrow[j], vector[j]));
            }
        }
        return S;
    }

    public static int[] roundKeys(int[] key, int round) {
        final int m0 = key[0];
        final int m1 = key[1];
        final int m2 = key[2];
        final int m3 = key[3];
        //
        final int[] Me = new int[] { m0, m2};
        final int[] Mo = new int[] { m1, m3};
        //
        final int rho = 1 << 24 | 1 << 16 | 1 << 8 | 1;
        final int Ai = h(2 * round * rho, Me[0], Me[1]);
        final int Bi = ROL(h((2 * round + 1) * rho, Mo[0], Mo[1]), 8);
        final int K2i = (int)((long)Ai + (long)Bi);
        final int K2i_1 = ROL((int) ((long) Ai + 2 * (long) Bi), 9);
        return new int[] { K2i, K2i_1};
    }

    public static byte[] asBytes(int intValue) {
        return new byte[] {
                (byte)(intValue >> 24),
                (byte)(intValue >> 16),
                (byte)(intValue >> 8),
                (byte)(intValue)
        };
    }

    public static int fromBytes(byte[] bytes) {
        int S0 = 0;
        for(int i = 0; i < 4; i++) {
            S0 |= (bytes[i] << i * 8);
        }
        return S0;
    }
}
