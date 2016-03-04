package com.cyl.fitmanager.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyl.fitmanager.Appcontext.MainApplication;
import com.cyl.fitmanager.R;
import com.cyl.fitmanager.Data.GroupProp;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cyl.fitmanager.Utils.parseDateInDay;

/**
 * 训练页面activity的基类，实现了训练状态的自动机框架，不同训练项目的动作识别（训练状态转移）由各自子类实现
 * Created by Administrator on 2016-1-21.
 */
public class TrainingBaseActivity extends Activity {
    private static final String TAG = "TrainingBaseActivity";
    String program;
    String style;
    SoundPool soundPool;
    int singleSoundID;
    Date startDate;
    // 布局view
    TextView tv_program;
    TextView tv_counting;
    TextView tv_todo;
    TextView tv_rest;
    LinearLayout ll_counting_bar;
    Button bt_quit;
    Button bt_finish;
    Button bt_reset_timer;
    List<TextView> countingBarItems = new ArrayList<>();
    // 训练参数
    int count;
    int finishedCount = 0;
    int currentGroup = 0;
    int trainingState;
    final int UNAVAILABLE = 0, INIT = 1, DOWN = 2, UP = 3;
    GroupProp groupProp;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_base);
        initData();
        initView();
        parseView();
        countDown(4);
    }

    /**
     * 初始化数据
     */
    void initData() {
        Bundle bundle = this.getIntent().getExtras();
        program = bundle.getString("program");
        style = bundle.getString("style");
        groupProp = bundle.getParcelable("group");
        if (null == groupProp) groupProp = new GroupProp();
        count = groupProp.count;
        soundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 0);
        singleSoundID = soundPool.load(this.getApplicationContext(), R.raw.shrink, 1);
        startDate = new Date();
    }

    /**
     * 初始化页面view句柄
     */
    void initView() {
        ll_counting_bar = (LinearLayout) findViewById(R.id.counting_bar);
        tv_counting = (TextView) findViewById(R.id.count_text);
        tv_program = (TextView) findViewById(R.id.program_text);
        tv_todo = (TextView) findViewById(R.id.todo_text);
        tv_rest = (TextView) findViewById(R.id.rest_text);
        bt_quit = (Button) findViewById(R.id.quit_button);
        bt_finish = (Button) findViewById(R.id.finish_button);
        bt_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTrainingQuit();
            }
        });
        bt_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTrainingFinish();
            }
        });
        bt_reset_timer = (Button) findViewById(R.id.reset_timer_button);
    }

    /**
     * 渲染页面View
     */
    void parseView() {
        tv_program.setText("(个)" + program);
        if (style.equals(getString(R.string.program_style))) {
            initTitleBar();
        }
    }

    /**
     * 初始化布局训练页面布局
     * 渲染页面title的训练进度/初始化页面中部的次数统计/渲染训练日按钮
     */
    private void initTitleBar() {
        LinearLayout.LayoutParams lp_count_item = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // 渲染title的训练进度
        for (int i = 1; i <= groupProp.size; i++) {
            // 根据训练计划组数添加title每组计划数文本view
            TextView count_bar_item = new TextView(this.getApplicationContext());
            count_bar_item.setText("" + groupProp.count);
            count_bar_item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            count_bar_item.setTextColor(getResources().getColor(R.color.white));
            count_bar_item.setTypeface(count_bar_item.getTypeface(), Typeface.BOLD);
            count_bar_item.setPadding(20, 0, 20, 0);
            count_bar_item.setLayoutParams(lp_count_item);
            count_bar_item.setGravity(Gravity.CENTER);
            countingBarItems.add(count_bar_item);
            ll_counting_bar.addView(count_bar_item);
            // 根据训练计划组数添加title箭头图标
            if (i != groupProp.size) {
                ImageView count_bar_bound = new ImageView(this.getApplicationContext());
                count_bar_bound.setBackgroundResource(R.drawable.seg_training_title_bar);
                ll_counting_bar.addView(count_bar_bound);
            }
        }
    }

    /**
     * 返回键的点击事件处理
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (style.equals(getString(R.string.free_style))) {
                onTrainingFinish();
            } else if (style.equals(getString(R.string.program_style))) {
                onTrainingQuit();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 响应完成当前训练计划事件，记录测试数据
     */
    void onTrainingFinish() {
        if (trainingState == UNAVAILABLE) {
            finish();
            return;
        }
        Date date = new Date();
        // 更新训练用时
        long seconds = (date.getTime() - startDate.getTime()) / 1000;
        long current_seconds;
        try {
            current_seconds = ((MainApplication) getApplication()).getDB().getLong(program + "_seconds_" + parseDateInDay(date));
        } catch (SnappydbException e) {
            // not found in db
            current_seconds = 0;
        }
//        Log.e("write seconds", program + "_seconds_" + parseDateInDay(date) + ": " + (current_seconds + seconds));
        try {
            if ((current_seconds + seconds) != 0)
                ((MainApplication) getApplication()).getDB().putLong(program + "_seconds_" + parseDateInDay(date), current_seconds + seconds);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        // 更新训练统计
        int current_count;
        try {
            current_count = ((MainApplication) getApplication()).getDB().getInt(program + "_free_count_" + parseDateInDay(date));
        } catch (SnappydbException e) {
            // not found in db
            current_count = 0;
        }
        Log.e("write count", program + "_program_count_" + parseDateInDay(date) + ": " + finishedCount);
        Log.e("write count", program + "_free_count_" + parseDateInDay(date) + ": " + count + current_count);
        try {
            if (finishedCount != 0)
                ((MainApplication) getApplication()).getDB().putInt(program + "_program_count_" + parseDateInDay(date), finishedCount);
            if (count != 0)
                ((MainApplication) getApplication()).getDB().putInt(program + "_free_count_" + parseDateInDay(date), count + current_count);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        // todo 弹一个对话框，展示本次训练完成情况，包括完成次数和所用时间
        new AlertDialog.Builder(this).
                setTitle("本次训练").
                setMessage("完成数量（个）：" + (finishedCount + count) + "\n" + "训练耗时（秒）：" + seconds).
                setCancelable(false).
                setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).
                show();
    }

    /**
     * 响应放弃当前训练事件
     */
    void onTrainingQuit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrainingBaseActivity.this);
        builder.setMessage("是否退出训练？将丢失当前训练进度。");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 响应一组动作完成
     */
    protected void onGroupFinish() {
        if (style.equals(getString(R.string.free_style))) {
            return;
        }
        currentGroup++;
        // 已完成所有组
        if (currentGroup >= groupProp.size) {
            finishedCount += (groupProp.size * groupProp.count);
            count = 0;
            style = getString(R.string.free_style);
            onFreeStylePageShow("本日训练计划已完成！");
        }
        // 未完成所有组
        else {
            countDown(groupProp.interval);
        }
    }

    /**
     * 响应单个动作完成
     */
    protected void onSingleFinish() {
        if (style.equals(getString(R.string.free_style))) {
            count++;
        } else {
            count--;
        }
        tv_counting.setText("" + count);
        soundPool.play(singleSoundID, 1, 1, 0, 0, 1);
    }

    /**
     * 展示自由模式的训练页面
     */
    void onFreeStylePageShow(String text) {
        // 页面中部textview
        tv_counting.setText("" + count);
        tv_program.setVisibility(View.VISIBLE);
        tv_todo.setVisibility(View.VISIBLE);
        tv_todo.setText("已完成");
        tv_rest.setVisibility(View.GONE);
        // 页面底部按钮
        bt_quit.setVisibility(View.GONE);
        bt_finish.setVisibility(View.VISIBLE);
        bt_reset_timer.setVisibility(View.GONE);

    }

    /**
     * 展示计划模式的训练页面
     */
    void onProgramStylePageShow() {
        // titlebar训练进度
        if (currentGroup != 0) {
            countingBarItems.get(currentGroup - 1).setBackgroundColor(getResources().getColor(R.color.transparent));
        }
        countingBarItems.get(currentGroup).setBackgroundColor(getResources().getColor(R.color.gray));
        // 页面中部textview
        tv_counting.setText("" + count);
        tv_program.setVisibility(View.VISIBLE);
        tv_todo.setVisibility(View.VISIBLE);
        tv_rest.setVisibility(View.GONE);
        // 页面底部按钮
        bt_quit.setVisibility(View.VISIBLE);
        bt_reset_timer.setVisibility(View.GONE);
        bt_finish.setVisibility(View.GONE);
    }

    /**
     * 展示倒计时等待页面
     */
    void onTimerPageShow() {
        // 页面中部textview
        tv_program.setVisibility(View.GONE);
        tv_todo.setVisibility(View.GONE);
        tv_rest.setVisibility(View.VISIBLE);
        // 页面底部返回/结束按钮
        if (currentGroup != 0) {
            bt_finish.setVisibility(View.GONE);
            bt_quit.setVisibility(View.GONE);
            bt_reset_timer.setVisibility(View.VISIBLE);
        } else if (style.equals(getString(R.string.free_style))) {
            bt_finish.setVisibility(View.VISIBLE);
            bt_quit.setVisibility(View.GONE);
            bt_reset_timer.setVisibility(View.GONE);
        } else {
            bt_finish.setVisibility(View.GONE);
            bt_quit.setVisibility(View.VISIBLE);
            bt_reset_timer.setVisibility(View.GONE);
        }
    }

    /**
     * @param time 启动训练页面的倒计时计数器
     */
    protected void countDown(long time) {
        final myCountDownTimer timer = new myCountDownTimer(time * 1000 + 200, 1, null);
        trainingState = UNAVAILABLE;
        onTimerPageShow();
        if (currentGroup != 0) {
            bt_reset_timer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timer.cancel();
                    String[] tmp = tv_counting.getText().toString().split(":");
                    int leftTime = Integer.parseInt(tmp[0]) * 60 + Integer.parseInt(tmp[1]);
                    // 制造快速倒计时效果
                    new myCountDownTimer(leftTime * 1000, 60, new myCountDownTimer(3200, 1, null)).start();
                    v.setClickable(false);
                }
            });
        }
        timer.start();
    }

    /**
     * 封装倒数计数器对页面的更新逻辑
     */
    private class myCountDownTimer extends CountDownTimer {
        myCountDownTimer nextTimer;
        int speed;

        /**
         * @param millisInFuture The number of millis in the future from the call
         *                       to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                       is called.
         */
        public myCountDownTimer(long millisInFuture, int speed, myCountDownTimer nextTimer) {
            super(millisInFuture / speed, 1000 / speed);
            this.speed = speed;
            this.nextTimer = nextTimer;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long minutes = millisUntilFinished * speed / 1000 / 60;
            long seconds = millisUntilFinished * speed / 1000 % 60;
            String sMinutes = minutes >= 10 ? "" + minutes : "0" + minutes;
            String sSeconds = seconds >= 10 ? "" + seconds : "0" + seconds;
            tv_counting.setText(sMinutes + ":" + sSeconds);
        }

        @Override
        public void onFinish() {
            if (nextTimer != null) {
                nextTimer.start();
            } else if (style.equals(getString(R.string.free_style))) {
                count = 0;
                trainingState = INIT;
                onFreeStylePageShow("已完成");
            } else if (style.equals(getString(R.string.program_style))) {
                count = groupProp.count;
                trainingState = INIT;
                onProgramStylePageShow();
            }
        }
    }
}
