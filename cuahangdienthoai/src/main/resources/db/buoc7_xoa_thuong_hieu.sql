-- BUOC 7: Xoa hoan toan chuc nang Thuong hieu
-- Chay sau khi da thay source code cua BUOC 7.

SET FOREIGN_KEY_CHECKS = 0;

SET @fk_name = (
    SELECT CONSTRAINT_NAME
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'san_pham'
      AND COLUMN_NAME = 'thuong_hieu_id'
      AND REFERENCED_TABLE_NAME = 'thuong_hieu'
    LIMIT 1
);

SET @sql = IF(
    @fk_name IS NULL,
    'SELECT 1',
    CONCAT('ALTER TABLE san_pham DROP FOREIGN KEY `', @fk_name, '`')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE san_pham DROP COLUMN thuong_hieu_id;
DROP TABLE IF EXISTS thuong_hieu;

SET FOREIGN_KEY_CHECKS = 1;
