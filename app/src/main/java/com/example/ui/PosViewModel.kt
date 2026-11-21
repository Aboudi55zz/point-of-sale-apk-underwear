package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.BrandEntity
import com.example.data.local.InvoiceWithItems
import com.example.data.local.ModelWithDetails
import com.example.data.local.PieceVariantEntity
import com.example.data.local.ProductModelEntity
import com.example.data.local.SaleInvoiceEntity
import com.example.data.local.SaleLineItemEntity
import com.example.data.repository.PosRepository
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.ThemeMode
import com.example.util.CurrencyScale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

data class CartItem(
    val pieceId: Long,
    val brandName: String,
    val modelCode: String,
    val modelName: String,
    val color: String,
    val sizeLabel: String,
    val unitPriceSyp: Long,
    val availableStock: Int,
    val quantity: Int
) {
    val lineTotalSyp: Long get() = unitPriceSyp * quantity
}

data class PosFilterState(
    val searchQuery: String = "",
    val selectedBrandId: Long? = null,
    val selectedCategory: String = "الكل"
)

data class InventoryFilterState(
    val searchQuery: String = "",
    val selectedBrandId: Long? = null,
    val selectedCategory: String = "الكل",
    val lowStockOnly: Boolean = false
)

class PosViewModel(private val repository: PosRepository) : ViewModel() {

