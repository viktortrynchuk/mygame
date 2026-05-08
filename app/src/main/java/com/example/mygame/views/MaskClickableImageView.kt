package com.example.mygame.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView

class MaskClickableImageView @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : AppCompatImageView(ctx, attrs) {

    var mask: Bitmap? = null
    var idFromPixel: (Int) -> Int = { it and 0x00FF_FFFF }
    var onRegionClick: ((Int) -> Unit)? = null
    private var tracking = false

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val d = drawable ?: return false
        val m = mask ?: return false
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                tracking = hitOpaquePixel(e.x, e.y, d, m)
                return tracking           // only grab if over a valid region
            }
            MotionEvent.ACTION_UP -> {
                if (tracking && hitOpaquePixel(e.x, e.y, d, m)) {
                    val (mx, my) = mapTouchToMask(e.x, e.y, d, m) ?: return false
                    onRegionClick?.invoke(idFromPixel(m.getPixel(mx, my)))
                    performClick()
                }
                tracking = false
                return tracking
            }
            MotionEvent.ACTION_CANCEL -> { tracking = false; return false }
        }
        return false
    }

    override fun performClick(): Boolean { super.performClick(); return true }

    private fun hitOpaquePixel(x: Float, y: Float, d: Drawable, m: Bitmap): Boolean {
        val p = mapTouchToMask(x, y, d, m) ?: return false
        val a = (m.getPixel(p.first, p.second) ushr 24) and 0xFF
        return a != 0
    }

    private fun mapTouchToMask(x: Float, y: Float, d: Drawable, m: Bitmap): Pair<Int, Int>? {
        val inv = Matrix()
        if (!imageMatrix.invert(inv)) return null
        val pts = floatArrayOf(x, y); inv.mapPoints(pts)
        val dx = pts[0]; val dy = pts[1]
        val dw = d.intrinsicWidth.toFloat(); val dh = d.intrinsicHeight.toFloat()
        if (dx !in 0f..(dw - 1f) || dy !in 0f..(dh - 1f)) return null
        val mx = (dx / dw * m.width).toInt().coerceIn(0, m.width - 1)
        val my = (dy / dh * m.height).toInt().coerceIn(0, m.height - 1)
        return mx to my
    }
}



