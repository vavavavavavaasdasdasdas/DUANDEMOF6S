SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF EXISTS (SELECT 1 FROM promotion_details GROUP BY promotion_id,product_id HAVING COUNT(*)>1)
    THROW 51008, 'Duplicate promotion/product links: resolve before Phase 8 migration.', 1;
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='UX_promotion_details_product_phase8' AND object_id=OBJECT_ID('promotion_details'))
    CREATE UNIQUE INDEX UX_promotion_details_product_phase8 ON promotion_details(promotion_id,product_id);
COMMIT TRANSACTION;
