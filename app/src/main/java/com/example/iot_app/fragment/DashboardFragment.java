package com.example.iot_app.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.example.iot_app.R;

// for chart
import android.widget.Toast;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import android.graphics.Color;
import com.github.mikephil.charting.components.YAxis;
import java.util.ArrayList;
import com.github.mikephil.charting.charts.CombinedChart;
import java.util.List;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;

public class DashboardFragment extends Fragment implements OnChartValueSelectedListener {
    MQTTHelper mqttHelper;
    TextView txtTemperature;
    TextView txtHumidity;

    private CombinedChart mChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);
        txtTemperature = view.findViewById(R.id.txtTemperature);
        txtHumidity = view.findViewById(R.id.txtHumidity);
        startMQTT();

        LinearLayout chart_temperature = view.findViewById(R.id.chartTemperature);
        LinearLayout temp_control = view.findViewById(R.id.temperature_control);
        chart_temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencharttemperature(Gravity.CENTER);
            }
        });

        temp_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTemperaturecontrol(Gravity.CENTER);
            }
        });

        getFeedData("NhanHuynh", "aio_zxeo20IqleJBD2jDpd8RSxDfiiQC", "NhanHuynh/feeds/temp-air", 10);
        return view;
    }

    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST",topic + "***" + message.toString());
                if(topic.contains("NhanHuynh/feeds/temp-air")){
                    txtTemperature.setText(message.toString());
                }
                if(topic.contains("NhanHuynh/feeds/humi-air")){
                    txtHumidity.setText(message.toString());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

// open temperature chart------------------------------------------------------------------------
    private void opencharttemperature(int gravity){
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.temperature_chart);

        mChart = (CombinedChart) dialog.findViewById(R.id.combinedChart);
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        mChart.setOnChartValueSelectedListener(this);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);

        final List<String> xLabel = new ArrayList<>();
        xLabel.add("1");
        xLabel.add("2");
        xLabel.add("3");
        xLabel.add("4");
        xLabel.add("5");
        xLabel.add("6");
        xLabel.add("7");
        xLabel.add("8");
        xLabel.add("9");
        xLabel.add("10");
        xLabel.add("11");
        xLabel.add("12");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int) value % xLabel.size());
            }
        });

        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        lineDatas.addDataSet((ILineDataSet) dataChart());

        data.setData(lineDatas);

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();


        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);


        if(Gravity.CENTER == gravity){
            dialog.setCancelable(true);
        } else{
            dialog.setCancelable(false);
        }


        dialog.show();
    }

    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(requireContext(), "Value: " + e.getY()
                + ", index: " + h.getX() + ", DataSet index: "
                + h.getDataSetIndex(), Toast.LENGTH_SHORT).show();

    }
    public void onNothingSelected() {

    }

    private static DataSet dataChart() {

        LineData d = new LineData();
        int[] data = new int[] { 27, 28, 29, 29, 29, 30, 32, 33, 33, 34, 34, 34 };

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < 12; index++) {
            entries.add(new Entry(index, data[index]));
        }

        LineDataSet set = new LineDataSet(entries, "Temperature");
        set.setColor(Color.GREEN);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.GREEN);
        set.setCircleRadius(5f);
        set.setFillColor(Color.GREEN);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.GREEN);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return set;
    }

    //----------------------------------------------------------------------------------------------------
    private void openTemperaturecontrol(int gravity){
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.temperature_control);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);


        if(Gravity.CENTER == gravity){
            dialog.setCancelable(true);
        } else{
            dialog.setCancelable(false);
        }

//        Hiển thị thông số seekbar lên texview
        TextView tv;
        SeekBar sbar;
        ImageView picture;
        LinearLayout bgTempControl;
        tv = dialog.findViewById(R.id.status_temp_seekbar);
        sbar = dialog.findViewById(R.id.seekBar_temp);
        picture = dialog.findViewById(R.id.temp_picture);
        bgTempControl = dialog.findViewById(R.id.bg_temp_control);

        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv.setText(String.valueOf(i));
                if( i > 30){
                    picture.setImageResource(R.drawable.hot_icon);
                    bgTempControl.setBackgroundResource(R.drawable.bg_windows_red);
                } else if(i <=20 && i >10){
                    picture.setImageResource(R.drawable.cool_icon);
                    bgTempControl.setBackgroundResource(R.drawable.bg_windows);
                } else if(i <= 10){
                    picture.setImageResource(R.drawable.cold_icon);
                    bgTempControl.setBackgroundResource(R.drawable.bg_windows_blue);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//------------------------------------
        dialog.show();
    }
    //lấy data------------------------------------------------
    private class FetchDataAsyncTask extends AsyncTask<Void, Void, String> {
        private String username;
        private String key;
        private String feed;
        private int numberOfDataPoints;

        // Constructor
        public FetchDataAsyncTask(String username, String key, String feed, int numberOfDataPoints) {
            this.username = username;
            this.key = key;
            this.feed = feed;
            this.numberOfDataPoints = numberOfDataPoints;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String apiUrl = "https://io.adafruit.com/api/v2/" + username + "/feeds/" + feed + "/data?limit=" + numberOfDataPoints;
                URL url = new URL(apiUrl);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-AIO-Key", key);

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder responseData = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responseData.append(line);
                }

                reader.close();
                urlConnection.disconnect();

                return responseData.toString();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                // Xử lý dữ liệu ở đây
                Log.d("AdafruitIO", "Data: " + result);
            } else {
                // Xử lý lỗi ở đây
                Log.e("AdafruitIO", "Error fetching data");
            }
        }
    }

    // Gọi AsyncTask từ onCreateView hoặc một phương thức khác
    private void getFeedData(String username, String key, String feed, int numberOfDataPoints) {
        new FetchDataAsyncTask(username, key, feed, numberOfDataPoints).execute();
    }
}



