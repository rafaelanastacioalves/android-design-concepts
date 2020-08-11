package com.example.rafaelanastacioalves.moby.repository.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rafaelanastacioalves.design.concepts.domain.entities.MainEntity
import com.rafaelanastacioalves.design.concepts.repository.database.AppDataBase
import com.rafaelanastacioalves.design.concepts.repository.database.DAO
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


@RunWith(AndroidJUnit4::class)
class AppDataBaseTest {

    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule();

    private val context: Context by lazy {
        ApplicationProvider.getApplicationContext() as Context
    }

    private val testedDAO: DAO by lazy {
        AppDataBase.getInstance().appDAO()
    }

    @Test
    fun when_savingMainEntity_Should_ReturnMainEntity() {

        AppDataBase.setupAtApplicationStartup(context)
        testedDAO.delteAllMainEntities()

        val mainEntityList: List<MainEntity> = Arrays.asList(
                MainEntity(
                        "1",
                        "Title",
                        "RS12,00",
                        "Real",
                        "www.google.com"
                ),
                MainEntity("2",
                        "Title2",
                        "RS123,00",
                        "Real",
                        "www.google.com"))
        
        testedDAO.saveMainEntityList(mainEntityList)

        val restoredMainEntityList = testedDAO.getMainEntityList()
        val restoredFirstMainEntity = restoredMainEntityList.get(0)

        assertThat(restoredMainEntityList.size, CoreMatchers.`is`(2))
        assertThat(restoredFirstMainEntity.title, StringContains("Title"))
        assertThat(restoredFirstMainEntity.price, StringContains("RS12,00"))
        assertThat(restoredFirstMainEntity.priceCurrency, StringContains("Real"))
        assertThat(restoredFirstMainEntity.imageUrl, StringContains("www.google.com"))

    }

    @Test
    fun when_ThereIsNoMainEntity_Should_Return_EmptyList() {
        AppDataBase.setupAtApplicationStartup(context)
        val testedDAO: DAO = testedDAO

        testedDAO.delteAllMainEntities()

        val restoredMainEntityList = testedDAO.getMainEntityList()

        assertThat(restoredMainEntityList.size, CoreMatchers.`is`(0))
    }

}