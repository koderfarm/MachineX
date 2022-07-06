package com.am.machinex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    Button enter_button, button_scan;
    EditText edit_machine_no, edit_line_no;
    RecyclerView service_types_listview;
    ProgressBar progressBar;
    Toolbar toolbar;
    TextView machinename_et;
    ServiceTypeAdapter serviceTypeAdapter;
    ArrayList<ServiceType> serviceTypeList;
    String usid;
    List<ServiceType> service_type_list;
    //qr code scanner object
    private IntentIntegrator qrScan;
    LinearLayout ll_machinename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.appbarColor));
        }
        HttpsTrustManager.allowAllSSL();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        init();
    }

    public void init() {
        service_type_list = new ArrayList<>();
        serviceTypeList = new ArrayList<>();
        enter_button = (Button) findViewById(R.id.btn_enter);
        button_scan = (Button) findViewById(R.id.button_scan);
        edit_machine_no = (EditText) findViewById(R.id.edit_machine_no);
        edit_line_no = (EditText) findViewById(R.id.edit_line_no);
        machinename_et = (TextView) findViewById(R.id.machinename_et);
        ll_machinename = (LinearLayout) findViewById(R.id.ll_machinename);
        service_types_listview = (RecyclerView) findViewById(R.id.service_types_listview);
        service_types_listview.setHasFixedSize(true);
        service_types_listview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        edit_machine_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ll_machinename.setVisibility(View.VISIBLE);
                getmachinename(edit_machine_no.getText().toString().trim());
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        if (getIntent().getStringExtra("user_id") != null) {
            usid = getIntent().getStringExtra("user_id");
        }
        //intializing scan object
        qrScan = new IntentIntegrator(this);
        setService_types_listview();
        checkButtonClick();
        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//initiating the qr code scan
                qrScan.setPrompt("Scan a barcode");
                qrScan.setCameraId(0);  // Use a specific camera of the device
                qrScan.setOrientationLocked(true);
                qrScan.setBeepEnabled(true);
                qrScan.setCaptureActivity(CaptureActivityPortrait.class);
                qrScan.initiateScan();
            }
        });
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {


                Log.d("MainActivity", "Cancelled scan");
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                edit_machine_no.setText(result.getContents());
                edit_machine_no.setEnabled(false);
                ll_machinename.setVisibility(View.VISIBLE);
                getmachinename(result.getContents());
            }
        }

    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("artlive.artisticmilliners.com:8081", session);
            }
        };
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }

    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = getResources().openRawResource(R.raw.artisticmilliners); // this cert file stored in \app\src\main\res\raw folder path

        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);

        return sslContext.getSocketFactory();
    }

    public void setService_types_listview() {
        progressBar.setVisibility(View.VISIBLE);
        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this, hurlStack);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://artlive.artisticmilliners.com:8081/ords/art/machinex/service_types/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                ServiceType serviceType;
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONArray obj = response.getJSONArray("items");
                    Log.e("obj", obj.toString());
                    serviceTypeList.clear();
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String checked_id = jsonObject.getString("check_id");
                        String check_type = jsonObject.getString("check_type");
                        String is_input = jsonObject.getString("is_input");

                        serviceType = new ServiceType(check_type, checked_id, is_input);
                        serviceTypeList.add(serviceType);

                        /*tv_fetch_article.setText(art_fancy_name);
                        tv_fetch_qty.setText(prodqty);*/
                        Log.e("checked_id", checked_id);
                        Log.e("check_type", check_type);


                    }
                    serviceTypeAdapter = new ServiceTypeAdapter(getApplicationContext(), serviceTypeList);
                    service_types_listview.setAdapter(serviceTypeAdapter);

                    /*service_types_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> myAdapter, View myView, int position, long mylng) {
                            ServiceType serviceType1 = (ServiceType) myAdapter.getItemAtPosition(position);
                            Log.e("Hello", serviceType1.getChecktype());

                        }
                    });*/
                    //String message = response.getString("message");
                    //Toast.makeText(getApplicationContext(), ""+status+message, Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("E", String.valueOf(e.toString()));

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Log.e("ERROR", String.valueOf(error.toString()));
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    public void getmachinename(String barcode) {
        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this, hurlStack);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://artlive.artisticmilliners.com:8081/ords/art/machinex/machine_info/" + barcode, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                try {
                    JSONArray obj = response.getJSONArray("items");
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String machine_serial = jsonObject.getString("machine_serial");
                        ll_machinename.setVisibility(View.VISIBLE);
                        machinename_et.setText(machine_serial);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("E", String.valueOf(e.toString()));

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("ERROR", String.valueOf(error.toString()));
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    int main_id;

    public void getmainID() {
        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this, hurlStack);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://artlive.artisticmilliners.com:8081/ords/art/machinex/maint_id/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                try {
                    JSONArray obj = response.getJSONArray("items");
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject jsonObject = obj.getJSONObject(i);
                        main_id = jsonObject.getInt("maint_id");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("E", String.valueOf(e.toString()));

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("ERROR", String.valueOf(error.toString()));
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    private void checkButtonClick() {

        enter_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                List<ServiceType> serviceTypeList = serviceTypeAdapter.arrayList;
                for (int i = 0; i < serviceTypeList.size(); i++) {
                    ServiceType serviceType = serviceTypeList.get(i);
                    Log.e("Servicetype count" + i, "check" + serviceType.isSelectedcheck());
                    Log.e("Servicetype count" + i, "uncheck" + serviceType.isSelectedunckecked());

                    service_type_list.add(serviceType);

                }
                for (int i = 0; i < service_type_list.size(); i++) {
                    if (service_type_list.get(i).isSelectedcheck()) {
                        status_ok = "Y";
                    } else {
                        status_ok = "N";
                    }
                    if (service_type_list.get(i).isSelectedunckecked()) {
                        status_nok = "Y";
                    } else {
                        status_nok = "N";
                    }
                    getmainID();
                    if (edit_machine_no.getText().toString().isEmpty()) {
                        Errorcustomdialog("Machine Number cannot be empty", "Error");
                    } else {

                        Log.e("status_ok" + status_ok, "");
                        Log.e("status_not_ok" + status_nok, "");
                        Log.e("main ID" + main_id, "");

                        postDataUsingVolley(status_ok, status_nok, main_id);

//                        get
                    }
                }


            }
        });

    }

    String status_ok, status_nok;

    private void postDataUsingVolley(String status_ok, String status_nok, int maint_id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving ...");
        progressDialog.show();
        String url = "https://artlive.artisticmilliners.com:8081/ords/art/machinex/service_done/?status_ok=" + status_ok + "&status_nok=" + status_nok + "&maint_id=" + maint_id;
        Log.e("URL SAVE", url);
        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this, hurlStack);

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                edit_machine_no.setText("");
                successcustomdialog("Data Created Successfully");

                //customdialog();

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Log.e("Error", error.toString());
                progressDialog.dismiss();
                if (error.toString().contains("TimeoutError")) {
                    edit_machine_no.setText("");
                    Errorcustomdialog("Check your internet connection", "Timeout");
                } else {
                    edit_machine_no.setText("");
                    Errorcustomdialog("Check your internet connection", "Internet Connection");
                }
                //                Toast.makeText(AddRollsActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("status_ok", status_ok);
                params.put("status_nok", status_nok);
                params.put("maint_id", "" + maint_id);

                // at last we are
                // returning our params.
                return params;
            }
        };
        //10000 is the time in milliseconds adn is equal to 10 sec
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // below line is to make
        // a json object request.
        queue.add(request);
    }
