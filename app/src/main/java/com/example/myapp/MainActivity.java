package com.example.myapp;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    EditText editTextUnits, editTextMobile;
    TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        editTextUnits = findViewById(R.id.editTextUnits);
        editTextMobile = findViewById(R.id.editTextMobile);
        textViewResult = findViewById(R.id.textViewResult);

        Button buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                // Check if the input is valid
                String unitsText = editTextUnits.getText().toString();
                String mobile = editTextMobile.getText().toString();

                if (TextUtils.isEmpty(unitsText)) {
                    Toast.makeText(MainActivity.this, "Please enter units", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(MainActivity.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                int units;
                try {
                    units = Integer.parseInt(unitsText);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Please enter valid number of units", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Calculate the bill amount
                double billAmount = calculateBill(units);

                // Format the bill amount to 2 decimal places
                @SuppressLint("DefaultLocale") String formattedBillAmount = String.format("%.2f", billAmount);

                textViewResult.setText("Bill Amount: Rs. " + formattedBillAmount);

                // Send SMS
                sendSMS(mobile, "Your electricity bill amount is Rs. " + formattedBillAmount);
            }
        });
    }

    private double calculateBill(int units) {
        double billAmount;
        if (units <= 100) {
            billAmount = units * 2.50;
        } else if (units <= 200) {
            billAmount = 100 * 2.50 + (units - 100) * 3.00;
        } else if (units <= 300) {
            billAmount = 100 * 2.50 + 100 * 3.00 + (units - 200) * 3.50;
        } else {
            billAmount = 100 * 2.50 + 100 * 3.00 + 100 * 3.50 + (units - 300) * 4.00;
        }
        return billAmount;
    }

    private void sendSMS(String mobile, String message) {
        // Check if SMS permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 1);
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobile, null, message, null, null);
            Toast.makeText(this, "SMS sent to " + mobile, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "SMS failed to send", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }
}
