package de.codeyourapp.tagmyplant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;


public class MainActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // call of the function for the scanner below
        codeScanner();
    }

    private void codeScanner() {
        // create a net object for the text view element
        TextView tv_textView = findViewById(R.id.tv_textView);

        // get the scanner_view object that was created in the activity_main.xml layout file
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        // create a new code scanner object and give it the context (superior class) and the scanner_View
        mCodeScanner = new CodeScanner(this, scannerView);

        // major adjustments at the Code_Scanner object:
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);    // which camera is used: back camera
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);   // what formats can be scanned: all formats
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);  // auto focus operates in fixed intervals
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);      // scanner doesn't stop scanning
        mCodeScanner.setAutoFocusEnabled(true);             // autofocus is enabled by default
        mCodeScanner.setFlashEnabled(false);                // flash lite is disabled by default

        // specify actions when the scanner decodes something:
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override   // override the function "onDecoded" of the supertype mCodeScanner
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {       // define a new Runnable that is given to the UI-Thread
                    @Override
                    public void run() {                           // override the run() function of the Runnable interface
                        tv_textView.setText(result.getText());    // show the decoded text in the TextView element below the CodeScanner
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
