package by.unsofter.ussdbelarus;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Кнопка "Назад"
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    // Клик по кнопке в тулбаре
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();

        return true;

    }
    // Ссылка на сайты с USSD
    public void onClick(View widget) {
        String url = "";

        switch (widget.getId()){
            case R.id.info_header_velcom:
                url = "https://a1.by/ru/services/Прочие-услуги/ussd/p/USSD";
                break;
            case R.id.info_header_mts:
                url = "https://www.mts.by/help/mobilnaya-svyaz/selfservices/ussd/";
                break;
            case R.id.info_header_life:
                url = "https://life.com.by/private/about/ussd/";
                break;
            case R.id.info_header_7788:
                url = "https://7788.by/ussd/";
                break;
            case R.id.info_header_belinvestbank:
                url = "https://www.belinvestbank.by/individual/page/14/";
                break;
            case R.id.info_header_vtb:
                url = "https://www.vtb-bank.by/chastnym-licam/bankovskie-kartochki/ussd-servisy";
                break;
            case R.id.info_header_belgazprom:
                url = "https://belgazprombank.by/personal_banking/plastikovie_karti/dopolnitel_nij_servis/sms_servis_informacija_o_dostupnoj_summe/";
                break;
            case R.id.info_header_priorbank:
                url = "https://www.priorbank.by/fin-gramotnost/balans-kartocki/";
                break;
            case R.id.info_header_priormobile:
                url = "https://www.priorbank.by/ussd-bank-prior-mobile/";
                break;
            case R.id.info_header_bps:
                url = "https://www.bps-sberbank.by/page/ussd-banking/";
                break;
            case R.id.info_header_belagroprom:
                url = "https://www.belapb.by/rus/natural/distanc_obsl/ussd-banking/";
                break;
            case R.id.info_header_unihelp:
                url = "https://unihelp.by/sdelat-pozhertvovanie/pozhertvovat-dengi-cherez-sms-i-ussd";
                break;
            case R.id.info_header_blago:
                url = "https://naviny.by/new/20181114/1542206747-life-otkryl-ussd-nomera-dlya-blagotvoritelnoy-pomoshchi";
                break;
            case R.id.info_header_adra:
                url = "http://adra.by/ussd-zapros";
                break;
            case R.id.info_header_ideabank:
                url = "https://www.ideabank.by/private-osoby/online-bank/ussd-bank/";
                break;
            case R.id.info_header_parking:
                url = "https://www.parkouka.by/home/show/payment";
                break;
            case R.id.info_header_minsktrans:
                url = "http://www.minsktrans.by/ru/adm.html?id=4081";
                break;
            case R.id.info_header_fonddobro:
                url = "https://добра.бел/";
                break;
        }

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    // Ссылка - "Оценить приложение"
    public void onRateClick(View rate) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=by.unsofter.ussdbelarus")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
}