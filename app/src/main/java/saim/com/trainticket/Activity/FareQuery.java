package saim.com.trainticket.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import saim.com.trainticket.R;
import saim.com.trainticket.Utils.ApiURL;
import saim.com.trainticket.Utils.MySingleton;
import saim.com.trainticket.Utils.SharedPrefDatabase;

public class FareQuery extends AppCompatActivity {

    ProgressDialog progressDialog;
    ArrayList<String> fromList = new ArrayList<>();
    ArrayList<String> toList = new ArrayList<>();
    ArrayList<String> trainList = new ArrayList<>();

    TextView inputFromList, inputToList, inputTrainList, inputTicketList;
    Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_query);

        init();

    }

    private void init() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Fare Query");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        inputFromList = (TextView) findViewById(R.id.inputFromList);
        inputToList = (TextView) findViewById(R.id.inputToList);
        inputTrainList = (TextView) findViewById(R.id.inputTrainList);
        inputTicketList = (TextView) findViewById(R.id.inputTicketList);

        inputToList.setVisibility(View.GONE);
        inputTrainList.setVisibility(View.GONE);
        inputTicketList.setVisibility(View.GONE);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        btnSearch.setVisibility(View.GONE);

        inputFromList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogView(fromList, inputFromList);
            }
        });

        inputToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogView(toList, inputToList);
            }
        });

        inputTrainList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogView(trainList, inputTrainList);
            }
        });

        GET_FROM_LIST();
    }


    public void showDialogView(ArrayList<String> fList, final TextView textView) {

        LayoutInflater factory = LayoutInflater.from(FareQuery.this);
        final View infoDialogView = factory.inflate(R.layout.dialog_from_list, null);
        final AlertDialog infoDialog = new AlertDialog.Builder(FareQuery.this).create();
        infoDialog.setView(infoDialogView);
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        EditText inputFList = infoDialogView.findViewById(R.id.inputSearch);


        ListView listDialog = (ListView) infoDialogView.findViewById(R.id.listDialog);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, fList);
        listDialog.setAdapter(arrayAdapter);

        inputFList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                arrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView.setText((String)parent.getItemAtPosition(position));

                if (textView.getId() == inputFromList.getId()) {
                    toList.clear();
                    GET_TO_LIST((String)parent.getItemAtPosition(position));
                } else if (textView.getId() == inputToList.getId()) {
                    trainList.clear();
                    GET_FARE_LIST(inputFromList.getText().toString() ,(String)parent.getItemAtPosition(position));
                }

                infoDialog.dismiss();
            }
        });
        infoDialog.show();

    }


    public void GET_FROM_LIST( ){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiURL.getFromLocation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        inputFromList.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("success")){

                                JSONArray jsonArrayUser = jsonObject.getJSONArray("result");
                                for (int i=0; i<jsonArrayUser.length(); i++) {

                                    JSONObject jsonObjectUser = jsonArrayUser.getJSONObject(i);

                                    String station_from = jsonObjectUser.getString("station_from");
                                    fromList.add(station_from);
                                }

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
                Log.d("HDHD ", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("user_id", new SharedPrefDatabase(getApplicationContext()).RetriveID());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        stringRequest.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public void GET_TO_LIST(final String FROM_STATION_NAME){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiURL.getToLocation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        inputToList.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("success")){

                                JSONArray jsonArrayUser = jsonObject.getJSONArray("result");
                                for (int i=0; i<jsonArrayUser.length(); i++) {

                                    JSONObject jsonObjectUser = jsonArrayUser.getJSONObject(i);

                                    String station_to = jsonObjectUser.getString("station_to");
                                    toList.add(station_to);

                                }

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
                Log.d("HDHD ", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("user_id", new SharedPrefDatabase(getApplicationContext()).RetriveID());
                params.put("from", FROM_STATION_NAME);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        stringRequest.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public void GET_FARE_LIST(final String FROM_STATION_NAME, final String TO_STATION_NAME){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiURL.getFareList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        inputTrainList.setVisibility(View.GONE);
                        btnSearch.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("success")){

                                String id = "";
                                String train_station_start = "";
                                String train_station_end = "";
                                String distance = "";
                                String second_simple = "";
                                String second_mail = "";
                                String commuter = "";
                                String sulov = "";
                                String shovon = "";
                                String sovon_chair = "";
                                String first_chair = "";
                                String first_birth = "";
                                String sigdha = "";
                                String ac_chair = "";
                                String ac_birth = "";

                                JSONArray jsonArrayUser = jsonObject.getJSONArray("result");
                                for (int i=0; i<jsonArrayUser.length(); i++) {

                                    JSONObject jsonObjectUser = jsonArrayUser.getJSONObject(i);

                                    id = jsonObjectUser.getString("id");
                                    train_station_start = jsonObjectUser.getString("train_station_start");
                                    train_station_end = jsonObjectUser.getString("train_station_end");
                                    distance = jsonObjectUser.getString("distance");
                                    second_simple = jsonObjectUser.getString("second_simple");
                                    second_mail = jsonObjectUser.getString("second_mail");
                                    commuter = jsonObjectUser.getString("commuter");
                                    sulov = jsonObjectUser.getString("sulov");
                                    shovon = jsonObjectUser.getString("shovon");
                                    sovon_chair = jsonObjectUser.getString("sovon_chair");
                                    first_chair = jsonObjectUser.getString("first_chair");
                                    first_birth = jsonObjectUser.getString("first_birth");
                                    sigdha = jsonObjectUser.getString("sigdha");
                                    ac_chair = jsonObjectUser.getString("ac_chair");
                                    ac_birth = jsonObjectUser.getString("ac_birth");

                                }
                                showFareDialogView(train_station_start, train_station_end, distance, second_simple, second_mail, commuter, sulov, shovon, sovon_chair, first_chair, first_birth, sigdha, ac_chair, ac_birth);

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
                Log.d("HDHD ", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("user_id", new SharedPrefDatabase(getApplicationContext()).RetriveID());
                params.put("from", FROM_STATION_NAME);
                params.put("to", TO_STATION_NAME);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        stringRequest.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    public void showFareDialogView(String dFrom, String dTo, String dDistance, String d2ndSimple, String d2ndMail, String dCommuter,
                                   String dSulov, String dShovon, String dShovonSit, String d1sChair, String d1sBirth, String bSigdha,
                                   String dAcChair, String dAcBirth) {

        LayoutInflater factory = LayoutInflater.from(FareQuery.this);
        final View infoDialogView = factory.inflate(R.layout.dialog_fare_list, null);
        final AlertDialog infoDialog = new AlertDialog.Builder(FareQuery.this).create();
        infoDialog.setView(infoDialogView);
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        TextView txtDialogFrom = infoDialogView.findViewById(R.id.txtDialogFrom);
        TextView txtDialogTo = infoDialogView.findViewById(R.id.txtDialogTo);
        TextView txtDialogDistance = infoDialogView.findViewById(R.id.txtDialogDistance);
        TextView txtDialog2ndSimple = infoDialogView.findViewById(R.id.txtDialog2ndSimple);
        TextView txtDialog2ndMail = infoDialogView.findViewById(R.id.txtDialog2ndMail);
        TextView txtDialogCommuter = infoDialogView.findViewById(R.id.txtDialogCommuter);
        TextView txtDialogSulov = infoDialogView.findViewById(R.id.txtDialogSulov);
        TextView txtDialogShovon = infoDialogView.findViewById(R.id.txtDialogShovon);
        TextView txtDialogShovonSit = infoDialogView.findViewById(R.id.txtDialogShovonSit);
        TextView txtDialog1stChair = infoDialogView.findViewById(R.id.txtDialog1stChair);
        TextView txtDialog1stBirth = infoDialogView.findViewById(R.id.txtDialog1stBirth);
        TextView txtDialogSnigha = infoDialogView.findViewById(R.id.txtDialogSnigha);
        TextView txtDialogAcChair = infoDialogView.findViewById(R.id.txtDialogAcChair);
        TextView txtDialogAcBirth = infoDialogView.findViewById(R.id.txtDialogAcBirth);

        txtDialogFrom.setText(dFrom);
        txtDialogTo.setText(dTo);
        txtDialogDistance.setText(dDistance + " KM");
        txtDialog2ndSimple.setText(d2ndSimple + " tk");
        txtDialog2ndMail.setText(d2ndMail + " tk");
        txtDialogCommuter.setText(dCommuter + " tk");
        txtDialogSulov.setText(dSulov + " tk");
        txtDialogShovon.setText(dShovon + " tk");
        txtDialogShovonSit.setText(dShovonSit + " tk");
        txtDialog1stChair.setText(d1sChair + " tk");
        txtDialog1stBirth.setText(d1sBirth + " tk");
        txtDialogSnigha.setText(bSigdha + " tk");
        txtDialogAcChair.setText(dAcChair + " tk");
        txtDialogAcBirth.setText(dAcBirth + " tk");

        infoDialog.show();

    }


}
