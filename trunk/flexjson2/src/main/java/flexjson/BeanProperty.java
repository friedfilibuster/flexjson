package flexjson;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class BeanProperty {
    private String name;
    private BeanAnalyzer bean;
    private Class propertyType;
    protected Field property;
    protected Method readMethod;
    protected Method writeMethod;
    protected Map<Class<?>, Method> writeMethods = new HashMap<Class<?>, Method>();

    public BeanProperty(String name, BeanAnalyzer bean) {
        this.name = name;
        this.bean = bean;
        this.property = bean.getDeclaredField(name);
    }

    public BeanProperty(Field property, BeanAnalyzer bean) {
        this.name = property.getName();
        this.bean = bean;
        this.property = property;
        this.propertyType = property.getType();
    }

    public String getName() {
        return name;
    }

    public Field getProperty() {
        return property;
    }

    public Class getPropertyType() {
        return propertyType;
    }

    public Method getReadMethod() {
        if (readMethod == null && bean.getSuperBean() != null && bean.getSuperBean().hasProperty(name)) {
            return bean.getSuperBean().getProperty(name).getReadMethod();
        } else {
            return readMethod;
        }
    }

    public Method getWriteMethod() {
        if (writeMethod == null) {
            writeMethod = writeMethods.get(propertyType);
            if (writeMethod == null && bean.getSuperBean() != null && bean.getSuperBean().hasProperty(name)) {
                return bean.getSuperBean().getProperty(name).getWriteMethod();
            }
        }
        return writeMethod;
    }

    public Collection<Method> getWriteMethods() {
        return writeMethods.values();
    }

    public void addWriteMethod(Method method) {
        Class clazz = method.getParameterTypes()[0];
        if (propertyType == null) {
            propertyType = clazz;
        }
        writeMethods.put(clazz, method);
        method.setAccessible(true);
    }

    public void setReadMethod(Method method) {
        if (propertyType == null) {
            propertyType = method.getReturnType();
            readMethod = method;
            readMethod.setAccessible(true);
        } else if (propertyType == method.getReturnType()) {
            readMethod = method;
            readMethod.setAccessible(true);
        }
    }

    public Boolean isAnnotated() {
        Method rm = getReadMethod();
        if (rm != null) {
            if (rm.isAnnotationPresent(JSON.class)) {
                return rm.getAnnotation(JSON.class).include();
            }
        }

        if (property != null) {
            if (property.isAnnotationPresent(JSON.class)) {
                return property.getAnnotation(JSON.class).include();
            }
        }
        return null; // this is important to indicate nothing was found (found and it was true, found and it was false, not found)
    }

    public Object getValue(Object instance) throws InvocationTargetException, IllegalAccessException {
        Method rm = getReadMethod();
        if (rm != null ) {
            return rm.invoke(instance, (Object[]) null);
        } else if (property != null) {
            return property.get(instance);
        } else {
            return null;
        }
    }

    public Boolean isReadable() { 
        Method rm = getReadMethod();
        return rm != null && !Modifier.isStatic(rm.getModifiers()) || property != null && !Modifier.isStatic(property.getModifiers()) && !Modifier.isTransient(property.getModifiers());
    }

    public Boolean isWritable() {
        Method wm = getWriteMethod();
        return wm != null && Modifier.isPublic(wm.getModifiers()) || property != null && Modifier.isPublic(property.getModifiers()) && !Modifier.isTransient(property.getModifiers());
    }

    public Boolean isTransient() {
        return property != null && Modifier.isTransient( property.getModifiers() );
    }

    /**
     * This method exists to help remove any properties that are only private members.
     * 
     * @return returns true if this property doesn't have a read method, write method, or is a non-public field.
     */
    protected boolean isNonProperty() {
        return getReadMethod() == null && getWriteMethod() == null && !Modifier.isPublic( property.getModifiers() );
    }
}
