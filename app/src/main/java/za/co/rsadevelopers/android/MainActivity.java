package za.co.rsadevelopers.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button payButton;
    EditText priceEditText;
    TextView nfcStatusView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add a listener to make sure that the format of the payment amount is correct.
        priceEditText = (EditText) findViewById(R.id.price_edit_text);
        priceEditText.addTextChangedListener(new MoneyTextWatcher(priceEditText));

        // Add event for paying.
        payButton = (Button) findViewById(R.id.pay_button);
        payButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPayButton();
            }
        });

        nfcStatusView = (TextView) findViewById(R.id.nfc_status);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkLogin();
        resetActivity();
    }

    private void resetActivity(){
        payButton.setText("Pay");
        priceEditText.setText("");
        nfcStatusView.setText("");
        SharedPreferences.Editor prefs = sharedPreferences.edit();
        prefs.putString("Payment_Amount","");
        prefs.apply();
    }

    private void checkLogin() {
        Long user_id = sharedPreferences.getLong("User_Id", 0);
        String email = sharedPreferences.getString("User_Email", "none");
        if (user_id == 0) {
            // Redirect to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            // Set username on screen.
            TextView txtLoggedInAs = (TextView) findViewById(R.id.logged_in_as);
            txtLoggedInAs.setText("Logged in as " + email);
        }
    }

    private void clickPayButton() {
        if (payButton.getText().equals("Pay")){
            String amount = priceEditText.getText().toString();
            SharedPreferences.Editor prefs = sharedPreferences.edit();
            prefs.putString("Payment_Amount",amount);
            prefs.apply();
            payButton.setText("Cancel Payment");
            nfcStatusView.setText("Waiting on NFC tag");
        }else{
            resetActivity();
        }
    }
}
