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

import flexjson.TypeContext;
import flexjson.BasicType;

public class StringArrayTransformer extends AbstractTransformer {

    public void transform(Object object) {
        String[] stringArr = (String[]) object;

        TypeContext typeContext = new TypeContext(BasicType.ARRAY);
        getContext().pushTypeContext(typeContext);
        getContext().writeOpenArray();
        for (String item : stringArr) {
            if (!typeContext.isFirst()) getContext().writeComma();
            typeContext.setFirst(false);
            getContext().transform(item.toUpperCase());
        }
        getContext().writeCloseArray();
        getContext().popTypeContext();

    }

}
