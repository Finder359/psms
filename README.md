# PSMS 餐饮业油烟监控平台

这是一个基于 Jeecg-Boot 的餐饮油烟排放监管平台，覆盖餐饮企业接入、测点管理、实时监测、数据分析、预警处置和统计报表等核心业务，适用于市、区县级环境监管部门及服务厂商的运营场景。

项目中已经集成了以 `psms` 业务模块为核心的后端代码，结合 MySQL + Redis 的数据基础设施，支持多区域权限、设备联网率统计、超标分析、运维工单等功能。

## 项目概述

该系统重点解决餐饮业油烟治理过程中的以下问题：

- 餐饮企业台账及基础信息管理
- 排口/测点/净化器设备接入管理
- 油烟浓度、颗粒物、非甲烷总烃等实时数据监测
- 设备运行状态、联网状态和异常告警分析
- 超标单位、超标次数、运行时长等智能分析
- 区域监管、企业分类统计、月度/年度趋势分析
- 异常工单流转与监督闭环

## 业务模块

当前后端代码主要位于：

- `jeecg-boot-module-system/src/main/java/org/jeecg/modules/psms/home`
- `.../enterprise`
- `.../base`
- `.../operation`
- `.../analyse`
- `.../supervision`
- `.../statement`
- `.../workorder`

关键业务模块说明：

- `home`：首页概览、企业接入数、测点联网率、数据流量、区域统计
- `enterprise`：企业档案、基础信息与接入管理
- `base`：基础数据及公共配置
- `operation`：运行分析、设备运行情况、经营时段统计
- `analyse`：超标分析、趋势对比、设备分析
- `supervision`：监督异常、告警统计、问题追踪
- `statement`：统计报表与汇总分析
- `workorder`：工单管理与处理闭环

## 技术栈

- Java 21
- Spring Boot 2.7.18
- Spring Cloud 2021.0.8
- MyBatis-Plus 3.4.3.1
- Apache Shiro 1.13.0 + JWT
- Druid 连接池
- Redis 缓存
- MySQL 8.0
- Maven 多模块构建
- Knife4j / Swagger 接口文档
- Quartz 定时任务

## 仓库结构

```text
psms/
├─ pom.xml                              # 根 Maven 配置
├─ README.md
├─ LICENSE
├─ docker-compose.yml                  # 本地开发容器编排
├─ docker-compose-server.yml           # 服务端部署编排
├─ db/
│  └─ psms.sql                         # 数据库结构和基础数据
├─ jeecg-boot-base/
├─ jeecg-boot-module-demo/
├─ jeecg-boot-module-system/
│  ├─ src/main/java/org/jeecg/modules/psms/
│  ├─ src/main/resources/
│  └─ target/
├─ jeecg-boot-starter/
├─ jeecg-cloud-module/
└─ ...
```

## 数据库与配置

数据库脚本位于：

- `db/psms.sql`

项目默认配置使用 MySQL 和 Redis，核心配置文件在：

- `jeecg-boot-module-system/src/main/resources/application.yml`
- `jeecg-boot-module-system/src/main/resources/application-dev.yml`

其中 `application-dev.yml` 中已配置：

- 服务端口：`8080`
- 上下文路径：`/psms`
- MySQL：`jdbc:mysql://127.0.0.1:3306/psms`
- Redis：`127.0.0.1:6379`

## 快速启动

### 1. 环境准备

请确认已安装：

- JDK 21
- Maven 3.8+
- MySQL 8.0
- Redis 5.0+

### 2. 初始化数据库

创建数据库：

```sql
CREATE DATABASE psms CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

导入脚本：

```bash
mysql -u root -p psms < db/psms.sql
```

### 3. 修改配置

编辑：

- `jeecg-boot-module-system/src/main/resources/application-dev.yml`

根据本地环境更新：

- 数据库用户名、密码
- Redis 地址和密码
- 上传目录、文件路径等必要配置

### 4. 编译项目

在项目根目录执行：

```bash
mvn clean package -DskipTests
```

### 5. 启动服务

可直接运行后端：

```bash
java -jar jeecg-boot-module-system/target/jeecg-boot-module-system-*.jar --spring.profiles.active=dev
```

启动后访问：

- 后台地址：`http://localhost:8080/psms`
- 接口文档：`http://localhost:8080/psms/doc.html`

## Docker 启动方式

项目根目录提供了 Docker Compose 配置，可一键启动 MySQL、Redis 和系统服务：

```bash
docker-compose up -d
```

注意：

- 如本地已有 MySQL/Redis 服务，请先确认端口未冲突
- 容器默认端口与应用配置一致，启动后可通过 `http://localhost:8080/psms` 访问

## 主要功能亮点

1. 企业与测点管理
   - 服务商、行政机构、餐饮企业、测点档案统一管理
   - 企业定位、坐标、经营类型、排污信息一体化录入

2. 实时监测与状态管理
   - 油烟、颗粒物、非甲烷总烃等浓度数据
   - 风机状态、净化器状态、电流、压差、流速等运行参数

3. 智能分析
   - 超标单位构成分析
   - 超标次数分析
   - 运行时长分析
   - 设备联网率与数据流量统计

4. 监管与告警
   - 异常告警统计
   - 设备未联动、停机、超标等提醒
   - 工单流转与问题跟踪

5. 综合看板
   - 区域统计、趋势分析、月度接入情况、设备状态总览

## 适用场景

- 市级/区县级餐饮油烟监管平台
- 厂商运维监控平台
- 餐饮企业排放治理平台
- 以排口为核心的环境监测系统

## 开发说明

本项目保留了 Jeecg-Boot 的低代码框架能力，并在其基础之上扩展了 PSMS 业务模块。适合在原有 Jeecg-Boot 生态中继续开发、扩展表单、菜单、角色权限和业务逻辑。

如果需要新增业务功能，建议：

- 在 `org.jeecg.modules.psms` 下按模块新增包
- 复用 Jeecg 的 `Controller + Service + Mapper + Entity` 结构
- 按业务模块维护菜单、权限和表单配置

## 许可证

本项目已包含 `LICENSE` 文件，使用前请确认所属许可条款。

## 备注

该仓库是基于 Jeecg-Boot 二次开发而成的实际业务系统，代码中已体现餐饮业油烟监控领域的业务实体和分析能力。后续若有前端源码或更完整部署文档，可按实际交付内容继续补充。
