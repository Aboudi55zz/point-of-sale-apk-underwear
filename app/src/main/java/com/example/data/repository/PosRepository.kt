package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.local.BrandEntity
import com.example.data.local.InvoiceWithItems
import com.example.data.local.ModelWithDetails
import com.example.data.local.PieceVariantEntity
import com.example.data.local.PosDao
import com.example.data.local.ProductModelEntity
import com.example.data.local.SaleInvoiceEntity
import com.example.data.local.SaleLineItemEntity
import com.example.data.local.SeedCatalogData
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.ThemeMode
import com.example.util.CurrencyScale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.posDataStore: DataStore<Preferences> by preferencesDataStore(name = "pos_settings")

class PosRepository(
    private val dao: PosDao,
    private val context: Context
) {
    private object PrefKeys {
        val CURRENCY_SCALE = stringPreferencesKey("currency_scale")
        val STORE_NAME = stringPreferencesKey("store_name")
        val APP_PALETTE = stringPreferencesKey("app_palette")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val brands: Flow<List<BrandEntity>> = dao.observeBrands()
    val modelsWithDetails: Flow<List<ModelWithDetails>> = dao.observeModelsWithDetails()
    val invoicesWithItems: Flow<List<InvoiceWithItems>> = dao.observeInvoicesWithItems()

    val currencyScale: Flow<CurrencyScale> = context.posDataStore.data.map { prefs ->
        val raw = prefs[PrefKeys.CURRENCY_SCALE]
        CurrencyScale.entries.firstOrNull { it.name == raw } ?: CurrencyScale.OLD_SYP
    }

    val storeName: Flow<String> = context.posDataStore.data.map { prefs ->
        prefs[PrefKeys.STORE_NAME]?.takeIf { it.isNotBlank() } ?: "متجر الألبسة الداخلية"
    }

    val appPalette: Flow<AppColorPalette> = context.posDataStore.data.map { prefs ->
        val raw = prefs[PrefKeys.APP_PALETTE]
        AppColorPalette.entries.firstOrNull { it.name == raw } ?: AppColorPalette.EMERALD_GOLD
    }

    val themeMode: Flow<ThemeMode> = context.posDataStore.data.map { prefs ->
        val raw = prefs[PrefKeys.THEME_MODE]
        ThemeMode.entries.firstOrNull { it.name == raw } ?: ThemeMode.SYSTEM
    }

    suspend fun ensureSeededIfEmpty() {
        if (dao.countBrands() == 0) {
            SeedCatalogData.populate(dao)
        }
    }

    suspend fun resetToDefaultCatalog() {
        dao.clearAllBrands()
        SeedCatalogData.populate(dao)
    }

    suspend fun setCurrencyScale(scale: CurrencyScale) {
        context.posDataStore.edit { prefs ->
            prefs[PrefKeys.CURRENCY_SCALE] = scale.name
        }
    }

    suspend fun setStoreName(name: String) {
        context.posDataStore.edit { prefs ->
            prefs[PrefKeys.STORE_NAME] = name.trim()
        }
    }

    suspend fun setAppPalette(palette: AppColorPalette) {
        context.posDataStore.edit { prefs ->
            prefs[PrefKeys.APP_PALETTE] = palette.name
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.posDataStore.edit { prefs ->
            prefs[PrefKeys.THEME_MODE] = mode.name
        }
    }

    // Brand CRUD
    suspend fun addBrand(name: String, notes: String, accentColorHex: Long): Long =
        dao.insertBrand(BrandEntity(name = name.trim(), notes = notes.trim(), accentColorHex = accentColorHex))

    suspend fun updateBrand(brand: BrandEntity) = dao.updateBrand(brand)

    suspend fun deleteBrand(brand: BrandEntity) = dao.deleteBrand(brand)

    // Model CRUD
    suspend fun addModel(
        brandId: Long,
        modelCode: String,
        name: String,
        category: String,
        fabricInfo: String
    ): Long = dao.insertModel(
        ProductModelEntity(
            brandId = brandId,
            modelCode = modelCode.trim(),
            name = name.trim(),
            category = category.trim(),
            fabricInfo = fabricInfo.trim()
        )
    )

    suspend fun updateModel(model: ProductModelEntity) = dao.updateModel(model)

    suspend fun deleteModel(model: ProductModelEntity) = dao.deleteModel(model)

    // Piece CRUD
    suspend fun addPiece(
        modelId: Long,
        color: String,
        sizeLabel: String,
        priceSyp: Long,
        stockQty: Int,
        minStockAlert: Int,
        skuBarcode: String
    ): Long = dao.insertPiece(
        PieceVariantEntity(
            modelId = modelId,
            color = color.trim(),
            sizeLabel = sizeLabel.trim(),
            priceSyp = priceSyp.coerceAtLeast(0L),
            stockQty = stockQty.coerceAtLeast(0),
            minStockAlert = minStockAlert.coerceAtLeast(0),
            skuBarcode = skuBarcode.trim()
        )
    )

    suspend fun updatePiece(piece: PieceVariantEntity) = dao.updatePiece(piece)

    suspend fun deletePiece(piece: PieceVariantEntity) = dao.deletePiece(piece)

    suspend fun adjustStock(pieceId: Long, delta: Int) = dao.adjustPieceStock(pieceId, delta)

    // Checkout & Invoices
    suspend fun completeSale(
        invoice: SaleInvoiceEntity,
        lineItems: List<SaleLineItemEntity>
    ): Long = dao.completeSaleTransaction(invoice, lineItems)

    suspend fun deleteInvoice(invoice: SaleInvoiceEntity, restoreStock: Boolean) =
        dao.deleteInvoiceAndOptionallyRestoreStock(invoice, restoreStock)
}
