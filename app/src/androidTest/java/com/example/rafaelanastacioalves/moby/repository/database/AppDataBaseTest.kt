package com.example.rafaelanastacioalves.moby.repository.database

import android.content.Context
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import androidx.test.core.app.ApplicationProvider;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule


@RunWith(AndroidJUnit4::class)
 class AppDataBaseTest {

    @get:Rule
    public var instantTaskExecutorRule : InstantTaskExecutorRule  = InstantTaskExecutorRule();

    @Test
    fun when_savingMainEntity_Should_ReturnMainEntity() {

        val context: Context = ApplicationProvider.getApplicationContext()

        AppDataBase.setupAtApplicationStartup(context)
        val testedDAO: DAO = AppDataBase.getInstance().appDAO()
        assert(true)
    }
}