package com.example.iot_app.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import com.example.iot_app.R;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

public class DeviceFragment extends Fragment {
    @Nullable
    MQTTHelper_Device mqttHelper;
    private boolean isPowerOn_led = false;
    private boolean isPowerOn_pump = false;
    private boolean isPowerOn_heater = false;
    private boolean isPowerOn_cool = false;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_management, container, false);
        LinearLayout lightControl = view.findViewById(R.id.light_control);
        LinearLayout pumpControl = view.findViewById(R.id.pump_control);
        LinearLayout heaterControl = view.findViewById(R.id.heater_control);
        LinearLayout coolControl = view.findViewById(R.id.cool_control);

        CardView buttonLight = view.findViewById(R.id.button_light);
        CardView buttonPump = view.findViewById(R.id.button_pump);
        CardView buttonHeater = view.findViewById(R.id.button_heater);
        CardView buttonCool = view.findViewById(R.id.button_cool);

        startMQTT();
        // kiểm soát công suất-----------------------begin-------------------------------------
        lightControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi LinearLayout được click
                openLightcontrol(Gravity.CENTER);
            }
        });
        pumpControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi LinearLayout được click
                openPumpcontrol(Gravity.CENTER);
            }
        });

        heaterControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi LinearLayout được click
                openHeatercontrol(Gravity.CENTER);
            }
        });

        coolControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi LinearLayout được click
                openCoolcontrol(Gravity.CENTER);
            }
        });

        // kiểm soát công suất------------------------------end-----------------------------------------
        //đồng bộ màu button với adafruit-------------------begin--------------------------------------
//        getData("NhanHuynh", "aio_oaXQ14CIwbosTjcgBbME33MLzO1A", "led", 1, new DataCallback() {
//            @Override
//            public void onDataReceived(int value) {
//                if(value > 0){
//                    isPowerOn_led = true;
//                    buttonLight.setCardBackgroundColor(Color.parseColor("#00CC66"));
//                }
//            }
//        });
//        getData("NhanHuynh", "aio_oaXQ14CIwbosTjcgBbME33MLzO1A", "pump", 1, new DataCallback() {
//            @Override
//            public void onDataReceived(int value) {
//                if(value > 0){
//                    isPowerOn_pump = true;
//                    buttonPump.setCardBackgroundColor(Color.parseColor("#00CC66"));
//                }
//            }
//        });
//        getData("NhanHuynh", "aio_oaXQ14CIwbosTjcgBbME33MLzO1A", "heater", 1, new DataCallback() {
//            @Override
//            public void onDataReceived(int value) {
//                if(value > 0){
//                    isPowerOn_heater = true;
//                    buttonHeater.setCardBackgroundColor(Color.parseColor("#00CC66"));
//                }
//            }
//        });
//        getData("NhanHuynh", "aio_oaXQ14CIwbosTjcgBbME33MLzO1A", "cooler", 1, new DataCallback() {
//            @Override
//            public void onDataReceived(int value) {
//                if(value > 0){
//                    isPowerOn_cool = true;
//                    buttonCool.setCardBackgroundColor(Color.parseColor("#00CC66"));
//                }
//            }
//        });




        //đồng bộ màu button với adafruit-----------------------end--------------------------------------

//        điều khiển-----start----------------------------

        buttonLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPowerOn_led = !isPowerOn_led;
                if (isPowerOn_led) {
                    sendDataMQTT("huyquang0081/feeds/led","1000");
                    buttonLight.setCardBackgroundColor(Color.parseColor("#00CC66")); // Màu nền khi bật
                } else {
                    sendDataMQTT("huyquang0081/feeds/led","0");
                    buttonLight.setCardBackgroundColor(Color.parseColor("#F44336")); // Màu nền khi tắt
                }
            }
        });
        buttonPump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPowerOn_pump = !isPowerOn_pump;
                if (isPowerOn_pump) {
                    sendDataMQTT("NhanHuynh/feeds/pump","1");
                    buttonPump.setCardBackgroundColor(Color.parseColor("#00CC66")); // Màu nền khi bật
                } else {
                    sendDataMQTT("NhanHuynh/feeds/pump","0");
                    buttonPump.setCardBackgroundColor(Color.parseColor("#F44336")); // Màu nền khi tắt
                }
            }
        });

        buttonHeater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPowerOn_heater = !isPowerOn_heater;
                if (isPowerOn_heater) {
                    sendDataMQTT("NhanHuynh/feeds/heater","1000");
                    buttonHeater.setCardBackgroundColor(Color.parseColor("#00CC66")); // Màu nền khi bật
                } else {
                    sendDataMQTT("NhanHuynh/feeds/heater","0");
                    buttonHeater.setCardBackgroundColor(Color.parseColor("#F44336")); // Màu nền khi tắt
                }
            }
        });

        buttonCool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPowerOn_cool = !isPowerOn_cool;
                if (isPowerOn_cool) {
                    sendDataMQTT("NhanHuynh/feeds/cooler","1000");
                    buttonCool.setCardBackgroundColor(Color.parseColor("#00CC66")); // Màu nền khi bật
                } else {
                    sendDataMQTT("NhanHuynh/feeds/cooler","0");
                    buttonCool.setCardBackgroundColor(Color.parseColor("#F44336")); // Màu nền khi tắt
                }
            }
        });

