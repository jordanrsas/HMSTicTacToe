package com.dtse.demo.gato

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.huawei.hms.jos.JosApps
import com.huawei.hms.jos.JosAppsClient
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), HmsAuth {

    companion object {
        private const val TAG = "HMS_MainActivity"
        private const val HMS_REQUEST_CODE = 8888
    }

    private var service: HuaweiIdAuthService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        initializeGame()

        authSilentSignIn()
    }

    private fun initializeGame() {
        val appsClient: JosAppsClient = JosApps.getJosAppsClient(this)
        appsClient.init()
        Log.i(TAG, "init success")
    }

    override fun signIn() {
        val authParams: HuaweiIdAuthParams =
            HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setIdToken()
                .createParams()

        service = HuaweiIdAuthManager.getService(this, authParams)
        startActivityForResult(service?.signInIntent, HMS_REQUEST_CODE)
    }

    override fun signOut() {
        service?.signOut()?.apply {
            addOnSuccessListener {
                Log.i(TAG, "signOut Success")
                nav_host_fragment.findNavController()
                    .navigate(R.id.action_SecondFragment_to_FirstFragment)
            }

            addOnFailureListener {
                Log.i(TAG, "signOut fail")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HMS_REQUEST_CODE) {
            val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                // The sign-in is successful, and the user's HUAWEI ID information and ID token are obtained.
                val huaweiAccount = authHuaweiIdTask.result
                Log.i(TAG, "idToken:${huaweiAccount.idToken}")
                nav_host_fragment.findNavController()
                    .navigate(R.id.action_FirstFragment_to_SecondFragment)
            } else {
                // The sign-in failed. No processing is required. Logs are recorded to facilitate fault locating.
                authHuaweiIdTask.exception.printLog(TAG, "SignIn failed")
            }
        }
    }

    private fun authSilentSignIn() {
        val authParams =
            HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .createParams()
        service = HuaweiIdAuthManager.getService(this, authParams)
        service?.silentSignIn()?.apply {
            addOnSuccessListener { authHuaweiId ->
                Log.i(TAG, "User already logged, Display name: ${authHuaweiId.displayName}")
                nav_host_fragment.findNavController()
                    .navigate(R.id.action_FirstFragment_to_SecondFragment)
            }

            addOnFailureListener { exception ->
                exception.printLog(TAG, "Silent sign in fail")
            }
        }
    }
}