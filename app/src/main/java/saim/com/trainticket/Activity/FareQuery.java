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

    TextView inputFromList, inputToList, inputTrainList, inputTicketList;
    Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_query);

        init();

    }

    private void init() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        inputFromList = (TextView) findViewById(R.id.inputFromList);
        inputToList = (TextView) findViewById(R.id.inputToList);
        inputTrainList = (TextView) findViewById(R.id.inputTrainList);
        inputTicketList = (TextView) findViewById(R.id.inputTicketList);

        btnSearch = (Button) findViewById(R.id.btnSearch);

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
                GET_TO_LIST((String)parent.getItemAtPosition(position));
                inputToList.setVisibility(View.VISIBLE);
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
}
