package cn.tsvico.common.util

import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.stereotype.Component

/**
 * Spring Bean 工具类
 */
@Component
class SpringBeanUtils : BeanFactoryPostProcessor {
    @Throws(BeansException::class)
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        Companion.beanFactory = beanFactory
    }

    companion object {
        private var beanFactory: ConfigurableListableBeanFactory? = null
        @Throws(BeansException::class)
        fun getBean(name: String): Any {
            return beanFactory!!.getBean(name)
        }

        @Throws(BeansException::class)
        fun <T> getBean(clz: Class<T>): T {
            return beanFactory!!.getBean(clz)
        }

        fun containsBean(name: String): Boolean {
            return beanFactory!!.containsBean(name)
        }
    }
}