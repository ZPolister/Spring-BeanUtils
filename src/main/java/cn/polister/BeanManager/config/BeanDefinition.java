package cn.polister.BeanManager.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Bean注册定义信息
 * @author Polister
 */
@Data
@Accessors(chain = true)
public class BeanDefinition {
    private String beanName; // Bean名称
    private Class<?> beanClass; // 对应的字节码
    private boolean singleton = true; // 是否单例模式
    private boolean lazyLoad = false; // 是否懒加载(饿汉模式使用)
    private boolean needCreate = true; // 是否需要创建实例（用于FX中对Fxml的Controller的过滤，饿汉模式使用）

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.beanName = beanClass.getName();
    }

    public BeanDefinition(Class<?> beanClass, String beanName) {
        this.beanClass = beanClass;
        this.beanName = beanName;
    }
}
