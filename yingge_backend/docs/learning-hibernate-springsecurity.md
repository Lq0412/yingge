# Hibernate & Spring Security 速记（结合本项目）

## 1. Hibernate / Spring Data JPA
- 实体映射：`@Entity @Table` 声明表；`@Id @GeneratedValue` 主键；字段用 `@Column` 控制长度/唯一/非空等。
- 仓库接口：继承 `JpaRepository<Entity, Long>`，可按方法名生成查询（例：`findByUserAccountAndIsDelete`），复杂查询可用 `@Query` 或 Specification。
- 事务与懒加载：默认单方法事务够用；若懒加载属性在事务外访问会报错，可在 Service 内完成数据访问或使用 DTO。
- 配置：`application-*.yml` 配置数据源与 `spring.jpa`；`ddl-auto=none` 时由脚本建表（如 `init.sql`）；`spring.jpa.show-sql=true` 便于调试。

## 2. Spring Security（当前使用程度）
- 目前仅用 `spring-security-crypto` 提供 `BCryptPasswordEncoder`：
  - 注册时 `encode` 保存密码哈希。
  - 登录时用 `matches` 校验明文。
- 会话管理：现阶段使用自定义 Session/JWT 逻辑，未启用完整过滤链。
- 如果后续接入完整安全链：
  - 引入 `spring-boot-starter-security`，配置鉴权规则与登录入口。
  - 可结合 JWT 做无状态认证，或保留 Session 并编写统一拦截器。

## 3. 在本项目的最小使用方式
1) 确认数据源配置正确，执行初始化 SQL。  
2) 使用 JPA 仓库完成增删改查，Service 层做业务校验与事务控制。  
3) 密码一律使用 BCrypt 加密存储；鉴权后再访问用户资源。  
4) 如需标准化鉴权/授权，优先规划 JWT + Security 过滤链，再替换现有手写 Session 逻辑。
