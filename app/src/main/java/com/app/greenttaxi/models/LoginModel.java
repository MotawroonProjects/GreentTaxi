package com.app.greenttaxi.models;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;


import com.app.greenttaxi.BR;
import com.app.greenttaxi.R;

import java.io.Serializable;

public class LoginModel extends BaseObservable implements Serializable {
    private String access_code;
    public ObservableField<String> error_access_code = new ObservableField<>();

    public LoginModel() {
        access_code ="";
    }

    public boolean isDataValid(Context context){
        if (!access_code.isEmpty()){
            error_access_code.set(null);
            return true;
        }else {
            error_access_code.set(context.getString(R.string.field_req));
            return false;
        }
    }
    @Bindable
    public String getAccess_code() {
        return access_code;
    }

    public void setAccess_code(String access_code) {
        this.access_code = access_code;
        notifyPropertyChanged(BR.access_code);

    }

}
