import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sala
 */
public class BlockProgram {

    public static final int BLOCK_SIZE = 16;

    public static void main(String[] args) {
        final String plainText = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        final byte[] message = plainText.getBytes(Charset.forName("UTF-8"));
        for(byte b : message) { System.out.print(String.format("%02X", b)); }
        System.out.println();
        final byte[] hash = Hasher.hash(message);
        final int[] hashBlock = Hasher.convertInput(hash, 0);
        final List<int[]> blocksList = new ArrayList<>(message.length / 8 + 2);
        for(int offset = 0; offset < message.length; offset+= BLOCK_SIZE) {
            int[] block = Hasher.convertInput(message, offset);
            blocksList.add(block);
        }
        blocksList.add(hashBlock);
        final int[][] blocks = blocksList.toArray(new int[blocksList.size()][]);
        final int[] initialVector = new int[] { 0, 0,0,0};
        final int[] key = new int[] {0x03020100, 0x07060504, 0x0B0A0908, 0x0F0E0D0C};
        TwoFishBlockChainingMode twoFishBlockChainingMode = new TwoFishBlockChainingMode();
        final int[][] encrypt = twoFishBlockChainingMode.encrypt(blocks, key, initialVector);
        final int[][] decrypt = twoFishBlockChainingMode.decrypt(encrypt, key, initialVector);
        final int[] receivedHash = blocks[blocks.length - 1];

        final int[][] receivedBlocks = Arrays.copyOf(decrypt, decrypt.length - 1);
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
        for(byte b : receivedBytes) {
            System.out.print(String.format("%02X", b));
        }
        System.out.println();
        //
        for(int word : receivedHash) {
            for (int j = 0; j < 4; j++) {
                System.out.print(String.format("%02X", (byte) (word >>> 8 * j)));
            }
        }
        System.out.println();
        //
        final byte[] computedHash = Hasher.hash(receivedBytes);
        for(byte b : computedHash) { System.out.print(String.format("%02X", b)); }
        System.out.println();
        System.out.println(new String(receivedBytes, Charset.forName("UTF-8")));

    }
}
