package by.unsofter.ussdbelarus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Константы
    final String CURRENTFILE   = "CURRENTFILE";
    public static final String NUMBER_SWITCH = "NUMBER_SWITCH";
    public static final String CALL_SWITCH   = "CALL_SWITCH";
    // Операторы
    public static final int olife         = 1;
    public static final int omts          = 2;
    public static final int ovelcom       = 3;
    public static final int ovelcomprivet = 4;
    public static final int ovelcomkorp   = 5;
    public static final int oother        = 6;
    public static final int ofavorites    = 7;
    public static final int oeditable     = 8;
    // Переменные
    public static int CurMobileOperator;   // Текущий мобильный оператор
    public static SharedPreferences sPref; // Параметры приложенич
    static String     CurOperator;         // Наименование текущего опреатора
    // Объекты
    public static List<USSD> USSDList;       // Список в памяти
    public static List<USSD> USSDFilterList; // Отфильтрованный список
    public static USSD curUSSD;              // Текущий USSD
    public static USSDAdapter adapter;                     // Адаптер
    // Видимые объекты
    public static EditText editSearch; // Ввод фильтра
    ListView ussdList;                 // Список отображения
    ListViewMenu listViewMenu;         // Меню выюора USSD
    Menu mMenu;
    // БД
    public static USSDDB USSDDb;

    // Методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Разрешение на чтение данных о телефоне
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE}, 1);
        }
        // Разрешение на чтение контактов
        permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS}, 2);
        }
        // Разрешение на звонки
        permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, 3);
        }

        USSDList = new ArrayList<USSD>();
        USSDFilterList = new ArrayList<USSD>();
        // Связать данные со списком
        adapter = new USSDAdapter(this);
        // Текущий запрос
        curUSSD = new USSD(0, "", "", 0, "", 0);
        // Параметры приложения
        sPref = getPreferences(MODE_PRIVATE);

        // Видимые элементы
        editSearch = (EditText) findViewById(R.id.editSearch);
        ussdList   = (ListView) findViewById(R.id.ussdList);
        listViewMenu = new ListViewMenu(this);

        ussdList.setAdapter(adapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // БД
        USSDDb = new USSDDB(this);

        editSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                FilterData(cs);
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

        ussdList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {

                    Resources res = getResources();
                    // Даннве нужно скопировать, а не оперировать ссылками
                    USSD curUSSDtmp = (USSD) parent.getItemAtPosition(position);

                    curUSSD.id       = curUSSDtmp.id;
                    curUSSD.Type     = curUSSDtmp.Type;
                    curUSSD.USSDCode = curUSSDtmp.USSDCode;
                    curUSSD.Shablon  = curUSSDtmp.Shablon;
                    curUSSD.Operator = curUSSDtmp.Operator;
                    curUSSD.Info     = curUSSDtmp.Info;

                    if (
                            (curUSSD.Type   > res.getInteger(R.integer.typeUSSD)) &&
                            (sPref.getBoolean(NUMBER_SWITCH, true))
                       ) {
                        Intent intent = new Intent(MainPage.this, ChangeNumber.class);
                        startActivityForResult(intent, 0);
                        return;
                    }

                    onActivityResult(0, -1, null);
                }
            });

        LoadData();
    }

    // Иконка тулбара
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_codes, menu);
        mMenu = menu;
        mMenu.setGroupVisible(0, (CurMobileOperator == ofavorites));
        return true;
    }
    // Клик по кнопке в тулбаре
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        intent = new Intent(MainPage.this, AddNewCode.class);
        startActivityForResult(intent, 1);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1)
            LoadData();
        else if (resultCode == 0)
            return;
        else
            listViewMenu.show();
    }

    //**
    // Фильтрация
    private void FilterData(CharSequence cs)
    {
        USSDFilterList.clear();

        for(USSD ussd: USSDList){
            if(
                    (cs == null || cs.length() == 0)          ||
                            (ussd.USSDCode.toLowerCase().contains(cs.toString().toLowerCase()) ||
                                    ussd.Info.toLowerCase().contains(cs.toString().toLowerCase()))
                    )
                USSDFilterList.add(ussd);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        switch (id)
        {
            case R.id.nav_life:
            case R.id.nav_mts:
            case R.id.nav_velcom:
            case R.id.nav_velcomprivet:
            case R.id.nav_velcomkorp:
            case R.id.nav_other:
            case R.id.nav_favorites:
                    SaveCurMobileOperator(id);
                    LoadData();
                break;

            case R.id.nav_about:
                intent = new Intent(MainPage.this, InfoActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_settings:
                intent = new Intent(MainPage.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    int OperatorIdToOperatorConst(int id) {
        int retValue;

        switch (id) {
            case R.id.nav_favorites:
                retValue = ofavorites;
                break;
            case R.id.nav_life:
                retValue = olife;
                break;
            case R.id.nav_mts:
                retValue = omts;
                break;
            case R.id.nav_velcom:
                retValue = ovelcom;
                break;
            case R.id.nav_velcomprivet:
                retValue = ovelcomprivet;
                break;
            case R.id.nav_velcomkorp:
                retValue = ovelcomkorp;
                break;
            case R.id.nav_other:
                retValue = oother;
                break;
            default:
                retValue = id;
                break;
        }

        return retValue;
    }

    // Сохранить текущего опреатора
    int SaveCurMobileOperator(int id)
    {
        int realId = OperatorIdToOperatorConst(id);

        Editor ed = sPref.edit();
        ed.putInt(CURRENTFILE, realId);
        ed.commit();

        return realId;
    }

    // Получить имя оператора по контстанте
    public static String ConstToMobileOperator(int id)
    {
        String retValue = "";

        switch (id)
        {
            case ofavorites:
                retValue = "Мои коды";
                break;
            case olife:
                retValue = "life:)";
                break;
            case omts:
                retValue = "МТС";
                break;
            case ovelcom:
                retValue = "A1";
                break;
            case ovelcomprivet:
                retValue = "A1 привет";
                break;
            case ovelcomkorp:
                retValue = "A1 корпорация";
                break;
            case oother:
                retValue = "Прочие";
                break;
            case oeditable:
                retValue = "Ручной ввод";
                break;
            default:
                retValue = "A1";
                break;
        }
        return retValue;
    }

    // Получить имя оператора по контстанте
    XmlPullParser ConstToFileName(int id)
    {
        XmlPullParser retValue = null;

        switch (id)
        {
            case olife:
                retValue = getResources().getXml(R.xml.life);
                break;
            case omts:
                retValue = getResources().getXml(R.xml.mts);
                break;
            case ovelcom:
                retValue = getResources().getXml(R.xml.velcom);
                break;
            case ovelcomprivet:
                retValue = getResources().getXml(R.xml.velcomp);
                break;
            case ovelcomkorp:
                retValue = getResources().getXml(R.xml.velcomk);
                break;
            case oother:
                retValue = getResources().getXml(R.xml.other);
                break;
            case ofavorites:
                break;
            default:
                retValue = getResources().getXml(R.xml.velcom);
                break;
        }
        return retValue;
    }

    // Загрузить данные о USSD из нужного файла
    void LoadData ()
    {
        CurMobileOperator = sPref.getInt(CURRENTFILE, 0);

        if (CurMobileOperator == 0) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String phoneOperator = telephonyManager.getNetworkOperatorName();

            if (phoneOperator.toLowerCase().indexOf("A1") >= 0)
                CurMobileOperator = ovelcom;
            else if (phoneOperator.toLowerCase().indexOf("privet") >= 0)
                CurMobileOperator = ovelcomprivet;
            else if (phoneOperator.toLowerCase().indexOf("korp") >= 0)
                CurMobileOperator = ovelcomkorp;
            else if (phoneOperator.toLowerCase().indexOf("mts") >= 0)
                CurMobileOperator = omts;
            else if (phoneOperator.toLowerCase().indexOf("life") >= 0)
                CurMobileOperator = olife;
            else
                CurMobileOperator = oother;

            CurMobileOperator = SaveCurMobileOperator(CurMobileOperator);
        }

        // Зачистим списоки
        USSDList.clear();
        USSDFilterList.clear();

        // Получить наименование оператора
        CurOperator = ConstToMobileOperator(CurMobileOperator);
        if (CurMobileOperator == ofavorites) { // Загрузка из БД
            Cursor cursor = USSDDb.getReadableDatabase().rawQuery("select * from favorites", null);

            if (cursor.moveToFirst())
              do
              {
                  USSDList.add(new USSD(
                                  cursor.getLong(cursor.getColumnIndex("id")),
                                  cursor.getString(cursor.getColumnIndex("code")),
                                  cursor.getString(cursor.getColumnIndex("info")),
                                  cursor.getInt(cursor.getColumnIndex("type")),
                                  cursor.getString(cursor.getColumnIndex("shablon")),
                                  cursor.getInt(cursor.getColumnIndex("operator"))
                          )
                  );
              } while (cursor.moveToNext());

            cursor.close();

            if (mMenu != null)
                mMenu.setGroupVisible(0, true);
        }
        else { // Загрузка из XML
            // Получить XML-файл
            XmlPullParser curXML = ConstToFileName(CurMobileOperator);

            // Наполнение списка
            try {
                while (curXML.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if (curXML.getEventType() == XmlPullParser.START_TAG && curXML.getName().equals("USSD"))
                        USSDList.add(new USSD(
                                        0,
                                        curXML.getAttributeValue(null, "code"),
                                        curXML.getAttributeValue(null, "info"),
                                        Integer.parseInt(curXML.getAttributeValue(null, "type")),
                                        curXML.getAttributeValue(null, "shablon"),
                                        CurMobileOperator
                                )
                        );

                    curXML.next();
                }
            } catch (Throwable t) {
                Toast.makeText(this,
                        "Ошибка при загрузке XML-документа: " + t.toString(), Toast.LENGTH_LONG)
                        .show();
            }

            if (mMenu != null)
                mMenu.setGroupVisible(0, false);
        }
        FilterData(editSearch.getText());

        setTitle(getString(R.string.app_name) + " " + CurOperator);
    }
}