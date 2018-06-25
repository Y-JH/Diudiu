package com.dalimao.mytaxi.lbs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalimao.mytaxi.R;

import java.util.List;

/**
 * @Title:PoiAdapter
 * @Package:com.dalimao.mytaxi.lbs
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/259:57
 */
public class PoiAdapter extends ArrayAdapter {
    private final String TAG = "PoiAdapter";
    private LayoutInflater inflater;
    private List<String> data;
    private OnItemtClickListener mOnItemtClickListener;

    public PoiAdapter(Context context, List data) {
        super(context, R.layout.poi_list_item);
        this.data = data;
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void setOnItemtClickListener(OnItemtClickListener listener) {
        mOnItemtClickListener = listener;
    }

    public void setData(List<String> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();

    }


    @Override
    public int getCount() {
        Log.e(TAG, "getCount==" + data.size());
        return null == data ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.poi_list_item, null);
            holder = new Holder();
            holder.textView = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);

        } else {
            Object tag = convertView.getTag();
            if (tag == null) {
                holder = new Holder();
                holder.textView = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) tag;
            }
        }
        holder.id = position;
        holder.textView.setText(data.get(position));

        return convertView;
    }


    class Holder {
        int id;
        TextView textView;
    }

    public static interface OnItemtClickListener {
        void onItemClick(int id);
    }
}
