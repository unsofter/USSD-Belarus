package by.unsofter.ussdbelarus;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class USSDAdapter extends ArrayAdapter<USSD> {

    Context context;

    public USSDAdapter(Context context) {
        super(context, R.layout.ussd_item, MainPage.USSDFilterList);

        this.context = context;
    }

    @Override
    public int getCount() {
        return MainPage.USSDFilterList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        USSD ussd = getItem(position);
        TextView codeView;
        TextView infoView;
        String editSearch = MainPage.editSearch.getText().toString();

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.ussd_item, null);
        }

        codeView = ((TextView) convertView.findViewById(R.id.USSDCode));
        infoView = ((TextView) convertView.findViewById(R.id.USSDinfo));

        String OperatorName = getOperatorName(ussd);

        SpannableString codeWord = new SpannableString(ussd.USSDCode + OperatorName);
        SpannableString infoWord = new SpannableString(ussd.Info);

        int codeLenth = ussd.USSDCode.toLowerCase().indexOf(editSearch);
        int infoLenth = ussd.Info.toLowerCase().indexOf(editSearch);

        if (editSearch.length() > 0)
        {
            if (codeLenth >= 0)
                codeWord.setSpan(new ForegroundColorSpan(Color.BLUE), codeLenth, codeLenth + editSearch.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (infoLenth >= 0)
                infoWord.setSpan(new ForegroundColorSpan(Color.BLUE), infoLenth, infoLenth + editSearch.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (OperatorName.length() > 0) {
            int color = Color.WHITE;

            switch (ussd.Operator) {
                case MainPage.olife:
                    color = context.getResources().getColor(R.color.colorLife);
                    break;

                case MainPage.omts:
                    color = context.getResources().getColor(R.color.colorMTS);
                    break;

                case MainPage.ovelcom:
                case MainPage.ovelcomkorp:
                case MainPage.ovelcomprivet:
                    color = context.getResources().getColor(R.color.colorVelcom);
                    break;

                case MainPage.oother:
                    color = context.getResources().getColor(R.color.colorOther);
                    break;

                case MainPage.oeditable:
                    color = context.getResources().getColor(R.color.clorEditable);
                    break;
            }

            codeWord.setSpan(new ForegroundColorSpan(color), ussd.USSDCode.length() + 1, ussd.USSDCode.length() + OperatorName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            codeWord.setSpan(new RelativeSizeSpan(0.6f), ussd.USSDCode.length() + 1, ussd.USSDCode.length() + OperatorName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        codeView.setText(codeWord);
        infoView.setText(infoWord);


        return convertView;
    }

    String getOperatorName(USSD ussd) {
        if (MainPage.CurMobileOperator == MainPage.ofavorites)
            return " " + MainPage.ConstToMobileOperator(ussd.Operator);
        else
            return "";
    }
}