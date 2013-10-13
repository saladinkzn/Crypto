/**
 * @author sala
 */
public class Utils {
    public static void printInput(int[] plainText) {
        for(int i = 0; i <= 3; i++) {
            for(byte b  : TwoFish.asBytes(plainText[i])) {
                System.out.print(String.format("%02X", b));
            }
        }
        System.out.println();
    }

    static void printInternal(int[] whitened) {
        for(int whitenedEntry : whitened) {
            System.out.print(String.format("%02X", whitenedEntry));
        }
        System.out.println();
    }
}
