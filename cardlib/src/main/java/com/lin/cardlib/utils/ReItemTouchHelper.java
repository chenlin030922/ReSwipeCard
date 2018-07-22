

package com.lin.cardlib.utils;


import com.lin.cardlib.CardTouchHelperCallback;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;


public class ReItemTouchHelper extends RecyclerView.ItemDecoration
        implements RecyclerView.OnChildAttachStateChangeListener {


    public static final int UP = 1;


    public static final int DOWN = 1 << 1;


    public static final int LEFT = 1 << 2;


    public static final int RIGHT = 1 << 3;

    // If you change these relative direction values, update Callback#convertToAbsoluteDirection,
    // Callback#convertToRelativeDirection.

    public static final int START = LEFT << 2;


    public static final int END = RIGHT << 2;


    public static final int ACTION_STATE_IDLE = 0;


    public static final int ACTION_STATE_SWIPE = 1;


    public static final int ACTION_STATE_DRAG = 2;


    public static final int ANIMATION_TYPE_SWIPE_SUCCESS = 1 << 1;


    public static final int ANIMATION_TYPE_SWIPE_CANCEL = 1 << 2;


    public static final int ANIMATION_TYPE_DRAG = 1 << 3;

    static final String TAG = "ItemTouchHelper";

    static final boolean DEBUG = false;

    static final int ACTIVE_POINTER_ID_NONE = -1;

    static final int DIRECTION_FLAG_COUNT = 8;

    private static final int ACTION_MODE_IDLE_MASK = (1 << DIRECTION_FLAG_COUNT) - 1;

    static final int ACTION_MODE_SWIPE_MASK = ACTION_MODE_IDLE_MASK << DIRECTION_FLAG_COUNT;

    static final int ACTION_MODE_DRAG_MASK = ACTION_MODE_SWIPE_MASK << DIRECTION_FLAG_COUNT;


    private static final int PIXELS_PER_SECOND = 1000;


    final List<View> mPendingCleanup = new ArrayList<View>();


    private final float[] mTmpPosition = new float[2];


    ViewHolder mSelected = null;


    float mInitialTouchX;

    float mInitialTouchY;


    float mSwipeEscapeVelocity;


    float mMaxSwipeVelocity;


    float mDx;

    float mDy;


    float mSelectedStartX;

    float mSelectedStartY;


    int mActivePointerId = ACTIVE_POINTER_ID_NONE;


    Callback mCallback;


    int mActionState = ACTION_STATE_IDLE;


    int mSelectedFlags;
    boolean isManually;

    List<RecoverAnimation> mRecoverAnimations = new ArrayList<RecoverAnimation>();

    private int mSlop;

    RecyclerView mRecyclerView;

    final Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSelected != null && scrollIfNecessary()) {
                if (mSelected != null) { //it might be lost during scrolling
                    moveIfNecessary(mSelected);
                }
                mRecyclerView.removeCallbacks(mScrollRunnable);
                ViewCompat.postOnAnimation(mRecyclerView, this);
            }
        }
    };


    VelocityTracker mVelocityTracker;

    //re-used list for selecting a swap target
    private List<ViewHolder> mSwapTargets;

    //re used for for sorting swap targets
    private List<Integer> mDistances;


    private RecyclerView.ChildDrawingOrderCallback mChildDrawingOrderCallback = null;


    View mOverdrawChild = null;


    int mOverdrawChildPosition = -1;


    GestureDetectorCompat mGestureDetector;

    private final OnItemTouchListener mOnItemTouchListener
            = new OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
            if (DEBUG) {
                Log.d(TAG, "intercept: x:" + event.getX() + ",y:" + event.getY() + ", " + event);
            }
            final int action = MotionEventCompat.getActionMasked(event);
            if (action == MotionEvent.ACTION_DOWN) {
                mActivePointerId = event.getPointerId(0);
                mInitialTouchX = event.getX();
                mInitialTouchY = event.getY();
                obtainVelocityTracker();
                if (mSelected == null) {
                    final RecoverAnimation animation = findAnimation(event);
                    if (animation != null) {
                        mInitialTouchX -= animation.mX;
                        mInitialTouchY -= animation.mY;
                        endRecoverAnimation(animation.mViewHolder, true);
                        if (mPendingCleanup.remove(animation.mViewHolder.itemView)) {
                            mCallback.clearView(mRecyclerView, animation.mViewHolder);
                        }
                        select(animation.mViewHolder, animation.mActionState);
                        updateDxDy(event, mSelectedFlags, 0);
                    }
                }
            } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                mActivePointerId = ACTIVE_POINTER_ID_NONE;
                select(null, ACTION_STATE_IDLE);
            } else if (mActivePointerId != ACTIVE_POINTER_ID_NONE) {
                // in a non scroll orientation, if distance change is above threshold, we
                // can select the item
                final int index = event.findPointerIndex(mActivePointerId);
                if (DEBUG) {
                    Log.d(TAG, "pointer index " + index);
                }
                if (index >= 0) {
                    checkSelectForSwipe(action, event, index);
                }
            }
            if (mVelocityTracker != null) {
                mVelocityTracker.addMovement(event);
            }
            return mSelected != null;
        }

        @Override
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
            if (DEBUG) {
                Log.d(TAG,
                        "on touch: x:" + mInitialTouchX + ",y:" + mInitialTouchY + ", :" + event);
            }
            if (mVelocityTracker != null) {
                mVelocityTracker.addMovement(event);
            }
            if (mActivePointerId == ACTIVE_POINTER_ID_NONE) {
                return;
            }
            final int action = MotionEventCompat.getActionMasked(event);
            final int activePointerIndex = event.findPointerIndex(mActivePointerId);
            if (activePointerIndex >= 0) {
                checkSelectForSwipe(action, event, activePointerIndex);
            }
            ViewHolder viewHolder = mSelected;
            if (viewHolder == null) {
                return;
            }
            switch (action) {
                case MotionEvent.ACTION_MOVE: {
                    // Find the index of the active pointer and fetch its position
                    if (activePointerIndex >= 0) {
                        if (mCallback.enableHardWare() && viewHolder.itemView.getLayerType() != View.LAYER_TYPE_HARDWARE) {
                            viewHolder.itemView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                        }
                        updateDxDy(event, mSelectedFlags, activePointerIndex);
                        moveIfNecessary(viewHolder);
                        mRecyclerView.removeCallbacks(mScrollRunnable);
                        mScrollRunnable.run();
                        mRecyclerView.invalidate();
                    }
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                    if (mVelocityTracker != null) {
                        mVelocityTracker.clear();
                    }
                    // fall through
                case MotionEvent.ACTION_UP:
                    select(null, ACTION_STATE_IDLE);
                    mActivePointerId = ACTIVE_POINTER_ID_NONE;
                    break;
                case MotionEvent.ACTION_POINTER_UP: {
                    final int pointerIndex = MotionEventCompat.getActionIndex(event);
                    final int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mActivePointerId = event.getPointerId(newPointerIndex);
                        updateDxDy(event, mSelectedFlags, pointerIndex);
                    }
                    break;
                }
            }
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            if (!disallowIntercept) {
                return;
            }
            select(null, ACTION_STATE_IDLE);
        }
    };


    private Rect mTmpRect;


    private long mDragScrollStartTimeInMs;


    public ReItemTouchHelper(Callback callback) {
        mCallback = callback;
        //addCode
        attachToRecyclerView(((CardTouchHelperCallback) callback).getRecyclerView());
    }

    private static boolean hitTest(View child, float x, float y, float left, float top) {
        return x >= left &&
                x <= left + child.getWidth() &&
                y >= top &&
                y <= top + child.getHeight();
    }


    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        if (mRecyclerView == recyclerView) {
            return; // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            final Resources resources = recyclerView.getResources();
            mSwipeEscapeVelocity = resources
                    .getDimension(android.support.v7.recyclerview.R.dimen.item_touch_helper_swipe_escape_velocity);
            mMaxSwipeVelocity = resources
                    .getDimension(android.support.v7.recyclerview.R.dimen.item_touch_helper_swipe_escape_max_velocity);
            setupCallbacks();
        }
    }

    private void setupCallbacks() {
        ViewConfiguration vc = ViewConfiguration.get(mRecyclerView.getContext());
        mSlop = vc.getScaledTouchSlop();
        mRecyclerView.addItemDecoration(this);
        mRecyclerView.addOnItemTouchListener(mOnItemTouchListener);
        mRecyclerView.addOnChildAttachStateChangeListener(this);
        initGestureDetector();
    }

    private void destroyCallbacks() {
        mRecyclerView.removeItemDecoration(this);
        mRecyclerView.removeOnItemTouchListener(mOnItemTouchListener);
        mRecyclerView.removeOnChildAttachStateChangeListener(this);
        // clean all attached
        final int recoverAnimSize = mRecoverAnimations.size();
        for (int i = recoverAnimSize - 1; i >= 0; i--) {
            final RecoverAnimation recoverAnimation = mRecoverAnimations.get(0);
            mCallback.clearView(mRecyclerView, recoverAnimation.mViewHolder);
        }
        mRecoverAnimations.clear();
        mOverdrawChild = null;
        mOverdrawChildPosition = -1;
        releaseVelocityTracker();
    }

    private void initGestureDetector() {
        if (mGestureDetector != null) {
            return;
        }
        mGestureDetector = new GestureDetectorCompat(mRecyclerView.getContext(),
                new ItemTouchHelperGestureListener());
    }

    private void getSelectedDxDy(float[] outPosition) {
        float dx = isManually ? 0 : mDx;
        float dy = isManually ? 0 : mDy;
        if ((mSelectedFlags & (LEFT | RIGHT)) != 0) {
            outPosition[0] = mSelectedStartX + dx - mSelected.itemView.getLeft();
        } else {
            outPosition[0] = ViewCompat.getTranslationX(mSelected.itemView);
        }
        if ((mSelectedFlags & (UP | DOWN)) != 0) {
            outPosition[1] = mSelectedStartY + dy - mSelected.itemView.getTop();
        } else {
            outPosition[1] = ViewCompat.getTranslationY(mSelected.itemView);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        float dx = 0, dy = 0;
        if (mSelected != null) {
            getSelectedDxDy(mTmpPosition);
            dx = mTmpPosition[0];
            dy = mTmpPosition[1];
        }
        mCallback.onDrawOver(c, parent, mSelected,
                mRecoverAnimations, mActionState, dx, dy);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // we don't know if RV changed something so we should invalidate this index.
        mOverdrawChildPosition = -1;
        float dx = 0, dy = 0;
        if (mSelected != null) {
            getSelectedDxDy(mTmpPosition);
            dx = mTmpPosition[0];
            dy = mTmpPosition[1];
        }
        mCallback.onDraw(c, parent, mSelected,
                mRecoverAnimations, mActionState, dx, dy);
    }


    void select(ViewHolder selected, int actionState) {
        if (selected == mSelected && actionState == mActionState) {
            return;
        }
        mDragScrollStartTimeInMs = Long.MIN_VALUE;
        final int prevActionState = mActionState;
        // prevent duplicate animations
        endRecoverAnimation(selected, true);
        mActionState = actionState;
        if (actionState == ACTION_STATE_DRAG) {
            // we remove after animation is complete. this means we only elevate the last drag
            // child but that should perform good enough as it is very hard to start dragging a
            // new child before the previous one settles.
            mOverdrawChild = selected.itemView;
            addChildDrawingOrderCallback();
        }
        int actionStateMask = (1 << (DIRECTION_FLAG_COUNT + DIRECTION_FLAG_COUNT * actionState))
                - 1;
        boolean preventLayout = false;

        if (mSelected != null) {
            final ViewHolder prevSelected = mSelected;
            if (prevSelected.itemView.getParent() != null) {
                final int swipeDir = prevActionState == ACTION_STATE_DRAG ? 0
                        : swipeIfNecessary(prevSelected);
                releaseVelocityTracker();
                // find where we should animate to
                final float targetTranslateX, targetTranslateY;
                int animationType;
                switch (swipeDir) {
                    case LEFT:
                    case RIGHT:
                    case START:
                    case END:
                        targetTranslateY = 0;
                        targetTranslateX = Math.signum(mDx) * mRecyclerView.getWidth();
                        break;
                    case UP:
                    case DOWN:
                        targetTranslateX = 0;
                        targetTranslateY = Math.signum(mDy) * mRecyclerView.getHeight();
                        break;
                    default:
                        targetTranslateX = 0;
                        targetTranslateY = 0;
                }
                if (prevActionState == ACTION_STATE_DRAG) {
                    animationType = ANIMATION_TYPE_DRAG;
                } else if (swipeDir > 0) {
                    animationType = ANIMATION_TYPE_SWIPE_SUCCESS;
                } else {
                    animationType = ANIMATION_TYPE_SWIPE_CANCEL;
                }
                getSelectedDxDy(mTmpPosition);
                final float currentTranslateX = mTmpPosition[0];
                final float currentTranslateY = mTmpPosition[1];
                final RecoverAnimation rv = new RecoverAnimation(prevSelected, animationType,
                        prevActionState, currentTranslateX, currentTranslateY,
                        targetTranslateX, targetTranslateY) {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (this.mOverridden) {
                            return;
                        }
                        if (swipeDir <= 0) {
                            // this is a drag or failed swipe. recover immediately
                            mCallback.clearView(mRecyclerView, prevSelected);
                            // full cleanup will happen on onDrawOver
                        } else {
                            // wait until remove animation is complete.
                            mPendingCleanup.add(prevSelected.itemView);
                            mIsPendingCleanup = true;
                            if (swipeDir > 0) {
                                // Animation might be ended by other animators during a layout.
                                // We defer callback to avoid editing adapter during a layout.
                                postDispatchSwipe(this, swipeDir);
                            }
                        }
                        // removed from the list after it is drawn for the last time
                        if (mOverdrawChild == prevSelected.itemView) {
                            removeChildDrawingOrderCallbackIfNecessary(prevSelected.itemView);
                        }
                    }
                };
                if (mCallback.getDefaultSwipeAnimationDuration() < 0) {
                    final long duration = mCallback.getAnimationDuration(mRecyclerView, animationType,
                            targetTranslateX - currentTranslateX, targetTranslateY - currentTranslateY);
                    rv.setDuration(duration);
                } else {
                    rv.setDuration(mCallback.getDefaultSwipeAnimationDuration());
                }
                mRecoverAnimations.add(rv);
                rv.start();
                preventLayout = true;
            } else {
                removeChildDrawingOrderCallbackIfNecessary(prevSelected.itemView);
                mCallback.clearView(mRecyclerView, prevSelected);
            }
            mSelected = null;
        }
        if (selected != null) {
            mSelectedFlags =
                    (mCallback.getAbsoluteMovementFlags(mRecyclerView, selected) & actionStateMask)
                            >> (mActionState * DIRECTION_FLAG_COUNT);
            mSelectedStartX = selected.itemView.getLeft();
            mSelectedStartY = selected.itemView.getTop();
            mSelected = selected;

            if (actionState == ACTION_STATE_DRAG) {
                mSelected.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
        }
        final ViewParent rvParent = mRecyclerView.getParent();
        if (rvParent != null) {
            rvParent.requestDisallowInterceptTouchEvent(mSelected != null);
        }
        if (!preventLayout) {
            mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
        }
        mCallback.onSelectedChanged(mSelected, mActionState);
        mRecyclerView.invalidate();
    }

    void postDispatchSwipe(final RecoverAnimation anim, final int swipeDir) {
        // wait until animations are complete.
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (mRecyclerView != null && mRecyclerView.isAttachedToWindow() &&
                        !anim.mOverridden &&
                        anim.mViewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    final RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
                    // if animator is running or we have other active recover animations, we try
                    // not to call onSwipedOut because DefaultItemAnimator is not good at merging
                    // animations. Instead, we wait and batch.
                    if ((animator == null || !animator.isRunning(null))
                            && !hasRunningRecoverAnim()) {
                        mCallback.onSwiped(anim.mViewHolder, swipeDir);
                    } else {
                        mRecyclerView.post(this);
                    }
                }
            }
        });
    }

    boolean hasRunningRecoverAnim() {
        final int size = mRecoverAnimations.size();
        for (int i = 0; i < size; i++) {
            if (!mRecoverAnimations.get(i).mEnded) {
                return true;
            }
        }
        return false;
    }

    public void swipeManually(int direction) {
        //Check to avoid error with moving cards
        if (mCallback.getSwitchListener()){
            mCallback.setSwitchListener(false);
            //find the first card
            ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(0);
            if (!hasRunningRecoverAnim()) {
                //reset to zero,make a fake mDx,mDy
                int addValue = 50;
                isManually = true;
                switch (direction) {
                    case DOWN:
                        mDx = 0;
                        mDy = mRecyclerView.getHeight() * mCallback
                                .getSwipeThreshold(viewHolder) + addValue;
                        break;
                    case RIGHT:
                        mDy = 0;
                        mDx = (mRecyclerView.getWidth() * mCallback
                                .getSwipeThreshold(viewHolder) + addValue);
                        break;
                    case LEFT:
                        mDy = 0;
                        mDx = -(mRecyclerView.getWidth() * mCallback
                                .getSwipeThreshold(viewHolder) + addValue);
                        break;
                    case UP:
                        mDx = 0;
                        mDy = -(mRecyclerView.getHeight() * mCallback
                                .getSwipeThreshold(viewHolder) + addValue);
                        break;

                }
                mSelected = viewHolder;
                select(null, ACTION_STATE_IDLE);
                mActivePointerId = ACTIVE_POINTER_ID_NONE;
            }
        }

    }


    boolean scrollIfNecessary() {
        if (mSelected == null) {
            mDragScrollStartTimeInMs = Long.MIN_VALUE;
            return false;
        }
        final long now = System.currentTimeMillis();
        final long scrollDuration = mDragScrollStartTimeInMs
                == Long.MIN_VALUE ? 0 : now - mDragScrollStartTimeInMs;
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        if (mTmpRect == null) {
            mTmpRect = new Rect();
        }
        int scrollX = 0;
        int scrollY = 0;
        lm.calculateItemDecorationsForChild(mSelected.itemView, mTmpRect);
        if (lm.canScrollHorizontally()) {
            int curX = (int) (mSelectedStartX + mDx);
            final int leftDiff = curX - mTmpRect.left - mRecyclerView.getPaddingLeft();
            if (mDx < 0 && leftDiff < 0) {
                scrollX = leftDiff;
            } else if (mDx > 0) {
                final int rightDiff =
                        curX + mSelected.itemView.getWidth() + mTmpRect.right
                                - (mRecyclerView.getWidth() - mRecyclerView.getPaddingRight());
                if (rightDiff > 0) {
                    scrollX = rightDiff;
                }
            }
        }
        if (lm.canScrollVertically()) {
            int curY = (int) (mSelectedStartY + mDy);
            final int topDiff = curY - mTmpRect.top - mRecyclerView.getPaddingTop();
            if (mDy < 0 && topDiff < 0) {
                scrollY = topDiff;
            } else if (mDy > 0) {
                final int bottomDiff = curY + mSelected.itemView.getHeight() + mTmpRect.bottom -
                        (mRecyclerView.getHeight() - mRecyclerView.getPaddingBottom());
                if (bottomDiff > 0) {
                    scrollY = bottomDiff;
                }
            }
        }
        if (scrollX != 0) {
            scrollX = mCallback.interpolateOutOfBoundsScroll(mRecyclerView,
                    mSelected.itemView.getWidth(), scrollX,
                    mRecyclerView.getWidth(), scrollDuration);
        }
        if (scrollY != 0) {
            scrollY = mCallback.interpolateOutOfBoundsScroll(mRecyclerView,
                    mSelected.itemView.getHeight(), scrollY,
                    mRecyclerView.getHeight(), scrollDuration);
        }
        if (scrollX != 0 || scrollY != 0) {
            if (mDragScrollStartTimeInMs == Long.MIN_VALUE) {
                mDragScrollStartTimeInMs = now;
            }
            mRecyclerView.scrollBy(scrollX, scrollY);
            return true;
        }
        mDragScrollStartTimeInMs = Long.MIN_VALUE;
        return false;
    }

    private List<ViewHolder> findSwapTargets(ViewHolder viewHolder) {
        if (mSwapTargets == null) {
            mSwapTargets = new ArrayList<ViewHolder>();
            mDistances = new ArrayList<Integer>();
        } else {
            mSwapTargets.clear();
            mDistances.clear();
        }
        final int margin = mCallback.getBoundingBoxMargin();
        final int left = Math.round(mSelectedStartX + mDx) - margin;
        final int top = Math.round(mSelectedStartY + mDy) - margin;
        final int right = left + viewHolder.itemView.getWidth() + 2 * margin;
        final int bottom = top + viewHolder.itemView.getHeight() + 2 * margin;
        final int centerX = (left + right) / 2;
        final int centerY = (top + bottom) / 2;
        final RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        final int childCount = lm.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View other = lm.getChildAt(i);
            if (other == viewHolder.itemView) {
                continue;//myself!
            }
            if (other.getBottom() < top || other.getTop() > bottom
                    || other.getRight() < left || other.getLeft() > right) {
                continue;
            }
            final ViewHolder otherVh = mRecyclerView.getChildViewHolder(other);
            if (mCallback.canDropOver(mRecyclerView, mSelected, otherVh)) {
                // find the index to add
                final int dx = Math.abs(centerX - (other.getLeft() + other.getRight()) / 2);
                final int dy = Math.abs(centerY - (other.getTop() + other.getBottom()) / 2);
                final int dist = dx * dx + dy * dy;

                int pos = 0;
                final int cnt = mSwapTargets.size();
                for (int j = 0; j < cnt; j++) {
                    if (dist > mDistances.get(j)) {
                        pos++;
                    } else {
                        break;
                    }
                }
                mSwapTargets.add(pos, otherVh);
                mDistances.add(pos, dist);
            }
        }
        return mSwapTargets;
    }


    void moveIfNecessary(ViewHolder viewHolder) {
        if (mRecyclerView.isLayoutRequested()) {
            return;
        }
        if (mActionState != ACTION_STATE_DRAG) {
            return;
        }

        final float threshold = mCallback.getMoveThreshold(viewHolder);
        final int x = (int) (mSelectedStartX + mDx);
        final int y = (int) (mSelectedStartY + mDy);
        if (Math.abs(y - viewHolder.itemView.getTop()) < viewHolder.itemView.getHeight() * threshold
                && Math.abs(x - viewHolder.itemView.getLeft())
                < viewHolder.itemView.getWidth() * threshold) {
            return;
        }
        List<ViewHolder> swapTargets = findSwapTargets(viewHolder);
        if (swapTargets.size() == 0) {
            return;
        }
        // may swap.
        ViewHolder target = mCallback.chooseDropTarget(viewHolder, swapTargets, x, y);
        if (target == null) {
            mSwapTargets.clear();
            mDistances.clear();
            return;
        }
        final int toPosition = target.getAdapterPosition();
        final int fromPosition = viewHolder.getAdapterPosition();
        if (mCallback.onMove(mRecyclerView, viewHolder, target)) {
            // keep target visible
            mCallback.onMoved(mRecyclerView, viewHolder, fromPosition,
                    target, toPosition, x, y);
        }
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {
    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {
        removeChildDrawingOrderCallbackIfNecessary(view);
        final ViewHolder holder = mRecyclerView.getChildViewHolder(view);
        if (holder == null) {
            return;
        }
        if (mSelected != null && holder == mSelected) {
            select(null, ACTION_STATE_IDLE);
        } else {
            endRecoverAnimation(holder, false); // this may push it into pending cleanup list.
            if (mPendingCleanup.remove(holder.itemView)) {
                mCallback.clearView(mRecyclerView, holder);
            }
        }
    }


    int endRecoverAnimation(ViewHolder viewHolder, boolean override) {
        final int recoverAnimSize = mRecoverAnimations.size();
        for (int i = recoverAnimSize - 1; i >= 0; i--) {
            final RecoverAnimation anim = mRecoverAnimations.get(i);
            if (anim.mViewHolder == viewHolder) {
                anim.mOverridden |= override;
                if (!anim.mEnded) {
                    anim.cancel();
                }
                mRecoverAnimations.remove(i);
                return anim.mAnimationType;
            }
        }
        return 0;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.setEmpty();
    }

    void obtainVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
        }
        mVelocityTracker = VelocityTracker.obtain();
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private ViewHolder findSwipedView(MotionEvent motionEvent) {
        final RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        if (mActivePointerId == ACTIVE_POINTER_ID_NONE) {
            return null;
        }
        final int pointerIndex = motionEvent.findPointerIndex(mActivePointerId);
        final float dx = motionEvent.getX(pointerIndex) - mInitialTouchX;
        final float dy = motionEvent.getY(pointerIndex) - mInitialTouchY;
        final float absDx = Math.abs(dx);
        final float absDy = Math.abs(dy);

        if (absDx < mSlop && absDy < mSlop) {
            return null;
        }
        if (absDx > absDy && lm.canScrollHorizontally()) {
            return null;
        } else if (absDy > absDx && lm.canScrollVertically()) {
            return null;
        }
        View child = findChildView(motionEvent);
        if (child == null) {
            return null;
        }
        return mRecyclerView.getChildViewHolder(child);
    }


    boolean checkSelectForSwipe(int action, MotionEvent motionEvent, int pointerIndex) {
        if (mSelected != null || action != MotionEvent.ACTION_MOVE
                || mActionState == ACTION_STATE_DRAG || !mCallback.isItemViewSwipeEnabled()) {
            return false;
        }
        if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) {
            return false;
        }
        final ViewHolder vh = findSwipedView(motionEvent);
        if (vh == null) {
            return false;
        }
        final int movementFlags = mCallback.getAbsoluteMovementFlags(mRecyclerView, vh);

        final int swipeFlags = (movementFlags & ACTION_MODE_SWIPE_MASK)
                >> (DIRECTION_FLAG_COUNT * ACTION_STATE_SWIPE);

        if (swipeFlags == 0) {
            return false;
        }

        // mDx and mDy are only set in allowed directions. We use custom x/y here instead of
        // updateDxDy to avoid swiping if user moves more in the other direction
        final float x = motionEvent.getX(pointerIndex);
        final float y = motionEvent.getY(pointerIndex);

        // Calculate the distance moved
        final float dx = x - mInitialTouchX;
        final float dy = y - mInitialTouchY;
        // swipe target is chose w/o applying flags so it does not really check if swiping in that
        // direction is allowed. This why here, we use mDx mDy to check slope value again.
        final float absDx = Math.abs(dx);
        final float absDy = Math.abs(dy);

        if (absDx < mSlop && absDy < mSlop) {
            return false;
        }
        if (absDx > absDy) {
            if (dx < 0 && (swipeFlags & LEFT) == 0) {
                return false;
            }
            if (dx > 0 && (swipeFlags & RIGHT) == 0) {
                return false;
            }
        } else {
            if (dy < 0 && (swipeFlags & UP) == 0) {
                return false;
            }
            if (dy > 0 && (swipeFlags & DOWN) == 0) {
                return false;
            }
        }
        mDx = mDy = 0f;
        mActivePointerId = motionEvent.getPointerId(0);
        select(vh, ACTION_STATE_SWIPE);
        return true;
    }

    View findChildView(MotionEvent event) {
        // first check elevated views, if none, then call RV
        final float x = event.getX();
        final float y = event.getY();
        if (mSelected != null) {
            final View selectedView = mSelected.itemView;
            if (hitTest(selectedView, x, y, mSelectedStartX + mDx, mSelectedStartY + mDy)) {
                return selectedView;
            }
        }
        for (int i = mRecoverAnimations.size() - 1; i >= 0; i--) {
            final RecoverAnimation anim = mRecoverAnimations.get(i);
            final View view = anim.mViewHolder.itemView;
            if (hitTest(view, x, y, anim.mX, anim.mY)) {
                return view;
            }
        }
        return mRecyclerView.findChildViewUnder(x, y);
    }


    public void startDrag(ViewHolder viewHolder) {
        if (!mCallback.hasDragFlag(mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start drag has been called but swiping is not enabled");
            return;
        }
        if (viewHolder.itemView.getParent() != mRecyclerView) {
            Log.e(TAG, "Start drag has been called with a view holder which is not a child of "
                    + "the RecyclerView which is controlled by this ItemTouchHelper.");
            return;
        }
        obtainVelocityTracker();
        mDx = mDy = 0f;
        select(viewHolder, ACTION_STATE_DRAG);
    }


    public void startSwipe(ViewHolder viewHolder) {
        if (!mCallback.hasSwipeFlag(mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start swipe has been called but dragging is not enabled");
            return;
        }
        if (viewHolder.itemView.getParent() != mRecyclerView) {
            Log.e(TAG, "Start swipe has been called with a view holder which is not a child of "
                    + "the RecyclerView controlled by this ItemTouchHelper.");
            return;
        }
        obtainVelocityTracker();
        mDx = mDy = 0f;
        select(viewHolder, ACTION_STATE_SWIPE);
    }

    RecoverAnimation findAnimation(MotionEvent event) {
        if (mRecoverAnimations.isEmpty()) {
            return null;
        }
        View target = findChildView(event);
        for (int i = mRecoverAnimations.size() - 1; i >= 0; i--) {
            final RecoverAnimation anim = mRecoverAnimations.get(i);
            if (anim.mViewHolder.itemView == target) {
                return anim;
            }
        }
        return null;
    }

    void updateDxDy(MotionEvent ev, int directionFlags, int pointerIndex) {
        final float x = ev.getX(pointerIndex);
        final float y = ev.getY(pointerIndex);

        // Calculate the distance moved
        mDx = x - mInitialTouchX;
        mDy = y - mInitialTouchY;
        if ((directionFlags & LEFT) == 0) {
            mDx = Math.max(0, mDx);
        }
        if ((directionFlags & RIGHT) == 0) {
            mDx = Math.min(0, mDx);
        }
        if ((directionFlags & UP) == 0) {
            mDy = Math.max(0, mDy);
        }
        if ((directionFlags & DOWN) == 0) {
            mDy = Math.min(0, mDy);
        }
    }

    private int swipeIfNecessary(ViewHolder viewHolder) {
        if (mActionState == ACTION_STATE_DRAG) {
            return 0;
        }
        final int originalMovementFlags = mCallback.getMovementFlags(mRecyclerView, viewHolder);
        final int absoluteMovementFlags = mCallback.convertToAbsoluteDirection(
                originalMovementFlags,
                ViewCompat.getLayoutDirection(mRecyclerView));
        final int flags = (absoluteMovementFlags
                & ACTION_MODE_SWIPE_MASK) >> (ACTION_STATE_SWIPE * DIRECTION_FLAG_COUNT);
        if (flags == 0) {
            return 0;
        }
        final int originalFlags = (originalMovementFlags
                & ACTION_MODE_SWIPE_MASK) >> (ACTION_STATE_SWIPE * DIRECTION_FLAG_COUNT);
        int swipeDir;
        float value = mRecyclerView.getWidth() * mCallback
                .getSwipeThreshold(viewHolder);
        //addCode
        float powDx = Math.abs(mDx) * Math.abs(mDx);
        float powDy = Math.abs(mDy) * Math.abs(mDy);
        double distance = Math.sqrt(powDx + powDy);
        if (distance < value) {
            //do not swipe out
            return 0;
        }
        boolean swipeVertical = powDx < powDy;
        if (swipeVertical) {
            //vertical
            //addCode
            if ((swipeDir = checkVerticalSwipe(viewHolder, flags)) > 0) {
                return swipeDir;
            }
//            if ((swipeDir = checkHorizontalSwipe(viewHolder, flags)) > 0) {
//                // if swipe dir is not in original flags, it should be the relative direction
//                if ((originalFlags & swipeDir) == 0) {
//                    // convert to relative
//                    return Callback.convertToRelativeDirection(swipeDir,
//                            ViewCompat.getLayoutDirection(mRecyclerView));
//                }
//                return swipeDir;
//            }
        } else {
            //horizontal
            if ((swipeDir = checkHorizontalSwipe(viewHolder, flags)) > 0) {
                // if swipe dir is not in original flags, it should be the relative direction
                if ((originalFlags & swipeDir) == 0) {
                    // convert to relative
                    return Callback.convertToRelativeDirection(swipeDir,
                            ViewCompat.getLayoutDirection(mRecyclerView));
                }
                return swipeDir;
            }
        }
        return 0;
    }

    private int checkHorizontalSwipe(ViewHolder viewHolder, int flags) {
        boolean isLeft = mDx < 0;
        if (mCallback.isForbidLeftSwipeOut() && isLeft) {
            return 0;
        }
        if (mCallback.isForbidRightSwipeOut() && !isLeft) {
            return 0;
        }
        if ((flags & (LEFT | RIGHT)) != 0) {
            final int dirFlag = mDx > 0 ? RIGHT : LEFT;
            if (mVelocityTracker != null && mActivePointerId > -1) {
                mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND,
                        mCallback.getSwipeVelocityThreshold(mMaxSwipeVelocity));
                final float xVelocity = VelocityTrackerCompat
                        .getXVelocity(mVelocityTracker, mActivePointerId);
                final float yVelocity = VelocityTrackerCompat
                        .getYVelocity(mVelocityTracker, mActivePointerId);
                final int velDirFlag = xVelocity > 0f ? RIGHT : LEFT;
                final float absXVelocity = Math.abs(xVelocity);
                if ((velDirFlag & flags) != 0 && dirFlag == velDirFlag &&
                        absXVelocity >= mCallback.getSwipeEscapeVelocity(mSwipeEscapeVelocity) &&
                        absXVelocity > Math.abs(yVelocity)) {
                    return velDirFlag;
                }
            }

            final float threshold = mRecyclerView.getWidth() * mCallback
                    .getSwipeThreshold(viewHolder);

            if ((flags & dirFlag) != 0 && Math.abs(mDx) > threshold) {
                return dirFlag;
            }
        }
        return 0;
    }

    private int checkVerticalSwipe(ViewHolder viewHolder, int flags) {
        boolean isUp = mDy < 0;
        if (mCallback.isForbidUpSwipeOut() && isUp) {
            return 0;
        }
        if (mCallback.isForbidDownSwipeOut() && (!isUp)) {
            return 0;
        }
        if ((flags & (UP | DOWN)) != 0) {
            final int dirFlag = mDy > 0 ? DOWN : UP;
            if (mVelocityTracker != null && mActivePointerId > -1) {
                mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND,
                        mCallback.getSwipeVelocityThreshold(mMaxSwipeVelocity));
                final float xVelocity = VelocityTrackerCompat
                        .getXVelocity(mVelocityTracker, mActivePointerId);
                final float yVelocity = VelocityTrackerCompat
                        .getYVelocity(mVelocityTracker, mActivePointerId);
                final int velDirFlag = yVelocity > 0f ? DOWN : UP;
                final float absYVelocity = Math.abs(yVelocity);
                if ((velDirFlag & flags) != 0 && velDirFlag == dirFlag &&
                        absYVelocity >= mCallback.getSwipeEscapeVelocity(mSwipeEscapeVelocity) &&
                        absYVelocity > Math.abs(xVelocity)) {
                    return velDirFlag;
                }
            }

            final float threshold = mRecyclerView.getHeight() * mCallback
                    .getSwipeThreshold(viewHolder);
            if ((flags & dirFlag) != 0 && Math.abs(mDy) > threshold) {
                return dirFlag;
            }
        }
        return 0;
    }

    private void addChildDrawingOrderCallback() {
        if (Build.VERSION.SDK_INT >= 21) {
            return;// we use elevation on Lollipop
        }
        if (mChildDrawingOrderCallback == null) {
            mChildDrawingOrderCallback = new RecyclerView.ChildDrawingOrderCallback() {
                @Override
                public int onGetChildDrawingOrder(int childCount, int i) {
                    if (mOverdrawChild == null) {
                        return i;
                    }
                    int childPosition = mOverdrawChildPosition;
                    if (childPosition == -1) {
                        childPosition = mRecyclerView.indexOfChild(mOverdrawChild);
                        mOverdrawChildPosition = childPosition;
                    }
                    if (i == childCount - 1) {
                        return childPosition;
                    }
                    return i < childPosition ? i : i + 1;
                }
            };
        }
        mRecyclerView.setChildDrawingOrderCallback(mChildDrawingOrderCallback);
    }

    void removeChildDrawingOrderCallbackIfNecessary(View view) {
        if (view == mOverdrawChild) {
            mOverdrawChild = null;
            // only remove if we've added
            if (mChildDrawingOrderCallback != null) {
                mRecyclerView.setChildDrawingOrderCallback(null);
            }
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }


    public static interface ViewDropHandler {


        public void prepareForDrop(View view, View target, int x, int y);
    }


    @SuppressWarnings("UnusedParameters")
    public abstract static class Callback {

        public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;

        public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;

        static final int RELATIVE_DIR_FLAGS = START | END |
                ((START | END) << DIRECTION_FLAG_COUNT) |
                ((START | END) << (2 * DIRECTION_FLAG_COUNT));

        private static final ReItemTouchUIUtil sUICallback;

        private static final int ABS_HORIZONTAL_DIR_FLAGS = LEFT | RIGHT |
                ((LEFT | RIGHT) << DIRECTION_FLAG_COUNT) |
                ((LEFT | RIGHT) << (2 * DIRECTION_FLAG_COUNT));

        private static final Interpolator sDragScrollInterpolator = new Interpolator() {
            @Override
            public float getInterpolation(float t) {
                return t * t * t * t * t;
            }
        };

        private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator() {
            @Override
            public float getInterpolation(float t) {
                t -= 1.0f;
                return t * t * t * t * t + 1.0f;
            }
        };


        private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000;

        private int mCachedMaxScrollSpeed = -1;

        static {
            if (Build.VERSION.SDK_INT >= 21) {
                sUICallback = new ReItemTouchUIUtilImpl.Lollipop();
            } else if (Build.VERSION.SDK_INT >= 11) {
                sUICallback = new ReItemTouchUIUtilImpl.Honeycomb();
            } else {
                sUICallback = new ReItemTouchUIUtilImpl.Gingerbread();
            }
        }


        public static ReItemTouchUIUtil getDefaultUIUtil() {
            return sUICallback;
        }


        public static int convertToRelativeDirection(int flags, int layoutDirection) {
            int masked = flags & ABS_HORIZONTAL_DIR_FLAGS;
            if (masked == 0) {
                return flags;// does not have any abs flags, good.
            }
            flags &= ~masked; //remove left / right.
            if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR) {
                // no change. just OR with 2 bits shifted mask and return
                flags |= masked << 2; // START is 2 bits after LEFT, END is 2 bits after RIGHT.
                return flags;
            } else {
                // add RIGHT flag as START
                flags |= ((masked << 1) & ~ABS_HORIZONTAL_DIR_FLAGS);
                // first clean RIGHT bit then add LEFT flag as END
                flags |= ((masked << 1) & ABS_HORIZONTAL_DIR_FLAGS) << 2;
            }
            return flags;
        }


        public static int makeMovementFlags(int dragFlags, int swipeFlags) {
            return makeFlag(ACTION_STATE_IDLE, swipeFlags | dragFlags) |
                    makeFlag(ACTION_STATE_SWIPE, swipeFlags) | makeFlag(ACTION_STATE_DRAG,
                    dragFlags);
        }


        public static int makeFlag(int actionState, int directions) {
            return directions << (actionState * DIRECTION_FLAG_COUNT);
        }


        public abstract int getMovementFlags(RecyclerView recyclerView,
                                             ViewHolder viewHolder);


        public boolean isForbidUpSwipeOut() {
            return false;
        }

        public boolean isForbidDownSwipeOut() {
            return false;
        }

        public boolean isForbidLeftSwipeOut() {
            return false;
        }

        public boolean isForbidRightSwipeOut() {
            return false;
        }

        public int getDefaultSwipeAnimationDuration() {
            return DEFAULT_SWIPE_ANIMATION_DURATION;
        }


        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            int masked = flags & RELATIVE_DIR_FLAGS;
            if (masked == 0) {
                return flags;// does not have any relative flags, good.
            }
            flags &= ~masked; //remove start / end
            if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR) {
                // no change. just OR with 2 bits shifted mask and return
                flags |= masked >> 2; // START is 2 bits after LEFT, END is 2 bits after RIGHT.
                return flags;
            } else {
                // add START flag as RIGHT
                flags |= ((masked >> 1) & ~RELATIVE_DIR_FLAGS);
                // first clean start bit then add END flag as LEFT
                flags |= ((masked >> 1) & RELATIVE_DIR_FLAGS) >> 2;
            }
            return flags;
        }

        final int getAbsoluteMovementFlags(RecyclerView recyclerView,
                                           ViewHolder viewHolder) {
            final int flags = getMovementFlags(recyclerView, viewHolder);
            return convertToAbsoluteDirection(flags, ViewCompat.getLayoutDirection(recyclerView));
        }

        boolean hasDragFlag(RecyclerView recyclerView, ViewHolder viewHolder) {
            final int flags = getAbsoluteMovementFlags(recyclerView, viewHolder);
            return (flags & ACTION_MODE_DRAG_MASK) != 0;
        }

        boolean hasSwipeFlag(RecyclerView recyclerView,
                             ViewHolder viewHolder) {
            final int flags = getAbsoluteMovementFlags(recyclerView, viewHolder);
            return (flags & ACTION_MODE_SWIPE_MASK) != 0;
        }


        public boolean canDropOver(RecyclerView recyclerView, ViewHolder current,
                                   ViewHolder target) {
            return true;
        }


        public abstract boolean onMove(RecyclerView recyclerView,
                                       ViewHolder viewHolder, ViewHolder target);


        public boolean isLongPressDragEnabled() {
            return true;
        }


        public boolean isItemViewSwipeEnabled() {
            return true;
        }


        public int getBoundingBoxMargin() {
            return 0;
        }


        public float getSwipeThreshold(ViewHolder viewHolder) {
            return .5f;
        }


        public float getMoveThreshold(ViewHolder viewHolder) {
            return .5f;
        }


        public float getSwipeEscapeVelocity(float defaultValue) {
            return defaultValue;
        }


        public float getSwipeVelocityThreshold(float defaultValue) {
            return defaultValue;
        }


        public ViewHolder chooseDropTarget(ViewHolder selected,
                                           List<ViewHolder> dropTargets, int curX, int curY) {
            int right = curX + selected.itemView.getWidth();
            int bottom = curY + selected.itemView.getHeight();
            ViewHolder winner = null;
            int winnerScore = -1;
            final int dx = curX - selected.itemView.getLeft();
            final int dy = curY - selected.itemView.getTop();
            final int targetsSize = dropTargets.size();
            for (int i = 0; i < targetsSize; i++) {
                final ViewHolder target = dropTargets.get(i);
                if (dx > 0) {
                    int diff = target.itemView.getRight() - right;
                    if (diff < 0 && target.itemView.getRight() > selected.itemView.getRight()) {
                        final int score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }
                if (dx < 0) {
                    int diff = target.itemView.getLeft() - curX;
                    if (diff > 0 && target.itemView.getLeft() < selected.itemView.getLeft()) {
                        final int score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }
                if (dy < 0) {
                    int diff = target.itemView.getTop() - curY;
                    if (diff > 0 && target.itemView.getTop() < selected.itemView.getTop()) {
                        final int score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }

                if (dy > 0) {
                    int diff = target.itemView.getBottom() - bottom;
                    if (diff < 0 && target.itemView.getBottom() > selected.itemView.getBottom()) {
                        final int score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }
            }
            return winner;
        }


        public abstract void onSwiped(ViewHolder viewHolder, int direction);


        public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                sUICallback.onSelected(viewHolder.itemView);
            }
        }

        private int getMaxDragScroll(RecyclerView recyclerView) {
            if (mCachedMaxScrollSpeed == -1) {
                mCachedMaxScrollSpeed = recyclerView.getResources().getDimensionPixelSize(
                        android.support.v7.recyclerview.R.dimen.item_touch_helper_max_drag_scroll_per_frame);
            }
            return mCachedMaxScrollSpeed;
        }


        public void onMoved(final RecyclerView recyclerView,
                            final ViewHolder viewHolder, int fromPos, final ViewHolder target, int toPos, int x,
                            int y) {
            final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof ViewDropHandler) {
                ((ViewDropHandler) layoutManager).prepareForDrop(viewHolder.itemView,
                        target.itemView, x, y);
                return;
            }

            // if layout manager cannot handle it, do some guesswork
            if (layoutManager.canScrollHorizontally()) {
                final int minLeft = layoutManager.getDecoratedLeft(target.itemView);
                if (minLeft <= recyclerView.getPaddingLeft()) {
                    recyclerView.scrollToPosition(toPos);
                }
                final int maxRight = layoutManager.getDecoratedRight(target.itemView);
                if (maxRight >= recyclerView.getWidth() - recyclerView.getPaddingRight()) {
                    recyclerView.scrollToPosition(toPos);
                }
            }

            if (layoutManager.canScrollVertically()) {
                final int minTop = layoutManager.getDecoratedTop(target.itemView);
                if (minTop <= recyclerView.getPaddingTop()) {
                    recyclerView.scrollToPosition(toPos);
                }
                final int maxBottom = layoutManager.getDecoratedBottom(target.itemView);
                if (maxBottom >= recyclerView.getHeight() - recyclerView.getPaddingBottom()) {
                    recyclerView.scrollToPosition(toPos);
                }
            }
        }

        void onDraw(Canvas c, RecyclerView parent, ViewHolder selected,
                    List<RecoverAnimation> recoverAnimationList,
                    int actionState, float dX, float dY) {
            final int recoverAnimSize = recoverAnimationList.size();
            //when action up execute
            for (int i = 0; i < recoverAnimSize; i++) {
                final ReItemTouchHelper.RecoverAnimation anim = recoverAnimationList.get(i);
                anim.update();
                final int count = c.save();
                onChildDraw(c, parent, anim.mViewHolder, anim.mX, anim.mY, anim.mActionState,
                        false);
                c.restoreToCount(count);
            }
            // when action down execute
            if (selected != null) {
                final int count = c.save();
                onChildDraw(c, parent, selected, dX, dY, actionState, true);
                c.restoreToCount(count);
            }
        }

        void onDrawOver(Canvas c, RecyclerView parent, ViewHolder selected,
                        List<RecoverAnimation> recoverAnimationList,
                        int actionState, float dX, float dY) {
            final int recoverAnimSize = recoverAnimationList.size();
            for (int i = 0; i < recoverAnimSize; i++) {
                final ReItemTouchHelper.RecoverAnimation anim = recoverAnimationList.get(i);
                final int count = c.save();
                onChildDrawOver(c, parent, anim.mViewHolder, anim.mX, anim.mY, anim.mActionState,
                        false);
                c.restoreToCount(count);
            }
            if (selected != null) {
                final int count = c.save();
                onChildDrawOver(c, parent, selected, dX, dY, actionState, true);
                c.restoreToCount(count);
            }
            boolean hasRunningAnimation = false;
            for (int i = recoverAnimSize - 1; i >= 0; i--) {
                final RecoverAnimation anim = recoverAnimationList.get(i);
                if (anim.mEnded && !anim.mIsPendingCleanup) {
                    recoverAnimationList.remove(i);
                } else if (!anim.mEnded) {
                    hasRunningAnimation = true;
                }
            }
            if (hasRunningAnimation) {
                parent.invalidate();
            }
        }


        public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
            sUICallback.clearView(viewHolder.itemView);
        }


        public boolean enableHardWare() {
            return true;
        }


        public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            sUICallback.onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState,
                    isCurrentlyActive);
        }


        public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                    ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
            sUICallback.onDrawOver(c, recyclerView, viewHolder.itemView, dX, dY, actionState,
                    isCurrentlyActive);
        }


        public long getAnimationDuration(RecyclerView recyclerView, int animationType,
                                         float animateDx, float animateDy) {
            final RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            if (itemAnimator == null) {
                return animationType == ANIMATION_TYPE_DRAG ? DEFAULT_DRAG_ANIMATION_DURATION
                        : DEFAULT_SWIPE_ANIMATION_DURATION;
            } else {
                return animationType == ANIMATION_TYPE_DRAG ? itemAnimator.getMoveDuration()
                        : itemAnimator.getRemoveDuration();
            }
        }


        public int interpolateOutOfBoundsScroll(RecyclerView recyclerView,
                                                int viewSize, int viewSizeOutOfBounds,
                                                int totalSize, long msSinceStartScroll) {
            final int maxScroll = getMaxDragScroll(recyclerView);
            final int absOutOfBounds = Math.abs(viewSizeOutOfBounds);
            final int direction = (int) Math.signum(viewSizeOutOfBounds);
            // might be negative if other direction
            float outOfBoundsRatio = Math.min(1f, 1f * absOutOfBounds / viewSize);
            final int cappedScroll = (int) (direction * maxScroll *
                    sDragViewScrollCapInterpolator.getInterpolation(outOfBoundsRatio));
            final float timeRatio;
            if (msSinceStartScroll > DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS) {
                timeRatio = 1f;
            } else {
                timeRatio = (float) msSinceStartScroll / DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS;
            }
            final int value = (int) (cappedScroll * sDragScrollInterpolator
                    .getInterpolation(timeRatio));
            if (value == 0) {
                return viewSizeOutOfBounds > 0 ? 1 : -1;
            }
            return value;
        }

        public abstract void setSwitchListener(boolean switchListener);

        public abstract boolean getSwitchListener();
    }


    public abstract static class SimpleCallback extends Callback {

        private int mDefaultSwipeDirs;

        private int mDefaultDragDirs;


        public SimpleCallback(int dragDirs, int swipeDirs) {
            mDefaultSwipeDirs = swipeDirs;
            mDefaultDragDirs = dragDirs;
        }


        public void setDefaultSwipeDirs(int defaultSwipeDirs) {
            mDefaultSwipeDirs = defaultSwipeDirs;
        }


        public void setDefaultDragDirs(int defaultDragDirs) {
            mDefaultDragDirs = defaultDragDirs;
        }


        public int getSwipeDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
            return mDefaultSwipeDirs;
        }


        public int getDragDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
            return mDefaultDragDirs;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            return makeMovementFlags(getDragDirs(recyclerView, viewHolder),
                    getSwipeDirs(recyclerView, viewHolder));
        }
    }

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {

        ItemTouchHelperGestureListener() {
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View child = findChildView(e);
            if (child != null) {
                ViewHolder vh = mRecyclerView.getChildViewHolder(child);
                if (vh != null) {
                    if (!mCallback.hasDragFlag(mRecyclerView, vh)) {
                        return;
                    }
                    int pointerId = e.getPointerId(0);
                    // Long press is deferred.
                    // Check w/ active pointer id to avoid selecting after motion
                    // event is canceled.
                    if (pointerId == mActivePointerId) {
                        final int index = e.findPointerIndex(mActivePointerId);
                        final float x = e.getX(index);
                        final float y = e.getY(index);
                        mInitialTouchX = x;
                        mInitialTouchY = y;
                        mDx = mDy = 0f;
                        if (DEBUG) {
                            Log.d(TAG,
                                    "onlong press: x:" + mInitialTouchX + ",y:" + mInitialTouchY);
                        }
                        if (mCallback.isLongPressDragEnabled()) {
                            select(vh, ACTION_STATE_DRAG);
                        }
                    }
                }
            }
        }
    }

    private class RecoverAnimation implements Animator.AnimatorListener {

        final float mStartDx;

        final float mStartDy;

        final float mTargetX;

        final float mTargetY;

        final ViewHolder mViewHolder;

        final int mActionState;

        private final ValueAnimator mValueAnimator;

        final int mAnimationType;

        public boolean mIsPendingCleanup;

        float mX;

        float mY;

        // if user starts touching a recovering view, we put it into interaction mode again,
        // instantly.
        boolean mOverridden = false;

        boolean mEnded = false;

        private float mFraction;

        public RecoverAnimation(ViewHolder viewHolder, int animationType,
                                int actionState, float startDx, float startDy, float targetX, float targetY) {
            mActionState = actionState;
            mAnimationType = animationType;
            mViewHolder = viewHolder;
            mStartDx = startDx;
            mStartDy = startDy;
            mTargetX = targetX;
            mTargetY = targetY;
            mValueAnimator = ValueAnimator.ofFloat(0f, 1f);
            mValueAnimator.addUpdateListener(
                    new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            setFraction(animation.getAnimatedFraction());
                        }
                    });
            mValueAnimator.setTarget(viewHolder.itemView);
            mValueAnimator.addListener(this);
            setFraction(0f);
        }

        public void setDuration(long duration) {
            mValueAnimator.setDuration(duration);
        }

        public void start() {
            mViewHolder.setIsRecyclable(false);
            mValueAnimator.start();
        }

        public void cancel() {
            mValueAnimator.cancel();
        }

        public void setFraction(float fraction) {
            mFraction = fraction;
        }


        public void update() {
            if (mStartDx == mTargetX) {
                mX = ViewCompat.getTranslationX(mViewHolder.itemView);
            } else {
                mX = mStartDx + mFraction * (mTargetX - mStartDx);
            }
            if (mStartDy == mTargetY) {
                mY = ViewCompat.getTranslationY(mViewHolder.itemView);
            } else {
                mY = mStartDy + mFraction * (mTargetY - mStartDy);
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!mEnded) {
                mViewHolder.setIsRecyclable(true);
            }
            isManually = false;
            mEnded = true;
            if (mCallback.enableHardWare()) {
                mViewHolder.itemView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            setFraction(1f); //make sure we recover the view's state.
            if (mCallback.enableHardWare()) {
                mViewHolder.itemView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
            isManually = false;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