/*
 private void postDataUsingVolley(String machine_no, int ServiceType, String user_id, String line_no, String input) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving ...");
        progressDialog.show();
        String url = "https://artlive.artisticmilliners.com:8081/ords/art/machinex/service_done/?mac_no=" + machine_no + "&service_type=" + ServiceType + "&usid=" + user_id + "&lineno=" + line_no + "&input_val=" + input;
        Log.e("URL SAVE", url);
        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this, hurlStack);

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                edit_machine_no.setText("");
                successcustomdialog("Data Created Successfully");

                //customdialog();

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Log.e("Error", error.toString());
                progressDialog.dismiss();
                if (error.toString().contains("TimeoutError")) {
                    edit_machine_no.setText("");
                    Errorcustomdialog("Check your internet connection", "Timeout");
                } else {
                    edit_machine_no.setText("");
                    Errorcustomdialog("Check your internet connection", "Internet Connection");
                }
                //                Toast.makeText(AddRollsActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("mac_no", machine_no);
                params.put("service_type", "" + ServiceType);
                params.put("usid", user_id);

                // at last we are
                // returning our params.
                return params;
            }
        };
        //10000 is the time in milliseconds adn is equal to 10 sec
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // below line is to make
        // a json object request.
        queue.add(request);
    }
*/

    public void successcustomdialog(String msg) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.customdialogbox);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView dialogtext = (TextView) dialog.findViewById(R.id.text_error);
        dialogtext.setText(msg);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void Errorcustomdialog(String msg, String timeout) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.error_dialogbox);
        Button dialogButton = (Button) dialog.findViewById(R.id.buttonOk);
        TextView error_heading = (TextView) dialog.findViewById(R.id.error_heading);
        TextView dialogtext = (TextView) dialog.findViewById(R.id.text_error);
        dialogtext.setText(msg);
        error_heading.setText(timeout);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}