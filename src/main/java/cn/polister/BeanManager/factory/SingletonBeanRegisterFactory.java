package cn.polister.BeanManager.factory;



import cn.polister.BeanManager.CreateBeanException;
import cn.polister.BeanManager.config.BeanDefinition;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例注册工厂（实例化Bean注册信息）
 * @author Polister
 */
public class SingletonBeanRegisterFactory {

    // 实例化的列表
    private static final Map<String, Object> beanList = new ConcurrentHashMap<>();

    /**
     * 创建Bean操作
     * @param beanDefinition Bean注册信息
     * @return 创建的实例
     */
    public static Object createBean(BeanDefinition beanDefinition) {

        // 执行创建
        Object bean = doCreate(beanDefinition);
        // 如果是单例Bean,放入单例池
        if (beanDefinition.isSingleton())
            beanList.put(beanDefinition.getBeanName(), bean);
        // 进行类内注解注入
        BeanInjectFactory.injectWithAutoWired(bean);
        return bean;
    }

    /**
     * 实例化Bean的创建周期
     * @param beanDefinition Bean的注册信息
     * @return Bean实例
     */
    private static Object doCreate(BeanDefinition beanDefinition) {

        // 没有对应信息，无法执行创建
        if (Objects.isNull(beanDefinition) ||
                Objects.isNull(beanDefinition.getBeanClass())) {
            throw new CreateBeanException("找不到定义的Bean信息");
        }
        Object instance;
        try {
            // 通过反射获取对应构造方法进行实例创建
            instance = beanDefinition.getBeanClass()
                        .getConstructor().newInstance();

        } catch (Exception e) {
            throw new CreateBeanException("创建实体Bean失败" + beanDefinition);
        }

        return instance;
    }

    // 获取Bean
    public static Object getBean(String beanName) {
        // 直接从缓存里拿
        Object bean = beanList.get(beanName);

        // 缓存池里没有，看看有没有注册
        if (Objects.isNull(bean)) {
            BeanDefinition beanDefinition = BeanDefinitionRegisterFactory
                                        .getBeanDefinition(beanName);
            // 没注册，不用找了
            if (Objects.isNull(beanDefinition)) {
                throw new CreateBeanException("Bean没有注册");
            }
            // 注册了，创建一下再返回
            bean = createBean(beanDefinition);
        }

        return bean;
    }

    public static void cacheModelBean(String beanName, Object object) {
        beanList.put(beanName, object);
    }
}