    val brands: StateFlow<List<BrandEntity>> = repository.brands
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allModels: StateFlow<List<ModelWithDetails>> = repository.modelsWithDetails
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<InvoiceWithItems>> = repository.invoicesWithItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currencyScale: StateFlow<CurrencyScale> = repository.currencyScale
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CurrencyScale.OLD_SYP)

    val storeName: StateFlow<String> = repository.storeName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "متجر الألبسة الداخلية")

    val appPalette: StateFlow<AppColorPalette> = repository.appPalette
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppColorPalette.EMERALD_GOLD)

    val themeMode: StateFlow<ThemeMode> = repository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    private val _posFilter = MutableStateFlow(PosFilterState())
    val posFilter: StateFlow<PosFilterState> = _posFilter.asStateFlow()

    private val _invFilter = MutableStateFlow(InventoryFilterState())
    val invFilter: StateFlow<InventoryFilterState> = _invFilter.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _discountSyp = MutableStateFlow(0L)
    val discountSyp: StateFlow<Long> = _discountSyp.asStateFlow()

    private val _lastCompletedInvoice = MutableStateFlow<InvoiceWithItems?>(null)
    val lastCompletedInvoice: StateFlow<InvoiceWithItems?> = _lastCompletedInvoice.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val filteredPosModels: StateFlow<List<ModelWithDetails>> = combine(
        allModels,
        _posFilter
    ) { models, filter ->
        filterModels(models, filter.searchQuery, filter.selectedBrandId, filter.selectedCategory, false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredInventoryModels: StateFlow<List<ModelWithDetails>> = combine(
        allModels,
        _invFilter
    ) { models, filter ->
        filterModels(models, filter.searchQuery, filter.selectedBrandId, filter.selectedCategory, filter.lowStockOnly)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureSeededIfEmpty()
        }
    }

    private fun filterModels(
        models: List<ModelWithDetails>,
        query: String,
        brandId: Long?,
        category: String,
        lowStockOnly: Boolean
    ): List<ModelWithDetails> {
        val q = query.trim().lowercase()
        return models.filter { entry ->
            val matchesBrand = brandId == null || entry.brand.id == brandId
            val matchesCategory = category == "الكل" || entry.model.category == category
            val matchesLowStock = !lowStockOnly || entry.pieces.any { it.stockQty <= it.minStockAlert }
            val matchesQuery = if (q.isEmpty()) true else {
                entry.model.modelCode.lowercase().contains(q) ||
                    entry.model.name.lowercase().contains(q) ||
                    entry.model.fabricInfo.lowercase().contains(q) ||
                    entry.brand.name.lowercase().contains(q) ||
                    entry.pieces.any {
                        it.color.lowercase().contains(q) ||
                            it.sizeLabel.lowercase().contains(q) ||
                            it.skuBarcode.lowercase().contains(q)
                    }
            }
            matchesBrand && matchesCategory && matchesLowStock && matchesQuery
        }
    }

    // Filter updates
    fun updatePosSearch(query: String) {
        _posFilter.update { it.copy(searchQuery = query) }
    }

    fun updatePosBrand(brandId: Long?) {
        _posFilter.update { it.copy(selectedBrandId = brandId) }
    }

    fun updatePosCategory(category: String) {
        _posFilter.update { it.copy(selectedCategory = category) }
    }

    fun updateInvSearch(query: String) {
        _invFilter.update { it.copy(searchQuery = query) }
    }

    fun updateInvBrand(brandId: Long?) {
        _invFilter.update { it.copy(selectedBrandId = brandId) }
    }

    fun updateInvCategory(category: String) {
        _invFilter.update { it.copy(selectedCategory = category) }
    }

    fun toggleInvLowStock() {
        _invFilter.update { it.copy(lowStockOnly = !it.lowStockOnly) }
    }

    // Cart Actions
    fun addToCart(modelDetails: ModelWithDetails, piece: PieceVariantEntity) {
        if (piece.stockQty <= 0) {
            _snackbarMessage.value = "القطعة غير متوفرة في المخزون حالياً (${modelDetails.model.name})"
            return
        }
        val currentList = _cartItems.value
        val existing = currentList.find { it.pieceId == piece.id }
        if (existing != null) {
            if (existing.quantity >= piece.stockQty) {
                _snackbarMessage.value = "الكمية المطلوبة وصلت للحد المتاح في المخزون (${piece.stockQty})"
                return
            }
            _cartItems.value = currentList.map {
                if (it.pieceId == piece.id) it.copy(quantity = it.quantity + 1, availableStock = piece.stockQty)
                else it
            }
        } else {
            _cartItems.value = currentList + CartItem(
                pieceId = piece.id,
                brandName = modelDetails.brand.name,
                modelCode = modelDetails.model.modelCode,
                modelName = modelDetails.model.name,
                color = piece.color,
                sizeLabel = piece.sizeLabel,
                unitPriceSyp = piece.priceSyp,
                availableStock = piece.stockQty,
                quantity = 1
            )
        }
    }

    fun updateCartQuantity(pieceId: Long, delta: Int) {
        _cartItems.update { list ->
            list.mapNotNull { item ->
                if (item.pieceId != pieceId) item
                else {
                    val nextQty = item.quantity + delta
                    when {
                        nextQty <= 0 -> null
                        nextQty > item.availableStock -> {
                            _snackbarMessage.value = "المتوفر في المخزون ${item.availableStock} قطعة فقط"
                            item
                        }
                        else -> item.copy(quantity = nextQty)
                    }
                }
            }
        }
    }

    fun removeFromCart(pieceId: Long) {
        _cartItems.update { list -> list.filterNot { it.pieceId == pieceId } }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _discountSyp.value = 0L
    }

    fun setDiscount(discountOldSyp: Long) {
        _discountSyp.value = discountOldSyp.coerceAtLeast(0L)
    }

    fun completeCheckout(customerName: String, paidSyp: Long?, note: String) {
        val currentCart = _cartItems.value
        if (currentCart.isEmpty()) return

        val subtotal = currentCart.sumOf { it.lineTotalSyp }
        val discount = _discountSyp.value.coerceIn(0L, subtotal)
        val total = max(0L, subtotal - discount)
        val paid = paidSyp ?: total

        viewModelScope.launch {
            val invoiceNo = "INV-${(System.currentTimeMillis() / 1000) % 1000000}"
            val invoice = SaleInvoiceEntity(
                invoiceNumber = invoiceNo,
                customerName = customerName.trim().ifBlank { "زبون نقدي" },
                subtotalSyp = subtotal,
                discountSyp = discount,
                totalSyp = total,
                paidSyp = paid,
                note = note.trim(),
                createdAt = System.currentTimeMillis()
            )
            val lineItems = currentCart.map { item ->
                SaleLineItemEntity(
                    invoiceId = 0L,
                    pieceId = item.pieceId,
                    brandName = item.brandName,
                    modelCode = item.modelCode,
                    modelName = item.modelName,
                    color = item.color,
                    sizeLabel = item.sizeLabel,
                    unitPriceSyp = item.unitPriceSyp,
                    quantity = item.quantity,
                    lineTotalSyp = item.lineTotalSyp
                )
            }
            val newId = repository.completeSale(invoice, lineItems)
            _lastCompletedInvoice.value = InvoiceWithItems(
                invoice = invoice.copy(id = newId),
                items = lineItems.map { it.copy(invoiceId = newId) }
            )
            clearCart()
        }
    }

    fun dismissCompletedInvoice() {
        _lastCompletedInvoice.value = null
    }

    // Brand CRUD
    fun addBrand(name: String, notes: String, accentColorHex: Long) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addBrand(name, notes, accentColorHex)
            _snackbarMessage.value = "تمت إضافة الماركة: ${name.trim()}"
        }
    }

    fun updateBrand(brand: BrandEntity) {
        if (brand.name.isBlank()) return
        viewModelScope.launch {
            repository.updateBrand(brand.copy(name = brand.name.trim(), notes = brand.notes.trim()))
            _snackbarMessage.value = "تم تعديل الماركة"
        }
    }

    fun deleteBrand(brand: BrandEntity) {
        viewModelScope.launch {
            repository.deleteBrand(brand)
            _invFilter.update { if (it.selectedBrandId == brand.id) it.copy(selectedBrandId = null) else it }
            _posFilter.update { if (it.selectedBrandId == brand.id) it.copy(selectedBrandId = null) else it }
            _snackbarMessage.value = "تم حذف الماركة: ${brand.name}"
        }
    }

    // Model CRUD
    fun addModel(
        brandId: Long,
        modelCode: String,
        name: String,
        category: String,
        fabricInfo: String,
        initialPieceColor: String?,
        initialPieceSize: String?,
        initialPriceSyp: Long?,
        initialStockQty: Int?
    ) {
        if (name.isBlank() || modelCode.isBlank()) return
        viewModelScope.launch {
            val modelId = repository.addModel(brandId, modelCode, name, category, fabricInfo)
            if (initialPriceSyp != null && !initialPieceColor.isNullOrBlank()) {
                repository.addPiece(
                    modelId = modelId,
                    color = initialPieceColor,
                    sizeLabel = initialPieceSize.orEmpty().ifBlank { "موحد" },
                    priceSyp = initialPriceSyp,
                    stockQty = initialStockQty ?: 20,
                    minStockAlert = 5,
                    skuBarcode = ""
                )
            }
            _snackbarMessage.value = "تمت إضافة الموديل #$modelCode"
        }
    }

    fun updateModel(model: ProductModelEntity) {
        if (model.name.isBlank() || model.modelCode.isBlank()) return
        viewModelScope.launch {
            repository.updateModel(
                model.copy(
                    modelCode = model.modelCode.trim(),
                    name = model.name.trim(),
                    category = model.category.trim(),
                    fabricInfo = model.fabricInfo.trim()
                )
            )
            _snackbarMessage.value = "تم تحديث الموديل #${model.modelCode}"
        }
    }

    fun deleteModel(model: ProductModelEntity) {
        viewModelScope.launch {
            repository.deleteModel(model)
            _snackbarMessage.value = "تم حذف الموديل #${model.modelCode}"
        }
    }

    // Piece CRUD
    fun addPiece(
        modelId: Long,
        color: String,
        sizeLabel: String,
        priceSyp: Long,
        stockQty: Int,
        minStockAlert: Int,
        skuBarcode: String
    ) {
        viewModelScope.launch {
            repository.addPiece(
                modelId = modelId,
                color = color.ifBlank { "متعدد" },
                sizeLabel = sizeLabel.ifBlank { "موحد" },
                priceSyp = priceSyp,
                stockQty = stockQty,
                minStockAlert = minStockAlert,
                skuBarcode = skuBarcode
            )
            _snackbarMessage.value = "تمت إضافة القطعة للمخزون"
        }
    }

    fun updatePiece(piece: PieceVariantEntity) {
        viewModelScope.launch {
            repository.updatePiece(
                piece.copy(
                    color = piece.color.trim().ifBlank { "متعدد" },
                    sizeLabel = piece.sizeLabel.trim().ifBlank { "موحد" },
                    priceSyp = piece.priceSyp.coerceAtLeast(0L),
                    stockQty = piece.stockQty.coerceAtLeast(0)
                )
            )
            // Keep cart unit price in sync if present
            _cartItems.update { list ->
                list.map { item ->
                    if (item.pieceId == piece.id) {
                        item.copy(
                            color = piece.color,
                            sizeLabel = piece.sizeLabel,
                            unitPriceSyp = piece.priceSyp,
                            availableStock = piece.stockQty
                        )
                    } else item
                }
            }
            _snackbarMessage.value = "تم تعديل بيانات القطعة"
        }
    }

    fun deletePiece(piece: PieceVariantEntity) {
        viewModelScope.launch {
            repository.deletePiece(piece)
            removeFromCart(piece.id)
            _snackbarMessage.value = "تم حذف القطعة"
        }
    }

    fun adjustPieceStock(pieceId: Long, delta: Int) {
        viewModelScope.launch {
            repository.adjustStock(pieceId, delta)
        }
    }

    // Invoices
    fun deleteInvoice(invoice: SaleInvoiceEntity, restoreStock: Boolean) {
        viewModelScope.launch {
            repository.deleteInvoice(invoice, restoreStock)
            _snackbarMessage.value = if (restoreStock) {
                "تم حذف الفاتورة وإعادة الكميات للمخزون"
            } else {
                "تم حذف الفاتورة"
            }
        }
    }

    // Settings
    fun toggleCurrencyScale() {
        val next = if (currencyScale.value == CurrencyScale.OLD_SYP) {
            CurrencyScale.NEW_SYP
        } else {
            CurrencyScale.OLD_SYP
        }
        setCurrencyScale(next)
    }

    fun setCurrencyScale(scale: CurrencyScale) {
        viewModelScope.launch {
            repository.setCurrencyScale(scale)
        }
    }

    fun setStoreName(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.setStoreName(name)
            _snackbarMessage.value = "تم حفظ اسم المتجر"
        }
    }

    fun setAppPalette(palette: AppColorPalette) {
        viewModelScope.launch {
            repository.setAppPalette(palette)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }

    fun resetDefaultCatalog() {
        viewModelScope.launch {
            repository.resetToDefaultCatalog()
            clearCart()
            _snackbarMessage.value = "تمت استعادة كتالوج فاميلي ودوسيل وABC الافتراضي"
        }
    }

    fun consumeSnackbar() {
        _snackbarMessage.value = null
    }

    companion object {
        val CATEGORIES = listOf("الكل", "رجالي", "نسائي", "ولادي", "بناتي", "بيبي", "عام")
        val MODEL_CATEGORIES = listOf("رجالي", "نسائي", "ولادي", "بناتي", "بيبي", "عام")

        fun factory(repository: PosRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PosViewModel(repository) as T
                }
            }
    }
}
