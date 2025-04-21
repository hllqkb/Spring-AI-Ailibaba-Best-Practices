package com.springai.springai.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MybatisPlusConfig {

	/**
	 * 配置 MybatisPlusInterceptor，用于分页插件等
	 * @return MybatisPlusInterceptor 实例
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// 添加分页插件，指定数据库类型为 PostgreSQL
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
		return interceptor;
	}

	/**
	 * 配置 GlobalConfig，包含 MetaObjectHandler 配置
	 * @return GlobalConfig 实例
	 */
	@Bean
	public GlobalConfig globalConfig() {
		GlobalConfig globalConfig = new GlobalConfig();
		// 设置 MetaObjectHandler，用于自动填充字段
		globalConfig.setMetaObjectHandler(metaObjectHandler());
		return globalConfig;
	}

	/**
	 * 配置 MetaObjectHandler，用于自动填充字段
	 * @return MetaObjectHandler 实例
	 */
	@Bean
	public MetaObjectHandler metaObjectHandler() {
		return new MetaObjectHandler() {
			/**
			 * 插入操作时自动填充字段
			 * @param metaObject 元对象
			 */
			@Override
			public void insertFill(MetaObject metaObject) {
				// 检查实体类中是否有 createTime 和 updateTime 字段
				boolean hasCreateTime = metaObject.hasSetter("createTime");
				boolean hasUpdateTime = metaObject.hasSetter("updateTime");
				boolean hasCreator = metaObject.hasSetter("creator");
				boolean hasUpdater = metaObject.hasSetter("updater");

				// 如果存在 createTime 和 updateTime 字段，则自动填充当前时间
				if (hasCreateTime && hasUpdateTime) {
					this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
					this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
				}

				// 尝试获取当前登录用户的 username，并填充 creator 和 updater 字段
				try {
					if (StpUtil.isLogin()) {
						String username = StpUtil.getLoginIdAsString();
						if (hasCreator) {
							this.setFieldValByName("creator", username, metaObject);
						}
						if (hasUpdater) {
							this.setFieldValByName("updater", username, metaObject);
						}
					}
				} catch (Exception e) {
					// 如果获取当前登录用户失败，记录调试信息
					log.debug("Failed to get login user during insert fill, this is normal during registration");
				}
			}

			/**
			 * 更新操作时自动填充字段
			 * @param metaObject 元对象
			 */
			@Override
			public void updateFill(MetaObject metaObject) {
				// 检查实体类中是否有 updateTime 和 updater 字段
				boolean hasUpdateTime = metaObject.hasSetter("updateTime");
				boolean hasUpdater = metaObject.hasSetter("updater");

				// 如果存在 updateTime 字段，则自动填充当前时间
				if (hasUpdateTime) {
					this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
				}

				// 尝试获取当前登录用户的 username，并填充 updater 字段
				try {
					if (StpUtil.isLogin()) {
						String username = StpUtil.getLoginIdAsString();
						if (hasUpdater) {
							this.setFieldValByName("updater", username, metaObject);
						}
					}
				} catch (Exception e) {
					// 如果获取当前登录用户失败，记录调试信息
					log.debug("Failed to get login user during update fill");
				}
			}
		};
	}
}