// điều khển-----end---------------------------------------------------

        return view;
    }

    private void openLightcontrol(int gravity){
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.control_light_layout);
        LabeledSwitch labeledSwitch = dialog.findViewById(R.id.switch_led);

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
        tv = dialog.findViewById(R.id.status_light_seekbar);
        sbar = dialog.findViewById(R.id.seekBar_light);
        sbar.setEnabled(true);
        picture = dialog.findViewById(R.id.light_picture);

        getData("huyquang0081", "aio_zsYd64kLgekFCfOgpCoedEX1tw3a", "led", 1, new DataCallback() {
            @Override
            public void onDataReceived(int value) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(String.valueOf(value));
                        sbar.setProgress(value);
                    }
                });
            }
        });
//        getData("NhanHuynh", "aio_oaXQ14CIwbosTjcgBbME33MLzO1A", "manual-led", 1, new DataCallback() {
//            @Override
//            public void onDataReceived(int value) {
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (value == 1) {
//                            labeledSwitch.setOn(true);
//                            sbar.setEnabled(true);
//                        } else {
//                            labeledSwitch.setOn(false);
//                            sbar.setEnabled(false);
//                        }
//                    }
//                });
//            }
//        });
//        labeledSwitch.setOnToggledListener(new OnToggledListener() {
//            @Override
//            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
//                if(isOn == true){
//                    sbar.setEnabled(true);
//                    sendDataMQTT("NhanHuynh/feeds/manual-led","1");
//                } else{
//                    sbar.setEnabled(false);
//                    sendDataMQTT("NhanHuynh/feeds/manual-led","0");
//                }
//            }
//        });

        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv.setText(String.valueOf(i));
                if( i > 20){
                    picture.setImageResource(R.drawable.light_turnon_image);
                } else{
                    picture.setImageResource(R.drawable.light_image);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String currentText = tv.getText().toString();
                sendDataMQTT("huyquang0081/feeds/led",currentText);
            }
        });
//------------------------------------

        dialog.show();
    }

    private void openPumpcontrol(int gravity){
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.control_pump_layout);
        LabeledSwitch labeledSwitch = dialog.findViewById(R.id.switch_pump);

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
        tv = dialog.findViewById(R.id.status_pump_seekbar);
        sbar = dialog.findViewById(R.id.seekBar_pump);
        sbar.setEnabled(false);

//        getData("NhanHuynh", "aio_oaXQ14CIwbosTjcgBbME33MLzO1A", "pump", 1, new DataCallback() {
//            @Override
//            public void onDataReceived(int value) {
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv.setText(String.valueOf(value));
//                        sbar.setProgress(value);
//                    }
//                });
//            }
//        });
//        getData("NhanHuynh", "aio_oaXQ14CIwbosTjcgBbME33MLzO1A", "manual-pump", 1, new DataCallback() {
//            @Override
//            public void onDataReceived(int value) {
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (value == 1) {
//                            labeledSwitch.setOn(true);
//                            sbar.setEnabled(true);
//                        } else {
//                            labeledSwitch.setOn(false);
//                            sbar.setEnabled(false);
//                        }
//                    }
//                });
//            }
//        });
        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sbar.setEnabled(true);
                    sendDataMQTT("NhanHuynh/feeds/manual-pump","1");
                } else{
                    sbar.setEnabled(false);
                    sendDataMQTT("NhanHuynh/feeds/manual-pump","0");
                }
            }
        });
        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String currentText = tv.getText().toString();
                sendDataMQTT("NhanHuynh/feeds/pump",currentText);
            }
        });
