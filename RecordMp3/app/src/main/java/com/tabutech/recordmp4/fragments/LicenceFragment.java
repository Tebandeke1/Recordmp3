package com.tabutech.recordmp4.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tabutech.recordmp4.R;


public class LicenceFragment extends DialogFragment {

    public LicenceFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogue = inflater.inflate(R.layout.fragment_licence,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(dialogue)
                .setTitle(getString(R.string.licence_title))
                .setNeutralButton(android.R.string.ok,null);

        return builder.create();

    }
}