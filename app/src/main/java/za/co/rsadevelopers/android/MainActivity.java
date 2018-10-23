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

    Button payButton;
    EditText priceEditText;
    TextView activityMessage;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add a listener to make sure that the format of the payment amount is correct.
        priceEditText = findViewById(R.id.price_edit_text);
        priceEditText.addTextChangedListener(new MoneyTextWatcher(priceEditText));

        // Add event for paying.
        payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPayButton();
            }
        });

        // Get text view for errors.
        activityMessage = findViewById(R.id.activity_message);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkLogin();
    }

    private void checkLogin() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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

    public static final String EXTRA_MESSAGE = "";
    private void clickPayButton() {
        // TODO Check that the amount is valid.
        Intent intent = new Intent(this, PaymentActivity.class);
        EditText editText = findViewById(R.id.price_edit_text);
        String amount = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, amount);
        startActivity(intent);
    }
}
