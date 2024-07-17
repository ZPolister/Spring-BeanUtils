package cn.polister.BeanManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bean注册专用注解 将类注册为Bean，交由容器管理
 * @author Polister
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String beanName() default "";
    boolean singleton() default true;
    boolean lazyLoad() default false;
}
