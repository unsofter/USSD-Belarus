package by.unsofter.ussdbelarus;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_code);

        // Кнопка "Назад"
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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
            String sCode = ((EditText) findViewById(R.id.addeditussd)).getText().toString().trim();
            String sInfo = ((EditText) findViewById(R.id.addeditinfo)).getText().toString().trim();

            if (sCode.length() == 0) {
                Toast.makeText(this, "Код должен быть задан", Toast.LENGTH_LONG).show();
                return true;
            }

            if ((sCode.charAt(0) != '*') || (sCode.charAt(sCode.length() - 1) != '#')) {
                Toast.makeText(this, "Допускаются коды, начинающиеся на \"*\" и заканчивающиеся на \"#\"", Toast.LENGTH_LONG).show();
                return true;
            }

            // Проверить код на уникальность
            Cursor cursor = MainPage.USSDDb.getReadableDatabase().rawQuery("select * from favorites where code=\"" + sCode +
                    "\" and operator=" + MainPage.oeditable, null);
            if (cursor.moveToFirst())
            {
                cursor.close();
                Toast.makeText(this, "Код " + sCode + " был добавлен в \"Мои коды\" ранее.", Toast.LENGTH_LONG).show();
            }
            else {
                cursor.close();
                ContentValues newValues = new ContentValues();
                newValues.put("code", sCode);
                newValues.put("info", sInfo);
                newValues.put("type", getResources().getInteger(R.integer.typeUSSD));
                newValues.put("shablon", getResources().getString(R.string.shablon_code));
                newValues.put("operator", MainPage.oeditable);

                MainPage.USSDDb.getReadableDatabase().insert("favorites", null, newValues);
                Toast.makeText(this, sCode + " - добавлен в \"Мои коды\"", Toast.LENGTH_LONG).show();
            }
            setResult(RESULT_OK);
        }
        else
            setResult(RESULT_CANCELED);

        finish();
        return true;
    }

}
