package za.co.rsadevelopers.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add a listener to make sure that the format of the payment amount is correct.
        EditText priceEditText = (EditText) findViewById(R.id.price_edit_text);
        priceEditText.addTextChangedListener(new MoneyTextWatcher(priceEditText));

        // Add event for paying.
        Button mEmailSignInButton = (Button) findViewById(R.id.pay_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Pay();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        CheckLogin();
    }

    private void CheckLogin() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Long user_id = prefs.getLong("User_Id", 0);
        String email = prefs.getString("User_Email", "none");
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

    private void Pay() {

    }
}
