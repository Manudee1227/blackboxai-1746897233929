package com.beautycam.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CurveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val points = mutableListOf<PointF>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    
    private var selectedPoint: Int = -1
    private var currentChannel = Channel.RGB
    private var onCurveChangeListener: ((Array<FloatArray>) -> Unit)? = null

    init {
        // Initialize with default linear curve points
        points.add(PointF(0f, 1f))  // Bottom-left
        points.add(PointF(0.5f, 0.5f))  // Center
        points.add(PointF(1f, 0f))  // Top-right

        // Setup paints
        paint.apply {
            color = Color.WHITE
            strokeWidth = 8f
            style = Paint.Style.FILL
        }

        pathPaint.apply {
            color = Color.WHITE
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }

        gridPaint.apply {
            color = Color.GRAY
            strokeWidth = 1f
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val width = width.toFloat()
        val height = height.toFloat()

        // Draw grid
        drawGrid(canvas, width, height)

        // Draw curve
        path.reset()
        path.moveTo(0f, height)
        
        // Create smooth curve through points
        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            
            path.cubicTo(
                p1.x * width, (1 - p1.y) * height,
                p2.x * width, (1 - p2.y) * height,
                p2.x * width, (1 - p2.y) * height
            )
        }

        canvas.drawPath(path, pathPaint)

        // Draw control points
        points.forEach { point ->
            canvas.drawCircle(
                point.x * width,
                (1 - point.y) * height,
                8f,
                paint
            )
        }
    }

    private fun drawGrid(canvas: Canvas, width: Float, height: Float) {
        // Draw vertical lines
        for (i in 0..4) {
            val x = width * i / 4
            canvas.drawLine(x, 0f, x, height, gridPaint)
        }

        // Draw horizontal lines
        for (i in 0..4) {
            val y = height * i / 4
            canvas.drawLine(0f, y, width, y, gridPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x / width
        val y = 1 - (event.y / height)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                selectedPoint = findNearestPoint(x, y)
                if (selectedPoint == -1 && points.size < 10) {
                    // Add new point
                    insertPoint(x, y)
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (selectedPoint != -1) {
                    updatePoint(selectedPoint, x, y)
                    notifyCurveChanged()
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                selectedPoint = -1
            }
        }
        return true
    }

    private fun findNearestPoint(x: Float, y: Float): Int {
        var minDist = Float.MAX_VALUE
        var index = -1
        
        points.forEachIndexed { i, point ->
            val dist = abs(point.x - x) + abs(point.y - y)
            if (dist < minDist && dist < 0.1f) {
                minDist = dist
                index = i
            }
        }
        return index
    }

    private fun insertPoint(x: Float, y: Float) {
        val newPoint = PointF(x, y)
        var insertIndex = 0
        
        // Find correct position to insert new point (maintain x-order)
        while (insertIndex < points.size && points[insertIndex].x < x) {
            insertIndex++
        }
        
        points.add(insertIndex, newPoint)
        selectedPoint = insertIndex
        notifyCurveChanged()
    }

    private fun updatePoint(index: Int, x: Float, y: Float) {
        val point = points[index]
        
        // Constrain x movement for end points
        if (index == 0) {
            point.x = 0f
        } else if (index == points.size - 1) {
            point.x = 1f
        } else {
            // Constrain x movement between adjacent points
            val minX = points[index - 1].x
            val maxX = points[index + 1].x
            point.x = max(minX, min(maxX, x))
        }
        
        // Constrain y to 0-1 range
        point.y = max(0f, min(1f, y))
    }

    fun setChannel(channel: Channel) {
        currentChannel = channel
        invalidate()
    }

    fun setCurveChangeListener(listener: (Array<FloatArray>) -> Unit) {
        onCurveChangeListener = listener
    }

    private fun notifyCurveChanged() {
        onCurveChangeListener?.invoke(points.map { 
            floatArrayOf(it.x, it.y) 
        }.toTypedArray())
    }

    fun reset() {
        points.clear()
        points.add(PointF(0f, 0f))
        points.add(PointF(0.5f, 0.5f))
        points.add(PointF(1f, 1f))
        notifyCurveChanged()
        invalidate()
    }

    enum class Channel {
        RGB, RED, GREEN, BLUE
    }

    // Preset curves
    fun setPreset(preset: CurvePreset) {
        points.clear()
        when (preset) {
            CurvePreset.LINEAR -> {
                points.add(PointF(0f, 0f))
                points.add(PointF(1f, 1f))
            }
            CurvePreset.MEDIUM_CONTRAST -> {
                points.add(PointF(0f, 0f))
                points.add(PointF(0.25f, 0.15f))
                points.add(PointF(0.75f, 0.85f))
                points.add(PointF(1f, 1f))
            }
            CurvePreset.HIGH_CONTRAST -> {
                points.add(PointF(0f, 0f))
                points.add(PointF(0.25f, 0.1f))
                points.add(PointF(0.75f, 0.9f))
                points.add(PointF(1f, 1f))
            }
            CurvePreset.CROSS_PROCESS -> {
                points.add(PointF(0f, 0f))
                points.add(PointF(0.25f, 0.3f))
                points.add(PointF(0.5f, 0.6f))
                points.add(PointF(0.75f, 0.8f))
                points.add(PointF(1f, 0.9f))
            }
            CurvePreset.NEGATIVE -> {
                points.add(PointF(0f, 1f))
                points.add(PointF(1f, 0f))
            }
        }
        notifyCurveChanged()
        invalidate()
    }

    enum class CurvePreset {
        LINEAR,
        MEDIUM_CONTRAST,
        HIGH_CONTRAST,
        CROSS_PROCESS,
        NEGATIVE
    }
}
