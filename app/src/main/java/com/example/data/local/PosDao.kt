package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PosDao {
    @Query("SELECT * FROM brands ORDER BY id ASC")
    fun observeBrands(): Flow<List<BrandEntity>>

    @Transaction
    @Query("SELECT * FROM product_models ORDER BY brandId ASC, modelCode ASC, id ASC")
    fun observeModelsWithDetails(): Flow<List<ModelWithDetails>>

    @Transaction
    @Query("SELECT * FROM sale_invoices ORDER BY createdAt DESC")
    fun observeInvoicesWithItems(): Flow<List<InvoiceWithItems>>

    @Query("SELECT COUNT(*) FROM brands")
    suspend fun countBrands(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brand: BrandEntity): Long

    @Update
    suspend fun updateBrand(brand: BrandEntity)

    @Delete
    suspend fun deleteBrand(brand: BrandEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: ProductModelEntity): Long

    @Update
    suspend fun updateModel(model: ProductModelEntity)

    @Delete
    suspend fun deleteModel(model: ProductModelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPiece(piece: PieceVariantEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPieces(pieces: List<PieceVariantEntity>)

    @Update
    suspend fun updatePiece(piece: PieceVariantEntity)

    @Delete
    suspend fun deletePiece(piece: PieceVariantEntity)

    @Query("SELECT * FROM piece_variants WHERE id = :pieceId")
    suspend fun getPieceById(pieceId: Long): PieceVariantEntity?

    @Query("UPDATE piece_variants SET stockQty = MAX(0, stockQty + :delta) WHERE id = :pieceId")
    suspend fun adjustPieceStock(pieceId: Long, delta: Int)

    @Insert
    suspend fun insertInvoice(invoice: SaleInvoiceEntity): Long

    @Insert
    suspend fun insertLineItems(items: List<SaleLineItemEntity>)

    @Delete
    suspend fun deleteInvoice(invoice: SaleInvoiceEntity)

    @Query("SELECT * FROM sale_line_items WHERE invoiceId = :invoiceId")
    suspend fun getLineItemsForInvoice(invoiceId: Long): List<SaleLineItemEntity>

    @Query("DELETE FROM brands")
    suspend fun clearAllBrands()

    @Transaction
    suspend fun completeSaleTransaction(
        invoice: SaleInvoiceEntity,
        lineItems: List<SaleLineItemEntity>
    ): Long {
        val invoiceId = insertInvoice(invoice)
        val itemsWithId = lineItems.map { it.copy(invoiceId = invoiceId) }
        insertLineItems(itemsWithId)
        for (item in lineItems) {
            item.pieceId?.let { pid ->
                adjustPieceStock(pid, -item.quantity)
            }
        }
        return invoiceId
    }

    @Transaction
    suspend fun deleteInvoiceAndOptionallyRestoreStock(
        invoice: SaleInvoiceEntity,
        restoreStock: Boolean
    ) {
        if (restoreStock) {
            val items = getLineItemsForInvoice(invoice.id)
            for (item in items) {
                item.pieceId?.let { pid ->
                    adjustPieceStock(pid, item.quantity)
                }
            }
        }
        deleteInvoice(invoice)
    }
}
