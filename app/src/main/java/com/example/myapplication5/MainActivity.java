package com.example.myapplication5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.json.JSONObject.NULL;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ItimeRecord> itimeRecords=new ArrayList<ItimeRecord>();
    private  ItimeRecordArrayAdapter itimeAdaper;
    private ListView listViewRecords;
    private AppBarConfiguration myAppBarConfiguration;
    private Handler myHandler;
    private TextView textViewTimeBottom,textViewTimeMiddle,textViewTimeTop;
    private FileDataSource fileDataSource;
    private View view;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new OnFabClickListener());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        myAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, myAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        InitData();
        listViewRecords=this.findViewById(R.id.list_view_itime_record);
        textViewTimeBottom=this.findViewById(R.id.text_view_time_bottom);
        textViewTimeMiddle=this.findViewById(R.id.text_view_time_middle);
        textViewTimeTop=this.findViewById(R.id.text_view_time_top);
        view=this.findViewById(R.id.linear_layout_time);



        itimeAdaper=new ItimeRecordArrayAdapter(this,R.layout.list_item_record,itimeRecords);
        listViewRecords.setAdapter(itimeAdaper);

        new TimeThread().start();
        myHandler = new Handler(){  //实现主页面倒计时
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1: {
                        if (itimeRecords != null) {
                            SimpleDateFormat dateFormatterChina = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            long sysTime = System.currentTimeMillis();
                            Drawable drawable=getResources().getDrawable(R.drawable.pic1);

                            view.setBackground(drawable);

                            ItimeRecord recordFirst = itimeRecords.get(0);
                            textViewTimeTop.setText(recordFirst.getTitle());
                            textViewTimeMiddle.setText(recordFirst.getYear() + "年" + recordFirst.getMonth() + "月" + recordFirst.getDay() + "日");


                            Calendar cal = Calendar.getInstance();
                            cal.set(recordFirst.getYear(), recordFirst.getMonth(), recordFirst.getDay(), recordFirst.getHour(), recordFirst.getMinute(), 0);
                            Date date = cal.getTime();
                            long timeStamp = date.getTime() - 8 * 60 * 60 * 1000;


                            if (sysTime < timeStamp) {
                                long dateMinus = timeStamp - sysTime;
                                long totalSeconds = dateMinus / 1000;

                                //求出现在的秒
                                long currentSecond = totalSeconds % 60;

                                //求出现在的分
                                long totalMinutes = totalSeconds / 60;
                                long currentMinute = totalMinutes % 60;

                                //求出现在的小时
                                long totalHour = totalMinutes / 60;
                                long currentHour = totalHour % 24;

                                //求出现在的天数
                                long totalDay = totalHour / 24;


                                textViewTimeBottom.setText(totalDay + "天" + currentMinute + "分" + currentSecond + "秒");
                            } else {
                                long dateMinus = sysTime - timeStamp;
                                long totalSeconds = dateMinus / 1000;

                                //求出现在的秒
                                long currentSecond = totalSeconds % 60;

                                //求出现在的分
                                long totalMinutes = totalSeconds / 60;
                                long currentMinute = totalMinutes % 60;

                                //求出现在的小时
                                long totalHour = totalMinutes / 60;
                                long currentHour = totalHour % 24;

                                //求出现在的天数
                                long totalDay = totalHour / 24;
                                textViewTimeBottom.setText(totalDay + "天" + currentMinute + "分" + currentSecond + "秒");
                            }

                        }
                    }
                    break;
                }
            }
        };


        listViewRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,TimeRecordDetailActivity.class);
                ItimeRecord itimeRecord=itimeRecords.get(position);
                Bundle bundle=new Bundle();
                bundle.putSerializable("record",itimeRecord);
                intent.putExtras(bundle);
                intent.putExtra("position",position);
                startActivityForResult(intent, 2);

            }
        });



    }


    private void InitData() {
        fileDataSource=new FileDataSource(this);
        itimeRecords=fileDataSource.load();
        ItimeRecord itimeRecord=new ItimeRecord();
        if(itimeRecords!=NULL){
            itimeRecords.add(new ItimeRecord(R.drawable.photo,"11","11",2019,12,30,6,12));
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        fileDataSource.save();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle bundle =  data.getExtras();
                    ItimeRecord record= (ItimeRecord) bundle.getSerializable("record");
                    itimeRecords.add(record);
                    itimeAdaper.notifyDataSetChanged();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    ItimeRecord record= (ItimeRecord) data.getExtras().getSerializable("record");
                    int position=data.getIntExtra("position",-1);
                    if(position!=-1){

                        itimeRecords.remove(position);
                        itimeRecords.add(position,record);
                        itimeAdaper.notifyDataSetChanged();

                    }
                }else{
                    int position=data.getIntExtra("position",-1);
                    if(position!=-1) {

                        itimeRecords.remove(position);
                        itimeAdaper.notifyDataSetChanged();
                    }
                }


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, myAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private class OnFabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this,TimeRecordEditActivity.class);
            ItimeRecord itimeRecord=new ItimeRecord();
            Bundle bundle=new Bundle();
            bundle.putSerializable("record",itimeRecord);
            intent.putExtras(bundle);

            startActivityForResult(intent, 1);

        }
    }


    private class ItimeRecordArrayAdapter extends ArrayAdapter<ItimeRecord> {
        int resourceId;
        public ItimeRecordArrayAdapter(@NonNull Context context, int resource, @NonNull List<ItimeRecord> objects) {
            super(context, resource, objects);
            resourceId=resource;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater mInflater= LayoutInflater.from(this.getContext());
            View item = mInflater.inflate(resourceId,null);


            TextView top = (TextView)item.findViewById(R.id.text_view_record_top);
            TextView middle=item.findViewById(R.id.text_view_record_middle);
            TextView bottom = (TextView)item.findViewById(R.id.text_view_record_bottom);
            TextView left=item.findViewById(R.id.text_view_record_left);

            ItimeRecord itimeRecord_item= this.getItem(position);

            top.setText(itimeRecord_item.getTitle());
            middle.setText(itimeRecord_item.getYear()+"年"+(itimeRecord_item.getMonth()+1)+"月"+itimeRecord_item.getDay()+"日");
            bottom.setText(itimeRecord_item.getWord());

            long sysTime = System.currentTimeMillis();
            Calendar cal = Calendar.getInstance();
            cal.set(itimeRecord_item.getYear(), itimeRecord_item.getMonth(), itimeRecord_item.getDay(), itimeRecord_item.getHour(), itimeRecord_item.getMinute(), 0);//第二个参数月份是实际值减1
            Date date = cal.getTime();
            long timeStamp = date.getTime() - 8 * 60 * 60 * 1000;
            if (sysTime < timeStamp) {
                long dateMinus = timeStamp - sysTime;
                long totalSeconds = dateMinus / 1000;
                //求出总的分
                long totalMinutes = totalSeconds / 60;
                //求出总的小时
                long totalHour = totalMinutes / 60;
                //求出总的天数
                long totalDay = totalHour / 24;
                left.setText(totalDay + "天" );
            } else {
                long dateMinus =sysTime-timeStamp ;
                long totalSeconds = dateMinus / 1000;
                //求出总的分
                long totalMinutes = totalSeconds / 60;
                //求出总的小时
                long totalHour = totalMinutes / 60;
                //求出总的天数
                long totalDay = totalHour / 24;
                left.setText(totalDay + "天" );
            }



            return item;
        }

    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    myHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }
}
