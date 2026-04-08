package com.example.fidyathussinnis;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {

    private TextView tvSilverGramPrice, tvBasePrice, tvProfit, tvFinalPrice;
    private EditText etGrams;
    private Button btnCalculate, btnClear, btnBackFromCalculator;

    private double silverPricePerGram = 4.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        tvSilverGramPrice = findViewById(R.id.tvSilverGramPrice);
        etGrams = findViewById(R.id.etGrams);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnClear = findViewById(R.id.btnClear);
        btnBackFromCalculator = findViewById(R.id.btnBackFromCalculator);
        tvBasePrice = findViewById(R.id.tvBasePrice);
        tvProfit = findViewById(R.id.tvProfit);
        tvFinalPrice = findViewById(R.id.tvFinalPrice);

        silverPricePerGram = getIntent().getDoubleExtra("silver_price", 4.5);

        tvSilverGramPrice.setText("سعر جرام الفضة: " + silverPricePerGram + " ₪");

        btnCalculate.setOnClickListener(v -> calculatePrice());
        btnClear.setOnClickListener(v -> clearFields());
        btnBackFromCalculator.setOnClickListener(v -> finish());
    }

    private void calculatePrice() {
        String gramsText = etGrams.getText().toString().trim();

        if (gramsText.isEmpty()) {
            Toast.makeText(this, "أدخل عدد الغرامات", Toast.LENGTH_SHORT).show();
            return;
        }

        double grams;
        try {
            grams = Double.parseDouble(gramsText);
        } catch (Exception e) {
            Toast.makeText(this, "أدخل رقمًا صحيحًا", Toast.LENGTH_SHORT).show();
            return;
        }

        if (grams <= 0) {
            Toast.makeText(this, "أدخل قيمة أكبر من صفر", Toast.LENGTH_SHORT).show();
            return;
        }

        double basePrice = grams * silverPricePerGram;
        double profit = basePrice * 0.10;
        double finalPrice = basePrice + profit;

        tvBasePrice.setText("السعر الأساسي: " + basePrice + " ₪");
        tvProfit.setText("الهامش 10%: " + profit + " ₪");
        tvFinalPrice.setText("السعر النهائي: " + finalPrice + " ₪");
    }

    private void clearFields() {
        etGrams.setText("");
        tvBasePrice.setText("السعر الأساسي: ");
        tvProfit.setText("الهامش 10%: ");
        tvFinalPrice.setText("السعر النهائي: ");
    }
}