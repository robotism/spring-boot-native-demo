package com.gankcode.springboot.utils

import com.gankcode.springboot.kt.log
import jakarta.annotation.PostConstruct
import jakarta.annotation.Resource
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.env.StandardEnvironment
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.util.ClassUtils
import org.springframework.util.StringUtils
import java.util.*
import java.util.function.Function

@Component
class ScanPackagesUtils {

    companion object {
        private val SCANNED_PACKAGES: MutableSet<String> = HashSet()
    }

    @Resource
    private val applicationContext: ApplicationContext? = null


    @Async
    @PostConstruct
    fun init() {
        log.info("scanPackages={}", componentScanPackages)
    }

    fun isComponentScanPackage(name: String): Boolean {
        if (!StringUtils.hasText(name)) {
            return false
        }
        for (packageName in componentScanPackages) {
            if (name.startsWith(packageName)) {
                return true
            }
        }
        return false
    }

    fun isComponentScanPackage(name: Class<*>): Boolean {
        return isComponentScanPackage(name.name)
    }

    val componentScanPackages: Set<String>
        get() {
            if (SCANNED_PACKAGES.isEmpty()) {
                val configurableApplicationContext = applicationContext as ConfigurableApplicationContext?
                val registry = configurableApplicationContext!!.beanFactory as BeanDefinitionRegistry
                SCANNED_PACKAGES.addAll(getComponentScanningPackages(registry))
            }
            return SCANNED_PACKAGES
        }

    protected fun getComponentScanningPackages(registry: BeanDefinitionRegistry): Set<String> {
        val packages: MutableSet<String> = LinkedHashSet()
        Arrays.stream(registry.beanDefinitionNames)
            .map { beanName: String? ->
                registry.getBeanDefinition(
                    beanName!!
                )
            }
            .map { it: BeanDefinition -> it.beanClassName }
            .forEach {
                addPackages(packages, it)
            }
        return packages
    }

    private fun addPackages(packages: MutableSet<String>, clazz: String?) {
        if (!StringUtils.hasText(clazz)) {
            return
        }
        try {
            addPackages(packages, Class.forName(clazz))
        } catch (ignored: Exception) {

        }
    }

    private fun addPackages(packages: MutableSet<String>, clazz: Class<*>) {
        val componentScan = AnnotationUtils.findAnnotation(
            clazz,
            ComponentScan::class.java
        )
        if (componentScan != null) {
            packages.addAll(listOf(*componentScan.value))
            packages.addAll(listOf(*componentScan.basePackages))
            packages.addAll(
                componentScan.basePackageClasses.map {
                    ClassUtils.getPackageName(it::class.java)
                }
            )
        }
        val genericSuperclass = clazz.genericSuperclass
        if (genericSuperclass is Class<*>) {
            addPackages(packages, genericSuperclass)
        }
    }


    fun <T> scan(vararg basePackages: String, filter: Function<Class<*>, Boolean>): List<Class<out T?>> {
        val list: MutableList<Class<out T?>> = ArrayList()
        val metadataReaders = scan(*basePackages)
        for (reader in metadataReaders) {
            val classMetadata = reader.classMetadata
            try {
                val clz = Class.forName(classMetadata.className)
                if (filter.apply(clz)) {
                    list.add(clz as Class<out T?>)
                }
            } catch (ignored: Exception) {
            }
        }
        return list
    }


    fun scan(vararg basePackages: String): List<MetadataReader> {
        val list: MutableList<MetadataReader> = ArrayList()

        for (basePackage in basePackages) {
            try {
                val packageSearchPath =
                    ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + "/**/*.class"
                val resources = PathMatchingResourcePatternResolver().getResources(packageSearchPath)

                val metadataReaderFactory: MetadataReaderFactory = CachingMetadataReaderFactory()
                for (resource in resources) {
                    list.add(metadataReaderFactory.getMetadataReader(resource!!))
                }
            } catch (ignored: Exception) {
            }
        }
        return list
    }

    private fun resolveBasePackage(basePackage: String): String {
        return ClassUtils.convertClassNameToResourcePath(
            StandardEnvironment().resolveRequiredPlaceholders(basePackage)
        )
    }

}