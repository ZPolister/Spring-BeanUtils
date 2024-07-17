package cn.polister.BeanManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bean自动注入注解（自动从Bean容器中获取对应实例并进行注入， 需要注册的类才能使用）
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoWired {
    String beanName() default "";
}
