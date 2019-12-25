package com.lipanampesa

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.lipanampesa.R.string

class FloatingService : Service(), View.OnClickListener {
  lateinit var windowManager: WindowManager
  lateinit var floatingBanner: View
  lateinit var topLabel: Button
  lateinit var collapseView: View
  lateinit var minimizeButton: View
  lateinit var accountView: View
  lateinit var paybillView: View
  lateinit var amountView: View
  lateinit var accountTextView: TextView
  lateinit var payBillTextView: TextView
  lateinit var amountTextView: TextView
  lateinit var closeView: ImageView

  private lateinit var lipaNaMpesa: LipaNaMpesa

  override fun onBind(intent: Intent): IBinder? {
    return null
  }

  override fun onClick(v: View?) {
  }

  override fun onStart(intent: Intent?, startId: Int) {
    super.onStart(intent, startId)
    val extras = intent!!.extras
    if (extras == null) {
      Log.d("Service", "lipa na mpesa param are empty")
      throw (Throwable("Params required"))
    } else {
      lipaNaMpesa = extras.getSerializable(LipaNaMpesaConstants.LIPANAMPESA) as LipaNaMpesa
      amountTextView.text = lipaNaMpesa.AMOUNT
      accountTextView.text = lipaNaMpesa.ACCOUNT
      payBillTextView.text = lipaNaMpesa.PAYBILL
      setPrimaryColor(lipaNaMpesa.PRIMARYCOLOR)

    }
  }

  private fun setPrimaryColor(primaryColor: Int) {
    topLabel.setBackgroundColor(primaryColor)
    closeView.setBackgroundColor(primaryColor)
  }

  override fun onCreate() {
    super.onCreate()

    //bind ui
    floatingBanner = LayoutInflater.from(this).inflate(R.layout.mpesa_widget_layout, null)
    collapseView = floatingBanner.findViewById(R.id.collapseView)
    closeView = floatingBanner.findViewById(R.id.closingImage)
    topLabel = floatingBanner.findViewById(R.id.topCollapsible)
    minimizeButton = floatingBanner.findViewById(R.id.minimizeButtton)
    accountView = floatingBanner.findViewById(R.id.accountView)
    paybillView = floatingBanner.findViewById(R.id.payBillView)
    payBillTextView = floatingBanner.findViewById(R.id.payBillNumber)
    accountTextView = floatingBanner.findViewById(R.id.accountTxt)
    amountView = floatingBanner.findViewById(R.id.amountView)
    amountTextView = floatingBanner.findViewById(R.id.amount)

    //layout params
    val params = LayoutParams(
      LayoutParams.WRAP_CONTENT,
      LayoutParams.WRAP_CONTENT,
      LayoutParams.TYPE_APPLICATION_OVERLAY,
      LayoutParams.FLAG_NOT_FOCUSABLE,
      PixelFormat.TRANSLUCENT
    )
    params.gravity = Gravity.TOP or Gravity.RIGHT
    windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.addView(floatingBanner, params)

    topLabel.setOnClickListener {
      collapseExpand()
    }

    closeView.setOnClickListener {
      stopSelf()
    }



    minimizeButton.setOnClickListener {
      collapseLayout()
    }

    //copy data

    paybillView.setOnClickListener {
      copyText(payBillTextView)
    }
    accountView.setOnClickListener {
      copyText(accountTextView)
    }
    amountView.setOnClickListener {
      copyText(amountTextView)
    }

  }

  fun collapseLayout() {
//    topExpand.visibility = View.VISIBLE
    collapseView.visibility = View.GONE
  }

  fun expandLayout() {
    collapseView.visibility = View.VISIBLE
//    topExpand.visibility = View.GONE

  }

  fun copyText(view: View?) {
    lateinit var clipboardManager: ClipboardManager

    val text: String = (view as TextView).text.toString()
    if (text.isNotEmpty()) {
      clipboardManager =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clipData = ClipData.newPlainText("lipaKey", text)
      clipboardManager.setPrimaryClip(clipData)
      Toast.makeText(applicationContext, "Copied", Toast.LENGTH_SHORT).show()
    } else {
      Toast.makeText(applicationContext, "No text to be copied", Toast.LENGTH_SHORT).show()
    }
  }

  fun collapseExpand() {
    if (collapseView.isVisible) {
      collapseLayout()
      topLabel.setText(string.click_to_expand)
    } else {
      expandLayout()
      topLabel.text = getString(string.payment_details)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    windowManager.removeView(floatingBanner)
  }

}
