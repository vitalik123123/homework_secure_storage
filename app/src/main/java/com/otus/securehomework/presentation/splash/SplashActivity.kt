package com.otus.securehomework.presentation.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.otus.securehomework.R
import com.otus.securehomework.crypto.biometric.Biometric
import com.otus.securehomework.data.source.local.UserPreferences
import com.otus.securehomework.presentation.auth.AuthActivity
import com.otus.securehomework.presentation.home.HomeActivity
import com.otus.securehomework.presentation.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var biometric: Biometric

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        userPreferences.accessToken.asLiveData().observe(this, Observer {
            if (it == null) {
                startNewActivity(AuthActivity::class.java)
            } else {
                when (biometric) {
                    is Biometric.BiometricWeak -> {
                        biometric.getManager(this) {
                            startNewActivity(HomeActivity::class.java)
                        }
                    }

                    is Biometric.BiometricStrong -> {
                        biometric.getManager(this) {
                            startNewActivity(HomeActivity::class.java)
                        }
                    }

                    is Biometric.BiometricNone -> {
                        userPreferences.accessToken.asLiveData().observe(this, Observer {
                            val activity = if (it == null) {
                                AuthActivity::class.java
                            } else {
                                HomeActivity::class.java
                            }
                            startNewActivity(activity)
                        })
                    }
                }
            }
        })


    }
}