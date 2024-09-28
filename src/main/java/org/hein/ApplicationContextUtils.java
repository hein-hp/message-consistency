package org.hein;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

/**
 * ApplicationContextUtils
 *
 * @author hein
 */
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        ApplicationContextUtils.context = context;
    }

    /**
     * Retrieves a bean from the ApplicationContext by its name and type.
     *
     * @param <T>   the type of the bean to retrieve.
     * @param name  the name of the bean.
     * @param clazz the class object representing the bean type.
     * @return the bean instance.
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }
}