package by.unsofter.ussdbelarus;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ListViewMenu extends AlertDialog implements View.OnClickListener {

        View menu;
        private Resources mRes;

        ImageButton startShared;
        ImageButton startPhone;
        ImageButton startClipboard;
        ImageButton startFavorites;

        TextView    startFavoritesText;

        Context context;
        ClipboardManager clipboard;

        Bitmap drFavorites;
        Bitmap drDelete;

        public ListViewMenu (Context context) {
            super(context);
            menu = getLayoutInflater().inflate(R.layout.listviewmenu_layout, null);
            setView(menu);
            mRes = context.getResources();

            clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);

            this.context = context;

            startShared    = (ImageButton) menu.findViewById(R.id.startShared);
            startPhone     = (ImageButton) menu.findViewById(R.id.startPhone);
            startClipboard = (ImageButton) menu.findViewById(R.id.startClipboard);
            startFavorites = (ImageButton) menu.findViewById(R.id.startFavorites);

            startFavoritesText = (TextView) menu.findViewById(R.id.startFavoritesText);

            startFavorites.setOnClickListener(this);
            startClipboard.setOnClickListener(this);
            startPhone.setOnClickListener(this);
            startShared.setOnClickListener(this);

            drFavorites = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_menu_favorites);
            drDelete    = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_listview_delete);
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_MENU) {
                dismiss();
                return true;
            }
             return super.onKeyUp(keyCode, event);
        }

        @Override
        public void show() {

            SpannableString ussdWords = new SpannableString(MainPage.curUSSD.USSDCode + " - " + MainPage.curUSSD.Info);
            TextView ussdViewData = (TextView) menu.findViewById(R.id.USSDViewData);
            // Задать текст заголовка
            ussdWords.setSpan(new ForegroundColorSpan(mRes.getColor(R.color.colorPrimary)), 0, MainPage.curUSSD.USSDCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ussdViewData.setText(ussdWords);
            // Поменять иконку и текст
            if (MainPage.CurMobileOperator == MainPage.ofavorites) {
                startFavorites.setImageBitmap(drDelete);
                startFavoritesText.setText("удалить");
            }
            else {
                startFavorites.setImageBitmap(drFavorites);
                startFavoritesText.setText("в мои коды");
            }
            super.show();
        }
        // Удалить код из моих кодов
        void delCurUSSD(List<USSD> curUssdList)
        {
            for (int i = 0; i < curUssdList.size(); i++ )
                if (curUssdList.get(i).id == MainPage.curUSSD.id)
                    curUssdList.remove(i);
        }

        @Override
        public void onClick (View v) {
            switch (v.getId()) {

                case R.id.startFavorites:
                    if (MainPage.CurMobileOperator == MainPage.ofavorites) {
                        MainPage.USSDDb.getWritableDatabase().delete("favorites", "id = ?", new String[] {Long.toString(MainPage.curUSSD.id)});

                        delCurUSSD(MainPage.USSDFilterList);
                        delCurUSSD(MainPage.USSDList);
                        MainPage.adapter.notifyDataSetChanged();

                        Toast.makeText(context, MainPage.curUSSD.USSDCode + " - удален из \"Мои коды\"", Toast.LENGTH_LONG).show();
                    }
                    else {
                        // Проверить код на уникальность
                        Cursor cursor = MainPage.USSDDb.getReadableDatabase().rawQuery("select * from favorites where code=\"" + MainPage.curUSSD.USSDCode +
                                                                                            "\" and operator=" + MainPage.curUSSD.Operator, null);
                        if (cursor.moveToFirst())
                        {
                            cursor.close();
                            Toast.makeText(context, "Код " + MainPage.curUSSD.USSDCode + " был добавлен в \"Мои коды\" ранее.", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            cursor.close();
                            ContentValues newValues = new ContentValues();
                            newValues.put("code", MainPage.curUSSD.USSDCode);
                            newValues.put("info", MainPage.curUSSD.Info);
                            newValues.put("type", MainPage.curUSSD.Type);
                            newValues.put("shablon", MainPage.curUSSD.Shablon);
                            newValues.put("operator", MainPage.curUSSD.Operator);

                            MainPage.USSDDb.getReadableDatabase().insert("favorites", null, newValues);
                            Toast.makeText(context, MainPage.curUSSD.USSDCode + " - добавлен в \"Мои коды\"", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;

                case R.id.startClipboard:
                    ClipData clip = ClipData.newPlainText("USSD код", MainPage.curUSSD.USSDCode + " - " + MainPage.curUSSD.Info);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, MainPage.curUSSD.USSDCode + " - добавлен в буфер обмена", Toast.LENGTH_LONG).show();
                    break;

                case R.id.startPhone:
                    Intent intentPh;
                    String phoneNumber = MainPage.curUSSD.USSDCode.replace("#", Uri.encode("#"));

                    if (MainPage.sPref.getBoolean(MainPage.CALL_SWITCH, true))
                        intentPh = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                    else
                        intentPh = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));

                    try {
                        context.startActivity(intentPh);
                    } catch (Throwable t) {
                        intentPh = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                        context.startActivity(intentPh);
                    }
                    break;

                case R.id.startShared:
                    Intent intentSh = new Intent(Intent.ACTION_SEND);
                    intentSh.setType("text/plain");
                    intentSh.putExtra(android.content.Intent.EXTRA_TEXT, MainPage.curUSSD.USSDCode + " - " + MainPage.curUSSD.Info);
                    context.startActivity(Intent.createChooser(intentSh,"Поделиться с помощью ..."));
                    break;
            }

            dismiss();
        }
}
