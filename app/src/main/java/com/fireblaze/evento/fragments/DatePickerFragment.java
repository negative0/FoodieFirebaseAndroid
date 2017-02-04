package com.fireblaze.evento.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by chait on 1/29/2017.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    public interface dateSetListener{
        void dateSet(int year, int month, int day, int hour, int min);
    }
    dateSetListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        listener.dateSet(i,i1,i2,0,0);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (dateSetListener) getActivity();
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() + "must implement listener" );
        }
    }
}