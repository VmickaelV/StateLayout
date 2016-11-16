package com.objectlife.statelayoutsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.objectlife.statelayout.StateLayout;


public class FourthActivity extends AppCompatActivity implements View.OnClickListener {

    private StateLayout mStateLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        mStateLayout = (StateLayout) findViewById(R.id.sl_layout_state);

        findViewById(R.id.btn_content).setOnClickListener(this);
        findViewById(R.id.btn_empty).setOnClickListener(this);
        findViewById(R.id.btn_error).setOnClickListener(this);
        findViewById(R.id.btn_loading).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_content:
                mStateLayout.setState(StateLayout.VIEW_CONTENT);
                break;

            case R.id.btn_empty:
                mStateLayout.setState(StateLayout.VIEW_EMPTY);
                break;

            case R.id.btn_error:
                mStateLayout.setState(StateLayout.VIEW_ERROR);
                break;

            case R.id.btn_loading:
                mStateLayout.setState(StateLayout.VIEW_LOADING);
                break;
        }
    }
}
