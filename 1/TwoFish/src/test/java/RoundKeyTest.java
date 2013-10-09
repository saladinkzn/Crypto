import org.junit.Test;

/**
 * @author sala
 */
public class RoundKeyTest {
    @Test
    public void testRoundKey() {
        int[] plainText = new int[] {0x03020100, 0x07060504, 0x0B0A0908, 0x0F0E0D0C };
        final int[] roundKeys01 = Program.roundKeys(plainText, 0);
        System.out.println(String.format("%H %H", roundKeys01[0], roundKeys01[1]));

    }

    @Test
    public void testSBox() {
        int[] plainText = new int[] {0x03020100, 0x07060504, 0x0B0A0908, 0x0F0E0D0C };
        final int[] s = Program.getS(plainText);
        System.out.println(String.format("%H | %H", s[0], 0x2F062AD7 ));
        System.out.println(String.format("%H | %H", s[1], 0xF204791A));
    }

    @Test
    public void testGalua() {
        Galua256 galua256 = new Galua256((byte)0b00011011);
        final byte multiply = galua256.multiply((byte) 0x57, (byte) 0x83);
        System.out.println(String.format("%H", multiply));
    }
}