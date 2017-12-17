package org.alaeri.cityvibe.player

/**
 * Created by Emmanuel Requier on 16/12/2017.
 * From the internet. Same as usual
 */
import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

class SquaredImageViewCompat : AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        setMeasuredDimension(width, width)
    }

}