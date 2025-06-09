package com.example.wifirtt.ui.data

object BoundingBox {
    var x: Float = 0.0F
    var y: Float = 0F
    var original: BoundingBox4Params = BoundingBox4Params(0F, 0F, 0F, 0F)

    fun addParam(bb: BoundingBox4Params) {
        original = bb
        x = bb.up - bb.down
        y = bb.right - bb.left
        ScanRTTRouters.normalizeRouters(bb)
    }
}