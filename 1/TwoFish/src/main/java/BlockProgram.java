import elliptic.EllipticCurve;
import elliptic.EllipticPoint;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sala
 */
public class BlockProgram {
    // ELLIPTIC CURVE PARAMS:
    final static BigInteger p = new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564821041", 10);
    final static BigInteger a = BigInteger.valueOf(7);
    final static BigInteger b = new BigInteger("5FBFF498AA938CE739B8E022FBAFEF40563F6E6A3472FC2A514C0CE9DAE23B7E", 16);
    final static BigInteger m = new BigInteger("8000000000000000000000000000000150FE8A1892976154C59CFC193ACCF5B3", 16);
    final static BigInteger q = new BigInteger("8000000000000000000000000000000150FE8A1892976154C59CFC193ACCF5B3", 16);
    final static BigInteger xP = BigInteger.valueOf(2);
    final static BigInteger yP = new BigInteger("8E2A8A0E65147D4BD6316030E16D19C85C97F0A9CA267122B96ABBCEA7E8FC8", 16);
    final static BigInteger d = new BigInteger("7A929ADE789BB9BE10ED359DD39A72C11B60961F49397EEE1D19CE9891EC3B28", 16);
    //
    final static BigInteger xQ = new BigInteger("7F2B49E270DB6D90D8595BEC458B50C58585BA1D4E9B788F6689DbD8E56FD80B", 16);
    final static BigInteger yQ = new BigInteger("26F1B489D6701DD185C8413A977B3CBBAF64D1C593D26627DFFB101A87FF77DA", 16);

    public static final int BLOCK_SIZE = 16;

