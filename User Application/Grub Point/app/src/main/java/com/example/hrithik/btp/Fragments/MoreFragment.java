package com.example.hrithik.btp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hrithik.btp.Activity.LoginActivity;
import com.example.hrithik.btp.Activity.SetupActivity;
import com.example.hrithik.btp.R;
import com.example.hrithik.btp.Activity.PhoneVerificationActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MoreFragment extends Fragment {
    String[] mobileArray = {"Profile","Refer and Earn","FAQ","Rate Us","About Us","Contact Us",
            "Sign Out"};
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);
        ArrayAdapter adapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()),android.R.layout.simple_list_item_1, mobileArray);
        ListView listView =  rootView.findViewById(R.id.lv);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 6)
                {
                    FirebaseAuth.getInstance().signOut();
                    Intent otpv = new Intent(getContext(), PhoneVerificationActivity.class);
                    otpv.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(otpv);

                } else if(position == 0){

                    startActivity(new Intent(getActivity(), SetupActivity.class));

                }



            }
        });
        return rootView;
    }
}
