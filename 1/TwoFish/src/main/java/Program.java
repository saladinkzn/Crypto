import java.nio.charset.Charset;

/**
 * @author sala
 */
public class Program {
    public static void main(String[] args) {
        int[] plainText = new int[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,0x07,0x08,0x09,0x0A, 0x0B,0x0C, 0x0D, 0x0E, 0x0F};//new int[] {0x03020100, 0x07060504, 0x0B0A0908, 0x0F0E0D0C };
        int[] p = new int[4];
        for(int i = 0; i < 4; i++) {
            p[i] = plainText[4*i] + 256*plainText[4*i+1] + (plainText[4*i+2] << 16) + (plainText[4*i+3] << 24);
        }
        System.out.println("Input:");
        Utils.printInput(p);
        int[] key = p;
        System.out.println("Key:");
        Utils.printInput(key);
        //
        int[] encrypted = TwoFish.encrypt(p, key);
        //
        System.out.println("Encrypted:");;
        Utils.printInput(encrypted);
        System.out.println();

        int[] decrypted = TwoFish.decrypt(encrypted, key);
        System.out.println("Decrypted:");
        Utils.printInput(decrypted);
        //

        System.out.println("Hash:");
        String message1 = "Hello, world, this is Timur";
        String message2 = "Hello, world, this is Timur ";
        String message3 = "Hello, world, this is Timus";
        for(String message : new String[] {message1, message2, message3}) {
            byte[] messageBytes = message.getBytes(Charset.forName("UTF-8"));
            final byte[] hash = Hasher.hash(messageBytes);
            for(byte b : hash) { System.out.print(String.format("%02X", b)); }
            System.out.println();

        }

    }

}
