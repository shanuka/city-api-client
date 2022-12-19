package com.example.cityapiclient

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import com.example.cityapiclient.data.local.CurrentUser
import com.example.cityapiclient.data.local.UserRepository
import com.example.cityapiclient.data.remote.apis.UserApiService
import com.example.sharedtest.data.remote.apis.UserResponseSuccess
import com.example.sharedtest.data.remote.apis.createClient
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(ExperimentalCoroutinesApi::class)
//@ExtendWith(MockKExtension::class)
class UserRepositoryTest {

    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val testDataStore = PreferenceDataStoreFactory.create(
        produceFile = { testContext.preferencesDataStoreFile("test_datastore") }
    )

    private val userApiService = spyk<UserApiService>()
    private val userRepo = UserRepository(testDataStore, userApiService)

    @BeforeEach
    fun clearDatastore() = runTest {
        userRepo.clear()
    }

    @Test
    fun isOnboardingComplete_False() = runTest {
        println("Instance: $testDataStore")
        userRepo.setLastOnboardingScreen(1)

        val userPreferences = userRepo.userPreferencesFlow.first()
        Assertions.assertEquals(1, userPreferences.lastOnboardingScreen)
        Assertions.assertEquals(false, userPreferences.isOnboardingComplete)
    }

    @Test
    fun isOnboardingComplete_True() = runTest {
        println("Instance: $testDataStore")
        userRepo.setLastOnboardingScreen(2)

        val userPreferences = userRepo.userPreferencesFlow.first()
        Assertions.assertEquals(2, userPreferences.lastOnboardingScreen)
        Assertions.assertEquals(true, userPreferences.isOnboardingComplete)
    }

    @Test
    fun getUnknownSignIn() = runTest {
        val currentUserFlow = userRepo.currentUserFlow.first()
        println("UserId: $currentUserFlow")
        Assertions.assertInstanceOf(CurrentUser.UnknownSignIn::class.java, currentUserFlow)
    }

    @Test
    fun getSignedInUser() = runTest {

        every { userApiService.client() } returns createClient(
            UserResponseSuccess, HttpStatusCode.OK
        )

        userRepo.setUserId(1)

        val currentUserFlow = userRepo.currentUserFlow.first()
        println("UserId: $currentUserFlow")
        Assertions.assertInstanceOf(CurrentUser.SignedInUser::class.java, currentUserFlow)
    }

}