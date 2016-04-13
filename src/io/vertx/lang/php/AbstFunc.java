/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the MIT License (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertx.lang.php;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.JavaValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.function.AbstractFunction;
import io.vertx.core.Vertx;

/**
 * Function to expose vertx instance in php
 * @author Leng Sheng Hong
 */
public class AbstFunc extends AbstractFunction {
    public Vertx vertx;

    AbstFunc(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Value call(Env env, Value[] values) {
        return new JavaValue(env, vertx, env.getJavaClassDefinition(vertx.getClass()));
    }
}
