package com.cyl.fitmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cyl.fitmanager.Appcontext.MainApplication;
import com.cyl.fitmanager.Activity.ProgramMainActivity;
import com.cyl.fitmanager.Data.GroupProp;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 应用启动入口，训练项目选择页面
 * Created by Administrator on 2016-1-14.
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private ListView lvEntry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainApplication) getApplication()).updateTrainingDay();
        setContentView(R.layout.activity_main);
        initData();
        initListView();
    }

    /**
     * 初始化训练数据
     */
    private void initData() {
        for (String program : Constant.PROGRAMS) {
            try {
                ((MainApplication) getApplication()).getDB().getObject(program + "_group", GroupProp.class);
            } catch (SnappydbException e) {
                GroupProp gp = new GroupProp();
                try {
                    ((MainApplication) getApplication()).getDB().put(program + "_group", gp);
                } catch (SnappydbException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void initListView() {
        lvEntry = (ListView) findViewById(R.id.entry_list);
        lvEntry.setAdapter(new EntryListAdapter());
    }

    private static class ViewHolder {
        public Button bt_entry = null;
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
            ViewHolder holder;
            if (null == convertView) {
                convertView = getLayoutInflater().inflate(R.layout.listitem_main, null);
                holder = new ViewHolder();
                holder.bt_entry = (Button) convertView.findViewById(R.id.entry_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.bt_entry.setText(mData.get(position));
            holder.bt_entry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String program = mData.get(position);
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
