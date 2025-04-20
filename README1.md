# Spring AI Ailibaba 最佳实践
> Spring AI Alibaba 开源项目基于 Spring AI 构建，是阿里云通义系列模型及服务在 Java AI 应用开发领域的最佳实践，提供高层次的 AI API 抽象与云原生基础设施集成方案，帮助开发者快速构建 AI 应用
>
> 本项目致力于用最简单和简洁代码实现基本功能

## 项目简介
基于Spring AI Ailibaba和OpenAI API开发的综合性知识库系统。
它旨在展示Spring AI Ailibaba框架的强大功能，并提供一个实用的RAG（检索增强生成）解决方案。
该项目集成了多种AI功能，包括自然语言处理、图像生成、语音处理、function calling、Vector Store、知识库等常见AI应用功能

## 项目功能
- 基本对话、流式对话(文生文)
- 文生图
- 文转声音
- FunctionCall
- 声音转文字
- 支持上传文件、zip压缩包自动解压向量化存储
- RAG检索增强、知识库
- 多模态对话
- 历史消息记录(内存、数据库)
- 对接Ollama的开源大模型

## 技术栈
- SpringBoot3
- spring-ai-alibaba 1.0.0-M5.1
- postgresql数据库

docker部署postgresql
```yaml
docker run --name my-postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres
```



最后更新时间：2025年04月19日17:36:42
