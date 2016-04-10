package com.william_l.wemore.Login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.william_l.wemore.Login.Presenter.ILoginPresenter;
import com.william_l.wemore.Login.Presenter.LoginPresenterCompl;
import com.william_l.wemore.Login.View.ILoginView;
import com.william_l.wemore.R;


/**
 * Created by william on 2015/8/2.
 */
public class LoginActivity extends Activity implements ILoginView, View.OnClickListener {

    private EditText editUser;
    private EditText editPass;
    private Button btnLogin;
    private Button btnClear;
    private ProgressBar progressBar;

    private ILoginPresenter loginPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        editUser = (EditText) this.findViewById(R.id.et_login_username);
        editPass = (EditText) this.findViewById(R.id.et_login_password);
        btnLogin = (Button) this.findViewById(R.id.btn_login_login);
        btnClear = (Button) this.findViewById(R.id.btn_login_clear);
        progressBar = (ProgressBar) this.findViewById(R.id.progress_login);


        btnLogin.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        loginPresenter = new LoginPresenterCompl(this);
        loginPresenter.setProgressBarVisiblity(View.INVISIBLE);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login_clear:
                loginPresenter.clean();
                break;
            case R.id.btn_login_login:
                loginPresenter.setProgressBarVisiblity(View.VISIBLE);
                btnLogin.setEnabled(false);
                btnClear.setEnabled(false);
                loginPresenter.doLogin(editUser.getText().toString(), editPass.getText().toString());
                break;
        }

    }

    @Override
    public void clearText() {

        editUser.setText("");
        editPass.setText("");

    }

    @Override
    public void onLoginResult(Boolean result, int code) {

        loginPresenter.setProgressBarVisiblity(View.INVISIBLE);
        btnLogin.setEnabled(true);
        btnClear.setEnabled(true);
        if (result) {
            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Login Fail, code = " + code, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setProgressBarVisiblity(int visibility) {
        progressBar.setVisibility(visibility);
    }
}
