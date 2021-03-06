package com.wangtian.message.sociality;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.wangtian.message.MyApplication;
import com.wangtian.message.R;
import com.wangtian.message.base.BaseMainMenuActivity;
import com.wangtian.message.netBean.CategoryBean;
import com.wangtian.message.netWork.NetWorkSubscriber;
import com.wangtian.message.netWork.NetWorkUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SocialListActivity extends BaseMainMenuActivity {

    @BindView(R.id.list_tb)
    SlidingTabLayout mListTb;
    @BindView(R.id.list_vp)
    ViewPager mListVp;
    private ProgressDialog dialog;
    private SocialVpAdapter mNewsVpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        ButterKnife.bind(this);
        setActivity(SocialListActivity.this, "社交");

        left(2).setOnClickListener(v -> menu.toggle(menu.isShown()));

        initView();
    }

    private void initView() {
        dialog = new ProgressDialog(SocialListActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


        HashMap<String, String> param = new HashMap<>();
        param.put("token", MyApplication.sLoginBean.getToken());
        Log.d("SocialListActivity", "getCategoryList = " + param.toString());
        NetWorkUtils.getInstance().getInterfaceService().getCategoryList(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetWorkSubscriber<ArrayList<CategoryBean>>() {

                    @Override
                    public void onNext(ArrayList<CategoryBean> categoryBeans) {
                        if (categoryBeans != null && categoryBeans.size() > 0) {
                            mListTb.setVisibility(View.VISIBLE);
                            String[] strings = new String[categoryBeans.size() + 1];
                            strings[0] = "全部";
                            ArrayList<String> types = new ArrayList<>();
                            types.add("ALL");
                            for (int i = 0; i < categoryBeans.size(); i++) {
                                CategoryBean categoryBean = categoryBeans.get(i);
                                strings[i + 1] = categoryBean.getBq() + "";
                                types.add(categoryBean.getId());
                            }


                            mNewsVpAdapter = new SocialVpAdapter(getSupportFragmentManager(), types);
                            mListVp.setAdapter(mNewsVpAdapter);
                            mListTb.setViewPager(mListVp, strings);

                        } else {
                            mListTb.setVisibility(View.GONE);
                            String[] strings = new String[1];
                            strings[0] = "全部";
                            ArrayList<String> types = new ArrayList<>();
                            types.add("ALL");
                            mNewsVpAdapter = new SocialVpAdapter(getSupportFragmentManager(), types);
                            mListVp.setAdapter(mNewsVpAdapter);
                            mListTb.setViewPager(mListVp, strings);
                        }
                    }
                });

    }
}
