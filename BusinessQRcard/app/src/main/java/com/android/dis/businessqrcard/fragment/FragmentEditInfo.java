package com.android.dis.businessqrcard.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.dis.businessqrcard.R;

public class FragmentEditInfo  extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.edit_info_layout;
    public static Context frg_context;

    public static EditText editText_fio, editText_company, editText_email, editText_phone;
    public Button saveBtn;
    public static String str_fio,str_company,str_email,str_phone;
    public SharedPreferences sPref;

    final String SAVED_FIO = "FIO";
    final String SAVED_COMPANY = "COMPANY";
    final String SAVED_EMAIL = "EMAIL";
    final String SAVED_PHONE = "PHONE";

    public static FragmentEditInfo getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentEditInfo fragment = new FragmentEditInfo();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.edit_info));
        frg_context = context;

        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(LAYOUT, container, false);

        editText_fio = (EditText) view.findViewById(R.id.editText_fio);
        editText_company = (EditText) view.findViewById(R.id.editText_company);
        editText_email = (EditText) view.findViewById(R.id.editText_email);
        editText_phone = (EditText) view.findViewById(R.id.editText_phone);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                str_fio = editText_fio.getText().toString();
                str_company = editText_company.getText().toString();
                str_email = editText_email.getText().toString();
                str_phone = editText_phone.getText().toString();
                saveText();
                FragmentShowQR.createQRcode();
            }
        });

        loadText();

        return view;
    }

    void saveText() {
        sPref = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_FIO, editText_fio.getText().toString());
        ed.putString(SAVED_COMPANY, editText_company.getText().toString());
        ed.putString(SAVED_EMAIL, editText_email.getText().toString());
        ed.putString(SAVED_PHONE, editText_phone.getText().toString());
        ed.commit();
    }

    void loadText() {
        sPref = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_FIO, "");
        String savedText_2 = sPref.getString(SAVED_COMPANY, "");
        String savedText_3 = sPref.getString(SAVED_EMAIL, "");
        String savedText_4 = sPref.getString(SAVED_PHONE, "");
        editText_fio.setText(savedText);
        editText_company.setText(savedText_2);
        editText_email.setText(savedText_3);
        editText_phone.setText(savedText_4);
        str_fio = savedText.toString();
        str_company = savedText_2.toString();
        str_email = savedText_3.toString();
        str_phone = savedText_4.toString();
    }
}
