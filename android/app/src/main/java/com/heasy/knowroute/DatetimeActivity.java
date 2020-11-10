package com.heasy.knowroute;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.heasy.knowroute.core.utils.DatetimeUtil;

import java.util.Calendar;
import java.util.Date;

import jsc.kit.datetimepicker.widget.DateTimePicker;

public class DatetimeActivity extends AppCompatActivity {
    private EditText editText1;
    private EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datetime);

        final Date startDate = DatetimeUtil.addDate(Calendar.DATE, -7);
        final Date endDate = DatetimeUtil.nowDate();

        editText1 = (EditText)findViewById(R.id.editText1);
        editText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show1(startDate, endDate);
            }
        });


        editText2 = (EditText)findViewById(R.id.editText2);
        editText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show2(startDate, endDate);
            }
        });
    }

    private void show1(Date startDate, Date endDate) {
        DateTimePicker dateTimePicker2 = new DateTimePicker(this, new DateTimePicker.ResultHandler() {
            @Override
            public void handle(Date date) {
                editText1.setText(DatetimeUtil.formatDate(date, DatetimeUtil.DEFAULT_PATTERN_DT2));
            }
        }, startDate, endDate);

        dateTimePicker2.show(DatetimeUtil.nowDate());
    }

    private void show2(Date startDate, Date endDate){
        DateTimePicker.Builder TBuilder = new DateTimePicker.Builder(this)
                .setTitle("选择年月日")
                .setCancelTextColor(Color.RED)
                .setOkTextColor(getResources().getColor(R.color.colorPrimary))
                .setTitleTextColor(0xFF999999)
                .setSelectedTextColor(getResources().getColor(R.color.colorAccent))
                .setKeepLastSelected(true)
                .setShowYMDHMLabel(true)
                .setShowType(DateTimePicker.ShowType.DAY);

        DateTimePicker dateTimePicker1 = new DateTimePicker(this, new DateTimePicker.ResultHandler() {
            @Override
            public void handle(Date date) {
                editText2.setText(DatetimeUtil.formatDate(date, DatetimeUtil.DEFAULT_PATTERN_DT2));
            }
        }, startDate, endDate, TBuilder);

        dateTimePicker1.show(DatetimeUtil.nowDate());
    }
}
