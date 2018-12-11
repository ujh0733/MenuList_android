package com.example.ryu.menu_subject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView option;
    private TextView textMenu, textPrice, todayMenu, todayMenuPrice;
    private String menu, price, isSpecial;
    private String ip = "192.168.42.243/androidServer/menuServer.php";
    private ProgressBar progressBar;

    private PopupWindow mPopupwindow;
    ListView listview;
    CustomAdapter customAdapter;

    private SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.allMenuList);
        customAdapter = new CustomAdapter();

        option = findViewById(R.id.option);

        todayMenu = findViewById(R.id.todayMenu);
        todayMenuPrice = findViewById(R.id.todayMenuPrice);

        textMenu = findViewById(R.id.textMenu);
        textPrice = findViewById(R.id.textPrice);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        preference = getSharedPreferences("ip", Activity.MODE_PRIVATE);
        String tempIP = preference.getString("ip", null);
        if(tempIP == null)
            ip = ip;
        else{
            ip = tempIP;
        }

        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();

        try{
            URL url = new URL("http://"+ip);
            new AsyncTask<URL, Integer, String>(){


                @Override
                protected void onProgressUpdate(Integer...values){
                    super.onProgressUpdate(values);
                    if(values.length > 0)
                        Log.i("http", String.valueOf(values[0]));
                }

                @Override
                protected void onPreExecute() {                 //작업 실행전 인터페이스 진행률 표시줄을 표시하는데 사용
                    super.onPreExecute();
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                protected String doInBackground(URL... params) {        //백그라운드에서 onPreExecute실행 완료 직후 호출 오랜시간 걸리는 백그라운드 계산 수행
                    int i = 0;
                    String result = new String();
                    if(params == null || params.length < 1) {
                        return null;
                    }
                    try{
                        publishProgress(i++);
                        HttpURLConnection connection = (HttpURLConnection)params[0].openConnection();
                        Log.i("http", String.valueOf(connection));
                        Log.i("http", "connected");
                        connection.setRequestMethod("GET");
                        //connection.setDoOutput(true);//쓰기모드 POST강제실행
                        connection.setDoInput(true);//읽기모드
                        connection.setUseCaches(false);
                        connection.setDefaultUseCaches(false);

                        publishProgress(i++);

                        InputStream is = connection.getInputStream();
                        StringBuilder builder = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        String line;
                        while((line = reader.readLine()) != null){
                            builder.append(line+"\n");
                            publishProgress(i++);
                        }
                        result = builder.toString();
                        Log.i("http", "result="+result);
                        publishProgress(i++);
                        
                    }catch(IOException me){
                        me.printStackTrace();
                    }
                    return result;
                }
                @Override
                protected void onPostExecute(String s){         //백그라운드 계산 완료 후 UI스레드에서 호출 백그라운드 계산 결과는 배개변수로 호출됨
                    super.onPostExecute(s);
                    if(s.length() == 0){
                        Toast.makeText(getApplicationContext(), "Not Data.. Check Your IP or Server...", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);

                    try{
                        //ArrayList<String> list = new ArrayList<>();
                        JSONArray array = new JSONArray(s);
                        for(int i = 0; i < array.length(); i++){
                            JSONObject jsonObject = array.optJSONObject(i);
                            menu = jsonObject.optString("menuName");
                            price = jsonObject.optString("price");
                            isSpecial = jsonObject.optString("isSpecial");

                            arrayList.add(new ArrayList<>());
                            arrayList.get(i).add(menu);
                            arrayList.get(i).add(price);

                            Log.i("menu"+i, menu);
                            Log.i("menu"+i, price);

                            if(isSpecial.equals("true")){
                                todayMenu.setText(menu);
                                todayMenuPrice.setText(price);
                            }
                        }

                        customAdapter.setList(arrayList);
                        listview.setAdapter(customAdapter);

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }.execute(url);
        }catch(MalformedURLException e){

        }
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    public void onSetting(View view) {

        Toast.makeText(this, "IP Setting Popup!!", Toast.LENGTH_SHORT).show();
        View popupView = getLayoutInflater().inflate(R.layout.ip_setting_popup, null);
        mPopupwindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mPopupwindow.setFocusable(true);

        mPopupwindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        EditText inputIP = popupView.findViewById(R.id.inputIP);
        inputIP.setText(ip);

        Button cancle = popupView.findViewById(R.id.buttonCANCLE);
        cancle.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mPopupwindow.dismiss();
                Toast.makeText(getApplicationContext(), "Cancle...", Toast.LENGTH_SHORT).show();
            }
        });

        Button ok = popupView.findViewById(R.id.buttonOK);
        ok.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mPopupwindow.dismiss();
                ip = inputIP.getText().toString();
                Toast.makeText(getApplicationContext(), "Setting ip "+ip, Toast.LENGTH_SHORT).show();

                if(preference == null) return;
                if(inputIP.getText().toString().length() == 0) return;

                SharedPreferences.Editor editor = preference.edit();
                editor.putString("ip", ip);
                editor.commit();

                recreate();
            }
        });

    }


}