    public static void main(String[] args) {
        final String plainText = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        System.out.println("Input:");
        System.out.println(plainText);
        final byte[] message = plainText.getBytes(Charset.forName("UTF-8"));
        //
        System.out.println("Input: ");
        printLineByteArray(message);
        //
        final byte[] extended = new byte[16*((message.length + 1) / 16 + 1)];
        System.arraycopy(message, 0, extended, 0, message.length);
        //
        extended[message.length] = (byte)0xFF;
        //
        System.out.println("Extended input:");
        printLineByteArray(extended);
        //
        final byte[] hash = Hasher.hash(extended);
        System.out.println("Hash: ");
        printLineByteArray(hash);
        //
        final BigInteger[] pqgyx = DiscreteLogarithm.getPQGYX();
        final BigInteger[] sign = DiscreteLogarithm.sign(hash, pqgyx[0], pqgyx[2], pqgyx[1], pqgyx[4], pqgyx[3]);
        final byte[] rTemp = sign[0].toByteArray();
        final byte[] sTemp = sign[1].toByteArray();
        //
        final byte[] rArr = rTemp[0] == 0 ? Arrays.copyOfRange(rTemp, 1, rTemp.length + 1) : rTemp;
        final byte[] sArr = sTemp[0] == 0 ? Arrays.copyOfRange(sTemp, 1, sTemp.length + 1) : sTemp;
        //
        System.out.println("Discrete sign: ("+sign[0]+", "+sign[1]+")");
        //
        EllipticCurve E = new EllipticCurve(a, b, p);
        final EllipticPoint P = new EllipticPoint(xP, yP);
        final BigInteger[] sign2 = EllipticCurveSigning.sign(hash, p, q, m, E, d, P);
        final byte[] rArray2 = sign2[0].toByteArray();
        final byte[] sArray2 = sign2[1].toByteArray();
        System.out.println("Elliptic sign: ("+sign2[0]+", "+sign2[1]+")");
        //
        final byte[] extended2 = new byte[extended.length + rArr.length + sArr.length + rArray2.length + sArray2.length];
        {
            int offset = 0;
            for(byte[] part : new byte[][] { extended, rArr, sArr, rArray2, sArray2}) {
                System.arraycopy(part, 0, extended2, offset, part.length);
                offset += part.length;
            }
        }
        System.out.println("Sent:");
        printLineByteArray(extended2);
        //
        final List<int[]> blocksList = new ArrayList<>();
        for(int offset = 0; offset < extended2.length; offset+= BLOCK_SIZE) {
            int[] block = Hasher.convertInput(extended2, offset);
            blocksList.add(block);
        }
        final int[][] blocks = blocksList.toArray(new int[blocksList.size()][]);
        final int[] initialVector = new int[] { 0, 0,0,0};
        final int[] key = new int[] {0x03020100, 0x07060504, 0x0B0A0908, 0x0F0E0D0C};
        TwoFishBlockChainingMode twoFishBlockChainingMode = new TwoFishBlockChainingMode();
        final int[][] encrypt = twoFishBlockChainingMode.encrypt(blocks, key, initialVector);
        //
        System.out.println("Encrypted:");
        printLineByteArray(convertBlocks(encrypt));
        //
        final int[][] decrypt = twoFishBlockChainingMode.decrypt(encrypt, key, initialVector);
        byte[] receivedBytes = convertBlocks(decrypt);
        System.out.println("Decrypted:");
        printLineByteArray(receivedBytes);
        //
        int from = receivedBytes.length - 32;
        byte[] receivedSArray2 = Arrays.copyOfRange(receivedBytes, from, from + 32);

        from -= 32;
        byte[] receivedRArray2 = Arrays.copyOfRange(receivedBytes, from, from + 32);
        from -= 16;
        byte[] receivedSArray1 = Arrays.copyOfRange(receivedBytes, from, from + 16);
        from -= 32;
        byte[] receivedRArray1 = Arrays.copyOfRange(receivedBytes, from, from + 32);
        //
        final BigInteger r1 = new BigInteger(receivedRArray1);
        final BigInteger s1 = new BigInteger(receivedSArray1);
        //
        final BigInteger r2 = new BigInteger(receivedRArray2);
        final BigInteger s2 = new BigInteger(receivedSArray2);
        //
        byte[] receivedMessage = Arrays.copyOfRange(receivedBytes, 0, from);
        final byte[] hashOfReceived = Hasher.hash(receivedMessage);
        System.out.println("Hash:");
        printLineByteArray(hashOfReceived);
        System.out.println("Verifying discrete sign: ");
        System.out.println(DiscreteLogarithm.verify(hashOfReceived, new BigInteger[]{r1, s1}, pqgyx[0], pqgyx[2], pqgyx[1], pqgyx[3]));
        System.out.println("Verifying elliptic sign: ");
        System.out.println(EllipticCurveSigning.verify(new BigInteger[]{r2, s2}, hashOfReceived, p, q, E, P, new EllipticPoint(xQ, yQ)));
        //
        Integer lastIndexOf = null;
        for(int i = receivedMessage.length - 1; i>=0;i--) {
            if(receivedMessage[i] == (byte)0xFF) {
                lastIndexOf = i;
            }
        }
        if(lastIndexOf != null) {
            System.out.println("Printing out the text:");
            System.out.println(new String(Arrays.copyOfRange(receivedMessage, 0, lastIndexOf), Charset.forName("UTF-8")));
        }

    }

    private static void printLineByteArray(byte[] receivedBytes) {
        for(byte b1 : receivedBytes) {
            System.out.print(String.format("%02X", b1));
        }
        System.out.println();
    }

    private static byte[] convertBlocks(int[][] receivedBlocks) {
        byte[] receivedBytes = new byte[receivedBlocks.length * 4 * 4];
        for(int i = 0; i < receivedBlocks.length; i++) {
            int[] receivedBlock = receivedBlocks[i];
            for(int j = 0; j < receivedBlock.length; j++) {
                int receivedBlockWord = receivedBlock[j];
                for(int k = 0; k < 4; k++) {
                    receivedBytes[i*16 + j * 4 + k] = (byte)(receivedBlockWord >>> (8*k));
                }

            }
        }
        return receivedBytes;
    }
}
