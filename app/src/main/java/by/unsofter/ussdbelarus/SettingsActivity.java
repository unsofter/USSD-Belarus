package by.unsofter.ussdbelarus;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    // Видимые элементы
    Switch settings_switch_call;
    Switch settings_switch_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Кнопка "Назад"
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Видимые элементы
        settings_switch_call   = (Switch) findViewById(R.id.settings_switch_call);
        settings_switch_number = (Switch) findViewById(R.id.settings_switch_number);
        settings_switch_call.setChecked(MainPage.sPref.getBoolean(MainPage.CALL_SWITCH, true));
        settings_switch_number.setChecked(MainPage.sPref.getBoolean(MainPage.NUMBER_SWITCH, true));
    }

    // Иконка тулбара
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    // Клик по кнопке в тулбаре
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            SharedPreferences.Editor ed = MainPage.sPref.edit();
            ed.putBoolean(MainPage.CALL_SWITCH, settings_switch_call.isChecked());
            ed.putBoolean(MainPage.NUMBER_SWITCH, settings_switch_number.isChecked());
            ed.commit();
        }

        finish();
        return true;
    }
}
