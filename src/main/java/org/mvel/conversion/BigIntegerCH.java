package org.mvel.conversion;

import org.mvel.ConversionException;
import org.mvel.ConversionHandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * MVEL (The MVFLEX Expression Language)
 * <p/>
 * Copyright (C) 2007 Christopher Brock, MVFLEX/Valhalla Project and the Codehaus
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class BigIntegerCH implements ConversionHandler {
    private static final Map<Class, Converter> CNV =
            new HashMap<Class, Converter>();


    public Object convertFrom(Object in) {
        if (!CNV.containsKey(in.getClass())) throw new ConversionException("cannot convert type: "
                + in.getClass().getName() + " to: " + Integer.class.getName());
        return CNV.get(in.getClass()).convert(in);
    }


    public boolean canConvertFrom(Class cls) {
        return CNV.containsKey(cls);
    }

    static {
        CNV.put(Object.class,
                new Converter() {
                    public BigInteger convert(Object o) {
                        return new BigInteger(String.valueOf(o));
                    }
                }
        );

        CNV.put(BigInteger.class,
                new Converter() {
                    public BigInteger convert(Object o) {
                        return (BigInteger) o;
                    }
                }
        );


        CNV.put(BigDecimal.class,
                new Converter() {
                    public BigInteger convert(Object o) {
                        return ((BigDecimal) o).toBigInteger();
                    }
                }
        );

        CNV.put(String.class,
                new Converter() {
                    public BigInteger convert(Object o) {
                        return new BigInteger((String) o);
                    }
                }
        );


        CNV.put(Short.class,
                new Converter() {
                    public BigInteger convert(Object o) {
                        return new BigInteger(String.valueOf(o));
                    }
                }
        );

        CNV.put(Long.class,
                new Converter() {
                    public BigInteger convert(Object o) {
                        return new BigInteger(String.valueOf(o));
                    }
                }
        );

        CNV.put(Integer.class,
                new Converter() {
                    public BigInteger convert(Object o) {
                        return new BigInteger(String.valueOf(o));
                    }
                }
        );

        CNV.put(String.class,
                new Converter() {
                    public BigInteger convert(Object o) {
                        return new BigInteger((String) o);
                    }
                }
        );

        CNV.put(char[].class,
                new Converter() {
                    public BigInteger convert(Object o) {
                        return new BigInteger(new String((char[]) o));
                    }
                }

        );
    }
}
