package com.doomcat.twilioapp.Activities;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.doomcat.twilioapp.R;
import com.doomcat.twilioapp.Services.TranslateService;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AdminActivity extends Activity {

    private EditText mTo;
    private EditText mBody;
    private Button mSend;
    private OkHttpClient mClient = new OkHttpClient();
    private Context mContext;
    private String translatedText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mTo = (EditText) findViewById(R.id.txtNumber);
        mBody = (EditText) findViewById(R.id.txtMessage);
        mSend = (Button) findViewById(R.id.btnSend);
        mContext = getApplicationContext();


        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = mBody.getText().toString();
                TranslateService.translateMessage(userInput, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            JSONObject jsonObject = new JSONObject(jsonData);
                            translatedText = jsonObject.getString("translationText");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });

                try {
                    //So we wait for translatedText to be filled first
                    Thread.sleep(700);
                    post("https://92495faa.ngrok.io/sms", new  Callback(){

                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("test1", mTo.getText().toString());
                                    Log.d("test2", mBody.getText().toString());
                                    mTo.setText("");
                                    mBody.setText("");
                                    Toast.makeText(getApplicationContext(),"SMS Sent!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    Call post(String url, Callback callback) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("To", mTo.getText().toString())
                .add("Body", translatedText)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Call response = mClient.newCall(request);
        response.enqueue(callback);
        return response;
    }


}