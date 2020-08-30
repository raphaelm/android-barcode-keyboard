package io.rami.barcodekeyboard


import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import me.dm7.barcodescanner.zxing.ZXingScannerView


class ScannerView : ZXingScannerView {
    private var mFramingRectInPreview: Rect? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {}

    @Synchronized
    override fun getFramingRectInPreview(previewWidth: Int, previewHeight: Int): Rect {
        if (this.mFramingRectInPreview == null) {
            val rect = Rect()
            rect.left = 0
            rect.top = 0
            rect.right = previewWidth
            rect.bottom = previewHeight

            this.mFramingRectInPreview = rect
        }

        return this.mFramingRectInPreview!!
    }
}