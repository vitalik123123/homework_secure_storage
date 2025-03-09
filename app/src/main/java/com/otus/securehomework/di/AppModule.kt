package com.otus.securehomework.di

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import com.otus.securehomework.crypto.Keys
import com.otus.securehomework.crypto.KeysLess23Version
import com.otus.securehomework.crypto.KeysMore23Version
import com.otus.securehomework.crypto.Security
import com.otus.securehomework.crypto.biometric.Biometric
import com.otus.securehomework.crypto.biometric.BiometricNoneImpl
import com.otus.securehomework.crypto.biometric.BiometricStrongImpl
import com.otus.securehomework.crypto.biometric.BiometricWeakImpl
import com.otus.securehomework.data.repository.AuthRepository
import com.otus.securehomework.data.repository.UserRepository
import com.otus.securehomework.data.source.local.UserPreferences
import com.otus.securehomework.data.source.network.AuthApi
import com.otus.securehomework.data.source.network.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRemoteDataSource(
        userPreferences: UserPreferences
    ): RemoteDataSource {
        return RemoteDataSource(userPreferences)
    }

    @Provides
    fun provideAuthApi(
        remoteDataSource: RemoteDataSource,
    ): AuthApi {
        return remoteDataSource.buildApi(AuthApi::class.java)
    }

    @Provides
    fun provideUserApi(
        remoteDataSource: RemoteDataSource,
    ): UserApi {
        return remoteDataSource.buildApi(UserApi::class.java)
    }

    @Singleton
    @Provides
    fun provideUserPreferences(
        @ApplicationContext context: Context,
        security: Security
    ): UserPreferences {
        return UserPreferences(context, security)
    }

    @Provides
    fun provideAuthRepository(
        authApi: AuthApi,
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepository(authApi, userPreferences)
    }

    @Provides
    fun provideUserRepository(
        userApi: UserApi
    ): UserRepository {
        return UserRepository(userApi)
    }

    @Singleton
    @Provides
    fun provideKeys(
        @ApplicationContext context: Context,
    ): Keys {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeysMore23Version()
        } else {
            KeysLess23Version(context)
        }
    }


    @Singleton
    @Provides
    fun provideBiometric(
        @ApplicationContext context: Context,
    ): Biometric {
        return if (BIOMETRIC_SUCCESS == BiometricManager.from(context)
                .canAuthenticate(BIOMETRIC_STRONG) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        ) BiometricStrongImpl()
        else if (BIOMETRIC_SUCCESS == BiometricManager.from(context)
                .canAuthenticate(BIOMETRIC_WEAK)
        ) BiometricWeakImpl()
        else BiometricNoneImpl()
    }
}