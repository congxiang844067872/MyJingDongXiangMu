package com.bwie.myjingdongxiangmu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bwie.myjingdongxiangmu.R;
import com.bwie.myjingdongxiangmu.activity.MyDingDanActivity;
import com.bwie.myjingdongxiangmu.bean.MyDataDingDanBean;
import com.bwie.myjingdongxiangmu.holder.MyDingDanHolder;
import com.bwie.myjingdongxiangmu.presenter.RetrofitPresenter;
import com.bwie.myjingdongxiangmu.util.ApiUtil;
import com.bwie.myjingdongxiangmu.util.OkHttp3Util;
import com.bwie.myjingdongxiangmu.util.RetrofitUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * Created by admin on 2018/4/2.
 */

public class MyYiQuAdapter extends RecyclerView.Adapter<MyDingDanHolder> {


    private final Context context;
    private final MyDataDingDanBean myDataDingDanBean;
    private final RetrofitPresenter retrofitPresenter;
    private final Map<String, String> map;

    public MyYiQuAdapter(Context context, MyDataDingDanBean myDataDingDanBean, RetrofitPresenter retrofitPresenter, Map<String, String> map) {

        this.context = context;
        this.myDataDingDanBean = myDataDingDanBean;
        this.retrofitPresenter = retrofitPresenter;
        this.map = map;
    }

    @Override
    public MyDingDanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dingdan,parent, false);
        MyDingDanHolder holder = new MyDingDanHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyDingDanHolder holder, final int position) {
        SharedPreferences sharedPreferences= context
                .getSharedPreferences("yonghuxinxi", Activity.MODE_PRIVATE);
        final String uid = sharedPreferences.getString("uid", "");
        final List<MyDataDingDanBean.DataBean> list = myDataDingDanBean.getData();
        holder.text_title.setText(list.get(position).getTitle());
        holder.text_price.setText("价格: "+list.get(position).getPrice());
        holder.text_tame.setText(list.get(position).getCreatetime());
        if (list.get(position).getStatus()==2){
            holder.text_daizhifu.setText("已取消");
            holder.text_daizhifu.setTextColor(Color.BLACK);
            holder.text_btn.setText("查看订单");
            holder.text_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("提示");
                    builder.setMessage("确定循环利用吗?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Map<String, String> params = new HashMap<>();
                            params.put("uid", uid);
                            params.put("orderId", String.valueOf(list.get(position).getOrderid()));
                            params.put("status", String.valueOf(0));
                            OkHttp3Util.doPost(ApiUtil.genxin, params, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        list.get(position).setStatus(0);
                                        //Log.d("+++++++++响哥", String.valueOf(list.get(position).getStatus()));
                                        retrofitPresenter.getUrl(RetrofitUtil
                                                .getService().doGet(ApiUtil.dingdan2, map));
                                        ((MyDingDanActivity)context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                notifyDataSetChanged();
                                            }
                                        });

                                    }
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return myDataDingDanBean.getData().size();
    }
}
