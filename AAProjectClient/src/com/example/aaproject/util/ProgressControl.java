package com.example.aaproject.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

public class ProgressControl {
	private Context mContext;
	private View mDefaultFormView;
	private View mProgressStatusView;
	private TextView mProgressStatusMessageView;
	
	public ProgressControl(Context context, View form, View status, TextView message){
		mContext = context;
		mDefaultFormView = form;
		mProgressStatusView = status;
		mProgressStatusMessageView = message;
	}

	public void setMessage(String message){
		mProgressStatusMessageView.setText(message);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = mContext.getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mProgressStatusView.setVisibility(View.VISIBLE);
			mProgressStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mDefaultFormView.setVisibility(View.VISIBLE);
			mDefaultFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mDefaultFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mDefaultFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public void setMessageById(int id) {
		mProgressStatusMessageView.setText(mContext.getResources().getString(id));
	}

}
