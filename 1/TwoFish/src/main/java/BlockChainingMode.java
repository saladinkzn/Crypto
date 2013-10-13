import java.util.Arrays;

/**
 * @author sala
 */
public abstract class BlockChainingMode {


    protected abstract int[] encrypt(int[] block, int[] key);
    protected abstract int[] decrypt(int[] block, int[] key);

    public int[][] encrypt(int[][] p, int[] key, int[] initialVector) {
        int[] fi = Arrays.copyOf(initialVector, initialVector.length);
        int[][] c = new int[p.length][];
        for(int i = 0; i < p.length; i++) {
            int[] cypherInput = new int[p[i].length];
            for(int j = 0; j < p[i].length; j++) {
                cypherInput[j] = fi[j] ^ p[i][j];
            }
            c[i] = encrypt(cypherInput, key);
            for(int j = 0; j < fi.length; j++) {
                fi[j] ^= c[i][j];
            }
        }
        return c;
    }

    public int[][] decrypt(int[][] c, int[] key, int[] initialVector) {
        int[] fi = Arrays.copyOf(initialVector, initialVector.length);
        int[][] p = new int[c.length][];
        for(int i = 0; i < c.length; i++) {
            int[] cypherOutput = decrypt(c[i], key);
            for(int j = 0; j < cypherOutput.length; j++) { cypherOutput[j] ^= fi[j]; }
            p[i] = cypherOutput;
            for(int j = 0; j < fi.length; j++) { fi[j] ^=c[i][j];}
        }
        return p;
    }
}
