package saim.com.trainticket.Activity;

import android.app.ProgressDialog;
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

public class TicketPurches extends AppCompatActivity {

    ProgressDialog progressDialog;
    ArrayList<String> fromList = new ArrayList<>();
    ArrayList<String> toList = new ArrayList<>();
    ArrayList<String> trainList = new ArrayList<>();

    TextView inputFromList, inputToList, inputTrainList, inputTicketList;
    Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_purches);

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

        LayoutInflater factory = LayoutInflater.from(TicketPurches.this);
        final View infoDialogView = factory.inflate(R.layout.dialog_from_list, null);
        final AlertDialog infoDialog = new AlertDialog.Builder(TicketPurches.this).create();
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
                    GET_TRAIN_LIST(inputFromList.getText().toString() ,(String)parent.getItemAtPosition(position));
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


    public void GET_TRAIN_LIST(final String FROM_STATION_NAME, final String TO_STATION_NAME){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiURL.getTrainList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        inputTrainList.setVisibility(View.VISIBLE);
                        btnSearch.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("success")){

                                String train_name = "";
                                JSONArray jsonArrayUser = jsonObject.getJSONArray("result");
                                for (int i=0; i<jsonArrayUser.length(); i++) {

                                    JSONObject jsonObjectUser = jsonArrayUser.getJSONObject(i);

                                    train_name = jsonObjectUser.getString("train_name");
                                    trainList.add(train_name);

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
}
