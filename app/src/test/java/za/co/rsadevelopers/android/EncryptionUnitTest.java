package za.co.rsadevelopers.android;

import org.junit.Test;

import za.co.rsadevelopers.android.helpers.Encryption;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EncryptionUnitTest {
    @Test
    public void encryption_isCorrect() {

        Encryption encryption = new Encryption();
        String text = "Encrypt this.";

        String cipherText = encryption.encrypt(text);
        assertNotEquals(text, cipherText);
        String clearText = encryption.decrypt(cipherText);
        assertEquals(clearText, text);

    }
}