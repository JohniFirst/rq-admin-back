-- 更新 file_info 表以支持 UUID
-- 如果表不存在，创建表
CREATE TABLE IF NOT EXISTS file_info (
    id VARCHAR(36) PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    filename VARCHAR(255) NOT NULL UNIQUE,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(100),
    upload_time TIMESTAMP NOT NULL,
    upload_user_id BIGINT,
    description TEXT,
    is_enabled VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP
);

-- 如果表已存在但ID字段不是VARCHAR(36)，需要执行以下操作：
-- 1. 备份现有数据
-- 2. 删除表
-- 3. 重新创建表
-- 4. 恢复数据（如果需要）

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_file_info_upload_time ON file_info(upload_time);
CREATE INDEX IF NOT EXISTS idx_file_info_upload_user_id ON file_info(upload_user_id);
CREATE INDEX IF NOT EXISTS idx_file_info_file_type ON file_info(file_type);
CREATE INDEX IF NOT EXISTS idx_file_info_is_enabled ON file_info(is_enabled); 