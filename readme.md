# System for Constructing and Visualizing Knowledge Graph

[TOC]

## 功能

* 知识图谱项目创建
* 知识图谱可视化定义
	* 节点创建、编辑、删除
	* 关系创建、编辑、删除
* 知识图谱布局编辑
	* 力导向图模式
	* 排版模式
	* 自由模式
	* 支持不同布局下的独立调整及持久化
* 知识图谱导出
	* 导出为png
	* 导出为xml（可用于创建）
* 知识图谱问答（食谱领域）
* 关系查询
* 节点查询
* 中心识别
	* neo4j page rank

> 演示效果见[用户手册](./用户手册.md)
>
> 启动相关见[部署文档](./部署文档.md)

## 总体架构

![arch](https://cyzblog.oss-cn-beijing.aliyuncs.com/arch.png)

## 服务端设计

> 在服务端，分为知识图谱服务和智能应用服务两个微服务
>
> * 知识图谱服务：包括用户管理、项目管理、图谱管理、微服务管理四个模块，主要负责知识图谱定义部分，使用 Spring Boot 搭建，是前端调用的总入口。
> * 智能应用服务：提供知识图谱智能问答的算法支持，作为微服务，请求由知识图谱服务统一转发。

### 知识图谱服务

![依赖包图3](https://cyzblog.oss-cn-beijing.aliyuncs.com/依赖包图3.png)

#### 用户管理模块

用户管理模块主要负责授权、认证，核心技术使用了

* 对称加密与非对称加密
* Spring Security

![sec3_secury](https://cyzblog.oss-cn-beijing.aliyuncs.com/sec3_secury.png)

#### 微服务调用模块

微服务调用模块，但由于在系统中仅有两个微服务，且为异构的服务（分别基于 Java 和 Python），这里并没有引入 Spring Cloud 作为微服务的框架，而是借用其思想自己实现了服务的注册、发现与调用。

配置文件：

```yml
# python微服务
pyserver:
  url: http://127.0.0.1:5000
```

* **服务调用**

	在服务调用上，使用RestTemplate进行统一的转发

* **服务发现**

	在服务发现上，使用心跳机制来与微服务通信。

	在 Flask 服务中，提供接口`/health` 返回微服务状态

	在 Spring Boot 服务中，启动一个线程对该接口进行监听，如果发现微服务异常，则对所有将转发到 Flask 服务的请求进行熔断。

![microservice](https://cyzblog.oss-cn-beijing.aliyuncs.com/microservice.png)

#### Helper模块

helper模块用于根据用户的自然语言，找到符合条件的项目

![sec3-helper](https://cyzblog.oss-cn-beijing.aliyuncs.com/sec3-helper.png)

#### 项目管理模块

项目管理中，很多数据的是读多写少的，在这里引入 Spring Cache 进行缓存管理。

#### 图谱管理模块

图谱管理模块中，持久层的使用方案如下：

* MySQL： 存储布局信息
* Neo4j： 存储图节点、关系信息
* Redis： 缓存及图id生成

### 智能应用服务设计

![KBQA](https://cyzblog.oss-cn-beijing.aliyuncs.com/KBQA.png)

#### 图谱构建

我们从[美食天下](https://www.meishichina.com/)中爬取了每菜系的100种菜谱，根据菜谱名字的规律，进行了数据清洗，并导入到neo4j数据库中。

清洗后并导出的数据已放在/load/export文件夹下，导入neo4j的流程如下

* 进入到neo4j文件夹，`cd /opt/neo4j-community-3.5.28`

* 将/load/export下的文件拷贝到imports目录中

* `bin/stop` 停止服务

* `rm -rf data/databases/graph.db/` 删除原数据（注意备份有用数据！）

* `bin/neo4j-admin import --nodes=import/caixi.csv  --nodes=import/shicai.csv --nodes=import/caipu.csv --relationships=import/belong.csv --relationships=import/zhushicai.csv --relationships=import/fuliao.csv --multiline-fields=true` 导入

* `bin/start` 启动服务

* 由于数据类型的原因，需要进行id的类型转换

	```CQL
	match ()-[r]->() set r.relationId=toInteger(r.relationId) return r;
	match (n) set n.nodeId=toInteger(n.nodeId) return n;
	```

#### KBQA

**问题类型设计**

| 问题类型             | 中文含义               | 问题举例             |
| -------------------- | ---------------------- | -------------------- |
| food_query✔          | 查询菜谱属性           | 烤肠怎么做           |
| food_constraint✔     | 查询符合约束的菜       | 微辣的炒菜有哪些     |
| desc_recipe ✔        | 描述菜谱               | 红烧肉               |
| desc_cuisine ✔       | 描述菜系               | 闽菜                 |
| desc_material ✔      | 描述食材               | 五花肉               |
| ingredients✔         | 食材查询               | 红烧肉用什么做       |
| ingredients_prime✔   | 主食材查询             | 红烧肉的主食材是什么 |
| ingredients_sub✔     | 辅料查询               | 红烧肉的辅料是什么   |
| ingredients_num✔     | 查询所需食材的量       | 红烧肉要用多少五花肉 |
| ingredients_include✔ | 某个菜是否用了某个东西 | 红烧肉要用五花肉吗？ |
| food_belong✔         | 查询菜的菜系           | 红烧肉属于什么菜系？ |
| cuisine_food✔        | 查询菜系有哪些菜       | 闽菜有哪些美食？     |
| cuisine_query ✔      | 查询菜系               | 有哪些菜系的菜       |
| unknown✔             | 不知道                 | 我帅吗               |

注：food_query实际上分为：

* food_query_zuofa
* food_query_kouwei
* food_query_gongyi
* food_query_leixing
* food_query_kouwei

**回答设计**

* 无法识别问题：抱歉，小助手听不懂您的问题。
* 找不到答案：抱歉，小助手暂时无法回答您的问题。

#### Centrality Algorithm

使用 neo4j-gds 自带的[中心算法](https://neo4j.com/docs/graph-data-science/current/algorithms/centrality/)，这里选用 [Page Rank](https://neo4j.com/docs/graph-data-science/current/algorithms/page-rank/) 进行实现

## 数据库设计

| 数据库 | 存储内容             |
| ------ | -------------------- |
| MySQL  | 存储布局信息         |
| Neo4j  | 存储图节点、关系信息 |
| Redis  | 缓存及图id生成       |

## 前端设计

### 前端整体架构

- 页面主要模块
	- 用户入口 User：登录/注册
	- 主页 Home：用户项目管理、其他模块入口
	- 知识图谱展示 Graph：知识图谱的展示、编辑、导出
	- 图谱智能助手 SmartHelper：图谱问答、实体/关系查询、中心识别
- 页面核心中间件、三方库工具
	- 前端开发框架基础 Vue
	- 页面路由 Vue Router
	- 全局状态管理 Vuex
	- 页面样式组件库 ElementUI
	- 图谱可视化 D3
	- 数据统计可视化 EChart
	- 智能助手问答页面 JwChat

![](https://cyzblog.oss-cn-beijing.aliyuncs.com/fe_structure.png)

### 图谱页面设计

- 核心模块
	- 图谱展示核心 GraphBoard
	- 图谱操作面板 GraphEditor
		- 图谱搜索 GraphEditorSearch
		- 实体/关系的新增、修改、删除、细节查看 GraphEditorItem
		- 图谱数据统计 GraphStatistics
	- 侧边操作栏 GraphSideBar
		- 布局模式、布局保存/恢复 GraphLayout
		- 新增实体/关系、图谱缩放重置、图谱导出 GraphAction
		- 智能服务
		- 项目设置

![](https://cyzblog.oss-cn-beijing.aliyuncs.com/fe_structure_graph.png)

### 图谱页面数据流

- 事件驱动模型
	- 由 GraphBoard 保存核心图数据
	- 其他模块引用图数据进行展示
	- 其他模块向 GraphBoard 发起事件修改视图

![](https://cyzblog.oss-cn-beijing.aliyuncs.com/fe_data_flow_graph.png)

### 详细设计模块

- 底层代码模块划分
	- api 后端交互封装
	- assets 静态资源(图片)
	- common 全局共用模块
	- views 主要视图
	- modules 主要视图模块
	- router 页面路由
	- store 全局状态管理

![](https://cyzblog.oss-cn-beijing.aliyuncs.com/fe_code_structure.png)

### 视图模块细节

- 每个主要视图在 `modules/` 下拥有一个包、每个包包含下列结构
	- components 子视图/组件
	- utils 视图内部工具函数
	- store 视图内共享全局数据

![](https://cyzblog.oss-cn-beijing.aliyuncs.com/fe_code_modules.png)

### 后端交互封装

- APIDispatcher 后端 API 封装成内部 DSL
- 对后端接口进行划分并分别向 APIDispatcher 进行注册

![](https://cyzblog.oss-cn-beijing.aliyuncs.com/fe_code_api_dispatcher.png)

- 透过 axios 接口对后端请求/响应进行拦截

![](https://cyzblog.oss-cn-beijing.aliyuncs.com/fe_code_api_interceptors.png)

- 实际调用形式：使用统一 `APIDispatcher` 进行转发

![](https://cyzblog.oss-cn-beijing.aliyuncs.com/fe_code_api_dispatcher_usage.png)