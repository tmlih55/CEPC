package com.example.cepc.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.cepc.R;
import com.example.cepc.db.PgSqlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListViewAdapter extends BaseAdapter {
    private static final String IP="192.168.43.74";
    private final static String RECORD_URI = "http://"+IP+":8021/records/";

    private Context mContext;

    private String mName;
    private double mTemperature;
    private String mPatient;
    private String mAddress;
    private String mDate;
    private int mCount;

    private LayoutInflater mLayoutInflater;

    public ListViewAdapter(Context context,String name){
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mName = name;
    }

    @Override
    public int getCount() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = PgSqlUtil.getJsonContent(RECORD_URI+"/findByName/"+mName);
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    mCount = jsonArray.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mCount;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        public TextView tv3_temperature;
        public TextView tv3_patient;
        public TextView tv3_date;
        public TextView tv3_address;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.record_item,null);
            holder = new ViewHolder();
            holder.tv3_temperature = (TextView)convertView.findViewById(R.id.item_temperature);
            holder.tv3_patient = (TextView)convertView.findViewById(R.id.item_patient);
            holder.tv3_date = (TextView)convertView.findViewById(R.id.item_date);
            holder.tv3_address = (TextView)convertView.findViewById(R.id.item_address);
            convertView.setTag(holder);
        }else { holder = (ViewHolder) convertView.getTag(); }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = PgSqlUtil.getJsonContent(RECORD_URI+"/findByName/"+mName);
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = jsonArray.getJSONObject(position);
                    mAddress=jsonObject.getString("address");
                    mDate=jsonObject.getString("date");
                    mPatient=jsonObject.getString("patient");
                    mTemperature=jsonObject.getDouble("temperature");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        holder.tv3_temperature.setText(mTemperature+"℃");
        holder.tv3_patient.setText("是否为四类患者："+mPatient);
        holder.tv3_date.setText("日期："+mDate);
        holder.tv3_address.setText("地点："+mAddress);
        return convertView;
    }
}

