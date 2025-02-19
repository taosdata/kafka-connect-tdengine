<!-- omit in toc -->
# TDengine Kafka Connector

[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/taosdata/kafka-connect-tdengine/build.yml)](https://github.com/taosdata/kafka-connect-tdengine/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/taosdata/kafka-connect-tdengine/graph/badge.svg?token=GQRD9WCQ64)](https://codecov.io/gh/taosdata/kafka-connect-tdengine)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/taosdata/kafka-connect-tdengine)
![GitHub License](https://img.shields.io/github/license/taosdata/kafka-connect-tdengine)
![GitHub Tag](https://img.shields.io/github/v/tag/taosdata/kafka-connect-tdengine?label=latest)
<br />
[![Twitter Follow](https://img.shields.io/twitter/follow/tdenginedb?label=TDengine&style=social)](https://twitter.com/tdenginedb)
[![YouTube Channel](https://img.shields.io/badge/Subscribe_@tdengine--white?logo=youtube&style=social)](https://www.youtube.com/@tdengine)
[![Discord Community](https://img.shields.io/badge/Join_Discord--white?logo=discord&style=social)](https://discord.com/invite/VZdSuUg4pS)
[![LinkedIn](https://img.shields.io/badge/Follow_LinkedIn--white?logo=linkedin&style=social)](https://www.linkedin.com/company/tdengine)
[![StackOverflow](https://img.shields.io/badge/Ask_StackOverflow--white?logo=stackoverflow&style=social&logoColor=orange)](https://stackoverflow.com/questions/tagged/tdengine)

简体中文 | [English](./README.md)

<!-- omit in toc -->
## 目录

- [1. 简介](#1-简介)
- [2. 文档](#2-文档)
- [3. 前置条件](#3-前置条件)
- [4. 构建](#4-构建)
- [5. 测试](#5-测试)
  - [5.1 运行测试](#51-运行测试)
  - [5.2 添加用例](#52-添加用例)
  - [5.3 性能测试](#53-性能测试)
- [6. CI/CD](#6-cicd)
- [7. 提交 Issue](#7-提交-issue)
- [8. 提交 PR](#8-提交-pr)
- [9. 引用](#9-引用)
- [10. 许可证](#10-许可证)


## 1. 简介

`TDengine Kafka Connector` 是一个高效的数据集成工具，用于 TDengine 和 Kafka 之间的数据同步。它既支持将 Kafka 中的数据实时同步到 TDengine 数据库中，也支持将 TDengine 中的数据实时同步到 Kafka。借助 TDengine 的高性能时序数据处理能力，`TDengine Kafka Connector` 能够帮助企业轻松实现海量时序数据的实时存储与分析。

## 2. 文档
- 使用 TDengine Kafka Connector, 请参考 [TDengine Kafka Connector](https://docs.taosdata.com/third-party/collection/kafka/)，包含了如何安装、配置和使用 `TDengine Kafka Connector` 的说明和示例。
- 本 README 主要是为想自己贡献、编译、测试 `TDengine Kafka Connector` 的开发者写的。如果要学习 TDengine，可以浏览 [官方文档](https://docs.taosdata.com/)。

## 3. 前置条件

- 已安装 Java 1.8 或以上版本运行时环境和 Maven 3.6 或以上版本，且正确设置了环境变量。
- 本地已经部署 TDengine，具体步骤请参考 [部署服务端](https://docs.taosdata.com/get-started/package/)，且已经启动 taosd 与 taosAdapter。如果是 Mac 系统，请使用 `sudo ln -s /usr/local/lib/libtaos.dylib /Library/Java/Extensions/libtaos.dylib` 建立 `taos` 动态库软连接。

## 4. 构建

项目目录下执行 `mvn clean package` 构建项目。

## 5. 测试
### 5.1 运行测试
项目目录下执行 `mvn test` 运行测试，测试用例会连接到本地的 TDengine 服务器与 taosAdapter 进行测试。
运行测试后，最终会打印类似如下结果。如果所有用例通过，Failures 和 Errors 都是 0。
```
[INFO] Results:
[INFO] 
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
```

### 5.2 添加用例
所有测试在项目的 `src/test/java/com/taosdata/kafka/connect` 目录下，按照测试的功能划分了目录，可以新增加测试文件或者在已有的测试文件中添加用例。
用例使用 JUnit 框架，一般在 `before` 方法中初始化资源，在 `after` 方法中释放资源。

### 5.3 性能测试
性能测试还在开发中。

## 6. CI/CD
- [Build Workflow](https://github.com/taosdata/kafka-connect-tdengine/actions/workflows/build.yml)
- [Code Coverage](https://app.codecov.io/gh/taosdata/kafka-connect-tdengine)

## 7. 提交 Issue
我们欢迎提交 [GitHub Issue](https://github.com/taosdata/kafka-connect-tdengine/issues/new?template=Blank+issue)。 提交时请说明下面信息：
- 问题描述，是否必现，最好能包含详细调用堆栈。
- Kafka 版本。
- TDengine Kafka Connector 版本。
- TDengine Kafka Connector 配置文件（用户名和密码不需要）。
- TDengine 服务端版本。

## 8. 提交 PR
我们欢迎开发者一起开发本项目，提交 PR 时请参考下面步骤：
1. Fork 本项目，请参考 ([how to fork a repo](https://docs.github.com/en/get-started/quickstart/fork-a-repo))。
1. 从 main 分支创建一个新分支，请使用有意义的分支名称 (`git checkout -b my_branch`)。注意不要直接在 main 分支上修改。
1. 修改代码，保证所有单元测试通过，并增加新的单元测试验证修改。
1. 提交修改到远端分支 (`git push origin my_branch`)。
1. 在 GitHub 上创建一个 Pull Request ([how to create a pull request](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request))。
1. 提交 PR 后，可以通过 [Pull Request](https://github.com/taosdata/kafka-connect-tdengine/pulls) 找到自己的 PR，点击对应链接进去可以看到自己 PR CI 是否通过，如果通过会显示 “All checks have passed”。无论 CI 是否通过，都可以点击 “Show all checks” -> “Details” 来查看详细用例日志。
1. 提交 PR 后，如果 CI 通过，可以在 [codecov](https://app.codecov.io/gh/taosdata/kafka-connect-tdengine/pulls) 页面找到自己 PR，看单测覆盖率。

## 9. 引用

- [TDengine 官网](https://www.taosdata.com/)
- [TDengine GitHub](https://github.com/taosdata/TDengine)

## 10. 许可证

[MIT License](./LICENSE)
