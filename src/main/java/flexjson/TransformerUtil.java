package flexjson;

import flexjson.transformer.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class TransformerUtil {

    private static final Map<Class, Transformer> defaultTransformers;

    static {
        HashMap<Class, Transformer> transformers = new HashMap<Class, Transformer>();

        // define all standard type transformers
        Transformer transformer = new NullTransformer();
        transformers.put(void.class, new TransformerWrapper(transformer));

        transformer = new ObjectTransformer();
        transformers.put(Object.class, new TransformerWrapper(transformer));

        transformer = new ClassTransformer();
        transformers.put(Class.class, new TransformerWrapper(transformer));

        transformer = new BooleanTransformer();
        transformers.put(boolean.class, new TransformerWrapper(transformer));
        transformers.put(Boolean.class, new TransformerWrapper(transformer));

        transformer = new NumberTransformer();
        transformers.put(Number.class, new TransformerWrapper(transformer));

        transformers.put(Integer.class, new TransformerWrapper(transformer));
        transformers.put(int.class, new TransformerWrapper(transformer));

        transformers.put(Long.class, new TransformerWrapper(transformer));
        transformers.put(long.class, new TransformerWrapper(transformer));

        transformers.put(Double.class, new TransformerWrapper(transformer));
        transformers.put(double.class, new TransformerWrapper(transformer));

        transformers.put(Float.class, new TransformerWrapper(transformer));
        transformers.put(float.class, new TransformerWrapper(transformer));

        transformers.put(BigDecimal.class, new TransformerWrapper(transformer));
        transformers.put(BigInteger.class, new TransformerWrapper(transformer));

        transformer = new StringTransformer();
        transformers.put(String.class, new TransformerWrapper(transformer));

        transformer = new CharacterTransformer();
        transformers.put(Character.class, new TransformerWrapper(transformer));
        transformers.put(char.class, new TransformerWrapper(transformer));

        transformer = new BasicDateTransformer();
        transformers.put(Date.class, new TransformerWrapper(transformer));

        transformer = new DefaultCalendarTransformer();
        transformers.put(Calendar.class, new TransformerWrapper(transformer));

        transformer = new EnumTransformer();
        transformers.put(Enum.class, new TransformerWrapper(transformer));

        transformer = new IterableTransformer();
        transformers.put(Iterable.class, new TransformerWrapper(transformer));

        transformer = new MapTransformer();
        transformers.put(Map.class, new TransformerWrapper(transformer));

        transformer = new ArrayTransformer();
        transformers.put(Arrays.class, new TransformerWrapper(transformer));

        try {
            Class hibernateProxy = Class.forName("org.hibernate.proxy.HibernateProxy");
            transformers.put(hibernateProxy, new TransformerWrapper(new HibernateTransformer()));
        } catch (ClassNotFoundException ex) {
            // no hibernate so ignore.
        }

        defaultTransformers = Collections.unmodifiableMap(transformers);
    }

    public static TypeTransformerMap getDefaultTypeTransformers() {
        return new TypeTransformerMap(defaultTransformers);
    }
}
