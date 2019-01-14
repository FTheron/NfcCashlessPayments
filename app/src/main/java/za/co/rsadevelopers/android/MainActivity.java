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

import java.text.MessageFormat;

public class MainActivity extends AppCompatActivity {
    public static final String LOGGED_IN_MESSAGE = "Logged in as {0}";
    public static final String MONETARY_AMOUNT = "za.co.rsadevelopers.android.MONETARY_AMOUNT";
    public static final String IS_LOAD = "za.co.rsadevelopers.android.IS_LOAD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add a listener to make sure that the format of the payment amount is correct.
        EditText priceEditText = findViewById(R.id.price_edit_text);
        priceEditText.addTextChangedListener(new MoneyTextWatcher(priceEditText));

        // Add event for paying.
        Button payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPayButton();
            }
        });

        Button loadButton = findViewById(R.id.load);
        loadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickLoadButton();
            }
        });

        Button syncButton = findViewById(R.id.sync);
        syncButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSyncButton();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkLogin();
        resetScreen();
    }

    private void resetScreen(){
        EditText priceEditText = findViewById(R.id.price_edit_text);
        priceEditText.setText("");
    }

    private void checkLogin() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Long user_id = sharedPreferences.getLong("User_Id", 0);
        String email = sharedPreferences.getString("User_Email", "none");
        if (user_id == 0) {
            // Redirect to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            // Set username on screen.
            TextView txtLoggedInAs = findViewById(R.id.logged_in_as);
            txtLoggedInAs.setText(MessageFormat.format(LOGGED_IN_MESSAGE, email));
        }
    }


    private void clickPayButton() {
        // TODO Check that the amount is valid.
        Intent intent = new Intent(this, PaymentActivity.class);
        EditText editText = findViewById(R.id.price_edit_text);
        String amount = editText.getText().toString();
        intent.putExtra(MONETARY_AMOUNT, amount);
        intent.putExtra(IS_LOAD, false);
        startActivity(intent);
    }

    private void clickLoadButton() {
        // TODO Check that the amount is valid.
        Intent intent = new Intent(this, PaymentActivity.class);
        EditText editText = findViewById(R.id.price_edit_text);
        String amount = editText.getText().toString();
        intent.putExtra(MONETARY_AMOUNT, amount);
        intent.putExtra(IS_LOAD, true);
        startActivity(intent);
    }

    private void clickSyncButton() {
        // TODO Read Database
    }
}
