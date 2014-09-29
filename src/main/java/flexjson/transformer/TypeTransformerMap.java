/**
 * Copyright 2007 Charlie Hubbard and Brandon Goodin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package flexjson.transformer;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to lookup type transformers from specific to generic implementation.
 * For example if an ArrayList transformer is provided
 */
public class TypeTransformerMap {

    private final Map<Class, Transformer> transformers;

    public TypeTransformerMap(Map<Class, Transformer> transformers) {
        this.transformers = Collections.synchronizedMap(new HashMap<Class, Transformer>(transformers));
    }

    public TypeTransformerMap(TypeTransformerMap parentTransformerMap) {
        this(parentTransformerMap.transformers);
    }

    @SuppressWarnings("unchecked")
    public Transformer getTransformer(Object key) {
        LookupContext lookupContext = new LookupContext();
        Class keyClass = (key == null ? void.class : key.getClass());

        Transformer transformer = findTransformer(keyClass, keyClass, lookupContext);
        if (!lookupContext.isCached() && transformer != null) {
            // If there was not a transformer directly mapped to the key
            // then cache it for future lookups
            putTransformer(keyClass, transformer);
        }
        return transformer;
    }

    private Transformer findTransformer(Class key, Class originalKey, LookupContext lookupContext) {

        if (key == null) return null;

        // if specific type found
        if (transformers.containsKey(key)) {
            if (key != originalKey) {
                // this transformer has not been associated with the provided key
                // set cache to false so that the key and transformer are put
                // in the map contents and future lookups occur more quickly
                lookupContext.setCached(false);
            }
            return transformers.get(key);
        }

        // handle arrays specially if no specific array type handler
        // Arrays.class is used for this because it would never appear
        // in an object that needs to be serialized.
        if (key.isArray()) {
            // if we have reached this point then
            // this transformer has not been associated with the provided key
            // set cache to false so that the key and transformer are put
            // in the map contents and future lookups occur more quickly
            lookupContext.setCached(false);
            return transformers.get(Arrays.class);
        }

        // check for interface transformer
        for (Class interfaze : key.getInterfaces()) {
            Transformer t = findTransformer(interfaze, originalKey, lookupContext);
            if (t != null) return t;
        }

        // if no interface transformers then check superclass
        return findTransformer(key.getSuperclass(), originalKey, lookupContext);

    }

    public Transformer putTransformer(Class aClass, Transformer transformer) {
        transformers.put(aClass, transformer);
        return transformer;
    }

    public boolean containsKey(Class stateClass) {
        return transformers.containsKey(stateClass);
    }

    class LookupContext {

        private boolean cached;

        public boolean isCached() {
            return cached;
        }

        public void setCached(boolean cached) {
            this.cached = cached;
        }
    }
}
