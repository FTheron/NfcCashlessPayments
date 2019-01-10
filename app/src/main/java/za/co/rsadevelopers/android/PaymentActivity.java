package za.co.rsadevelopers.android;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Objects;

import za.co.rsadevelopers.android.helpers.Helper;
import za.co.rsadevelopers.android.helpers.SQLiteDBHelper;
import za.co.rsadevelopers.android.models.TagData;

public class PaymentActivity extends AppCompatActivity {
    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String ERROR_FORMAT = "Tag type not accepted";
    public static final String WRITE_SUCCESS = "{0} Processed successfully. New balance {1}";
    public static final String BALANCE_LOADED = "{0} Loaded. Balance {1}";
    public static final String BALANCE_CHECK = "Balance remaining {0}";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    public static final String READ_WAITING = "Waiting for NFC tag.";
    public static final String EMPTY_TAG = "No funds loaded - Tag is Empty.";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds. Transaction Failed.";
    public static final String NFC_NOT_SUPPORTED = "NFC is not supported by this device.";

    ImageView nfcStatusImage;
    TextView nfcStatusMessage;
    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;
    String monetaryAmount;
    Tag thisTag;
    Boolean isProcessed = false;
    Boolean isLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Add event for paying.
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nfcStatusImage = findViewById(R.id.nfc_payment_status_image);
        nfcStatusImage.setVisibility(View.GONE);

        nfcStatusMessage = findViewById(R.id.nfc_status_message);
        nfcStatusMessage.setText(READ_WAITING);

        mPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enable the Foreground Dispatch to detect NFC intent
        enableNFC();

        // Get payment amount
        monetaryAmount = getIntent().getStringExtra(MainActivity.MONETARY_AMOUNT);
        isLoad = Objects.requireNonNull(getIntent().getExtras()).getBoolean(MainActivity.IS_LOAD);
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableNFC();
    }

    // Catches the nfc scan event and processes the tag.
    @Override
    protected void onNewIntent(Intent intent) {
        if (isProcessed) return;

        // Process payment.
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
        } else {
            if (isLoad){
                // Only do this if the tag is empty.
                thisTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                loadNewTag();
            }else {
                nfcStatusMessage.setText(EMPTY_TAG);
            }
        }
    }

    private void loadNewTag(){
        try {
            // TODO Get Tag ID
            BigDecimal loadAmount = Helper.cleanCurrency(monetaryAmount);
            String tagId = "0000000000000000000000000000000-";
            writeToTag(tagId + Helper.getPaddedStringAmount(loadAmount));
            // TODO: saveTransactionToDB(tagId, loadAmount);
            nfcStatusMessage.setText(MessageFormat.format(BALANCE_LOADED, Helper.createCurrency(loadAmount), Helper.createCurrency(loadAmount)));
        } catch (IOException e) {
            nfcStatusMessage.setText(WRITE_ERROR);
            e.printStackTrace();
        } catch (FormatException e) {
            nfcStatusMessage.setText(ERROR_FORMAT);
            e.printStackTrace();
        }
    }

    private void processPayment(String message) {
        // TODO Decrypt the message

        if (!tagIsValid(message)) return;

        TagData tagData = extractTagData(message);

        BigDecimal transactionAmount = Helper.cleanCurrency(monetaryAmount);
        if (!executeTransaction(tagData, transactionAmount)) return;

        String newTagMessage = createNewTag(tagData);

        // TODO Encrypt the new message

        try {
            if (thisTag == null) {
                nfcStatusMessage.setText(ERROR_DETECTED);
            } else {
                writeToTag(newTagMessage);
                saveTransactionToDB(tagData);
                isProcessed = true;
                if (isLoad)
                    nfcStatusMessage.setText(MessageFormat.format(WRITE_SUCCESS, Helper.createCurrency(transactionAmount), Helper.createCurrency(tagData.Balance)));
                else
                    nfcStatusMessage.setText(MessageFormat.format(WRITE_SUCCESS, Helper.createCurrency(transactionAmount), Helper.createCurrency(tagData.Balance)));
            }
        } catch (IOException e) {
            nfcStatusMessage.setText(WRITE_ERROR);
            e.printStackTrace();
        } catch (FormatException e) {
            nfcStatusMessage.setText(ERROR_FORMAT);
            e.printStackTrace();
        }
    }

    private boolean tagIsValid(String message) {
        if (message == null || message.length() != 45) {
            nfcStatusMessage.setText(ERROR_FORMAT);
        }
        return true;
    }

    // Extract the needed values from the tag message.
    private TagData extractTagData(String tagMessage) {

        TagData data = new TagData();
        data.ClientId = tagMessage.substring(0, 32);
        String stringBalance = tagMessage.substring(32, 45);
        data.Balance = Helper.cleanCurrency(stringBalance);

        return data;
    }

    private boolean executeTransaction(TagData tag, BigDecimal transactionAmount) {
        // If the payment value is 0 assume that the client wants to see his balance.
        if (transactionAmount.compareTo(BigDecimal.ZERO) == 0) {
            nfcStatusMessage.setText(MessageFormat.format(BALANCE_CHECK, Helper.createCurrency(tag.Balance)));
            return false;
        }

        if (!isLoad) transactionAmount = transactionAmount.negate();
        // Add/Subtract the payment from the client's balance.
        tag.Balance = tag.Balance.add(transactionAmount);

        // If the client has insufficient funds display an appropriate message.
        if (tag.Balance.compareTo(BigDecimal.ZERO) < 0) {
            nfcStatusMessage.setText(INSUFFICIENT_FUNDS);
            return false;
        }
        return true;
    }

    private String createNewTag(TagData tag) {
        return tag.ClientId + Helper.getPaddedStringAmount(tag.Balance);
    }

    private String readTag(NdefMessage[] msgs) {
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
        NdefRecord[] records = {createRecord(text)};
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
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
    }

    private void enableNFC() {
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            nfcStatusMessage.setText(NFC_NOT_SUPPORTED);
        } else {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    private void disableNFC() {
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    private void saveTransactionToDB(TagData tag) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.TRANSACTION_COLUMN_CLIENT_ID, tag.ClientId);
        values.put(SQLiteDBHelper.TRANSACTION_COLUMN_VALUE, Helper.ToCents(tag.Balance));
        database.insert(SQLiteDBHelper.TRANSACTION_TABLE_NAME, null, values);
    }
}
