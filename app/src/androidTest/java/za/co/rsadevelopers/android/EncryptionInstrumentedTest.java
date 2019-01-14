package za.co.rsadevelopers.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import za.co.rsadevelopers.android.helpers.Encryption;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EncryptionInstrumentedTest {
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
