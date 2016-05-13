package com.william_l.wemore.Register;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.william_l.wemore.Login.Model.UserModel;
import com.william_l.wemore.R;
import com.william_l.wemore.databinding.ActivityDbRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater();
        ActivityDbRegisterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_db_register);
        UserModel userModel = new UserModel("123", "321");
        binding.setUser(userModel);

    }

    private void onTestMethod() {

        // You’re done! Run the application and you’ll see Test User in the UI. Alternatively, you can get the view via:
        ActivityDbRegisterBinding binding = ActivityDbRegisterBinding.inflate(getLayoutInflater());

        // If you are using data binding items inside a ListView or RecyclerView adapter, you may prefer to use:
        // ListItemBinding binding = ListItemBinding.inflate(layoutInflater, viewGroup, false);
        // // or
        // ListItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item, viewGroup, false);

        binding.getUser();

    }




}

