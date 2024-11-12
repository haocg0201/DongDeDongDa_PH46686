package com.haocg.myfootballapplication.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.haocg.myfootballapplication.LoginMainActivity;
import com.haocg.myfootballapplication.R;

public class OnboardingFragment3 extends Fragment {
    private Button btnGetStart;
    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_onboarding3, container, false);

        btnGetStart = mView.findViewById(R.id.btnGetStart);
        btnGetStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginMainActivity.class);
                requireActivity().startActivity(intent);
                requireActivity().finish();
            }
        });
        return mView;
    }
}