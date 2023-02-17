package com.trs.gb.tools;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类，获取Bean对象
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    /**
     * ,通过名称获取Spring容器中的Bean对象
     * @param name
     * @return
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }
    /**
     * 通过类型获取Spring容器中的Bean对象
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        return applicationContext.getBean(clz);
    }
    /**
     * 通过名称和类型获取Spring容器中的Bean对象
     * @param name
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> clz){
        return applicationContext.getBean(name, clz);
    }

}
