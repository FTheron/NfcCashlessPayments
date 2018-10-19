package za.co.rsadevelopers.android;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.MessageFormat;

public class PaymentActivity extends AppCompatActivity {
    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String ERROR_FORMAT = "Tag type not accepted";
    public static final String WRITE_SUCCESS = "{0} Processed successfully. Balance remaining {1}";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    public static final String READ_WAITING = "Waiting for NFC tag.";
    public static final String EMPTY_TAG = "No funds loaded - Tag is Empty.";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds. Transaction Failed.";

    ImageView nfcStatusImage;
    TextView nfcStatusMessage;
    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;
    String payAmount;
    Tag thisTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Add event for paying.
        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completePayment();
            }
        });

        nfcStatusImage = (ImageView) findViewById(R.id.nfc_payment_status_image);
        nfcStatusImage.setVisibility(View.GONE);

        nfcStatusMessage = (TextView) findViewById(R.id.nfc_status_message);
        nfcStatusMessage.setText(READ_WAITING);

        // initialize the NFC adapter and define Pending Intent.
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            //nfc not support your device.
            return;
        }
        mPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enable the Foreground Dispatch to detect NFC intent
        enableNFC();

        // Get payment amount
        payAmount = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);
    }

    @Override
    protected void onPause(){
        super.onPause();
        disableNFC();
    }

    // Catches the nfc scan event and processes the tag.
    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            thisTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (rawMessages != null) {
                NdefMessage[] payLoad = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    payLoad[i] = (NdefMessage) rawMessages[i];
                }
                // Process the messages array.
                String message = readTag(payLoad);
                processPayment(message);
            }
        }else {
            thisTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            nfcStatusMessage.setText(EMPTY_TAG);
            try {
                writeToTag("0000000000000000000000000000000-0000000005550");
            } catch (IOException e) {
                nfcStatusMessage.setText(WRITE_ERROR);
                e.printStackTrace();
            } catch (FormatException e) {
                nfcStatusMessage.setText(WRITE_ERROR);
                e.printStackTrace();
            }
        }
    }

    private void processPayment(String message){
        if (message == null || message.length() != 45){
            nfcStatusMessage.setText(ERROR_FORMAT);
            return;
        }
        // TODO Decrypt the message

        // Extract the needed values from the tag message.
        String clientId = message.substring(0,32);
        String stringBalance = message.substring(32, 45);
        BigDecimal balance = Helper.cleanCurrency(stringBalance);
        BigDecimal payment = Helper.cleanCurrency(payAmount);

        balance = balance.subtract(payment);
        if (balance.compareTo(BigDecimal.ZERO) < 0){
            nfcStatusMessage.setText(INSUFFICIENT_FUNDS);
            return;
        }

        // TODO Save the transaction for later upload

        // TODO Change the tag message to reflect the new balance
        String newTagMessage = clientId + Helper.getPaddedStringAmount(balance);

        // TODO Encrypt the new message
        // Write message to tag
        try {
            if(thisTag ==null) {
                nfcStatusMessage.setText(ERROR_DETECTED);
            } else {
                writeToTag(newTagMessage);
                nfcStatusMessage.setText(MessageFormat.format(WRITE_SUCCESS, Helper.createCurrency(payment), Helper.createCurrency(balance)));
            }
        } catch (IOException e) {
            nfcStatusMessage.setText(WRITE_ERROR);
            e.printStackTrace();
        } catch (FormatException e) {
            nfcStatusMessage.setText(WRITE_ERROR);
            e.printStackTrace();
        }
    }

    private String readTag(NdefMessage[] msgs){
        if (msgs == null || msgs.length == 0) return null;
        String text = "";
        // String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

    private void writeToTag(String text) throws IOException, FormatException {
        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(thisTag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }

    private void completePayment(){
        finish();
    }

    private void enableNFC(){
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    private void disableNFC(){
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }
}
