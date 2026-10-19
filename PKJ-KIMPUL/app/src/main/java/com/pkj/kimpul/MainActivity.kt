package com.pkj.kimpul

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var progress: ProgressBar
    private lateinit var offline: View
    private var hasError = false
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooser = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
        val uris = if (r.resultCode == Activity.RESULT_OK)
            WebChromeClient.FileChooserParams.parseResult(r.resultCode, r.data) else null
        filePathCallback?.onReceiveValue(uris)
        filePathCallback = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PKJ)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        swipe = findViewById(R.id.swipe)
        progress = findViewById(R.id.progress)
        offline = findViewById(R.id.offline)
        findViewById<Button>(R.id.retry).setOnClickListener { reload() }

        swipe.setColorSchemeResources(R.color.brand_purple)
        swipe.setOnRefreshListener { reload() }
        swipe.setOnChildScrollUpCallback { _, _ -> webView.scrollY > 0 }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            allowFileAccess = false
            mediaPlaybackRequiresUserGesture = false
        }
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView, req: WebResourceRequest): Boolean {
                val url = req.url
                val scheme = url.scheme ?: return false
                if (scheme == "http" || scheme == "https") {
                    val host = url.host ?: ""
                    val external = host.contains("wa.me") || host.contains("whatsapp.com") ||
                        host.contains("maps.google") || host == "maps.app.goo.gl"
                    if (!external) return false
                }
                return openExternal(url)
            }
            override fun onPageStarted(v: WebView, url: String?, f: Bitmap?) {
                hasError = false
                progress.visibility = View.VISIBLE
            }
            override fun onPageFinished(v: WebView, url: String?) {
                progress.visibility = View.GONE
                swipe.isRefreshing = false
                offline.visibility = if (hasError) View.VISIBLE else View.GONE
            }
            override fun onReceivedError(v: WebView, req: WebResourceRequest, err: WebResourceError) {
                if (req.isForMainFrame) { hasError = true; offline.visibility = View.VISIBLE }
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(v: WebView, p: Int) { progress.progress = p }
            override fun onShowFileChooser(v: WebView, cb: ValueCallback<Array<Uri>>, p: FileChooserParams): Boolean {
                filePathCallback?.onReceiveValue(null)
                filePathCallback = cb
                return try { fileChooser.launch(p.createIntent()); true }
                catch (e: ActivityNotFoundException) { filePathCallback = null; false }
            }
        }
        webView.setDownloadListener { url, _, _, _, _ -> openExternal(Uri.parse(url)) }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack()
                else { isEnabled = false; onBackPressedDispatcher.onBackPressed() }
            }
        })

        if (savedInstanceState != null) webView.restoreState(savedInstanceState)
        else webView.loadUrl(BuildConfig.WEB_URL)
    }

    private fun reload() {
        offline.visibility = View.GONE
        hasError = false
        if (webView.url == null) webView.loadUrl(BuildConfig.WEB_URL) else webView.reload()
    }

    private fun openExternal(uri: Uri): Boolean = try {
        startActivity(Intent(Intent.ACTION_VIEW, uri)); true
    } catch (e: ActivityNotFoundException) { true }

    override fun onSaveInstanceState(outState: Bundle) { super.onSaveInstanceState(outState); webView.saveState(outState) }
    override fun onPause() { super.onPause(); webView.onPause() }
    override fun onResume() { super.onResume(); webView.onResume() }
    override fun onDestroy() { webView.destroy(); super.onDestroy() }
}
