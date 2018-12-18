package soexample.umeng.com.shopcar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import soexample.umeng.com.shopcar.adapter.MyAdapter;
import soexample.umeng.com.shopcar.bean.Bean;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String url = "http://www.zhaoapi.cn/product/getCarts?uid=71";
    private ExpandableListView el_cart;
    private CheckBox cb_cart_all_select;
    private TextView tv_cart_total_price;
    private Button but_cart_pay;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        OkHttpClient build = new OkHttpClient.Builder().build();
        final Request request = new Request.Builder().url(url).build();
        build.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String s = response.body().string();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Bean bean = gson.fromJson(s, Bean.class);
                        List<Bean.DataBean> data = bean.getData();
//                        适配器
                        adapter = new MyAdapter(MainActivity.this, data);
                        adapter.setOnCartListChangeListener(new MyAdapter.OnCartListChangeListener() {
                            @Override
                            public void SellerSelectedChange(int groupPosition) {
                                boolean b = adapter.isCurrenSellerAllProductSelected(groupPosition);
                                adapter.changeCurrentSellerAllProdutSelected(groupPosition, !b);
                                adapter.notifyDataSetChanged();
                                refreshAllSelectedAndTotalPriceAndTotalNumber();
                            }

                            @Override
                            public void changeCurrentProductSelected(int groupPosition, int childPosition) {
                                adapter.changeCurrenProdevtSelected(groupPosition,childPosition);
                                adapter.notifyDataSetChanged();
                                refreshAllSelectedAndTotalPriceAndTotalNumber();
                            }

                            @Override
                            public void ProductNumberChange(int groupPosition, int childPosition, int number) {
                                adapter.changeCurrenProductNumber(groupPosition,childPosition,number);
                                adapter.notifyDataSetChanged();
                                refreshAllSelectedAndTotalPriceAndTotalNumber();
                            }
                        });
                        el_cart.setAdapter(adapter);
                        for(int i=0;i<data.size();i++){
                            el_cart.expandGroup(i);
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        el_cart = (ExpandableListView) findViewById(R.id.el_cart);
        cb_cart_all_select = (CheckBox) findViewById(R.id.cb_cart_all_select);
        tv_cart_total_price = (TextView) findViewById(R.id.tv_cart_total_price);
        but_cart_pay = (Button) findViewById(R.id.but_cart_pay);
        cb_cart_all_select.setOnClickListener(this);
        but_cart_pay.setOnClickListener(this);

    }

    private void refreshAllSelectedAndTotalPriceAndTotalNumber() {

        boolean b = adapter.isAllProductsSelected();
        cb_cart_all_select.setChecked(b);
//计算总金额
        double v = adapter.calculateTotaprice();
        tv_cart_total_price.setText("总价：￥" + v);
        //计算总数量
        int i = adapter.CalculateTotalNumber();
        but_cart_pay.setText("去结算(" + i + ")");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_cart_pay:

                break;
            case R.id.cb_cart_all_select:
                boolean b = adapter.isAllProductsSelected();
                adapter.changeAllProductsSelected(!b);
                adapter.notifyDataSetChanged();
                refreshAllSelectedAndTotalPriceAndTotalNumber();
                break;
        }
    }
}
