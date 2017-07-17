package com.cyl.fitmanager.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyl.fitmanager.Constant;
import com.cyl.fitmanager.Controller.DataController;
import com.cyl.fitmanager.Fragment.TrainChartFragment;
import com.cyl.fitmanager.Fragment.TrainConfigFragment;
import com.cyl.fitmanager.Fragment.TrainEntryFragment;
import com.cyl.fitmanager.Model.ProgramContext;
import com.cyl.fitmanager.R;
import com.cyl.fitmanager.Model.GroupProp;
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

import static com.cyl.fitmanager.Utils.firstDayOfMonth;
import static com.cyl.fitmanager.Utils.parseDateInDay;
import static com.cyl.fitmanager.Utils.parseDateInMonth;
import static com.cyl.fitmanager.Utils.parseSecondToTime;

/**
 * 训练项目主页面，包括训练计划信息和历史数据展示部分
 * TODO 计划使用fragment和依赖注入库重构这个页面
 * Created by Administrator on 2016-1-21.
 */
public class ProgramMainActivity extends Activity {
    private static final String TAG = "ProgramMainActivity";

    // 渲染数据
    String program;
    // fragment容器
    LinearLayout ll_fragment_container;

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
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 获取训练计划标志串
        program = ProgramContext.getInstance().getProgram();
    }

    /**
     * 初始化页面view句柄
     */
    private void initView() {
        // 往fragment容器添加fragment
        ll_fragment_container = (LinearLayout) findViewById(R.id.fragment_container);
        for (Fragment fragment : Constant.fragments) {
            FrameLayout containerFrameLayout = new FrameLayout(ProgramMainActivity.this);
//            containerFrameLayout.setLayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            containerFrameLayout.setId(View.generateViewId());
            ll_fragment_container.addView(containerFrameLayout);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().
                    add(ll_fragment_container.getId(), fragment).
                    commitAllowingStateLoss();
        }
    }

    /**
     * 渲染页面View
     */
    private void parseView() {
        // 渲染title
        ((TextView) findViewById(R.id.program_text)).setText(program);
    }
}