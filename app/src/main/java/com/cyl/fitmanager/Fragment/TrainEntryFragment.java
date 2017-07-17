package com.cyl.fitmanager.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cyl.fitmanager.Constant;
import com.cyl.fitmanager.Model.ProgramContext;
import com.cyl.fitmanager.R;
import com.cyl.fitmanager.Utils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cyl.fitmanager.Utils.parseDateInDay;

/**
 * 训练入口页面组件
 */
public class TrainEntryFragment extends BaseConfigFragment {
    @BindView(R.id.free_button)
    Button btn_free_style;
    @BindView(R.id.program_button)
    Button btn_program_style;
    @BindView(R.id.next_training_day)
    TextView tv_nextTrainingDay;

    private ProgramContext programContext;

    private static TrainEntryFragment instance;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TrainEntryFragment.
     */
    public static TrainEntryFragment newInstance() {
        if (null == instance) {
            instance = new TrainEntryFragment();
        }
        return instance;
    }

    public TrainEntryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_train_entry, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        programContext = ProgramContext.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTrainingDay();
    }

    @OnClick({R.id.free_button, R.id.program_button})
    public void startTrain(View view) {
        Log.e("startTraining", ((Button) view).getText().toString());
        Bundle bundle = new Bundle();
        Intent intent = new Intent(getActivity(), Constant.PROGRAM_CLASS.get(programContext.getProgram()));
        bundle.putString("style", ((Button) view).getText().toString());
        intent.putExtras(bundle);
        startTraining(intent);
    }

    /**
     * 刷新训练日
     */
    void refreshTrainingDay() {
        Utils.updateTrainingDay();
        String today = parseDateInDay(new Date());
        String training_day = programContext.getConfig().getNextTrainingDay();
        tv_nextTrainingDay.setText(training_day);
        // 下一训练日为当日，开放训练入口
        if (today.equals(training_day)) {
            btn_program_style.setClickable(true);
            btn_program_style.setAlpha(1f);
        }
        // 关闭训练入口
        else {
            btn_program_style.setClickable(false);
            btn_program_style.setAlpha(.5f);
        }
    }

    /**
     * @param intent 展示训练提示对话框
     */
    void startTraining(Intent intent) {
        if (!programContext.getConfig().getTrainTipFlag()) {
            startActivity(intent);
        } else {
            new TipDialog(getActivity(), R.style.dialog, intent).show();
        }
    }

    /**
     * 在进入训练页面前用以展示训练指引的dialog类
     */
    class TipDialog extends Dialog {
        Context mContext;
        Intent mIntent;
        @BindView(R.id.tip_text)
        TextView tvTips;
        @BindView(R.id.continue_button)
        Button btContinue;
        @BindView(R.id.no_show_checkbox)
        CheckBox cbShowTip;

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
            ButterKnife.bind(this, view);
            tvTips.setText(Constant.PROGRAM_TIP.get(programContext.getProgram()));
            Window dialogWindow = getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = getResources().getDisplayMetrics(); // 获取屏幕宽、高用
            lp.width = (int) (d.widthPixels * 0.9);
            dialogWindow.setAttributes(lp);
        }

        @OnClick(R.id.continue_button)
        public void onClick() {
            programContext.getConfig().setTrainTipFlag(!cbShowTip.isChecked());
            startActivity(mIntent);
            dismiss();
        }
    }
}
