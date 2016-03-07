package com.cyl.fitmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyl.fitmanager.Appcontext.MainApplication;
import com.cyl.fitmanager.Activity.ProgramMainActivity;
import com.cyl.fitmanager.Data.GroupProp;
import com.cyl.fitmanager.Receiver.AlarmReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.cyl.fitmanager.Utils.parseDateInDay;

/**
 * 应用启动入口，训练项目选择页面
 * Created by Administrator on 2016-1-14.
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private ListView lvEntry;
    private AlarmReceiver alarm = new AlarmReceiver();
    private EntryListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainApplication) getApplication()).updateTrainingDay();
        alarm.setAlarm(this);
        setContentView(R.layout.activity_main);
        initData();
        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    /**
     * 初始化训练数据
     */
    private void initData() {
        for (String program : Constant.PROGRAMS) {
            GroupProp gp = ((MainApplication) getApplication()).getObjInSp(program + "_group", GroupProp.class);
            if (null == gp) {
                gp = new GroupProp();
                ((MainApplication) getApplication()).putObjInSp(program + "_group", gp);
            }
        }
        adapter = new EntryListAdapter();
    }

    private void initListView() {
        lvEntry = (ListView) findViewById(R.id.entry_list);
        lvEntry.setAdapter(adapter);
    }

    private static class ViewHolder {
        public TextView tv_entry = null;
        public LinearLayout ll_entry = null;
    }

    private class EntryListAdapter extends BaseAdapter {
        private List<String> mData = new ArrayList<>();

        EntryListAdapter() {
            mData = Arrays.asList(Constant.PROGRAMS);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final String program = mData.get(position);
            ViewHolder holder;
            if (null == convertView) {
                convertView = getLayoutInflater().inflate(R.layout.listitem_main, null);
                holder = new ViewHolder();
                holder.tv_entry = (TextView) convertView.findViewById(R.id.entry_text);
                holder.ll_entry = (LinearLayout) convertView.findViewById(R.id.entry_background);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_entry.setText(program);
            if (((MainApplication) getApplication()).updateTrainingDay(program).equals(parseDateInDay(new Date()))) {
                holder.ll_entry.setBackgroundResource(R.drawable.listitem_main_unfinished_selector);
                Animation alphaAnimation=new AlphaAnimation(1, (float) 0.6);
                alphaAnimation.setRepeatCount(Animation.INFINITE);
                alphaAnimation.setRepeatMode(Animation.REVERSE);
                alphaAnimation.setDuration(500);
                holder.ll_entry.startAnimation(alphaAnimation);
            } else {
                holder.ll_entry.setBackgroundResource(R.drawable.listitem_main_finished_selector);
                holder.ll_entry.clearAnimation();
                holder.ll_entry.clearAnimation();
            }
            holder.ll_entry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("program", program);
                    Intent intent = new Intent(MainActivity.this, ProgramMainActivity.class);
                    intent.putExtras(bundle);
                    // 深蹲施工中
                    if (position != 0) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "抱歉，深蹲现场施工中", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return convertView;
        }
    }
}
