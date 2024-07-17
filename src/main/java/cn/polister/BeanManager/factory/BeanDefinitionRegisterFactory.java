package cn.polister.BeanManager.factory;



import cn.polister.BeanManager.config.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean定义信息注册工厂（通过这个工厂可获得一个类的对应注册信息）
 * @author Polister
 */
public class BeanDefinitionRegisterFactory {

    private static final Map<String, BeanDefinition> beanDefinitionMap =
            new ConcurrentHashMap<>();

    /**
     * 注册一个Bean信息
     * @param beanName Bean名称
     * @param beanClass Bean字节码
     * @param lazyLoad 是否懒加载
     * @param singleton 是否单例
     */
    public static void registerBeanDefinition(String beanName, Class<?> beanClass, boolean lazyLoad, boolean singleton) {
        BeanDefinition beanDefinition = new BeanDefinition(beanClass, beanName);
        beanDefinition.setLazyLoad(lazyLoad);
        beanDefinition.setSingleton(singleton);

        beanDefinitionMap.put(beanName,beanDefinition);
    }

    /**
     * 获取一个Bean信息
     * @param beanName Bean名称
     * @return 一个Bean信息类
     */
    public static BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    /**
     * 注册一个Bean实例,无需由Manager管理是否需要生成（用于适配JavaFX）
     * @param beanName Bean名字
     * @param object 实例
     */
    public static void registerModel(String beanName, Object object) {
        beanDefinitionMap.put(beanName, new BeanDefinition(object.getClass(), beanName).setNeedCreate(false));
        SingletonBeanRegisterFactory.cacheModelBean(beanName, object);
        BeanInjectFactory.injectWithAutoWired(object);
    }

    /**
     * 实现对应Bean实例（饿汉模式）
     */
    public static void initInstanceBeans() {
        beanDefinitionMap.forEach((k, v) -> {
            if (v.isSingleton() && !v.isLazyLoad() && v.isNeedCreate()) {
                SingletonBeanRegisterFactory.getBean(v.getBeanName());
            }
        });
    }
}
