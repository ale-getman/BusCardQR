package com.android.dis.businessqrcard.fragment;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dis.businessqrcard.CameraPreview;
import com.android.dis.businessqrcard.R;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 24.02.2016.
 */
public class FragmentScanQR  extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.scan_qr_layout;
    public static Context frg_context;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private FrameLayout preview;
    private TextView scanText;
    private ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private String lastScannedCode;
    private Image codeImage;
    public Button addContactBtn;
    public ImageButton flashBtn;
    public boolean flag_flash = true;
    public List<String> flashModes;
    public Button onCamera;

    static {
        System.loadLibrary("iconv");
    }

    public static FragmentScanQR getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentScanQR fragment = new FragmentScanQR();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.scan_qr));
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

        autoFocusHandler = new Handler();

        preview = (FrameLayout) view.findViewById(R.id.cameraPreview);
        addContactBtn = (Button) view.findViewById(R.id.addContact);
        flashBtn = (ImageButton) view.findViewById(R.id.flashBtn);
        flashBtn.setVisibility(View.GONE);
        onCamera = (Button) view.findViewById(R.id.onCamera);

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        scanText = (TextView) view.findViewById(R.id.scanText);


        addContactBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              if(barcodeScanned) {
                  addContactToBook();
                  Toast.makeText(frg_context, "Контакт добавлен", Toast.LENGTH_SHORT).show();
              }
            }
        });

        onCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resumeCamera();
                //flashBtn.setVisibility(View.VISIBLE);
                onCamera.setVisibility(View.GONE);
            }
        });

        flashBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Camera.Parameters params = mCamera.getParameters();
                if(flag_flash) {
                    params.setFlashMode(flashModes.get(3));
                    mCamera.setParameters(params);
                    flag_flash = !flag_flash;
                }
                else
                {
                    params.setFlashMode(flashModes.get(0));
                    mCamera.setParameters(params);
                    flag_flash = !flag_flash;
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LOGI", "onResume");
        if(onCamera.getVisibility() == View.GONE)
            resumeCamera();
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
        Log.d("LOGI", "onPause");
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            //
        }
        Log.d("LOGI", "getCameraInstance");
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.cancelAutoFocus();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        Log.d("LOGI", "releaseCamera");
    }

    private void resumeCamera() {
        scanText.setText(getString(R.string.scan_process_label));
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(frg_context, mCamera, previewCb, autoFocusCB);
        preview.removeAllViews();
        preview.addView(mPreview);
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            codeImage = new Image(size.width, size.height, "Y800");
            previewing = true;
            mPreview.refreshDrawableState();
        }
        flashModes = mCamera.getParameters().getSupportedFlashModes();
        Log.d("LOGI", "resumeCAmera");
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing && mCamera != null) {
                mCamera.autoFocus(autoFocusCB);
            }
            Log.d("LOGI", "doAutoFocus");
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
//            Log.d("CameraTestActivity", "onPreviewFrame data length = " + (data != null ? data.length : 0));
            codeImage.setData(data);
            int result = scanner.scanImage(codeImage);
            if (result != 0) {
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    lastScannedCode = sym.getData();
                    if (lastScannedCode != null) {
                        scanText.setText(getString(R.string.scan_result_label) +"\n"+ lastScannedCode);
                        barcodeScanned = true;
                    }
                }
            }
            camera.addCallbackBuffer(data);
            Log.d("LOGI", "previewCB");
        }
    };

    // Mimic continuous auto-focusing
    final Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    public void addContactToBook(){

        String scannedCode[] = lastScannedCode.split("\n");

        Uri rawContactUri = this.getActivity().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, new ContentValues());
        /* Получаем id добавленного контакта */
        long rawContactId =  ContentUris.parseId(rawContactUri);

        ContentValues values = new ContentValues();

        /* Связываем наш аккаунт с данными */
        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        /* Устанавливаем MIMETYPE для поля данных */
        values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        /* Имя для нашего аккаунта */
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, scannedCode[0]);

        this.getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        values.clear();

        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        /* Тип данных – номер телефона */
        values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        /* Номер телефона */
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, scannedCode[3]);
        /* Тип – мобильный */
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

        this.getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        values.clear();

        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        /* Тип данных – номер телефона */
        values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        /* Номер телефона */
        values.put(ContactsContract.CommonDataKinds.Email.ADDRESS, scannedCode[2]);

        this.getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        values.clear();

        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        /* Тип данных – номер телефона */
        values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
        /* Номер телефона */
        values.put(ContactsContract.CommonDataKinds.Organization.COMPANY, scannedCode[1]);

        this.getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }
}
