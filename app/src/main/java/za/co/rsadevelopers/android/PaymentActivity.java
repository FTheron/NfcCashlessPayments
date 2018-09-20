package za.co.rsadevelopers.android;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.charset.Charset;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Add event for paying.
        Button cancelPaymentButton = (Button) findViewById(R.id.cancel_payment_button);
        cancelPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelPayment();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.PAYMENT_AMOUNT);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.payment_amount);
        textView.setText(message);
    }

    // Catches the nfc scan event and processes the tag.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
                // Process the messages array.
            }
        }
    }

    protected void CreateTag(){
        NdefRecord uriRecord = new NdefRecord(
                NdefRecord.TNF_ABSOLUTE_URI ,
                "android.rsadevelopers.co.za/wallet".getBytes(Charset.forName("US-ASCII")),
                new byte[0], new byte[0]);
        NdefRecord rtdUriRecord = NdefRecord.createUri("android.rsadevelopers.co.za/wallet");
    }

    protected void CancelPayment(){
        finish();
    }

}
