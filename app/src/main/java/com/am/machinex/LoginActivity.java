package com.am.machinex;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText inputEmail, inputPassword;
    String androidID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.appbarColor));
        }
        HttpsTrustManager.allowAllSSL();
        androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        init();
    }

    public void init() {
        inputEmail = (EditText) findViewById(R.id.edit_email);
        inputPassword = (EditText) findViewById(R.id.edit_password);
        btnLogin = (Button) findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputEmail.getText().length() <= 0 || inputPassword.getText().length() <= 0) {
                    Errorcustomdialog("Fields cannot be empty", "Error");
                } else {
                    GETLogin(inputEmail.getText().toString().trim(), inputPassword.getText().toString().trim());

                }
            }
        });
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

    public void GETLogin(String username, String password) {
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://artlive.artisticmilliners.com:8081/ords/art/apis/auth/" + username + "/" + password, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                Login login;
                try {
                    JSONArray obj = response.getJSONArray("items");
                    Log.e("obj", obj.toString());
                   if (obj.length() == 0){
                       Errorcustomdialog("Check Your Username and Password", "Error");

                   }else {
                       for (int i = 0; i < obj.length(); i++) {
                           JSONObject jsonObject = obj.getJSONObject(i);
                           String user_id = jsonObject.getString("user_id");
                           String user_name = jsonObject.getString("user_name");
                           String empname = jsonObject.getString("empname");
                           String dept = jsonObject.getString("dept");
                           int rtn_val = jsonObject.getInt("rtn_val");
                        /*tv_fetch_article.setText(art_fancy_name);
                        tv_fetch_qty.setText(prodqty);*/
                           Log.e("user_id", user_id);
                           Log.e("user_name", user_name);
                           Log.e("empname", empname);
                           Log.e("dept", dept);
                           Log.e("dept", dept);
                           Log.e("rtn_val", "" + rtn_val);
                           // check the other values like this so on..
                           Intent history_intent = new Intent(LoginActivity.this, MenuActivity.class);
                           history_intent.putExtra("user_id", user_id);
                           history_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                           startActivity(history_intent);
                           finish();

                       }
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
                if (error.toString().contains("TimeoutError")) {
                    Errorcustomdialog("Check your internet connection", "Timeout");
                }
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    public void Errorcustomdialog(String msg, String timeout) {
        final Dialog dialog = new Dialog(LoginActivity.this);
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
