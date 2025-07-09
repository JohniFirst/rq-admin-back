# 文件上传功能说明

## 概述

本项目新增了完整的文件上传功能，包括单文件上传、多文件上传、文件下载、文件管理等接口。

## 功能特性

- ✅ 单文件上传
- ✅ 多文件批量上传
- ✅ 文件下载
- ✅ 文件信息管理
- ✅ 文件状态控制
- ✅ 文件删除（单个/批量）
- ✅ 文件列表查询
- ✅ 按日期自动分类存储
- ✅ 文件大小限制
- ✅ 唯一文件名生成
- ✅ 数据库记录管理

## 接口说明

### 1. 单文件上传

**接口地址：** `POST /api/file/upload`

**请求参数：**

- `file`: 要上传的文件（MultipartFile）

**响应示例：**

```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": {
    "id": 1,
    "originalFilename": "test.jpg",
    "filename": "uuid-filename.jpg",
    "fileSize": 1024,
    "fileType": "image/jpeg",
    "filePath": "2024/01/15/uuid-filename.jpg",
    "uploadTime": "2024-01-15T10:30:00"
  }
}
```

### 2. 多文件上传

**接口地址：** `POST /api/file/upload/multiple`

**请求参数：**

- `files`: 要上传的文件数组（MultipartFile[]）

**响应示例：**

```json
{
  "code": 200,
  "message": "所有文件上传成功",
  "data": [
    {
      "id": 1,
      "originalFilename": "file1.jpg",
      "filename": "uuid1-filename.jpg",
      "fileSize": 1024,
      "fileType": "image/jpeg",
      "filePath": "2024/01/15/uuid1-filename.jpg",
      "uploadTime": "2024-01-15T10:30:00"
    }
  ]
}
```

### 3. 文件下载

**接口地址：** `GET /api/file/download/{dateFolder}/{filename}`

**路径参数：**

- `dateFolder`: 日期文件夹（如：2024/01/15）
- `filename`: 文件名

### 4. 获取文件列表

**接口地址：** `GET /api/file/list`

**查询参数：**

- `pageNumber`: 页码（默认 0）
- `pageSize`: 每页大小（默认 10）

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "originalFilename": "test.jpg",
        "filename": "uuid-filename.jpg",
        "filePath": "2024/01/15/uuid-filename.jpg",
        "fileSize": 1024,
        "fileType": "image/jpeg",
        "uploadTime": "2024-01-15T10:30:00",
        "uploadUserId": null,
        "description": null,
        "isEnabled": "ENABLED",
        "createTime": "2024-01-15T10:30:00",
        "updateTime": null
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

### 5. 获取文件详情

**接口地址：** `GET /api/file/info/{fileId}`

**路径参数：**

- `fileId`: 文件 ID

### 6. 更新文件状态

**接口地址：** `PUT /api/file/status/{fileId}`

**路径参数：**

- `fileId`: 文件 ID

**查询参数：**

- `isEnabled`: 文件状态（ENABLED/DISABLED）

### 7. 更新文件描述

**接口地址：** `PUT /api/file/description/{fileId}`

**路径参数：**

- `fileId`: 文件 ID

**查询参数：**

- `description`: 文件描述

### 8. 删除文件

**接口地址：** `DELETE /api/file/info/{fileId}`

**路径参数：**

- `fileId`: 文件 ID

### 9. 批量删除文件

**接口地址：** `DELETE /api/file/batch`

**请求体：**

```json
[1, 2, 3]
```

### 10. 删除物理文件

**接口地址：** `DELETE /api/file/delete/{dateFolder}/{filename}`

**路径参数：**

- `dateFolder`: 日期文件夹
- `filename`: 文件名

## 配置说明

### 应用配置（application.yml）

```yaml
# 文件上传配置
file:
  upload:
    path: uploads # 文件存储根目录
    max-size: 10485760 # 最大文件大小（10MB）

spring:
  servlet:
    multipart:
      max-file-size: 100MB # Spring Boot文件大小限制
      max-request-size: 100MB # 请求大小限制
```

### 数据库表结构

文件信息存储在 `file_info` 表中：

```sql
CREATE TABLE file_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    filename VARCHAR(255) NOT NULL UNIQUE,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(100),
    upload_time DATETIME NOT NULL,
    upload_user_id BIGINT,
    description TEXT,
    is_enabled ENUM('ENABLED', 'DISABLED') NOT NULL DEFAULT 'ENABLED',
    create_time DATETIME NOT NULL,
    update_time DATETIME
);
```

## 文件存储结构

文件按日期自动分类存储：

```
uploads/
├── 2024/
│   ├── 01/
│   │   ├── 15/
│   │   │   ├── uuid1-filename1.jpg
│   │   │   └── uuid2-filename2.pdf
│   │   └── 16/
│   │       └── uuid3-filename3.docx
│   └── 02/
│       └── 01/
│           └── uuid4-filename4.xlsx
```

## 测试页面

访问 `http://localhost:8080/file-upload.html` 可以查看文件上传测试页面，包含：

- 单文件上传测试
- 多文件上传测试
- 文件列表查看
- 文件删除功能
- 文件下载功能

## 使用示例

### JavaScript 上传示例

```javascript
// 单文件上传
const formData = new FormData();
formData.append("file", fileInput.files[0]);

fetch("/api/file/upload", {
  method: "POST",
  body: formData,
})
  .then((response) => response.json())
  .then((result) => {
    console.log("上传成功:", result);
  });

// 多文件上传
const formData = new FormData();
for (let i = 0; i < fileInput.files.length; i++) {
  formData.append("files", fileInput.files[i]);
}

fetch("/api/file/upload/multiple", {
  method: "POST",
  body: formData,
})
  .then((response) => response.json())
  .then((result) => {
    console.log("批量上传成功:", result);
  });
```

### Java 调用示例

```java
// 上传文件
@PostMapping("/upload")
public FormatResponseData<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
    // 文件上传逻辑已在 FileUploadController 中实现
}

// 获取文件列表
@GetMapping("/files")
public FormatResponseData<Page<FileInfo>> getFiles(
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "10") Integer pageSize) {
    Page<FileInfo> files = fileInfoService.findAll(pageNumber, pageSize);
    return new FormatResponseData<>(ResponseStatus.SUCCESS, files);
}
```

## 注意事项

1. **文件大小限制**：默认最大 10MB，可在配置文件中调整
2. **文件类型**：支持所有文件类型，无特殊限制
3. **存储路径**：文件存储在项目根目录的 `uploads` 文件夹下
4. **文件名**：上传的文件会生成唯一的 UUID 文件名，避免冲突
5. **数据库记录**：每个上传的文件都会在数据库中创建记录
6. **用户关联**：目前上传用户 ID 字段预留，需要根据实际登录系统集成
7. **安全性**：建议在生产环境中添加文件类型验证和用户权限控制

## 扩展功能

可以根据需要扩展以下功能：

1. 文件类型白名单验证
2. 图片压缩和缩略图生成
3. 文件预览功能
4. 文件分享和权限控制
5. 文件版本管理
6. 云存储集成（如阿里云 OSS、腾讯云 COS 等）
7. 文件加密存储
8. 断点续传功能
