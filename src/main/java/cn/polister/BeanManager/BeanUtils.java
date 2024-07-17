package cn.polister.BeanManager;




import cn.polister.BeanManager.config.BeanDefinition;
import cn.polister.BeanManager.factory.BeanDefinitionRegisterFactory;
import cn.polister.BeanManager.factory.SingletonBeanRegisterFactory;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Bean实例管理容器工具
 * @author Polister
 */
public class BeanUtils {
    // 需要注册的bean（已弃用）
    private static final Class<?>[] toBeans = {
    };

    // 注册列表中的Bean
    @Deprecated
    public static void initBeans() {
        for (var clazz : toBeans) {
            Component clazzAnnotation = clazz.getAnnotation(Component.class);

            BeanDefinitionRegisterFactory.registerBeanDefinition(clazz.getName(), clazz,
                    clazzAnnotation.lazyLoad(), clazzAnnotation.singleton());
        }
    }

    /**
     * 注册FX中的Bean，因为这时已经创建了（适配FX生命周期）
     * @param object 已经创建好的Controller实例
     */
    public static void FXInitBean(Object object) {
        BeanDefinitionRegisterFactory.registerModel(object.getClass().getName(), object);
    }

    /**
     * 扫描全局需要注入实例的字段进行注入(已弃用)
     * @param packageMame 扫描的包名
     */
    @Deprecated
    public static void applyAutoWired(String packageMame) {
        List<Class<?>> classes = scanAnnotation(packageMame, AutoWired.class);
        for (Class<?> clazz : classes) {
            if (Objects.isNull(BeanDefinitionRegisterFactory.getBeanDefinition(clazz.getName()))) {
                continue;
            }

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (Objects.nonNull(field.getAnnotation(AutoWired.class))) {
                    Object bean = getBean(clazz.getName());
                    try {
                        field.setAccessible(true);
                        field.set(bean, getBean(field.getType().getName()));
                    } catch (IllegalAccessException e) {
                        throw new InjectBeanException("注入Bean失败");
                    }
                }
            }
        }
    }

    /**
     * 注册Bean 扫描指定包及其子包下的所有类，将带有Component注解的类进行注册
     * @param packageName 报名
     */
    public static void registerBeansWithComponentScan(String packageName) {
        // 先进行扫描获得带有Component注解的类字节码
        List<Class<?>> classes = scanAnnotation(packageName, Component.class);
        for (var clazz : classes) {
            // 获得注解内信息
            Component clazzAnnotation = clazz.getAnnotation(Component.class);
            String beanName = clazzAnnotation.beanName();
            // 对没有注明名字的Bean，取其字节码名称为Bean名称
            if (Objects.isNull(beanName) || beanName.isEmpty()) {
                beanName = clazz.getName();
            }
            // 执行注册
            BeanDefinitionRegisterFactory.registerBeanDefinition(beanName, clazz,
                    clazzAnnotation.lazyLoad(), clazzAnnotation.singleton());
        }
    }

    /**
     * 扫描指定包下的带有指定注解的类
     * @param packageName 包名
     * @param annotation 要扫描的注解名
     * @return 带有对应注解的类字节码列表
     */
    private static List<Class<?>> scanAnnotation(String packageName, Class<? extends Annotation> annotation) {

        // 使用反射获取对应字节码类
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(annotation);
        return typesAnnotatedWith.stream().toList();
    }


    /**
     * 实例化所有注册的类（饿汉模式）
     */
    public static void initInstanceBeans() {
        BeanDefinitionRegisterFactory.initInstanceBeans();
    }

    /**
     * 通过名称获取对应Bean实例
     * @param beanName Bean的名称
     * @return 对应实例
     */
    @SuppressWarnings("unchecked")
    public static<T> T getBean(String beanName) {

        // 先查有没有注册
        BeanDefinition beanDefinition = BeanDefinitionRegisterFactory.getBeanDefinition(beanName);
        // 没有注册，直接返回
        if (Objects.isNull(beanDefinition)) {
            return null;
        }
        // 调用实例工厂去找一个 返回
        return (T) SingletonBeanRegisterFactory.getBean(beanName);
    }
}
