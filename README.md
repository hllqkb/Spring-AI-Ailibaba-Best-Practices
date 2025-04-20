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
- [ ] 多模态RAG对话
- [x] 简单对话
- [x] 多模态简单对话
- [ ] 文档上传时，提取文档内部图片，调用多模态模型对图片进行描述然后入库。后续对话时，将文档里的图片及描述作为上下文。

### 前端工作

- [x] 对话界面: 快速搭建
- [x] 知识库管理界面
- [x] 知识库下附件管理界面
- [ ] 对话界面优化

springai-alibaba-best-practices:maven父工程，聚合其他子模块
springai-common:子模块，存放公共类、工具类等。
springai-service:子模块，存放核心类、接口，Controller, Service, Mapper等。



## 开发

### 环境

- node: v18
- jdk: 17
- minio + pgvector: [docker-compose.yml](env/docker-compose.yml)

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

- 修改配置文件：`application.yml` 和 `llm.yml`

&emsp;注意：`application.yml`配置里的`llm-dev.yml`需要改为`llm.yml`,当然也可以新建`llm-dev.yml`,在代码推送时，`llm-dev.yml`文件会被忽略:

```yaml
spring:
  config:
    import: classpath:llm-dev.yml
```

配置application-dev.yml和llm.yml

- 启动`SpringAiApp`
  :子模块，存放实体类、VO类等。