package com.android.dis.businessqrcard.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.dis.businessqrcard.Contents;
import com.android.dis.businessqrcard.MainActivity;
import com.android.dis.businessqrcard.QRCodeEncoder;
import com.android.dis.businessqrcard.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

/**
 * Created by User on 24.02.2016.
 */
public class FragmentShowQR  extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.show_qr_layout;
    public static Context frg_context;
    public static ImageView myImage;

    public static FragmentShowQR getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentShowQR fragment = new FragmentShowQR();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.show_qr));
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

        myImage = (ImageView) view.findViewById(R.id.img_qr_code);
        createQRcode();

        return view;
    }

    public static void createQRcode(){

        String qrInputText = FragmentEditInfo.str_fio + "\n" + FragmentEditInfo.str_company
                + "\n" + FragmentEditInfo.str_email + "\n" + FragmentEditInfo.str_phone;

        int smallerDimension = MainActivity.smallerDimension;

        //Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();

            myImage.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
