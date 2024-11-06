//package com.gankcode.springboot.db.config.mybatis.plus;
//
//import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
//import com.baomidou.mybatisplus.core.MybatisConfiguration;
//import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
//import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
//import com.gankcode.springboot.annotation.ConditionalOnDebug;
//import com.gankcode.springboot.db.config.DynamicTableNameProcessor;
//import com.gankcode.springboot.db.config.mybatis.handler.InstantTypeHandler;
//import com.gankcode.springboot.db.config.mybatis.handler.JsonNodeTypeHandler;
//import com.gankcode.springboot.db.config.mybatis.handler.OffsetDateTimeTypeHandler;
//import com.gankcode.springboot.db.config.mybatis.handler.ZonedDateTimeTypeHandler;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.logging.slf4j.Slf4jImpl;
//import org.apache.ibatis.session.AutoMappingBehavior;
//import org.apache.ibatis.session.ExecutorType;
//import org.apache.ibatis.session.LocalCacheScope;
//import org.apache.ibatis.type.EnumOrdinalTypeHandler;
//import org.apache.ibatis.type.JdbcType;
//import org.apache.ibatis.type.TypeAliasRegistry;
//import org.apache.ibatis.type.TypeHandlerRegistry;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.*;
//
///**
// * @author Mr.Fan
// */
//@Slf4j
//@Configuration
//@MapperScan(basePackages = {"com.gankcode"}, annotationClass = Mapper.class)
//public class MybatisAutoConfiguration implements ConfigurationCustomizer {
//
//    @SneakyThrows
//    @Override
//    public void customize(MybatisConfiguration configuration) {
//        final TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
//        final TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
//
//        configuration.setCacheEnabled(true);
//        configuration.setLazyLoadingEnabled(true);
//        configuration.setAggressiveLazyLoading(true);
//        configuration.setMultipleResultSetsEnabled(true);
//        configuration.setUseColumnLabel(true);
//        configuration.setUseGeneratedKeys(false);
//        configuration.setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
//        configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
//        configuration.setDefaultStatementTimeout(60);
//        configuration.setDefaultFetchSize(100);
//        configuration.setSafeRowBoundsEnabled(false);
//        configuration.setMapUnderscoreToCamelCase(true);
//        configuration.setLocalCacheScope(LocalCacheScope.SESSION);
//        configuration.setJdbcTypeForNull(JdbcType.NULL);
//        configuration.setLazyLoadTriggerMethods(new HashSet<>(Arrays.asList("equals,clone,hashCode,toString".split(","))));
//        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
//        configuration.setLogImpl(Slf4jImpl.class);
//
//        typeAliasRegistry.registerAlias(Integer.class.getSimpleName(), Integer.class);
//        typeAliasRegistry.registerAlias(Long.class.getSimpleName(), Long.class);
//        typeAliasRegistry.registerAlias(HashMap.class.getSimpleName(), HashMap.class);
//        typeAliasRegistry.registerAlias(LinkedHashMap.class.getSimpleName(), LinkedHashMap.class);
//        typeAliasRegistry.registerAlias(ArrayList.class.getSimpleName(), ArrayList.class);
//        typeAliasRegistry.registerAlias(LinkedList.class.getSimpleName(), LinkedList.class);
//
//        typeHandlerRegistry.register(JsonNodeTypeHandler.class);
//        typeHandlerRegistry.register(InstantTypeHandler.class);
//        typeHandlerRegistry.register(OffsetDateTimeTypeHandler.class);
//        typeHandlerRegistry.register(ZonedDateTimeTypeHandler.class);
//
//    }
//
//
//    @ConditionalOnDebug
//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor(final DynamicTableNameProcessor dynamicTableNameProcessor) {
//        final MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
//        interceptor.addInnerInterceptor(new WarnIllegalSQLInnerInterceptor());
//        interceptor.addInnerInterceptor(new DynamicTableNameInnerInterceptor((sql, tableName) -> dynamicTableNameProcessor.process(tableName)));
//        return interceptor;
//    }
//
//
//}
