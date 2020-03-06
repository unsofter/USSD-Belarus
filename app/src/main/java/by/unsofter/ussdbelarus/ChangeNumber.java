package by.unsofter.ussdbelarus;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeNumber extends AppCompatActivity {

    // Видимые элементы
    TextView changeUSSDView;
    TextView changeSuffics;
    EditText changeEditNumber;
    // Переменные
    String       mPhoneNumber;
    CharSequence number;
    int          numbers;

    private static final int CONTACT_PICK_RESULT = 999;

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
        if ((item == null) || (item.getItemId() != android.R.id.home)) {
            if (GetShablonString(MainPage.curUSSD.Shablon) != getResources().getString(R.string.shablon_code))
                if (changeEditNumber.getText().length() == 0) {
                    Toast.makeText(this,
                            "Введите " + getResources().getString(R.string.change_number), Toast.LENGTH_LONG)
                            .show();
                    return true;
                }

            boolean bPhone = (MainPage.curUSSD.Shablon.indexOf(getResources().getString(R.string.shablon_phone9)) >= 0);

            // Убрать клавиатуру, если она есть
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(changeEditNumber.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            MainPage.curUSSD.USSDCode = changeUSSDView.getText().toString();

            if (bPhone && (mPhoneNumber.length() > 0))
                MainPage.curUSSD.Shablon = MainPage.curUSSD.Shablon.replace(getResources().getString(R.string.shablon_phone9), mPhoneNumber);

            if (number.length() > 0) {
                MainPage.curUSSD.Shablon = MainPage.curUSSD.Shablon.replace(GetShablonString(MainPage.curUSSD.Shablon), number);
                MainPage.curUSSD.Type = getResources().getInteger(R.integer.typeUSSD);
            }

            setResult(RESULT_OK);
        }
        else
            setResult(RESULT_CANCELED);

        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_number);
        // Кнопка "Назад"
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        number       = "";
        mPhoneNumber = "";
        // Видимые элементы
        changeUSSDView   = (TextView) findViewById(R.id.ChangeUSSDView);
        changeSuffics    = (TextView) findViewById(R.id.ChangeSuffics);
        changeEditNumber = (EditText) findViewById(R.id.ChangeEditNumber);

        changeEditNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                number = cs;
                SetUSSDString();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        setTitle(getString(R.string.change_number_title));

        setResult(RESULT_CANCELED);

        // Выбрать телефон из контакта
        try {
            if (MainPage.curUSSD.Shablon.indexOf(getResources().getString(R.string.shablon_phone9)) >= 0) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, CONTACT_PICK_RESULT);
            } else {
                number = changeEditNumber.getText();
                SetUSSDString();
            }
        }
        catch (Throwable t)
        {
            Toast.makeText(this,
                    "Ошибка при загрузке кода: " + t.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTACT_PICK_RESULT) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                mPhoneNumber = cursor.getString(column)
                                     .replace(" ", "")
                                     .replace("+", "")
                                     .replace("-", "")
                                     .replace("(", "")
                                     .replace(")", "");
                if (mPhoneNumber.length() >= 9)
                    mPhoneNumber = mPhoneNumber.substring(mPhoneNumber.length() - 9);

                SetUSSDString();
            }
            else
                finish();
        }
    }

    String GetShablonString(String USSDShablon)
    {
        numbers = 0;

        if (USSDShablon.indexOf(getResources().getString(R.string.shablon_summa)) >= 0)
            return getResources().getString(R.string.shablon_summa);
        else if (USSDShablon.indexOf(getResources().getString(R.string.shablon_number)) >= 0)
            return getResources().getString(R.string.shablon_number);
        else if (USSDShablon.indexOf(getResources().getString(R.string.shablon_number4)) >= 0) {
            numbers = 4;
            return getResources().getString(R.string.shablon_number4);
        }
        else if (USSDShablon.indexOf(getResources().getString(R.string.shablon_number5)) >= 0) {
            numbers = 5;
            return getResources().getString(R.string.shablon_number5);
        }
        else if (USSDShablon.indexOf(getResources().getString(R.string.shablon_number14)) >= 0) {
            numbers = 14;
            return getResources().getString(R.string.shablon_number14);
        }
        else
            return getResources().getString(R.string.shablon_code);
    }

    void SetUSSDString()
    {
        String chShablon = GetShablonString(MainPage.curUSSD.Shablon);

        String sText = MainPage.curUSSD.Shablon.replace(getResources().getString(R.string.shablon_phone9), mPhoneNumber);
        // Получившийся USSD-код
        sText = sText.replace(chShablon, number);
        changeUSSDView.setText(sText);

        // Выйти, если нужно ввести только номер телефона
        if (chShablon == getResources().getString(R.string.shablon_code))
        {
            onOptionsItemSelected(null);
            return;
        }

        // "Осталось ..."
        if (numbers == 0)
            changeSuffics.setVisibility(View.GONE);
        else {
            int sNumber = numbers - changeEditNumber.getText().length();
            changeSuffics.setText(getResources().getString(R.string.change_ostalos) + " " + Integer.toString(sNumber));
        }
    }
}
