package com.jaygoo.widget;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import static com.jaygoo.widget.VerticalRangeSeekBar.DIRECTION_LEFT;
import static com.jaygoo.widget.VerticalRangeSeekBar.DIRECTION_RIGHT;
import static com.jaygoo.widget.VerticalRangeSeekBar.TEXT_DIRECTION_VERTICAL;


/**
 * //                       _ooOoo_
 * //                      o8888888o
 * //                      88" . "88
 * //                      (| -_- |)
 * //                       O\ = /O
 * //                   ____/`---'\____
 * //                 .   ' \\| |// `.
 * //                  / \\||| : |||// \
 * //                / _||||| -:- |||||- \
 * //                  | | \\\ - /// | |
 * //                | \_| ''\---/'' | |
 * //                 \ .-\__ `-` ___/-. /
 * //              ______`. .' /--.--\ `. . __
 * //           ."" '< `.___\_<|>_/___.' >'"".
 * //          | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //            \ \ `-. \_ __\ /__ _/ .-` / /
 * //    ======`-.____`-.___\_____/___.-`____.-'======
 * //                       `=---='
 * //
 * //    .............................................
 * //             佛祖保佑             永无BUG
 * =====================================================
 * 作    者：JayGoo
 * 创建日期：2019-06-05
 * 描    述:
 * =====================================================
 */
public class VerticalSeekBar extends SeekBar {

    private int indicatorTextOrientation;
    VerticalRangeSeekBar verticalSeekBar;

    public VerticalSeekBar(RangeSeekBar rangeSeekBar, AttributeSet attrs, boolean isLeft) {
        super(rangeSeekBar, attrs, isLeft);
        initAttrs(attrs);
        verticalSeekBar = (VerticalRangeSeekBar) rangeSeekBar;
    }

    private void initAttrs(AttributeSet attrs) {
        try {
            TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalRangeSeekBar);
            indicatorTextOrientation = t.getInt(R.styleable.VerticalRangeSeekBar_rsb_indicator_text_orientation, TEXT_DIRECTION_VERTICAL);
            t.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void drawIndicator(Canvas canvas, Paint paint, String text2Draw) {
        if (text2Draw == null) return;
        //draw indicator
        if (indicatorTextOrientation == TEXT_DIRECTION_VERTICAL) {
            drawVerticalIndicator(canvas, paint, text2Draw);
        } else {
            super.drawIndicator(canvas, paint, text2Draw);
        }
    }

    protected void drawVerticalIndicator(Canvas canvas, Paint paint, String text2Draw) {
        //measure indicator text
        paint.setTextSize(indicatorTextSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(indicatorBackgroundColor);
        paint.getTextBounds(text2Draw, 0, text2Draw.length(), indicatorTextRect);

        int realIndicatorWidth = indicatorTextRect.height() + indicatorPaddingLeft + indicatorPaddingRight;
        if (indicatorWidth > realIndicatorWidth) {
            realIndicatorWidth = indicatorWidth;
        }

        int realIndicatorHeight = indicatorTextRect.width() + indicatorPaddingTop + indicatorPaddingBottom;
        if (indicatorHeight > realIndicatorHeight) {
            realIndicatorHeight = indicatorHeight;
        }

        indicatorRect.left = thumbWidth / 2 - realIndicatorWidth / 2;
        indicatorRect.top = (int) (bottom - realIndicatorHeight - getThumbScaleHeight() - indicatorMargin);
        indicatorRect.right = indicatorRect.left + realIndicatorWidth;
        indicatorRect.bottom = indicatorRect.top + realIndicatorHeight;

        //draw default indicator arrow
        if (indicatorBitmap == null) {
            //arrow three point
            //  b   c
            //    a
            int ax = thumbWidth / 2;
            int ay = (int) (bottom - getThumbScaleHeight() - indicatorMargin);
            int bx = ax - indicatorArrowSize;
            int by = ay - indicatorArrowSize;
            int cx = ax + indicatorArrowSize;
            indicatorArrowPath.reset();
            indicatorArrowPath.moveTo(ax, ay);
            indicatorArrowPath.lineTo(bx, by);
            indicatorArrowPath.lineTo(cx, by);
            indicatorArrowPath.close();
            canvas.drawPath(indicatorArrowPath, paint);
            indicatorRect.bottom -= indicatorArrowSize;
            indicatorRect.top -= indicatorArrowSize;
        }

        int defaultPaddingOffset = Utils.dp2px(getContext(), 1);
        int leftOffset = indicatorRect.width() / 2 - (int) (rangeSeekBar.getProgressWidth() * currPercent) - rangeSeekBar.getProgressLeft() + defaultPaddingOffset;
        int rightOffset = indicatorRect.width() / 2 - (int) (rangeSeekBar.getProgressWidth() * (1 - currPercent)) - rangeSeekBar.getProgressPaddingRight() + defaultPaddingOffset;
        if (leftOffset > 0) {
            indicatorRect.left += leftOffset;
            indicatorRect.right += leftOffset;
        } else if (rightOffset > 0) {
            indicatorRect.left -= rightOffset;
            indicatorRect.right -= rightOffset;
        }

        //draw indicator background
        if (indicatorBitmap != null) {
            Utils.drawNinePath(canvas, indicatorBitmap, indicatorRect);
        } else if (indicatorRadius > 0f) {
            canvas.drawRoundRect(new RectF(indicatorRect), indicatorRadius, indicatorRadius, paint);
        } else {
            canvas.drawRect(indicatorRect, paint);
        }

        //draw indicator content text
        int tx, ty;
        if (indicatorPaddingLeft > 0) {
            tx = indicatorRect.left + indicatorPaddingLeft;
        } else if (indicatorPaddingRight > 0) {
            tx = indicatorRect.right - indicatorPaddingRight - indicatorTextRect.width();
        } else {
            tx = indicatorRect.left + (realIndicatorWidth - indicatorTextRect.width()) / 2;
        }

        if (indicatorPaddingTop > 0) {
            ty = indicatorRect.top + indicatorTextRect.height() + indicatorPaddingTop;
        } else if (indicatorPaddingBottom > 0) {
            ty = indicatorRect.bottom - indicatorTextRect.height() - indicatorPaddingBottom;
        } else {
            ty = indicatorRect.bottom - (realIndicatorHeight - indicatorTextRect.height()) / 2 + 1;
        }

        //draw indicator text
        paint.setColor(indicatorTextColor);
        int degrees = 0;
        float rotateX = (tx + indicatorTextRect.width() / 2f);
        float rotateY = (ty - indicatorTextRect.height() / 2f);

        if (indicatorTextOrientation == TEXT_DIRECTION_VERTICAL) {
            if (verticalSeekBar.getOrientation() == DIRECTION_LEFT) {
                degrees = 90;
            } else if (verticalSeekBar.getOrientation() == DIRECTION_RIGHT) {
                degrees = -90;
            }
        }
        if (degrees != 0) {
            canvas.rotate(degrees, rotateX, rotateY);
        }
        canvas.drawText(text2Draw, tx, ty, paint);
        if (degrees != 0) {
            canvas.rotate(-degrees, rotateX, rotateY);
        }
    }

    public int getIndicatorTextOrientation() {
        return indicatorTextOrientation;
    }

    public void setIndicatorTextOrientation(@VerticalRangeSeekBar.TextDirectionDef int orientation) {
        this.indicatorTextOrientation = orientation;
    }
}