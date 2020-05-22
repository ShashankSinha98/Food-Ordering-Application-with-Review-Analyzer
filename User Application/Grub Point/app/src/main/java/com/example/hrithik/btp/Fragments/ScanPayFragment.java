package com.example.hrithik.btp.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hrithik.btp.R;
import com.example.hrithik.btp.Activity.QRScanActivity;
import com.readystatesoftware.viewbadger.BadgeView;

import static android.support.v4.content.ContextCompat.checkSelfPermission;


public class ScanPayFragment extends Fragment {
    ImageView scanpaybutton;
    public static TextView scanTapTV;


    private static final int REQUEST_CAMERA = 2;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scanpay, container, false);
        scanpaybutton = rootView.findViewById(R.id.scan_iv);
        scanTapTV = rootView.findViewById(R.id.tap_to_scan_tv);

        Typeface pacifico = Typeface.createFromAsset(getContext().getAssets(), "fonts/pacifico.ttf");
        scanTapTV.setTypeface(pacifico);


        BadgeView badgeView = new BadgeView(getContext(), scanpaybutton);


        scanpaybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getContext(), QRScanActivity.class);
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
            }
        });
        return rootView;
    }
}
