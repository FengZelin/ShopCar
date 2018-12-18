package soexample.umeng.com.shopcar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * date:2018/12/17
 * author:冯泽林(asus)
 * function:
 */
public class SubAddView extends LinearLayout implements View.OnClickListener {
    private TextView sub_view, num_view, add_view;
    private int number = 1;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        num_view.setText(number+"");
    }

    public SubAddView(Context context) {
        this(context, null);
    }

    public SubAddView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.activity_sub_add, this);
        add_view = view.findViewById(R.id.add_view);
        num_view = view.findViewById(R.id.num_view);
        sub_view = view.findViewById(R.id.sub_view);
//        监听事件
        sub_view.setOnClickListener(this);
        add_view.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_view:
                ++number;
                num_view.setText(number+"");
                if (mOnNumberChangeListener != null) {
                    mOnNumberChangeListener.onNumberChange(number);
                }
                break;
            case R.id.sub_view:
                if (number > 1) {
                    --number;
                    num_view.setText(number+"");
                    if (mOnNumberChangeListener != null) {
                        mOnNumberChangeListener.onNumberChange(number);
                    }
                } else {
                    Toast.makeText(getContext(), "不能再少了哦！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public interface OnNumberChangeListener {
        void onNumberChange(int num);
    }

    private OnNumberChangeListener mOnNumberChangeListener;

    public void setOnNumberChangeListener(OnNumberChangeListener mOnNumberChangeListener) {
        this.mOnNumberChangeListener = mOnNumberChangeListener;
    }
}
