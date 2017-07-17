package com.cyl.fitmanager.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cyl.fitmanager.Controller.DataController;
import com.cyl.fitmanager.Controller.ProgramStatsController;
import com.cyl.fitmanager.Model.ProgramContext;
import com.cyl.fitmanager.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cyl.fitmanager.Utils.firstDayOfMonth;
import static com.cyl.fitmanager.Utils.parseDateInDay;
import static com.cyl.fitmanager.Utils.parseDateInMonth;
import static com.cyl.fitmanager.Utils.parseSecondToTime;

/**
 * 展示训练数据图表的fragment
 */
public class TrainChartFragment extends BaseConfigFragment {
    // 数据
    Date trainingMonthDate;
    TreeSet<Date> trainingMonths;
    // 页面对应view
    @BindView(R.id.training_month)
    TextView tv_training_month;
    @BindView(R.id.month_counts)
    TextView tv_month_counts;
    @BindView(R.id.month_seconds)
    TextView tv_month_seconds;
    @BindView(R.id.average_count)
    TextView tv_average_count;
    @BindView(R.id.previous_month_button)
    Button btn_previous_month;
    @BindView(R.id.forward_month_button)
    Button btn_forward_month;
    @BindView(R.id.training_chart)
    BarChart bc_trainingChart;

    // 单例对象
    private static TrainChartFragment instance;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TrainChartFragment.
     */
    public static synchronized TrainChartFragment newInstance() {
        if(null == instance) {
            instance = new TrainChartFragment();
        }
        return instance;
    }

    public TrainChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        // 获取当月首日日期
        trainingMonthDate = firstDayOfMonth();
        // 获取训练月份数组数据
        try {
            trainingMonths = DataController.getInstance().getDB().get("training_months", TreeSet.class);
            if (null == trainingMonths) trainingMonths = new TreeSet<>();
            if (!trainingMonths.contains(trainingMonthDate)) {
                trainingMonths.add(trainingMonthDate);
            }
        } catch (SnappydbException e) {
            trainingMonths = new TreeSet<>();
            trainingMonths.add(trainingMonthDate);
        }
        try {
            DataController.getInstance().getDB().put("training_months", trainingMonths);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_train_chart, container, false);
        unbinder = ButterKnife.bind(this, view);

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
        refreshChart();

        tv_training_month.setText(parseDateInMonth(trainingMonthDate));

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // onclick binding part
    @OnClick(R.id.previous_month_button)
    public void previousMonth() {
        trainingMonthDate = trainingMonths.lower(trainingMonthDate);
        refreshChart();
    }

    @OnClick(R.id.forward_month_button)
    public void forwardMonth() {
        trainingMonthDate = trainingMonths.higher(trainingMonthDate);
        refreshChart();
    }

    /**
     * @param date
     * @return 返回date月份对应的展示训练数据集
     */
    BarData getBarData(Date date) {
        String program = ProgramContext.getInstance().getProgram();
        ProgramStatsController stats = ProgramContext.getInstance().getStats();
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
            String dateStr = parseDateInDay(date);
            xValues.add("" + i);
            int value = 0;
            // date月第i天自由模式训练量
            value = stats.getFreeCount(program, dateStr);
            // date月第i天计划模式训练量
            value += stats.getProgramCount(program, dateStr);
            counts += value;
            yValues.add(new BarEntry(value, i));
            if (value != 0) day_count++;
            // date月第i天训练时长
            seconds += stats.getTimeCount(program, dateStr);
            // 往后推一天
            cal.add(Calendar.DATE, 1);
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
        if (null != trainingMonths.lower(trainingMonthDate)) {
            btn_previous_month.setVisibility(View.VISIBLE);
        } else {
            btn_previous_month.setVisibility(View.GONE);
        }
        if (null != trainingMonths.higher(trainingMonthDate)) {
            btn_forward_month.setVisibility(View.VISIBLE);
        } else {
            btn_forward_month.setVisibility(View.GONE);
        }
        tv_training_month.setText(parseDateInMonth(trainingMonthDate));
        bc_trainingChart.setData(getBarData(trainingMonthDate));
        bc_trainingChart.animateY(1000);
    }
}
