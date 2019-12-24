package com.example.myapplication5;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeRecordDetailActivity extends AppCompatActivity {

    private ItimeRecord record;
    private ImageView imageViewDelete,imageViewPen,imageViewJiantou;
    private Handler myHandler;
    private TextView textView;
    private  int position;

    private Intent myIntent;
    private Bundle myBundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_record_detail);


        imageViewDelete=this.findViewById(R.id.image_view_delete);
        imageViewPen=this.findViewById(R.id.image_view_pen);
        imageViewJiantou=this.findViewById(R.id.image_view_jiantou);
        textView=this.findViewById(R.id.textView2);

        record = (ItimeRecord) getIntent().getSerializableExtra("record");
        position=getIntent().getIntExtra("position",-1);

        myIntent=new Intent();
        myBundle=new Bundle();
        myIntent.putExtra("position",position);


        new TimeThread().start();
        myHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1: {
                        SimpleDateFormat dateFormatterChina = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        long sysTime = System.currentTimeMillis();


                        Calendar cal = Calendar.getInstance();
                        cal.set(record.getYear(), record.getMonth(), record.getDay(), record.getHour(), record.getMinute(), 0);
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


                            textView.setText(totalDay + "天" + currentMinute + "分" + currentSecond  + "秒");
                        } else {
                            long dateMinus =sysTime-timeStamp ;
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
                            textView.setText(totalDay + "天" + currentMinute + "分" + currentSecond + "秒");
                        }

                    }
                    break;
                    default:
                        break;

                }
            }
        };




        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_FIRST_USER, myIntent);
                TimeRecordDetailActivity.this.finish();
            }
        });

        imageViewPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TimeRecordDetailActivity.this,TimeRecordEditActivity.class);

                Bundle bundle=new Bundle();
                bundle.putSerializable("recordDToE",record);
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);

            }
        });

        imageViewJiantou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myBundle.putSerializable("record",record);
                myIntent.putExtras(myBundle);


                setResult(RESULT_OK, myIntent);
                TimeRecordDetailActivity.this.finish();


            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {

                    Bundle bundle = data.getExtras();
                    record = (ItimeRecord) bundle.getSerializable("record");
                }
                break;
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
