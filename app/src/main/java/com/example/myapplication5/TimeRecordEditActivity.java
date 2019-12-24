package com.example.myapplication5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeRecordEditActivity extends AppCompatActivity {

    private ItimeRecord record,recordDToE;
    private EditItemArrayAdapter theAdaper;
    private ArrayList<Edititem> edititems=new ArrayList<Edititem>();
    private ImageView imageViewDuihao,imageViewJiantou;
    private EditText editTextTitle,editTextRemark;
    private ListView listViewJiahao;



    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_record_edit);
        editTextRemark=this.findViewById(R.id.edit_text_remark);
        editTextTitle=this.findViewById(R.id.edit_text_title);
        listViewJiahao=this.findViewById(R.id.list_view_jiahao);
        imageViewDuihao=this.findViewById(R.id.image_view_duihao);
        imageViewJiantou=this.findViewById(R.id.image_view_jiantou);

        InitData();
        theAdaper=new EditItemArrayAdapter(this,R.layout.list_item,edititems);
        listViewJiahao.setAdapter(theAdaper);


        record = (ItimeRecord) getIntent().getSerializableExtra("record");
        recordDToE= (ItimeRecord) getIntent().getSerializableExtra("recordDToE");
        if(recordDToE!=null){
            editTextTitle.setText(recordDToE.getTitle());
            editTextRemark.setText(recordDToE.getWord());
            String text=recordDToE.getYear() + "年" + recordDToE.getMonth() + "月" + recordDToE.getDay()+"日  "+recordDToE.getHour()+"："+recordDToE.getMinute();
            edititems.get(0).setBottomView(text);
            theAdaper.notifyDataSetChanged();
        }




        listViewJiahao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {

                    view.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                showDatePickDlg();
                                return true;
                            }
                            return false;
                        }
                    });
                }
                if(position==1){
                    final String[] items = {"每月", "每周", "每年","自定义","无"};
                    AlertDialog.Builder listDialog =
                            new AlertDialog.Builder(TimeRecordEditActivity.this);
                    listDialog.setTitle("周期");
                    listDialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Edititem edititem=edititems.get(1);
                            String text=items[which];
                            edititem.setBottomView(text);

                            theAdaper.notifyDataSetChanged();
                        }
                    });
                    listDialog.show();
                }

                if(position==2){
                    final String[] items = {"图库", "文档"};
                    AlertDialog.Builder listDialog =
                            new AlertDialog.Builder(TimeRecordEditActivity.this);
                    Window window = listDialog.create().getWindow();
                    window.setGravity(Gravity.BOTTOM);
                    listDialog.setTitle("选取照片");
                    listDialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Edititem edititem=edititems.get(2);
                            String text=items[which];
                            edititem.setBottomView(text);
                            theAdaper.notifyDataSetChanged();
                        }
                    });
                    listDialog.show();
                }
                if(position==3){
                    final EditText edit = new EditText(TimeRecordEditActivity.this);

                    AlertDialog.Builder editDialog = new AlertDialog.Builder(TimeRecordEditActivity.this);
                    editDialog.setTitle("标签");
                    editDialog.setIcon(R.mipmap.ic_launcher_round);
                    editDialog.setView(edit);
                    editDialog.setPositiveButton("确定"
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Edititem edititem=edititems.get(3);
                                    edititem.setBottomView(edit.getText().toString().trim());
                                    theAdaper.notifyDataSetChanged();
                                    Toast.makeText(TimeRecordEditActivity.this,
                                            edit.getText().toString().trim(),Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });

                    editDialog.create().show();
                }
            }
        });

        imageViewJiantou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeRecordEditActivity.this.finish();

            }
        });

        imageViewDuihao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent();
                Bundle bundle=new Bundle();

                if(recordDToE!=null){
                    recordDToE.setTitle(editTextTitle.getText().toString());
                    recordDToE.setWord(editTextRemark.getText().toString());
                    recordDToE.setPhotoId(R.drawable.photo);
                    bundle.putSerializable("record",recordDToE);

                }
                if(record!=null) {
                    record.setTitle(editTextTitle.getText().toString());
                    record.setWord(editTextRemark.getText().toString());
                    record.setPhotoId(R.drawable.photo);
                    bundle.putSerializable("record", record);
                }
                mIntent.putExtras(bundle);


                setResult(RESULT_OK, mIntent);
                TimeRecordEditActivity.this.finish();
            }
        });





    }

    private class EditItemArrayAdapter extends ArrayAdapter<Edititem> {

        int resourceId;
        public EditItemArrayAdapter(@NonNull Context context, int resource, @NonNull List<Edititem> objects) {
            super(context, resource, objects);
            resourceId=resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater mInflater= LayoutInflater.from(this.getContext());
            View item = mInflater.inflate(resourceId,null);

            ImageView image = (ImageView)item.findViewById(R.id.image_view_left);
            TextView top = (TextView)item.findViewById(R.id.text_view_top);
            TextView bottom = (TextView)item.findViewById(R.id.text_view_bottom);

            Edititem edititem_item= this.getItem(position);
            image.setImageResource(edititem_item.getImageId());
            top.setText(edititem_item.getTopView());
            bottom.setText(edititem_item.getBottomView());

            return item;
        }
    }


    private void InitData() {


        edititems.add(new Edititem(R.drawable.time,"日期","使用日期计算器"));
        edititems.add(new Edititem(R.drawable.repeat,"重复设置","无"));
        edititems.add(new Edititem(R.drawable.photo,"图片",""));
        edititems.add(new Edititem(R.drawable.marker,"添加标签",""));
    }


    private void showTimePickDlg(final int year, final int monthOfYear, final int dayOfMonth) {  //显示时钟对话框
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog=new TimePickerDialog(TimeRecordEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Edititem edititem=edititems.get(0);

                String text=year + "年" + (monthOfYear+1) + "月" + dayOfMonth+"日  "+i+"："+i1;
                edititem.setBottomView(text);

                if(record!=null) {
                    record.setYear(year);
                    record.setMonth(monthOfYear);
                    record.setDay(dayOfMonth);
                    record.setHour(i);
                    record.setMinute(i1);
                }
                if(recordDToE!=null) {
                    recordDToE.setYear(year);
                    recordDToE.setMonth(monthOfYear);
                    recordDToE.setDay(dayOfMonth);
                    recordDToE.setHour(i);
                    recordDToE.setMinute(i1);

                }
                theAdaper.notifyDataSetChanged();

            }
        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),true);
        timePickerDialog.show();
    }


    protected void showDatePickDlg() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(TimeRecordEditActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                showTimePickDlg( year, monthOfYear, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }
}