//------------------------------------

        dialog.show();
    }

    private void openHeatercontrol(int gravity){
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.control_heater_layout);
        LabeledSwitch labeledSwitch = dialog.findViewById(R.id.switch_heater);

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
        tv = dialog.findViewById(R.id.status_heater_seekbar);
        sbar = dialog.findViewById(R.id.seekBar_heater);
        sbar.setEnabled(false);

//        getData("NhanHuynh", "aio_oaXQ14CIwbosTjcgBbME33MLzO1A", "heater", 1, new DataCallback() {
//            @Override
//            public void onDataReceived(int value) {
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv.setText(String.valueOf(value));
//                        sbar.setProgress(value);
//                    }
//                });
//            }
//        });
//        getData("NhanHuynh", "aio_oaXQ14CIwbosTjcgBbME33MLzO1A", "manual-heater-cooler", 1, new DataCallback() {
//            @Override
//            public void onDataReceived(int value) {
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (value == 1) {
//                            labeledSwitch.setOn(true);
//                            sbar.setEnabled(true);
//                        } else {
//                            labeledSwitch.setOn(false);
//                            sbar.setEnabled(false);
//                        }
//                    }
//                });
//            }
//        });
        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sbar.setEnabled(true);
                    sendDataMQTT("NhanHuynh/feeds/manual-heater-cooler","1");
                } else{
                    sbar.setEnabled(false);
                    sendDataMQTT("NhanHuynh/feeds/manual-heater-cooler","0");
                }
            }
        });
        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String currentText = tv.getText().toString();
                sendDataMQTT("NhanHuynh/feeds/heater",currentText);
            }
        });
//------------------------------------
        dialog.show();
    }

    private void openCoolcontrol(int gravity){
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.control_cool_layout);
        LabeledSwitch labeledSwitch = dialog.findViewById(R.id.switch_cool);

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
        tv = dialog.findViewById(R.id.status_cool_seekbar);
        sbar = dialog.findViewById(R.id.seekBar_cool);
        sbar.setEnabled(false);

//        getData("NhanHuynh", "aio_oaXQ14CIwbosTjcgBbME33MLzO1A", "cooler", 1, new DataCallback() {
//            @Override
//            public void onDataReceived(int value) {
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv.setText(String.valueOf(value));
//                        sbar.setProgress(value);
//                    }
//                });
//            }
//        });
//        getData("NhanHuynh", "aio_oaXQ14CIwbosTjcgBbME33MLzO1A", "manual-heater-cooler", 1, new DataCallback() {
//            @Override
//            public void onDataReceived(int value) {
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (value == 1) {
//                            labeledSwitch.setOn(true);
//                            sbar.setEnabled(true);
//                        } else {
//                            labeledSwitch.setOn(false);
//                            sbar.setEnabled(false);
//                        }
//                    }
//                });
//            }
//        });
        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sbar.setEnabled(true);
                    sendDataMQTT("NhanHuynh/feeds/manual-heater-cooler","1");
                } else{
                    sbar.setEnabled(false);
                    sendDataMQTT("NhanHuynh/feeds/manual-heater-cooler","0");
                }
            }
        });
        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String currentText = tv.getText().toString();
                sendDataMQTT("NhanHuynh/feeds/cooler",currentText);
            }
        });
//------------------------------------

        dialog.show();
    }


    public void startMQTT(){
        mqttHelper = new MQTTHelper_Device(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }


    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }

    //lấy data------------------------------------------------
    public interface DataCallback {
        void onDataReceived(int value);
    }

    private void getData(String username, String key, String feed, int numberOfDataPoints, DataCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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

                    processResponse(responseData.toString(), callback);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("AdafruitIO", "Error fetching data");
                }
            }
        }).start();
    }

    private void processResponse(String response, DataCallback callback) {
        if (response != null) {
            try {
                JSONArray dataArray = new JSONArray(response);

                if (dataArray.length() > 0) {
                    JSONObject lastDataPoint = dataArray.getJSONObject(dataArray.length() - 1);
                    int value = lastDataPoint.getInt("value");

                    Log.d("AdafruitIO", "Value: " + value);

                    // Gọi lại callback với giá trị đã lấy được
                    callback.onDataReceived(value);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("AdafruitIO", "Error parsing JSON");
            }
        }
    }
}
