package com.proscan.generator_domain.use_case

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.proscan.generator_domain.model.OutputFormat

class GenerateQrBitmap {
    operator fun invoke(
        content: String,
        format: OutputFormat = OutputFormat.QR_CODE,
        width: Int = 512,
        height: Int = 512
    ): Bitmap? {
        return try {
            val writer = MultiFormatWriter()
            val zxingFormat = format.toZxing()
            val bitMatrix: BitMatrix = writer.encode(content, zxingFormat, width, height)
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bmp
        } catch (e: Exception) {
            null
        }
    }

    private fun OutputFormat.toZxing(): BarcodeFormat = when (this) {
        OutputFormat.QR_CODE     -> BarcodeFormat.QR_CODE
        OutputFormat.EAN_13      -> BarcodeFormat.EAN_13
        OutputFormat.UPC_E       -> BarcodeFormat.UPC_E
        OutputFormat.UPC_A       -> BarcodeFormat.UPC_A
        OutputFormat.CODE_39     -> BarcodeFormat.CODE_39
        OutputFormat.CODE_93     -> BarcodeFormat.CODE_93
        OutputFormat.CODE_128    -> BarcodeFormat.CODE_128
        OutputFormat.ITF         -> BarcodeFormat.ITF
        OutputFormat.PDF_417     -> BarcodeFormat.PDF_417
        OutputFormat.CODABAR     -> BarcodeFormat.CODABAR
        OutputFormat.DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
        OutputFormat.AZTEC       -> BarcodeFormat.AZTEC
    }
}
