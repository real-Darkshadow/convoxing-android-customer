package com.convoxing.convoxing_customer.utils.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer(
    chart: BarChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler,
    private val cornerRadius: Float = 20f // Customize corner radius
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val rectF = RectF()

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = dataSet.barBorderWidth

        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        // Initialize the buffer
        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.feed(dataSet)

        // Check if the underlying array is null and return early if so
        val dataBuffer = buffer.buffer ?: return

        trans.pointValuesToPixel(dataBuffer)

        val isSingleColor = dataSet.colors.size == 1

        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }

        // Iterate over the buffer safely now that we know it isn't null.
        for (j in dataBuffer.indices step 4) {
            if (!mViewPortHandler.isInBoundsLeft(dataBuffer[j + 2])) continue
            if (!mViewPortHandler.isInBoundsRight(dataBuffer[j])) break

            if (!isSingleColor) {
                mRenderPaint.color = dataSet.getColor(j / 4)
            }

            rectF.set(
                dataBuffer[j], dataBuffer[j + 1],
                dataBuffer[j + 2], dataBuffer[j + 3]
            )

            c.drawRoundRect(rectF, cornerRadius, cornerRadius, mRenderPaint)

            if (drawBorder) {
                c.drawRoundRect(rectF, cornerRadius, cornerRadius, mBarBorderPaint)
            }
        }
    }
}