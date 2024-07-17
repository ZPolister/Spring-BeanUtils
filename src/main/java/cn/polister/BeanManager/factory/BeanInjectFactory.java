package cn.polister.BeanManager.factory;


import cn.polister.BeanManager.AutoWired;
import cn.polister.BeanManager.BeanUtils;
import cn.polister.BeanManager.InjectBeanException;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Bean自动注入类（AutoWired注解实现）
 * @author Polister
 */
public class BeanInjectFactory {

    /**
     * 对加了注解的类进行注入
     * @param obj 实例
     */
    protected static void injectWithAutoWired(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        // 遍历参数 查询是否有注入注解
        for (Field field : fields) {
            AutoWired annotation = field.getAnnotation(AutoWired.class);
            if (Objects.nonNull(annotation)) {
                try {
                    // 对其进行注入
                    field.setAccessible(true);
                    // 获取注解名称
                    String beanName = annotation.beanName();
                    // 注解没有加名称的话，用字节码当名称
                    if (Objects.isNull(beanName) || beanName.isEmpty()) {
                        beanName = field.getType().getName();
                    }
                    // 获取是否有这个Bean
                    Object bean = BeanUtils.getBean(beanName);
                    // 没有 不注入了
                    if (Objects.isNull(bean)) {
                        continue;
                    }
                    // 判断获取到的Bean是否是目标的子类或者实现
                    if (!bean.getClass().isAssignableFrom(field.getType())) {
                        throw new InjectBeanException("注入类与注册实例类型不同!");
                    }
                    // 注入操作
                    field.set(obj, bean);
                } catch (IllegalAccessException e) {
                    throw new InjectBeanException("注入Bean失败");
                }
            }
        }
    }
}
