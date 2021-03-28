package io.rami.barcodekeyboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class BarcodeInputService : InputMethodService(), ZXingScannerView.ResultHandler {
    var scannerView: ZXingScannerView? = null
    var button: Button? = null
    var lastText: String = ""
    var lastTime: Long = 0
    override fun onCreateInputView(): View {
        val v = layoutInflater.inflate(R.layout.input, null)
        scannerView = v.findViewById(R.id.zxing_scanner)

        button = v.findViewById<Button>(R.id.button)
        button!!.setOnClickListener {
            val i = Intent(this, PermissionCheckActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
        enforcePermission()
        return v
    }

    fun enforcePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val i = Intent(this, PermissionCheckActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        } else {
            button?.visibility = View.GONE
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        scannerView?.setResultHandler(this)
        enforcePermission()
        scannerView?.startCamera()
    }

    override fun onFinishInput() {
        super.onFinishInput()
        scannerView?.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        scannerView?.resumeCameraPreview(this)
        if (rawResult.text == lastText && System.currentTimeMillis() - lastTime < 5000) {
            return
        }
        lastText = rawResult.text
        lastTime = System.currentTimeMillis()
        currentInputConnection.also { ic: InputConnection ->
            ic.commitText(rawResult.text, 1)
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        }
    }
}
