package flexjson.transformer;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class HibernateTransformer extends ObjectTransformer {

    protected Class resolveClass(Object obj) {
        return findBeanClass( obj );
    }

    public Class<?> findBeanClass(Object object) {
        try {
            Method method = object.getClass().getMethod("getHibernateLazyInitializer");
            Object initializer = method.invoke( object );
            Method pmethod = initializer.getClass().getMethod("getPersistentClass");
            return (Class<?>)pmethod.invoke( initializer );
//            Class[] classes = object.getClass().getInterfaces();
//            for( Class clazz : classes ) {
//                if( clazz.getName().equals("org.hibernate.proxy.HibernateProxy") ) {
//                }
//            }
        } catch (IllegalAccessException e) {
        } catch (NoSuchMethodException e) {
        } catch (InvocationTargetException e) {
        }
        return object.getClass();
    }

}
