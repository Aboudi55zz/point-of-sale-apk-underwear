package com.example.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "brands")
data class BrandEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val notes: String = "",
    val accentColorHex: Long = 0xFF0A5C52,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "product_models",
    foreignKeys = [
        ForeignKey(
            entity = BrandEntity::class,
            parentColumns = ["id"],
            childColumns = ["brandId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("brandId")]
)
data class ProductModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brandId: Long,
    val modelCode: String,
    val name: String,
    val category: String,
    val fabricInfo: String = ""
)

@Entity(
    tableName = "piece_variants",
    foreignKeys = [
        ForeignKey(
            entity = ProductModelEntity::class,
            parentColumns = ["id"],
            childColumns = ["modelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("modelId")]
)
data class PieceVariantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val modelId: Long,
    val color: String,
    val sizeLabel: String,
    val priceSyp: Long,
    val stockQty: Int = 20,
    val minStockAlert: Int = 5,
    val skuBarcode: String = ""
)

@Entity(tableName = "sale_invoices")
data class SaleInvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val customerName: String = "",
    val subtotalSyp: Long,
    val discountSyp: Long = 0,
    val totalSyp: Long,
    val paidSyp: Long,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "sale_line_items",
    foreignKeys = [
        ForeignKey(
            entity = SaleInvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PieceVariantEntity::class,
            parentColumns = ["id"],
            childColumns = ["pieceId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("invoiceId"), Index("pieceId")]
)
data class SaleLineItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val pieceId: Long?,
    val brandName: String,
    val modelCode: String,
    val modelName: String,
    val color: String,
    val sizeLabel: String,
    val unitPriceSyp: Long,
    val quantity: Int,
    val lineTotalSyp: Long
)

data class ModelWithDetails(
    @Embedded val model: ProductModelEntity,
    @Relation(
        parentColumn = "brandId",
        entityColumn = "id"
    )
    val brand: BrandEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "modelId"
    )
    val pieces: List<PieceVariantEntity>
)

data class InvoiceWithItems(
    @Embedded val invoice: SaleInvoiceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val items: List<SaleLineItemEntity>
)
