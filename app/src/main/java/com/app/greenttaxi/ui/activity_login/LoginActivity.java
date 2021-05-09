package com.app.greenttaxi.ui.activity_login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;


import com.app.greenttaxi.R;
import com.app.greenttaxi.databinding.ActivityLoginBinding;
import com.app.greenttaxi.language.Language;
import com.app.greenttaxi.models.LoginModel;
import com.app.greenttaxi.models.UserModel;
import com.app.greenttaxi.mvp.activity_login_presenter.ActivityLoginPresenter;
import com.app.greenttaxi.mvp.activity_login_presenter.ActivityLoginView;
import com.app.greenttaxi.share.Common;
import com.app.location_service.LocationService;
import com.app.preferences.Preferences;


import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity implements ActivityLoginView {
    public static ProgressDialog dialog;
    private ActivityLoginBinding binding;
    private LoginModel model;
    private ActivityLoginPresenter presenter;
    private double lat=0.0,lng=0.0;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int READ_REQ = 1;
    private final String gps_perm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int gps_req = 22;

    public  void showprogress() {
         dialog = Common.createProgressDialog(this, this.getString(R.string.update_location));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
       // dialog.show();        //dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       // dialog.setMessage("Bitte warten, verbinde mit KNX-Board");
        //dialog.setIndeterminate(true);
       // dialog.show();
    }

    public static void hideprogress() {
        dialog.dismiss();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase,Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        getDataFromIntent();
        initView();
        showprogress();

    }
    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, gps_perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{gps_perm}, gps_req);
        } else {

           /* if (isGpsOpen())
            {
                StartService(LocationRequest.PRIORITY_HIGH_ACCURACY);
            }else
                {
                    CreateGpsDialog();

                }*/
        }
    }
    private void getDataFromIntent() {
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat",0.0);
        lng = intent.getDoubleExtra("lng",0.0);
    }

    private void initView() {
        model = new LoginModel();
        binding.setModel(model);
        presenter = new ActivityLoginPresenter(this,this);
        binding.btnLogin.setOnClickListener(view -> {
            presenter.checkData(model);
        });
        checkReadPermission();



    }
    public void checkReadPermission() {
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED||ActivityCompat.checkSelfPermission(this,write_permission ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM,write_permission}, READ_REQ);
        } else {
            CheckPermission();

            //SelectImage(READ_REQ);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_REQ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              //  SelectImage(requestCode);
                CheckPermission();

            } else {
                CheckPermission();

                // Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }

        }
    }


  /*  @Override
    public void onLoginValid() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("access_code",model.getAccess_code());
        startActivity(intent);
        finish();

    }*/



    @Override
    public void onUserFound(UserModel userModel) {
        Preferences.getInstance().create_update_userdata(this,userModel);
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    @Override
    public void onUserNoFound() {
        Toast.makeText(this,  getString(R.string.code_not_found), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }



}