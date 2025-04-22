# Spring Ai Alibaba Best Practices

> 使用[SprinAI Alibaba](https://java2ai.com/docs/1.0.0-M6.1/overview/)和[RAG 技术](https://www.promptingguide.ai/zh/techniques/rag)实现的个人知识库 AI 问答系统，适配 OpenAI 接口
>
> 采用简洁代码实现的最佳实践

## TODO 

### 后端工作

- [x] 对话附件上传接口：多模态需要。文档或图片上传时返回id，携带该id发起对话，Media 在后端根据id构建。
- [x] `DatabaseChatMemory` 实现：Message 存储到数据库中
- [x] 知识库增删改查接口：添加知识库、删除知识库、知识库列表
- [x] 知识库附件上传接口：指定知识库（携带id）上传，将文档存储到向量数据库（meta记录知识库id-baseId），同时生成附件文档对象。
- [x] 知识库下的附件文档删查接口：在指定知识库下，可以删除附件、查询附件。
- [x] 对话信息接口：创建对话、查询对话信息
- [x] 非多模态RAG对话: 指定多个知识库进行对话
- [x] Redis缓存用户信息和查询数据
- [ ] 多模态RAG对话
- [x] 简单对话
- [x] 多模态简单对话
- [ ] 文档上传时，提取文档内部图片，调用多模态模型对图片进行描述然后入库。后续对话时，将文档里的图片及描述作为上下文。

### 前端工作

- [x] 对话界面: 快速搭建
- [x] 知识库管理界面
- [x] 知识库下附件管理界面
- [ ] 对话界面优化

## 项目结构

**springai-alibaba-best-practices** : maven父工程，聚合其他子模块

**springai-common** : 子模块，存放公共类、工具类等。

**springai-service** : 子模块，存放核心类、接口，Controller, Service, Mapper等。

**springai-bom** : 子模块，统一依赖管理





## 部署

### 环境

- node: v18
- jdk: 17

### Docker部署相关环境

[Docker部署Minio（详细步骤）_minio docker部署-CSDN博客](https://blog.csdn.net/Keep__Me/article/details/135999640)

安装pgvector & postgresql直接运行docker文件夹下面的docker-compose.yml文件即可

~~[利用Docker来安装pgvector & postgresql,及使用注意事项,排坑](https://www.bgegao.com/2024/10/3138.html)~~

### 启动前端

```shell
pnpm install
pnpm start
```

### 启动后端

- 修改配置文件：`application-dev.yml` 和 `llm.yml`

**application-dev.yml （）内的是要更改的**

```yaml
vectorstore:
      pgvector:
        initialize-schema: true
        table-name: vector_store_1024
        remove-existing-vector-store-table: false
        dimensions: (写你的向量化模型的维度，默认是1024)
  datasource:
    driver-class-name: org.postgresql.Driver
    username: (数据库账号)
    password: (数据库密码)
    url: jdbc:postgresql://localhost:5433/springaialibaba
  data:
    redis:
      host: localhost  # Redis服务器地址
      port: 6379  # Redis服务器连接端口
      password: (Redis密码) # Redis服务器连接密码（默认为空）
      database: 0  # 0-15
# MinIO
minio:
  endpoint: http://127.0.0.1:9000/
  access-key: (Minio账号，默认为minio)
  secret-key: (Minio密码，默认为minio123)
```

llm.yml

```yaml
chat:
  # 通用对话模型
  simple:
    base-url: (ai的基础api)
    api-key: (ai的key)
    model: (ai模型)
  # 超长文本对话模型
  long:
    base-url: https://example.com
    api-key: sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxx
    model: model
  # 多模态对话模型
  multimodal:
    base-url: 
    api-key: 
    model:
# 嵌入模型
embedding:
  base-url: 
  api-key: 
  model: 

```



- 最后启动`SpringAiApp`