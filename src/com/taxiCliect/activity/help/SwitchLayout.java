package com.taxiCliect.activity.help;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class SwitchLayout extends ViewGroup {

	private int mCurScreen;
	private static final int SNAP_VELOCITY = 600;// 瞬间速度
	private Scroller scroller;// 滚动控制器
	private VelocityTracker tracker;// 拖动手势的速率跟踪器
	private float mLastMotionX;
	private Context context;
	private OnViewChangeListener onViewChangeListener;// 改变imageView

	public SwitchLayout(Context context) {
		super(context);
		this.context = context;
		init(context);
	}

	public SwitchLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(context);
	}

	public SwitchLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init(context);
	}

	// 初始化控件
	private void init(Context context) {
		mCurScreen = 0;
		scroller = new Scroller(context);
	}

	/**
	 * 必须要调用的方法 目的：当前的view在给自己的子控件指派大小和位置必须要调用的方法
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			int childLeft = 0;
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					int childWidth = childView.getMeasuredWidth();
					// 子控件被填充在父控件中(第二个阶段，第一个阶段为onMeasure，即计算)
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;// 改变左边的起始位置
				}
			}
		}
	}

	/**
	 * 计算控件的宽和高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		// 滚到到哪一屏，通过width来控制
		scrollTo(mCurScreen * width, 0);
	}

	public void setOnViewChangeListener(OnViewChangeListener changeListener) {
		this.onViewChangeListener = changeListener;
	}

	/**
	 * 设置自定义控件中的哪个子控件展示在当前屏幕中
	 * 
	 * @param pos
	 */
	public void snapToScreen(int pos) {
		System.out.println("当前的位置：" + pos);
		if (getScrollX() != (pos * getWidth())) {
			int destina = pos * getWidth() - getScrollX();// 要移动的距离
			// 开始滚动，从起始地开始，后两个参数为滚动的距离。用坐标来表示

			scroller.startScroll(getScrollX(), 0, destina, 0);
			mCurScreen = pos;
			invalidate();// 在主线程中调用这个方法来重绘view，必须在主线程中调用
			// 改变imageView的显示(根据guide_round.xml可以看出，当状态enabled的
			// 的时候，用黑点表示，也就是不是当前选中。)
			System.out.println("ImageView改变监听" + onViewChangeListener);
			if (onViewChangeListener != null) {
				onViewChangeListener.onViewChange(pos);
			}
		}

	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Return the kind of action being performed --
		// one of either ACTION_DOWN, ACTION_MOVE, ACTION_UP, or ACTION_CANCEL.
		int action = event.getAction();
		/**
		 * 屏幕上的每一次触摸都会被onTouchEvent捕获到，可以从event得到其x，y的值。
		 * 特别注意：并且得到的是你当前的触点的x，y坐标的值，也就是说，往左划动的话， 你的x的值是变小的。
		 */
		float x = event.getX();
		System.out.println("onTouchEvent--" + x);
		switch (action) {
		// 按下
		case MotionEvent.ACTION_DOWN:
			if (tracker == null) {
				tracker = VelocityTracker.obtain();
				tracker.addMovement(event);
			}
			if (!scroller.isFinished())
				// 防止scroller滚动到最终的x和y的位置
				scroller.abortAnimation();
			mLastMotionX = x;
			System.out.println("mLastMotionX--" + mLastMotionX);
			break;
		// 移动
		case MotionEvent.ACTION_MOVE:
			int deltalX = (int) (mLastMotionX - x);// 开始位置的触点与当前位置的差值
			if (canMove(deltalX)) {
				if (tracker != null) {
					tracker.addMovement(event);
				}
				mLastMotionX = x;
				// 控件滚动的位置
				scrollBy(deltalX, 0);
			}
			break;
		// 松手
		/**
		 * 往左滑动，因为x是在减小，所以横向速率是负值。 往右滑动，因为x是在增大，所以横向速率是正值。
		 */
		case MotionEvent.ACTION_UP:
			int velocityX = 0;
			if (tracker != null) {
				tracker.addMovement(event);
				tracker.computeCurrentVelocity(1000);// 计算1s滚动的速度
				System.out.println("tracker -- " + tracker);
				velocityX = (int) tracker.getXVelocity();// 得到最终的横向速率
				System.out.println("横向速率--" + velocityX);
				System.out.println("mCurScreen--" + mCurScreen);
			}
			// 不是第一屏，且是往右滑动
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0)
				snapToScreen(mCurScreen - 1);
			// 往左滑动，且不是最后一个
			else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < (getChildCount() - 1)) {
				snapToScreen(mCurScreen + 1);
			}
			// 往左滑动，且是最后一个,这里直接finish掉activity
			else if (velocityX == 0 && mCurScreen == (getChildCount() - 1)) {
				Activity activity = (Activity) context;
				// Intent intent = new Intent();
				// intent.setClass(context,
				// yanbin.switchDemo.OtherActivity.class);
				// context.startActivity(intent);
				activity.finish();
			}
			// 速率不够快是，另一种跳转方式
			else {
				snapToDestination();
			}
			if (tracker != null) {
				tracker.recycle();
				tracker = null;
			}
			break;
		}
		return true;
	}

	// 滑动到下一屏
	private void snapToDestination() {
		int screenWidth = getWidth();
		// 最终滑动的位置超过1\2时，才滚动，否则，destScreen得到将是当前屏的值。
		int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);

	}

	// 可滚动的条件
	private boolean canMove(int deltalX) {
		// getScrollX()，得到触点view在左边的x轴的坐标,它的值是从第一张图到最后一张图算的，
		// 也就是说：getScrollX()得到的值是 将整个图展开，然后相对的那个x坐标。
		if (getScrollX() <= 0 && deltalX < 0) {// 显示的是第一张图，并且是往右划
			return false;
		}
		// 显示的是最后一张图并且是往左划
		if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltalX > 0) {
			return false;
		}
		return true;
	}

}
