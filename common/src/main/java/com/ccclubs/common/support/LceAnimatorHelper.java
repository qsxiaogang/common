package com.ccclubs.common.support;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import com.ccclubs.common.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * 用于显示 loading - content - error 模式的动画效果
 * Little helper class for animating content, error and loading view
 */
public class LceAnimatorHelper {

  private LceAnimatorHelper() {
  }

  /**
   * Show the loading view. No animations, because sometimes loading things is pretty fast (i.e.
   * retrieve data from memory com.ccclubs.common.cache).
   */
  public static void showLoading(@NonNull View loadingView, @NonNull View contentView,
      @NonNull View errorView) {

    contentView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    loadingView.setVisibility(View.VISIBLE);
  }

  /**
   * Shows the error view instead of the loading view
   */
  public static void showErrorView(@NonNull final View loadingView, @NonNull final View contentView,
      final View errorView) {

    contentView.setVisibility(View.GONE);

    final Resources resources = loadingView.getResources();
    // Not visible yet, so animate the view in

    AnimatorSet set = new AnimatorSet();
    //ObjectAnimator in = ObjectAnimator.ofFloat(errorView, View.ALPHA, 1f);
    ObjectAnimator in = ObjectAnimator.ofFloat(errorView, "alpha", 1f);
    //ObjectAnimator loadingOut = ObjectAnimator.ofFloat(loadingView, View.ALPHA, 0f);
    final ObjectAnimator loadingOut = ObjectAnimator.ofFloat(loadingView, "alpha", 0f);

    set.playTogether(in, loadingOut);
    set.setDuration(resources.getInteger(R.integer.lce_error_view_show_animation_time));

    set.addListener(new AnimatorListenerAdapter() {

      @Override public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        errorView.setVisibility(View.VISIBLE);
      }

      @Override public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        loadingView.setVisibility(View.GONE);
        //loadingView.setAlpha(1f); // For future showLoading calls
      }
    });

    set.start();
  }

  /**
   * Display the content instead of the loadingView
   */
  public static void showContent(@NonNull final View loadingView, @NonNull final View contentView,
      @NonNull final View errorView) {

    if (contentView.getVisibility() == View.VISIBLE) {
      // No Changing needed, because contentView is already visible
      errorView.setVisibility(View.GONE);
      loadingView.setVisibility(View.GONE);
    } else {

      errorView.setVisibility(View.GONE);

      final Resources resources = loadingView.getResources();
      final int translateInPixels =
          resources.getDimensionPixelSize(R.dimen.lce_content_view_animation_translate_y);
      // Not visible yet, so animate the view in
      AnimatorSet set = new AnimatorSet();
      //ObjectAnimator contentFadeIn = ObjectAnimator.ofFloat(contentView, View.ALPHA, 0f, 1f);
      ObjectAnimator contentFadeIn = ObjectAnimator.ofFloat(contentView, "alpha", 0f, 1f);
      //ObjectAnimator contentTranslateIn =
      //    ObjectAnimator.ofFloat(contentView, View.TRANSLATION_Y, translateInPixels, 0);
      ObjectAnimator contentTranslateIn =
          ObjectAnimator.ofFloat(contentView, "y", translateInPixels, 0);

      //ObjectAnimator loadingFadeOut = ObjectAnimator.ofFloat(loadingView, View.ALPHA, 1f, 0f);
      ObjectAnimator loadingFadeOut = ObjectAnimator.ofFloat(loadingView, "alpha", 1f, 0f);
      //ObjectAnimator loadingTranslateOut =
      //    ObjectAnimator.ofFloat(loadingView, View.TRANSLATION_Y, 0, -translateInPixels);
      ObjectAnimator loadingTranslateOut =
          ObjectAnimator.ofFloat(loadingView, "y", 0, -translateInPixels);

      set.playTogether(contentFadeIn, contentTranslateIn, loadingFadeOut, loadingTranslateOut);
      set.setDuration(resources.getInteger(R.integer.lce_content_view_show_animation_time));

      set.addListener(new AnimatorListenerAdapter() {

        @Override public void onAnimationStart(Animator animation) {
          //contentView.setTranslationY(0);
          //loadingView.setTranslationY(0);
          contentView.setVisibility(View.VISIBLE);
        }

        @Override public void onAnimationEnd(Animator animation) {
          loadingView.setVisibility(View.GONE);
          //loadingView.setAlpha(1f); // For future showLoading calls
          //contentView.setTranslationY(0);
          //loadingView.setTranslationY(0);
        }
      });

      set.start();
    }
  }
}
