package saim.com.trainticket.Activity;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import saim.com.trainticket.R;
import saim.com.trainticket.Utils.ApiURL;
import saim.com.trainticket.Utils.MySingleton;
import saim.com.trainticket.Utils.SharedPrefDatabase;

public class Login extends AppCompatActivity {

    ProgressDialog progressDialog;

    LinearLayout layoutLoginMain, layoutRegistrationMain;
    TextView txtTarmsCondition, txtDeveloper;

    //Login
    EditText inputEmail, inputPassword;
    Button btnLogin;
    TextView txtForgetPassword, txtRegistration;

    //Registration
    EditText inputNameR, inputMobileR, inputEmailR, inputPasswordR, inputPasswordRC;
    Button btnRegistration;
    TextView txtLoginBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeLogin);
        setContentView(R.layout.activity_login);

        init();
    }

    public void init(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait checking your information.");
        progressDialog.setCanceledOnTouchOutside(false);

        txtTarmsCondition = (TextView) findViewById(R.id.txtTarmsCondition);
        txtDeveloper = (TextView) findViewById(R.id.txtDeveloper);
        layoutLoginMain = (LinearLayout) findViewById(R.id.layoutLoginMain);
        layoutRegistrationMain = (LinearLayout) findViewById(R.id.layoutRegistrationMain);

        //Login
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtForgetPassword = (TextView) findViewById(R.id.txtForgetPassword);
        txtRegistration = (TextView) findViewById(R.id.txtRegistration);

        //Registration
        inputNameR = (EditText) findViewById(R.id.inputNameR);
        inputMobileR = (EditText) findViewById(R.id.inputMobileR);
        inputEmailR = (EditText) findViewById(R.id.inputEmailR);
        inputPasswordR = (EditText) findViewById(R.id.inputPasswordR);
        inputPasswordRC = (EditText) findViewById(R.id.inputPasswordRC);
        btnRegistration = (Button) findViewById(R.id.btnRegistration);
        txtLoginBack = (TextView) findViewById(R.id.txtLoginBack);


        ButtonClicked();
    }

    public void ButtonClicked(){

        txtRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.SlideOutRight).duration(250).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        layoutLoginMain.setVisibility(View.GONE);
                        layoutRegistrationMain.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInLeft).duration(250).playOn(layoutRegistrationMain);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(layoutLoginMain);
            }
        });

        txtLoginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.SlideOutLeft).duration(250).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        layoutRegistrationMain.setVisibility(View.GONE);
                        layoutLoginMain.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInRight).duration(250).playOn(layoutLoginMain);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(layoutRegistrationMain);
            }
        });

        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), ActivityForgetPassword.class));
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inputEmail.getText().toString().isEmpty() || inputPassword.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Email or Password can not be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    SaveUserLogin(inputEmail.getText().toString(), inputPassword.getText().toString());
                }
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputNameR.getText().toString().isEmpty() || inputMobileR.getText().toString().isEmpty() ||
                        inputEmailR.getText().toString().isEmpty() || inputPasswordR.getText().toString().isEmpty() ||
                        inputPasswordRC.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Input field can not be empty!", Toast.LENGTH_SHORT).show();

                } else {
                    if (!inputPasswordR.getText().toString().equals(inputPasswordRC.getText().toString())){
                        Toast.makeText(getApplicationContext(), "Password not matched!", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.show();
                        UserRegistraion(inputNameR.getText().toString(), inputMobileR.getText().toString(), inputEmailR.getText().toString(), inputPasswordR.getText().toString());
                    }
                }
            }
        });
    }


    public void SaveUserLogin(final String email, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiURL.getLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d("LOGIN_REQUEST", response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("success")){

                                JSONArray jsonArrayUser = jsonObject.getJSONArray("user");
                                for (int i=0; i<jsonArrayUser.length(); i++) {

                                    JSONObject jsonObjectUser = jsonArrayUser.getJSONObject(i);

                                    String user_id = jsonObjectUser.getString("user_id");
                                    String user_name = jsonObjectUser.getString("user_name");
                                    String user_email = jsonObjectUser.getString("user_email");
                                    String user_mobile = jsonObjectUser.getString("user_mobile");
                                    String user_pass = jsonObjectUser.getString("user_pass");
                                    String user_image = jsonObjectUser.getString("user_image");
                                    String user_address = jsonObjectUser.getString("user_address");


                                    new SharedPrefDatabase(getApplicationContext()).StoreLOGIN_STATUS(true);
                                    new SharedPrefDatabase(getApplicationContext()).StoreID(user_id);
                                    new SharedPrefDatabase(getApplicationContext()).StoreNAME(user_name);
                                    new SharedPrefDatabase(getApplicationContext()).StoreEMAIL(user_email);
                                    new SharedPrefDatabase(getApplicationContext()).StoreMOBILE(user_mobile);
                                    new SharedPrefDatabase(getApplicationContext()).StorePASS(user_pass);
                                    new SharedPrefDatabase(getApplicationContext()).StoreMOBILE(user_image);
                                    new SharedPrefDatabase(getApplicationContext()).StoreMOBILE(user_address);

                                }
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();

                            }else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            Log.d("HDHD ", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("user_email", email);
                params.put("user_pass", password);

                return params;
            }
        };
        stringRequest.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public void UserRegistraion(final String name, final String mobile, final String email, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiURL.getRegistration,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("success")){
                                txtLoginBack.performClick();
                                String message = jsonObject.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Log.d("HDHD ", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("user_name", name);
                params.put("user_email", mobile);
                params.put("user_mobile", email);
                params.put("user_pass", password);

                return params;
            }
        };
        stringRequest.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
