/**
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package soya.framework.commons.reflect.objenesis.instantiator.basic;

import soya.framework.commons.reflect.objenesis.ObjenesisException;
import soya.framework.commons.reflect.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Constructor;

/**
 * Instantiates a class by grabbing the no args constructor and calling Constructor.newInstance().
 * This can deal with default public constructors, but that's about it.
 * 
 * @author Joe Walnes
 * @param <T>
 * @see ObjectInstantiator
 */
public class ConstructorInstantiator<T> implements ObjectInstantiator<T> {

   protected Constructor<T> constructor;

   public ConstructorInstantiator(Class<T> type) {
      try {
         constructor = type.getDeclaredConstructor((Class[]) null);
      }
      catch(Exception e) {
         throw new ObjenesisException(e);
      }
   }

   public T newInstance() {
      try {
         return constructor.newInstance((Object[]) null);
      }
      catch(Exception e) {
          throw new ObjenesisException(e);
      }
   }

}