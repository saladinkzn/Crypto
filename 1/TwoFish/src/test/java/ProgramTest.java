import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

/**
 * @author sala
 */
public class ProgramTest {
    @Test
    public void testWhitening() {
        long a = 0x01234567890ABCDEL;
        System.out.println(a >> 32);
        System.out.println((int)a);
    }
}
