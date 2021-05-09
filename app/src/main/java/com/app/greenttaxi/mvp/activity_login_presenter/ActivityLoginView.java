package com.app.greenttaxi.mvp.activity_login_presenter;


import com.app.greenttaxi.models.UserModel;

public interface ActivityLoginView {
    void onUserFound(UserModel userModel);
    void onUserNoFound();
    void onFailed(String msg);


}
