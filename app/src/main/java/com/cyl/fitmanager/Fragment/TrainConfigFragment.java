package com.cyl.fitmanager.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.cyl.fitmanager.Model.GroupProp;
import com.cyl.fitmanager.Model.ProgramContext;
import com.cyl.fitmanager.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cyl.fitmanager.Utils.genArrayList;

/**

 */
public class TrainConfigFragment extends BaseConfigFragment {
    // 页面对应view
    List<Button> trainingDayButtons = new ArrayList<>();
    @BindView(R.id.training_program)
    LinearLayout ll_training_program;
    @BindView(R.id.group_count)
    TextView tv_group_count;
    @BindView(R.id.group_size)
    TextView tv_group_size;
    @BindView(R.id.group_interval)
    TextView tv_group_interval;
    // 训练计划
    GroupProp groupProp;
    // 单例
    private static TrainConfigFragment instance;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TrainConfigFragment.
     */
    public static synchronized TrainConfigFragment newInstance() {
        if(null == instance) {
            instance = new TrainConfigFragment();
        }
        return instance;
    }

    public TrainConfigFragment() {
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
        View view = inflater.inflate(R.layout.fragment_train_config, container, false);
        unbinder = ButterKnife.bind(this, view);
        trainingDayButtons.clear();
        trainingDayButtons.add((Button) view.findViewById(R.id.training_day_0));
        trainingDayButtons.add((Button) view.findViewById(R.id.training_day_1));
        trainingDayButtons.add((Button) view.findViewById(R.id.training_day_2));
        trainingDayButtons.add((Button) view.findViewById(R.id.training_day_3));
        trainingDayButtons.add((Button) view.findViewById(R.id.training_day_4));
        trainingDayButtons.add((Button) view.findViewById(R.id.training_day_5));
        trainingDayButtons.add((Button) view.findViewById(R.id.training_day_6));
        // 渲染训练日button集合
        for (int i = 0, mask = 1; i < 7; i++) {
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
                        ProgramContext.getInstance().getConfig().setProp(groupProp);
                    }
                    //refreshTrainingDay();
                }
            });
        }
        initData();
        return view;
    }

    @OnClick(R.id.training_program)
    public void onClick(View v) {
        new ProgramEditDialog(getActivity(), R.style.dialog).show();
    }

    void initData() {
        groupProp = ProgramContext.getInstance().getConfig().getProp();
        // view初始化
        tv_group_count.setText("" + groupProp.count);
        tv_group_size.setText("" + groupProp.size);
        tv_group_interval.setText("" + groupProp.interval);
        // 渲染训练日button集合
        for (int i = 0, mask = 1; i < 7; i++) {
            if ((groupProp.trainingDayBitMap & mask) != 0) {
                trainingDayButtons.get(i).setBackgroundResource(R.drawable.btn_training_day_selected);
            }
            mask <<= 1;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /**
     * 修改训练计划对应的对话框
     */
    class ProgramEditDialog extends Dialog {
        Context mContext;
        @BindView(R.id.group_size_picker)
        NumberPicker npGroupSize;
        @BindView(R.id.group_count_picker)
        NumberPicker npGroupCount;
        @BindView(R.id.group_interval_picker)
        NumberPicker npGroupInterval;
        ArrayList<String> groupSizeData;
        ArrayList<String> groupCountData;
        ArrayList<String> groupIntervalData;

        public ProgramEditDialog(Context context, int theme) {
            super(context, theme);
            mContext = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // 增加三个滑动选择控件，各自对应训练参数
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_edit_program, null);
            setContentView(view);
            ButterKnife.bind(this, view);
            // 设置显示数据对应数组
            groupSizeData = genArrayList(1, 1, 10);
            groupCountData = genArrayList(10, 5, 11);
            groupIntervalData = genArrayList(10, 10, 18);
            // 获取picker
//            npGroupCount = (NumberPicker) findViewById(R.id.group_count_picker);
//            npGroupSize = (NumberPicker) findViewById(R.id.group_size_picker);
//            npGroupInterval = (NumberPicker) findViewById(R.id.group_interval_picker);
            setNumberPiker(npGroupCount, tv_group_count, groupCountData);
            setNumberPiker(npGroupSize, tv_group_size, groupSizeData);
            setNumberPiker(npGroupInterval, tv_group_interval, groupIntervalData);
        }

        private void setNumberPiker(NumberPicker np, final TextView tv, final ArrayList<String> data) {
            np.setMinValue(0);
            np.setMaxValue(data.size() - 1);
            np.setDisplayedValues(data.toArray(new String[data.size()]));
            np.setWrapSelectorWheel(false);
            np.setValue(data.indexOf(tv.getText().toString()));
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    tv.setText(data.get(newVal));
                    updateGroup();
                }
            });
        }
    }

    /**
     * 根据当前文本值更新训练计划
     */
    void updateGroup() {
        groupProp.size = Integer.parseInt(tv_group_size.getText().toString());
        groupProp.count = Integer.parseInt(tv_group_count.getText().toString());
        groupProp.interval = Integer.parseInt(tv_group_interval.getText().toString());
        ProgramContext.getInstance().getConfig().setProp(groupProp);
    }
}
