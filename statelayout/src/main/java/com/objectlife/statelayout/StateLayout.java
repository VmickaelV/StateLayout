/*
 * Copyright (C) 2016 objectlife
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.objectlife.statelayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.zip.Inflater;

/**
 * A subclass of FrameLayout that can display different state of view.like contentView, emptyView,
 * errorView and loadingView. you can set state view by {@link #setContentView(View)} or {@link #setLoadingViewResId(int)},
 * and you can switch state by call {@link #setState(int)}.
 *
 * @author objectlife (wangyuyanmail[at]gmail[dot]com)
 */
public class StateLayout extends FrameLayout {

    @IntDef({VIEW_CONTENT, VIEW_EMPTY, VIEW_ERROR, VIEW_LOADING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewState {}

    public static final int VIEW_CONTENT = 0x00000000;
    public static final int VIEW_EMPTY   = 0x00000001;
    public static final int VIEW_ERROR   = 0x00000002;
    public static final int VIEW_LOADING = 0x00000003;

    private View mContentView;
    private View mEmptyView;
    private View mErrorView;
    private View mLoadingView;

    private int defViewState = VIEW_LOADING;

    public StateLayout(Context context) {
        this(context, null);
    }

    public StateLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
	    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.slStateLayout, 0, 0);
	    defViewState = a.getInt(R.styleable.slStateLayout_slInitState, defViewState);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int refLayout;

        refLayout = a.getResourceId(R.styleable.slStateLayout_slContentStateViewLayout, 0);
        if (refLayout > 0) {
            layoutInflater.inflate(refLayout, this, true);
            mContentView = this.getChildAt(this.getChildCount()-1);
            ((LayoutParams) mContentView.getLayoutParams()).slState = VIEW_CONTENT;
        }

        refLayout = a.getResourceId(R.styleable.slStateLayout_slErrorStateViewLayout, 0);
        if (refLayout > 0) {
            layoutInflater.inflate(refLayout, this, true);
            mErrorView = this.getChildAt(this.getChildCount()-1);
            ((LayoutParams) mErrorView.getLayoutParams()).slState = VIEW_ERROR;
        }

        refLayout = a.getResourceId(R.styleable.slStateLayout_slEmptyStateViewLayout, 0);
        if (refLayout > 0) {
            layoutInflater.inflate(refLayout, this, true);
            mEmptyView = this.getChildAt(this.getChildCount()-1);
            ((LayoutParams) mEmptyView.getLayoutParams()).slState = VIEW_EMPTY;
        }

        refLayout = a.getResourceId(R.styleable.slStateLayout_slLoadingStateViewLayout, 0);
        if (refLayout > 0) {
            layoutInflater.inflate(refLayout, this, true);
            mLoadingView = this.getChildAt(this.getChildCount()-1);
            ((LayoutParams) mLoadingView.getLayoutParams()).slState = VIEW_LOADING;
        }

        a.recycle();

        // Code Hack
        int t = defViewState;
        setState(-1);
        setState(t);
    }

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);

        LayoutParams that = (LayoutParams) params;

        switch (that.slState) {
            case VIEW_CONTENT:
                mContentView = child;
                break;
            case VIEW_EMPTY:
                mEmptyView = child;
                break;
            case VIEW_ERROR:
                mErrorView = child;
                break;
            case VIEW_LOADING:
                mLoadingView = child;
                break;
        }

        child.setVisibility(that.slState == defViewState ? VISIBLE : GONE);
	}

    @Override
    protected FrameLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /**
     *<p>Set content view.</p>
     * @param contentView The content view to add
     * @return This StateLayout object to allow for chaining of calls to set methods
     */
    public StateLayout setContentView(View contentView) {
        this.mContentView = contentView;
        initStateView(mContentView);
        return this;
    }

    /**
     *<p>Specify content view with the given id</p>
     * @param viewResId The id to specify
     * @return This StateLayout object to allow for chaining of calls to set methods
     */
    public StateLayout setContentViewResId(@IdRes int viewResId) {
        mContentView = findViewById(viewResId);
	    if (mContentView != null && defViewState != VIEW_CONTENT) {
		    mContentView.setVisibility(GONE);
	    }
        return this;
    }

    /**
     *<p>Set empty view.</p>
     * @param emptyView The empty view to add
     * @return This StateLayout object to allow for chaining of calls to set methods
     */
    public StateLayout setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        initStateView(mEmptyView);
        return this;
    }

    /**
     *<p>Specify empty view with the given id</p>
     * @param viewResId The id to specify
     * @return This StateLayout object to allow for chaining of calls to set methods
     */
    public StateLayout setEmptyViewResId(@IdRes int viewResId) {
        mEmptyView = findViewById(viewResId);
	    if (mEmptyView != null && defViewState != VIEW_EMPTY) {
		    mEmptyView.setVisibility(GONE);
	    }
        return this;
    }

    /**
     *<p>set error view.</p>
     * @param errorView the error view to add
     * @return This StateLayout object to allow for chaining of calls to set methods
     */
    public StateLayout setErrorView(View errorView) {
        this.mErrorView = errorView;
        initStateView(mErrorView);
        return this;
    }

    /**
     *<p>Specify error view with the given id</p>
     * @param viewResId The id to specify
     * @return This StateLayout object to allow for chaining of calls to set methods
     */
    public StateLayout setErrorViewResId(@IdRes int viewResId) {
        mErrorView = findViewById(viewResId);
	    if (mErrorView != null && defViewState != VIEW_ERROR) {
		    mErrorView.setVisibility(GONE);
	    }
        return this;
    }

    /**
     *<p>Set loading view.</p>
     * @param loadingView the loading view to add
     * @return This StateLayout object to allow for chaining of calls to set methods
     */
    public StateLayout setLoadingView(View loadingView) {
        this.mLoadingView = loadingView;
        initStateView(mLoadingView);
        return this;
    }

    /**
     *<p>Specify loading view with the given id</p>
     * @param viewResId The id to specify
     * @return This StateLayout object to allow for chaining of calls to set methods
     */
    public StateLayout setLoadingViewResId(@IdRes int viewResId) {
        mLoadingView = findViewById(viewResId);
	    if (mLoadingView != null && defViewState != VIEW_LOADING) {
		    mLoadingView.setVisibility(GONE);
	    }
        return this;
    }

    /**
     * first init and call one of
     * {@link #setContentView(View)}
     * {@link #setEmptyView(View)}
     * {@link #setErrorView(View)}
     * {@link #setLoadingView(View)} ,you must call it to init state.
     *
     * @param state
     */
    public void initWithState(@ViewState int state) {
        if (state == defViewState){// default view state
            showLoadingView();
        } else {
            setState(state);
        }
    }

    public void setState(@ViewState int state) {
        if (defViewState == state) {
            return;
        }
        defViewState = state;
        switch (state) {
            case VIEW_CONTENT:
                showContentView();
                break;

            case VIEW_EMPTY:
                showEmptyView();
                break;

            case VIEW_ERROR:
                showErrorView();
                break;

            case VIEW_LOADING:
                showLoadingView();
                break;
        }
    }

    /**
     * Return the current view
     *
     * @return One of {@link #VIEW_CONTENT},{@link #VIEW_EMPTY},{@link #VIEW_ERROR},{@link #VIEW_LOADING}
     */
    @CheckResult
    public int getState(){
        return defViewState;
    }

    private void initStateView(View stateView) {
        if (stateView != null) {
            addView(stateView);
        }
    }

    private void showContentView() {
        showView(mContentView);
        hideView(mEmptyView);
        hideView(mErrorView);
        hideView(mLoadingView);
    }

    private void showEmptyView() {
        showView(mEmptyView);
        hideView(mContentView);
        hideView(mErrorView);
        hideView(mLoadingView);
    }

    private void showErrorView() {
        showView(mErrorView);
        hideView(mEmptyView);
        hideView(mContentView);
        hideView(mLoadingView);
    }

    private void showLoadingView() {
        showView(mLoadingView);
        hideView(mEmptyView);
        hideView(mErrorView);
        hideView(mContentView);
    }


    private void showView(View view) {
        if (view != null) {
            view.setVisibility(VISIBLE);
        }
    }

    private void hideView(View view) {
        if (view != null) {
            view.setVisibility(GONE);
        }
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        private static final String TAG = "SL.LayoutParams";
        @ViewState
        public int slState = VIEW_LOADING;

        public LayoutParams(int width, int height, int gravity, @ViewState int state) {
            super(width, height, gravity);
            slState = state;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(LayoutParams source) {
            super(source);
            slState = source.slState;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.slStateLayout);
            slState = a.getInt(R.styleable.slStateLayout_slState, -1);
            a.recycle();
        }

        @Override
        public String toString() {
            return "LayoutParams{" + super.toString() + ", " +
                    "slState=" + slState +
                    '}';
        }
    }

}
