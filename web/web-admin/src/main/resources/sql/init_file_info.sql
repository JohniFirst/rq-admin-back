-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS file_info;

-- 创建 file_info 表
CREATE TABLE file_info (
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

-- 创建索引
CREATE INDEX idx_file_info_upload_time ON file_info(upload_time);
CREATE INDEX idx_file_info_upload_user_id ON file_info(upload_user_id);
CREATE INDEX idx_file_info_file_type ON file_info(file_type);
CREATE INDEX idx_file_info_is_enabled ON file_info(is_enabled);
CREATE INDEX idx_file_info_filename ON file_info(filename);

-- 插入测试数据（可选）
-- INSERT INTO file_info (id, original_filename, filename, file_path, file_size, file_type, upload_time, create_time, is_enabled)
-- VALUES (
--     '550e8400-e29b-41d4-a716-446655440000',
--     'test.txt',
--     '550e8400-e29b-41d4-a716-446655440001.txt',
--     '2024/01/15/550e8400-e29b-41d4-a716-446655440001.txt',
--     1024,
--     'text/plain',
--     NOW(),
--     NOW(),
--     'ENABLED'
-- ); 