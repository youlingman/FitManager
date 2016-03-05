package com.cyl.fitmanager.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.cyl.fitmanager.Appcontext.MainApplication;
import com.cyl.fitmanager.R;
import com.cyl.fitmanager.Data.GroupProp;
import com.cyl.fitmanager.Constant;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import static com.cyl.fitmanager.Utils.firstDayOfMonth;
import static com.cyl.fitmanager.Utils.genArrayList;
import static com.cyl.fitmanager.Utils.parseDateInDay;
import static com.cyl.fitmanager.Utils.parseDateInMonth;
import static com.cyl.fitmanager.Utils.parseSecondToTime;

/**
 * 训练项目主页面，包括训练计划信息和历史数据展示部分
 * Created by Administrator on 2016-1-21.
 */
public class ProgramMainActivity extends Activity {
    private static final String TAG = "ProgramMainActivity";
    // 渲染数据
    String program;
    GroupProp groupProp;
    Date trainingMonthDate;
    TreeSet<Date> trainingMonths;
    // 页面对应view
    List<Button> trainingDayButtons = new ArrayList<>();
    LinearLayout ll_training_program;
    TextView tv_nextTrainingDay;
    TextView tv_training_month;
    TextView tv_month_counts;
    TextView tv_month_seconds;
    TextView tv_average_count;
    TextView tv_group_count;
    TextView tv_group_size;
    TextView tv_group_interval;
    Button btn_previous_month;
    Button btn_forward_month;
    Button btn_free_style;
    Button btn_program_style;
    BarChart bc_trainingChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_main);
        initData();
        initView();
        parseView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTrainingDay();
        refreshChart();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 获取训练计划标志串
        Bundle bundle = this.getIntent().getExtras();
        program = bundle.getString("program");
        // 获取当月首日日期
        trainingMonthDate = firstDayOfMonth();
        // 获取训练计划数据
        try {
            groupProp = ((MainApplication) getApplication()).getDB().getObject(program + "_group", GroupProp.class);
        } catch (SnappydbException e) {
            groupProp = new GroupProp();
        }
        // 获取训练月份数组数据
        try {
            trainingMonths = ((MainApplication) getApplication()).getDB().get("training_months", TreeSet.class);
            if (!trainingMonths.contains(trainingMonthDate)) {
                trainingMonths.add(trainingMonthDate);
            }
        } catch (SnappydbException e) {
            trainingMonths = new TreeSet<>();
            trainingMonths.add(trainingMonthDate);
        }
        try {
            ((MainApplication) getApplication()).getDB().put("training_months", trainingMonths);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化页面view句柄
     */
    private void initView() {
        // 训练入口按钮
        btn_free_style = (Button) findViewById(R.id.free_button);
        btn_program_style = (Button) findViewById(R.id.program_button);
        // 下一训练日
        tv_nextTrainingDay = (TextView) findViewById(R.id.next_training_day);
        // 训练计划
        ll_training_program = (LinearLayout) findViewById(R.id.training_program);
        tv_group_count = (TextView) findViewById(R.id.group_count);
        tv_group_size = (TextView) findViewById(R.id.group_size);
        tv_group_interval = (TextView) findViewById(R.id.group_interval);
        trainingDayButtons.add((Button) findViewById(R.id.training_day_0));
        trainingDayButtons.add((Button) findViewById(R.id.training_day_1));
        trainingDayButtons.add((Button) findViewById(R.id.training_day_2));
        trainingDayButtons.add((Button) findViewById(R.id.training_day_3));
        trainingDayButtons.add((Button) findViewById(R.id.training_day_4));
        trainingDayButtons.add((Button) findViewById(R.id.training_day_5));
        trainingDayButtons.add((Button) findViewById(R.id.training_day_6));
        // 训练数据展示
        tv_training_month = (TextView) findViewById(R.id.training_month);
        btn_previous_month = (Button) findViewById(R.id.previous_month_button);
        btn_forward_month = (Button) findViewById(R.id.forward_month_button);
        tv_month_counts = (TextView) findViewById(R.id.month_counts);
        tv_month_seconds = (TextView) findViewById(R.id.month_seconds);
        tv_average_count = (TextView) findViewById(R.id.average_count);
        bc_trainingChart = (BarChart) findViewById(R.id.training_chart);
    }

    /**
     * 渲染页面View
     */
    private void parseView() {
        // 渲染title
        ((TextView) findViewById(R.id.program_text)).setText(program);
        // 渲染下一训练日textview
        refreshTrainingDay();
        // 渲染训练计划textview
        tv_group_count.setText("" + groupProp.count);
        tv_group_size.setText("" + groupProp.size);
        tv_group_interval.setText("" + groupProp.interval);
        // 设置训练计划点击弹出修改框
        ll_training_program.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ProgramEditDialog(ProgramMainActivity.this, R.style.dialog).show();
            }
        });
        // 渲染训练日button集合
        for (int i = 0, mask = 1; i < 7; i++) {
            if ((groupProp.trainingDayBitMap & mask) != 0) {
                trainingDayButtons.get(i).setBackgroundResource(R.drawable.btn_training_day_selected);
            }
            mask <<= 1;
            trainingDayButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = trainingDayButtons.indexOf(v);
                    if (index != -1) {
                        int mask = 0x1 << index;
                        groupProp.trainingDayBitMap ^= mask;
                        if ((groupProp.trainingDayBitMap & mask) != 0) {
                            v.setBackgroundResource(R.drawable.btn_training_day_selected);
                        } else {
                            v.setBackgroundResource(R.drawable.btn_training_day_unselected);
                        }
                        try {
                            ((MainApplication) getApplication()).getDB().put(program + "_group", groupProp);
                        } catch (SnappydbException e) {
                            e.printStackTrace();
                        }
                    }
                    refreshTrainingDay();
                }
            });
        }
        // 渲染训练数据图表
        bc_trainingChart.setBackgroundResource(R.color.black);
        bc_trainingChart.setGridBackgroundColor(getResources().getColor(R.color.black));
        bc_trainingChart.setDescription("");
        bc_trainingChart.setData(getBarData(trainingMonthDate));
        bc_trainingChart.getXAxis().setDrawGridLines(false);
        bc_trainingChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        bc_trainingChart.getXAxis().setTextColor(getResources().getColor(R.color.white));
        bc_trainingChart.getAxisLeft().setTextColor(getResources().getColor(R.color.orange));
        bc_trainingChart.getLegend().setEnabled(false);
        bc_trainingChart.animateY(1000);
        // 设置图表轮转按钮
        btn_forward_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainingMonthDate = trainingMonths.higher(trainingMonthDate);
                refreshChart();
            }
        });
        btn_previous_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainingMonthDate = trainingMonths.lower(trainingMonthDate);
                refreshChart();
            }
        });
        refreshChart();
        tv_training_month.setText(parseDateInMonth(trainingMonthDate));
        // 训练按钮点击跳转
        View.OnClickListener trainingListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgramMainActivity.this, Constant.PROGRAM_CLASS.get(program));
                Bundle bundle = ProgramMainActivity.this.getIntent().getExtras();
                bundle.putParcelable("group", groupProp);
                bundle.putString("style", ((Button) v).getText().toString());
                intent.putExtras(bundle);
                startTraining(intent);
            }
        };
        btn_free_style.setOnClickListener(trainingListener);
        btn_program_style.setOnClickListener(trainingListener);
    }

    /**
     * @param intent 展示训练提示对话框
     */
    void startTraining(Intent intent) {
        boolean no_tip = ((MainApplication) getApplication()).getSP().getBoolean(program + "_no_tip", false);
        if (no_tip == true) {
            startActivity(intent);
        } else {
            new TipDialog(this, R.style.dialog, intent).show();
        }
    }

    /**
     * @param date
     * @return 返回date月份对应的展示训练数据集
     */
    BarData getBarData(Date date) {
        long seconds = 0;
        int counts = 0;
        int day_count = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<BarEntry> yValues = new ArrayList<>();
        for (int i = 1; true; i++) {
            date = cal.getTime();
            xValues.add("" + i);
            int value = 0;
            try {
                value = ((MainApplication) getApplication()).getDB().getInt(program + "_free_count_" + parseDateInDay(date));
            } catch (SnappydbException e) {
                // not found
            }
            try {
                value += ((MainApplication) getApplication()).getDB().getInt(program + "_program_count_" + parseDateInDay(date));
            } catch (SnappydbException e) {
                // not found
            }
            counts += value;
            yValues.add(new BarEntry(value, i));
            cal.add(Calendar.DATE, 1);
            if (value != 0) day_count++;
            try {
                seconds += ((MainApplication) getApplication()).getDB().getLong(program + "_seconds_" + parseDateInDay(date));
            } catch (SnappydbException e) {
//                e.printStackTrace();
            }
            // 只展示date对应月内的数据
            if (cal.get(Calendar.MONTH) != month) break;
        }
        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, "柱状图");
        barDataSet.setColor(getResources().getColor(R.color.orange));
        barDataSet.setBarSpacePercent(55);
        BarData barData = new BarData(xValues, barDataSet);

        tv_month_counts.setText("" + counts);
        tv_month_seconds.setText(parseSecondToTime(seconds));
        if (0 == day_count) {
            tv_average_count.setText("-");
        } else {
            tv_average_count.setText("" + (counts / day_count));
        }

        return barData;
    }


    /**
     * 根据训练日期的月份刷新图表轮转按钮和展示数据
     */
    void refreshChart() {
        if (trainingMonths.lower(trainingMonthDate) != null) {
            btn_previous_month.setVisibility(View.VISIBLE);
        } else {
            btn_previous_month.setVisibility(View.GONE);
        }
        if (trainingMonths.higher(trainingMonthDate) != null) {
            btn_forward_month.setVisibility(View.VISIBLE);
        } else {
            btn_forward_month.setVisibility(View.GONE);
        }
        tv_training_month.setText(parseDateInMonth(trainingMonthDate));
        bc_trainingChart.setData(getBarData(trainingMonthDate));
        bc_trainingChart.animateY(1000);
    }

    /**
     * 刷新训练日
     */
    void refreshTrainingDay() {
        ((MainApplication) getApplication()).updateTrainingDay();
        String today = parseDateInDay(new Date());
        String training_day = ((MainApplication) getApplication()).getSP().getString("next_training_day_" + program, "-");
        tv_nextTrainingDay.setText(training_day);
        // TODO 下一训练日为当日，开放训练入口
        if(today.equals(training_day)) {
            btn_program_style.setClickable(true);
            btn_program_style.setAlpha(1f);
        }
        // TODO 关闭训练入口
        else {
            btn_program_style.setClickable(false);
            btn_program_style.setAlpha(.5f);
        }
    }

    /**
     * 根据当前文本值更新训练计划
     */
    void updateGroup() {
        groupProp.size = Integer.parseInt(tv_group_size.getText().toString());
        groupProp.count = Integer.parseInt(tv_group_count.getText().toString());
        groupProp.interval = Integer.parseInt(tv_group_interval.getText().toString());
        try {
            ((MainApplication) getApplication()).getDB().put(program + "_group", groupProp);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在进入训练页面前用以展示训练指引的dialog类
     */
    class TipDialog extends Dialog {
        Context mContext;
        Intent mIntent;
        TextView tv_tips;
        Button bt_continue;
        CheckBox cb_showTip;

        public TipDialog(Context context, int theme, Intent intent) {
            super(context, theme);
            mContext = context;
            mIntent = intent;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_training_tip, null);
            setContentView(view);
            tv_tips = (TextView) findViewById(R.id.tip_text);
            bt_continue = (Button) findViewById(R.id.continue_button);
            cb_showTip = (CheckBox) findViewById(R.id.no_show_checkbox);
            tv_tips.setText(Constant.PROGRAM_TIP.get(program));
            bt_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainApplication) getApplication()).getSP().edit().putBoolean(program + "_no_tip", cb_showTip.isChecked()).commit();
                    startActivity(mIntent);
                    dismiss();
                }
            });
            Window dialogWindow = getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = getResources().getDisplayMetrics(); // 获取屏幕宽、高用
            lp.width = (int) (d.widthPixels * 0.9);
            dialogWindow.setAttributes(lp);
        }
    }

    /**
     * 修改训练计划对应的对话框
     */
    class ProgramEditDialog extends Dialog {
        Context mContext;
        NumberPicker np_group_size;
        NumberPicker np_group_count;
        NumberPicker np_group_interval;
        ArrayList<String> group_size_data;
        ArrayList<String> group_count_data;
        ArrayList<String> group_interval_data;

        public ProgramEditDialog(Context context, int theme) {
            super(context, theme);
            mContext = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // TODO 增加三个滑动选择控件和确定按钮，按钮绑定点击更新训练计划和dismiss对话框的逻辑
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_edit_program, null);
            setContentView(view);
            // 设置显示数据对应数组
            group_size_data = genArrayList(1, 1, 10);
            group_count_data = genArrayList(10, 5, 11);
            group_interval_data = genArrayList(10, 10, 18);
            // 获取picker
            np_group_count = (NumberPicker) findViewById(R.id.group_count_picker);
            np_group_size = (NumberPicker) findViewById(R.id.group_size_picker);
            np_group_interval = (NumberPicker) findViewById(R.id.group_interval_picker);
            setNumberPiker(np_group_count, tv_group_count, group_count_data);
            setNumberPiker(np_group_size, tv_group_size, group_size_data);
            setNumberPiker(np_group_interval, tv_group_interval, group_interval_data);
        }

        private void setNumberPiker(NumberPicker np, final TextView tv, final ArrayList<String> datas) {
            np.setWrapSelectorWheel(true);
            np.setMinValue(0);
            np.setMaxValue(datas.size() - 1);
            np.setDisplayedValues(datas.toArray(new String[datas.size()]));
            np.setValue(datas.indexOf(tv.getText().toString()));
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    tv.setText(datas.get(newVal));
                    updateGroup();
                }
            });
        }
    }
}