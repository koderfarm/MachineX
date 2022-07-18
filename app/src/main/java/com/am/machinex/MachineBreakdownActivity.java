package com.am.machinex;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.am.machinex.adapter.FaultListAdapter;
import com.am.machinex.adapter.PartNameAdapter;
import com.am.machinex.adapter.ServiceListAdapter;
import com.am.machinex.adapter.ServiceResolvedAdapter;
import com.am.machinex.models.FaultList;
import com.am.machinex.models.PartName;
import com.am.machinex.models.ServiceListModel;
import com.am.machinex.models.ServiceResolvedModel;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MachineBreakdownActivity extends AppCompatActivity {
    String usid;
    TextView start_time, stop_time;
    Time startnow = new Time();
    Time stopnow = new Time();
    Button btn_start_stop, btn_clear, btn_submit;
    boolean start = false;
    LocalTime time;
    ArrayList<ServiceListModel> serviceListModelArrayList = new ArrayList<>();
    ArrayList<ServiceResolvedModel> serviceResolvedModelArrayList = new ArrayList<>();
    ArrayList<FaultList> faultListArrayList = new ArrayList<>();
    ArrayList<PartName> partNameArrayList = new ArrayList<>();
    ProgressBar service_list_pb, service_resolved_pb, fault_list_pb, part_name_pb;
    ServiceListAdapter arrayAdapter;
    ServiceResolvedAdapter resolvedAdapter;
    FaultListAdapter faultListAdapter;
    PartNameAdapter partNameAdapter;
    Spinner spin_service_list, spin_service_resolve, spin_fault_list, spin_part_name;
    LinearLayout rl_service_list, rl_service_resolve;
    LinearLayout  rl_fault_list, rl_part_name;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_breakdown);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.appbarColor));
        }
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void init() {
        startnow.setToNow();
        stopnow.setToNow();
        if (getIntent().getStringExtra("user_id") != null) {
            usid = getIntent().getStringExtra("user_id");
        }

        spin_service_list = (Spinner) findViewById(R.id.spin_service_list);
        spin_service_resolve = (Spinner) findViewById(R.id.spin_service_resolve);
        spin_fault_list = (Spinner) findViewById(R.id.spin_fault_list);
        spin_part_name = (Spinner) findViewById(R.id.spin_part_name);
        start_time = (TextView) findViewById(R.id.start_time);
        service_list_pb = (ProgressBar) findViewById(R.id.service_list_pb);
        rl_service_list = (LinearLayout) findViewById(R.id.service_list_rl);
        service_resolved_pb = (ProgressBar) findViewById(R.id.service_resolved_pb);
        rl_service_resolve = (LinearLayout) findViewById(R.id.service_resolved_rl);
        fault_list_pb = (ProgressBar) findViewById(R.id.fault_list_pb);
        part_name_pb = (ProgressBar) findViewById(R.id.part_name_pb);
        rl_fault_list = (LinearLayout) findViewById(R.id.fault_list_rl);
        rl_part_name = (LinearLayout) findViewById(R.id.part_name_rl);
        stop_time = (TextView) findViewById(R.id.stop_time);
        btn_start_stop = (Button) findViewById(R.id.btn_start_stop);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock();

            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                clock();

            }
        });

        getServiceList();
        getServiceResolved();
        getfault_list();
        getpartname();

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

    private SSLSocketFactory getSSLSocketFactory() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
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

    public void getServiceList() {
        service_list_pb.setVisibility(View.VISIBLE);
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://artlive.artisticmilliners.com:8081/ords/art/machinex/service_list/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                ServiceListModel serviceListModel;
                try {
                    service_list_pb.setVisibility(View.GONE);
                    rl_service_list.setVisibility(View.VISIBLE);
                    JSONArray obj = response.getJSONArray("items");
                    Log.e("obj", obj.toString());
                    serviceListModelArrayList.clear();
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String service_type = jsonObject.getString("service_type");
                        String service_type_code = jsonObject.getString("service_type_code");

                        serviceListModel = new ServiceListModel(service_type, service_type_code);
                        serviceListModelArrayList.add(serviceListModel);
                    }
                    arrayAdapter = new ServiceListAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, serviceListModelArrayList);
                    arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spin_service_list.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("E", String.valueOf(e.toString()));

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                service_list_pb.setVisibility(View.GONE);
                Log.e("ERROR", String.valueOf(error.toString()));
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    public void getServiceResolved() {
        service_resolved_pb.setVisibility(View.VISIBLE);
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://artlive.artisticmilliners.com:8081/ords/art/machinex/service_resolved/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                ServiceResolvedModel serviceResolvedModel;
                try {
                    service_resolved_pb.setVisibility(View.GONE);
                    rl_service_resolve.setVisibility(View.VISIBLE);
                    JSONArray obj = response.getJSONArray("items");
                    Log.e("obj", obj.toString());
                    serviceResolvedModelArrayList.clear();
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String user_name = jsonObject.getString("user_name");
                        int user_id = jsonObject.getInt("user_id");

                        serviceResolvedModel = new ServiceResolvedModel(user_id, user_name);
                        serviceResolvedModelArrayList.add(serviceResolvedModel);
                    }
                    resolvedAdapter = new ServiceResolvedAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, serviceResolvedModelArrayList);
                    resolvedAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spin_service_resolve.setAdapter(resolvedAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("E", String.valueOf(e.toString()));

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                service_resolved_pb.setVisibility(View.GONE);
                Log.e("ERROR", String.valueOf(error.toString()));
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    public void getfault_list() {
        fault_list_pb.setVisibility(View.VISIBLE);
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://artlive.artisticmilliners.com:8081/ords/art/machinex/fault_list", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                FaultList faultList;
                try {
                    fault_list_pb.setVisibility(View.GONE);
                    rl_fault_list.setVisibility(View.VISIBLE);
                    JSONArray obj = response.getJSONArray("items");
                    Log.e("obj", obj.toString());
                    faultListArrayList.clear();
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String machine_fault = jsonObject.getString("machine_fault");
                        String machine_fault_code = jsonObject.getString("machine_fault_code");

                        faultList = new FaultList(machine_fault, machine_fault_code);
                        faultListArrayList.add(faultList);
                    }
                    faultListAdapter = new FaultListAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, faultListArrayList);
                    faultListAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spin_fault_list.setAdapter(faultListAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("E", String.valueOf(e.toString()));

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                fault_list_pb.setVisibility(View.GONE);
                Log.e("ERROR", String.valueOf(error.toString()));
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    public void getpartname() {
        part_name_pb.setVisibility(View.VISIBLE);
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://artlive.artisticmilliners.com:8081/ords/art/machinex/part_name/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                PartName partName;
                try {
                    part_name_pb.setVisibility(View.GONE);
                    rl_part_name.setVisibility(View.VISIBLE);
                    JSONArray obj = response.getJSONArray("items");
                    Log.e("obj", obj.toString());
                    partNameArrayList.clear();
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String inventory_item_id = jsonObject.getString("inventory_item_id");
                        String description = jsonObject.getString("description");

                        partName = new PartName(inventory_item_id, description);
                        partNameArrayList.add(partName);
                    }
                    partNameAdapter = new PartNameAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, partNameArrayList);
                    partNameAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spin_part_name.setAdapter(partNameAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("E", String.valueOf(e.toString()));

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                part_name_pb.setVisibility(View.GONE);
                Log.e("ERROR", String.valueOf(error.toString()));
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    private void clock() {
        final Handler hander = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        getTime();
//                        clock();
                    }
                });
            }
        }).start();
    }

    String start_time_string, stop_time_string;

    void getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        if (!start) {
            start_time_string = dateFormat.format(new Date());
            start_time.setText(start_time_string);
            btn_start_stop.setText("Stop");
            start = true;
        } else if (start) {
            stop_time_string = dateFormat.format(new Date());
            stop_time.setText(stop_time_string);
            btn_start_stop.setText("Start");
            start = false;

        }
    }
}
