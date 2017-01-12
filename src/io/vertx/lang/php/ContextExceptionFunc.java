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

import com.caucho.quercus.env.*;
import com.caucho.quercus.function.AbstractFunction;
import io.vertx.core.Context;
import io.vertx.core.Verticle;

/**
 * Function to expose config JsonObject of a verticle in php
 * @author Leng Sheng Hong
 */
public class ContextExceptionFunc extends AbstractFunction {
    public Context context;

    ContextExceptionFunc(Context context) {
        this.context = context;
    }

    @Override
    public Value call(Env env, Value[] values) {
        Callable errorHandler = values[0].toCallable(env, false);
        context.exceptionHandler(throwable -> {
            errorHandler.call(env, env.createException("com.caucho.quercus.QuercusRuntimeException", throwable.getMessage()));
        });
        return NullValue.NULL;
    }
}
