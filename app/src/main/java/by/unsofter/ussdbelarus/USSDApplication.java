package by.unsofter.ussdbelarus;

import android.app.Application;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class USSDApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Создание расширенной конфигурации библиотеки.
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("32f9853b-406b-4dd7-9917-fdb21274b3bc").build();
        // Инициализация AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Отслеживание активности пользователей.
        YandexMetrica.enableActivityAutoTracking(this);
    }
}