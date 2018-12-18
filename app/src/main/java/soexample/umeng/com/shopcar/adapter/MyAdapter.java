package soexample.umeng.com.shopcar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import soexample.umeng.com.shopcar.R;
import soexample.umeng.com.shopcar.SubAddView;
import soexample.umeng.com.shopcar.bean.Bean;

/**
 * date:2018/12/18
 * author:冯泽林(asus)
 * function:
 */
public class MyAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Bean.DataBean> list;
    private CheckBox cb_child_item;
    private TextView tv_title_child;
    private TextView tv_price_child;
    private SubAddView sub_add_view;

    public MyAdapter(Context context, List<Bean.DataBean> list) {
        this.list = list;
        this.context = context;
    }

    @Override//决定有多少个组
    public int getGroupCount() {
        return list.size();
    }

    @Override//一个组里有多少个子条目
    public int getChildrenCount(int i) {
        return list.get(i).getList().size();
    }


    @Override    //设置外层
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        GroupViewHodel hodel;
        if (view == null) {
            hodel = new GroupViewHodel();
            view = View.inflate(context, R.layout.item, null);
            hodel.cb_cart_item = view.findViewById(R.id.cb_group_item);
            hodel.tv_title_group = view.findViewById(R.id.tv_title_group);
            view.setTag(hodel);
        } else {
            hodel = (GroupViewHodel) view.getTag();
        }
        Bean.DataBean dataBean = list.get(i);
//        商家名称
        hodel.tv_title_group.setText(dataBean.getSellerName());
//      商家的复选框
        boolean bb = isCurrenSellerAllProductSelected(i);
        hodel.cb_cart_item.setChecked(bb);
//        点击复选框触发监听
        hodel.cb_cart_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onCartListChangeListener != null) {
                    onCartListChangeListener.SellerSelectedChange(i);
                }
            }
        });
        return view;
    }

    public void changeCurrentSellerAllProdutSelected(int position, boolean b) {
        List<Bean.DataBean.ListBean> data = this.list.get(position).getList();
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setSelected(b ? 1 : 0);
        }
    }

    //  用来判断外层的Selected值 如果是0 不选中 否则选中
    public boolean isCurrenSellerAllProductSelected(int position) {
        List<Bean.DataBean.ListBean> data = this.list.get(position).getList();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getSelected() == 0) {
                return false;
            }
        }
        return true;
    }

    //    外层ViewHolder
    public static class GroupViewHodel {
        public CheckBox cb_cart_item;
        public TextView tv_title_group;
    }

    @Override
    public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder holder;
        if (view == null) {
            holder = new ChildViewHolder();
            view = View.inflate(context, R.layout.childitem, null);
            holder.cb_child_item = view.findViewById(R.id.cb_child_item);
            holder.iv_icon_child = view.findViewById(R.id.iv_icon_child);
            holder.sub_add_view = view.findViewById(R.id.sub_add_view);
            holder.tv_title_child = view.findViewById(R.id.tv_title_child);
            holder.tv_price_child = view.findViewById(R.id.tv_price_child);
            view.setTag(holder);
        } else {
            holder = (ChildViewHolder) view.getTag();
        }
        Bean.DataBean.ListBean listBean = list.get(i).getList().get(i1);

//       商品简介
        holder.tv_title_child.setText(listBean.getTitle());
//        商品价格
        holder.tv_price_child.setText("￥" + listBean.getPrice());
//        图片
        String images = listBean.getImages();
        String[] split = images.split("\\|");
        Glide.with(context).load(split[0]).into(holder.iv_icon_child);
//      商品复选框点击
        holder.cb_child_item.setChecked(listBean.getSelected() == 1);
        holder.cb_child_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCartListChangeListener.changeCurrentProductSelected(i, i1);

            }
        });

        holder.sub_add_view.setOnNumberChangeListener(new SubAddView.OnNumberChangeListener() {
            @Override
            public void onNumberChange(int num) {
                onCartListChangeListener.ProductNumberChange(i, i1, num);
            }
        });
        return view;
    }

    public void changeCurrenProductNumber(int i, int i1, int number) {
        Bean.DataBean.ListBean bean = list.get(i).getList().get(i1);
        bean.setNum(number);
    }

    public void changeCurrenProdevtSelected(int i, int i1) {
        Bean.DataBean.ListBean listBean = list.get(i).getList().get(i1);
        listBean.setSelected(listBean.getSelected() == 0 ? 1 : 0);
    }

    public double calculateTotaprice() {
        double totaprice = 0;
        for (int i = 0; i < list.size(); i++) {
            List<Bean.DataBean.ListBean> data = list.get(i).getList();
            for (int j = 0; j < data.size(); j++) {
                if (data.get(j).getSelected() == 1) {
                    double price = data.get(j).getPrice();
                    int num = data.get(j).getNum();
                    totaprice += price * num;
                }
            }
        }
        return totaprice;
    }

    public int CalculateTotalNumber() {
        int number = 0;
        for (int i = 0; i < list.size(); i++) {
            List<Bean.DataBean.ListBean> data = list.get(i).getList();
            for (int j = 0; j < data.size(); j++) {
//                只有选中才计算
                if (data.get(j).getSelected() == 1) {
                    int num = data.get(j).getNum();
                    number += num;
                }
            }
        }
        return number;
    }

    public boolean isAllProductsSelected() {
        for (int i = 0; i < list.size(); i++) {
            List<Bean.DataBean.ListBean> data = list.get(i).getList();
            for (int j = 0; j < data.size(); j++) {
                if (data.get(j).getSelected() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void changeAllProductsSelected(boolean b) {
        for (int i = 0; i < list.size(); i++) {
            List<Bean.DataBean.ListBean> data = list.get(i).getList();
            for (int j = 0; j < data.size(); j++) {
                data.get(j).setSelected(b ? 1 : 0);
            }
        }
    }

    // 子类
    public static class ChildViewHolder {
        public CheckBox cb_child_item;
        public ImageView iv_icon_child;
        public TextView tv_title_child;
        public TextView tv_price_child;
        public SubAddView sub_add_view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    //   自定义接口回调
    public interface OnCartListChangeListener {
        void SellerSelectedChange(int groupPosition);

        void changeCurrentProductSelected(int groupPosition, int childPosition);

        void ProductNumberChange(int groupPosition, int childPosition, int number);
    }

    OnCartListChangeListener onCartListChangeListener;

    public void setOnCartListChangeListener(OnCartListChangeListener onCartListChangeListener) {
        this.onCartListChangeListener = onCartListChangeListener;
    }

    //    以下不用管
    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}
