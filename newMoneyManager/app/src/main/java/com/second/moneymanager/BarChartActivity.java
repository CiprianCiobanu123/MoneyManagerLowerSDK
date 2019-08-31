package com.second.moneymanager;

import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

public class BarChartActivity extends AppCompatActivity {

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        BarChart chart = findViewById(R.id.barchart);

        ArrayList expensesForBarChart = new ArrayList<>();
        ArrayList<String> categories = new ArrayList<>();


        prefs = getSharedPreferences("com.mycompany.MoneyManager", MainActivity.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        final int day = Integer.parseInt(prefs.getString("day", ""));
        final int month = Integer.parseInt(prefs.getString("month", ""));
        final int year = Integer.parseInt(prefs.getString("year", ""));

        float valueExpenses = 0;

        ArrayList<Expense> expenses = new ArrayList<>();
        ArrayList<Double> expensesValues = new ArrayList<>();
        double totalValuesFromExpenseValues;

        if (prefs.getString("monthlyOrYearly", "").equals("Monthly")) {

            try {

                ExpensesDB db = new ExpensesDB(BarChartActivity.this);
                db.open();
                expenses = db.getExpensesByYear(String.valueOf(calendar.get(Calendar.YEAR)));

                for (int i = 0; i < expenses.size(); i++) {

                    totalValuesFromExpenseValues = 0;

                    valueExpenses = (float) (valueExpenses + expenses.get(i).getPrice());

                    expensesValues = db.getExpensesValuesByCategory(expenses.get(i).getCategory());

                    for (int j = 0; j < expensesValues.size(); j++) {
                        totalValuesFromExpenseValues = totalValuesFromExpenseValues + expensesValues.get(j);
                    }
                    if (totalValuesFromExpenseValues > 0) {
                        expensesForBarChart.add(new BarEntry(((float) (totalValuesFromExpenseValues * 100) / valueExpenses), i));
                    }

                    categories.add(expenses.get(i).getCategory());
                }

                db.close();
            } catch (SQLException e) {
                Toast.makeText(BarChartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        BarDataSet bardataset = new BarDataSet(expensesForBarChart, "No Of Employee");
        chart.animateY(2000);
        BarData data = new BarData(categories, bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(data);
    }
}
