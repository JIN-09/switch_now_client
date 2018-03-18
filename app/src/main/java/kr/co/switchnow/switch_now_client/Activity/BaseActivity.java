package kr.co.switchnow.switch_now_client.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.AnimationUtils;

import kr.co.switchnow.switch_now_client.R;

/**
 * Created by ceo on 2017-03-26.
 */

public class BaseActivity extends Activity {


    public Activity thisActivity;

    public BaseActivity(Activity activity){
        this.thisActivity =  activity;
    }


    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        this.overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    public void overridePendingTransitionEnter() {
        thisActivity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }


    public void overridePendingTransitionExit() {
        thisActivity.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public static void slideInFromLeft(Context context, View view) {
        runSimpleAnimation(context, view, R.anim.slide_from_left);
    }

    /**
     * Animates a view so that it slides from its current position, out of view to the left.
     *
     * @param context
     * @param view
     */
    public static void slideOutToLeft(Context context, View view) {
        runSimpleAnimation(context, view, R.anim.slide_to_left);
    }

    /**
     * Animates a view so that it slides in the from the right of it's container.
     *
     * @param context
     * @param view
     */
    public static void slideInFromRight(Context context, View view) {
        runSimpleAnimation(context, view, R.anim.slide_from_right);
    }

    /**
     * Animates a view so that it slides from its current position, out of view to the right.
     *
     * @param context
     * @param view
     */
    public static void slideOutToRight(Context context, View view) {
        runSimpleAnimation(context, view, R.anim.slide_to_right);
    }


    public static void fadeIn(Context context, View view) {
        runSimpleAnimation(context, view, R.anim.fadein);
    }

    public static void fadeOut(Context context, View view) {
        runSimpleAnimation(context, view, R.anim.fadeout);
    }

    /**
     * Runs a simple animation on a View with no extra parameters.
     *
     * @param context
     * @param view
     * @param animationId
     */
    private static void runSimpleAnimation(Context context, View view, int animationId) {
        view.startAnimation(AnimationUtils.loadAnimation(
                context, animationId
        ));
    }


}
