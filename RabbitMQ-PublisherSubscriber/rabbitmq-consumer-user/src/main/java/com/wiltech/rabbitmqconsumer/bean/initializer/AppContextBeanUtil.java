package com.wiltech.rabbitmqconsumer.bean.initializer;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Class to instantiate beans via look up.
 * This class should instantiate any managed bean onto a non managed bean class.
 * Its usage is AppContextUtil.getBean(MyBeanToInstantiate.class)
 */
@Service
public class AppContextBeanUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        context = appContext;
    }

    //    public static ApplicationContext getApplicationContext() {
    //        return context;
    //    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}
