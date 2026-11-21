package com.example.data.local

object SeedCatalogData {
    suspend fun populate(dao: PosDao) {
        val familyId = dao.insertBrand(
            BrandEntity(
                name = "فاميلي (Family)",
                notes = "شركة فاميلي للألبسة الداخلية - قائمة 17/10/2026",
                accentColorHex = 0xFF1F5E78
            )
        )
        insertModels(dao, familyId, SeedFamilyDuciel.familyModels)

        val ducielId = dao.insertBrand(
            BrandEntity(
                name = "دوسيل (Duciel)",
                notes = "شركة دوسيل - سادة ومكلف وسنكل - قائمة 2026/11/9",
                accentColorHex = 0xFF08514A
            )
        )
        insertModels(dao, ducielId, SeedFamilyDuciel.ducielModels)

        val abcId = dao.insertBrand(
            BrandEntity(
                name = "ABC",
                notes = "شركة ABC للألبسة الداخلية القطنية (النسائية، الرجالية، الولادية، البناتية، البيبي)",
                accentColorHex = 0xFFB87B1E
            )
        )
        insertModels(dao, abcId, SeedAbc.abcModels)
    }

    private suspend fun insertModels(dao: PosDao, brandId: Long, models: List<SeedModel>) {
        for (sm in models) {
            val modelId = dao.insertModel(
                ProductModelEntity(
                    brandId = brandId,
                    modelCode = sm.modelCode,
                    name = sm.name,
                    category = sm.category,
                    fabricInfo = sm.fabricInfo
                )
            )
            val pieceEntities = sm.pieces.map { p ->
                PieceVariantEntity(
                    modelId = modelId,
                    color = p.color,
                    sizeLabel = p.sizeLabel,
                    priceSyp = p.priceSyp,
                    stockQty = p.stockQty,
                    minStockAlert = 5
                )
            }
            dao.insertPieces(pieceEntities)
        }
    }
}
