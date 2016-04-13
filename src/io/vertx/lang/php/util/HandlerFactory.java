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
package io.vertx.lang.php.util;

import io.vertx.core.eventbus.Message;
import io.vertx.lang.php.*;
import io.vertx.lang.php.buffer.Buffer;

import io.vertx.core.AsyncResult;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;

/**
 * A utility class for creating common Vert.x handlers.
 *
 * @author Jordan Halterman
 */
public class HandlerFactory {

  private HandlerFactory() {
  }

  /**
   * Creates a generic handler.
   */
  public static <T> io.vertx.core.Handler<T> createGenericHandler(Env env, Value handler) {
    PhpTypes.assertCallable(env, handler);
    return new Handler<T>(env, PhpTypes.toCallable(handler));
  }

  /**
   * Creates a void result handler.
   */
  public static io.vertx.core.Handler<Void> createVoidHandler(Env env, Value handler) {
    PhpTypes.assertCallable(env, handler);
    return new Handler<Void>(env, PhpTypes.toCallable(handler)) {
      @Override
      public void handle(Void arg) {
        Env env = getEnvironment();
        getCallable().call(env);
      }
    };
  }

  /**
   * Creates a buffer handler.
   */
  public static io.vertx.core.Handler<io.vertx.core.buffer.Buffer> createBufferHandler(Env env, Value handler) {
    PhpTypes.assertCallable(env, handler);
    return new Handler<io.vertx.core.buffer.Buffer>(env, PhpTypes.toCallable(handler), new ResultModifier<io.vertx.core.buffer.Buffer, Buffer>() {
      @Override
      public Buffer modify(io.vertx.core.buffer.Buffer buffer) {
        return new Buffer(buffer);
      }
    });
  }

  /**
   * Creates an exception handler.
   */
  public static io.vertx.core.Handler<Throwable> createExceptionHandler(Env env, Value handler) {
    PhpTypes.assertCallable(env, handler);
    return new Handler<Throwable>(env, PhpTypes.toCallable(handler));
  }

  /**
   * Creates a generic asynchronous handler.
   */
  public static <T> io.vertx.core.Handler<AsyncResult<T>> createAsyncGenericHandler(Env env, Value handler) {
    PhpTypes.assertCallable(env, handler);
    return new AsyncResultHandler<T>(env, PhpTypes.toCallable(handler));
  }

  /**
   * Creates an asynchronous void handler.
   */
  public static io.vertx.core.Handler<AsyncResult<Void>> createAsyncVoidHandler(Env env, Value handler) {
    PhpTypes.assertCallable(env, handler);
    return new AsyncResultHandler<Void>(env, PhpTypes.toCallable(handler)) {
      @Override
      public void handle(AsyncResult<Void> result) {
        Env env = getEnvironment();
        if (result.succeeded()) {
          getCallable().call(env, env.wrapJava(null));
        }
        else {
          getCallable().call(env, env.wrapJava(result.cause()));
        }
      }
    };
  }

  /**
   * Creates an asynchronous buffer result handler.
   */
  public static io.vertx.core.Handler<AsyncResult<io.vertx.core.buffer.Buffer>> createAsyncBufferHandler(Env env, Value handler) {
    PhpTypes.assertCallable(env, handler);
    return new AsyncResultHandler<io.vertx.core.buffer.Buffer>(env, PhpTypes.toCallable(handler), new AsyncResultWrapper<io.vertx.core.buffer.Buffer, Buffer>() {
      @Override
      public Buffer wrap(io.vertx.core.buffer.Buffer buffer) {
        return new Buffer(buffer);
      }
    });
  }

}
