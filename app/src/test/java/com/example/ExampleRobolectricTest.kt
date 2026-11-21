package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.SaleInvoiceEntity
import com.example.data.local.SaleLineItemEntity
import com.example.data.local.SeedCatalogData
import com.example.util.CurrencyFormatter
import com.example.util.CurrencyScale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app_name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("نقطة بيع", appName)
    }

    @Test
    fun `currency formatter handles old and new SYP scales`() {
        assertEquals("45,000 ل.س", CurrencyFormatter.format(45000L, CurrencyScale.OLD_SYP))
        assertEquals("450 ل.س جديدة", CurrencyFormatter.format(45000L, CurrencyScale.NEW_SYP))
        assertEquals("475.5 ل.س جديدة", CurrencyFormatter.format(47550L, CurrencyScale.NEW_SYP))
        assertEquals(45000L, CurrencyFormatter.parseInputToOldSyp("450", CurrencyScale.NEW_SYP))
        assertEquals(45000L, CurrencyFormatter.parseInputToOldSyp("45000", CurrencyScale.OLD_SYP))
    }

    @Test
    fun `seed catalog populates all 3 brands and checkout decrements stock`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = db.posDao()
            SeedCatalogData.populate(dao)

            val brands = dao.observeBrands().first()
            assertEquals(3, brands.size)

            val models = dao.observeModelsWithDetails().first()
            assertTrue(models.size > 150)

            val targetModel = models.first()
            val targetPiece = targetModel.pieces.first()
            val initialStock = targetPiece.stockQty

            dao.completeSaleTransaction(
                invoice = SaleInvoiceEntity(
                    invoiceNumber = "INV-TEST",
                    customerName = "زبون اختبار",
                    subtotalSyp = targetPiece.priceSyp * 2,
                    discountSyp = 0L,
                    totalSyp = targetPiece.priceSyp * 2,
                    paidSyp = targetPiece.priceSyp * 2
                ),
                lineItems = listOf(
                    SaleLineItemEntity(
                        invoiceId = 0L,
                        pieceId = targetPiece.id,
                        brandName = targetModel.brand.name,
                        modelCode = targetModel.model.modelCode,
                        modelName = targetModel.model.name,
                        color = targetPiece.color,
                        sizeLabel = targetPiece.sizeLabel,
                        unitPriceSyp = targetPiece.priceSyp,
                        quantity = 2,
                        lineTotalSyp = targetPiece.priceSyp * 2
                    )
                )
            )

            val updatedPiece = dao.getPieceById(targetPiece.id)!!
            assertEquals(initialStock - 2, updatedPiece.stockQty)
        } finally {
            db.close()
        }
    }
}
