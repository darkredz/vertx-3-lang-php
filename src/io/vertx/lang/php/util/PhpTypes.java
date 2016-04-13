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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.caucho.quercus.env.*;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import com.caucho.quercus.Location;

/**
 * Static class for validating PHP variable values.
 *
 * This is required for validating values as passed to Java
 * callbacks by Quercus. Often times, while a value may not
 * be "null" it may actually represent a "NULL" PHP value,
 * so this class helps validate the values of PHP variables
 * as represented in Java objects.
 *
 * @author Jordan Halterman
 */
public class PhpTypes {

  private PhpTypes() {
  }

  /**
   * Validates that a PHP value is null.
   */
  public static boolean isNull(Value value) {
    return value == null || value.isNull();
  }

  /**
   * Validates that a PHP value is null.
   */
  public static boolean isNull(Env env, Value value) {
    return PhpTypes.isNull(value);
  }

  /**
   * Asserts that a PHP value is null.
   */
  public static void assertNull(Env env, Value value) {
    if (!PhpTypes.isNull(value)) {
      env.error(PhpTypes.buildErrorMessage(env, "Object is not null.", new Object[]{}));
    }
  }

  /**
   * Asserts that a PHP value is null.
   */
  public static void assertNull(Env env, Value value, String message, Object... args) {
    if (!PhpTypes.isNull(value)) {
      env.error(PhpTypes.buildErrorMessage(env, message, args));
    }
  }

  /**
   * Validates that a PHP value is not null.
   */
  public static boolean notNull(Value value) {
    return !PhpTypes.isNull(value);
  }

  /**
   * Validates that a PHP value is not null.
   */
  public static boolean notNull(Env env, Value value) {
    return !PhpTypes.isNull(value);
  }

  /**
   * Asserts that a PHP value is not null.
   */
  public static void assertNotNull(Env env, Value value) {
    if (!PhpTypes.notNull(value)) {
      env.error(PhpTypes.buildErrorMessage(env, "Object is null.", new Object[]{}));
    }
  }

  /**
   * Asserts that a PHP value is not null.
   */
  public static void assertNotNull(Env env, Value value, String message, Object... args) {
    if (!PhpTypes.notNull(value)) {
      env.error(PhpTypes.buildErrorMessage(env, message, args));
    }
  }

  /**
   * Validates that a PHP value is set to its default value.
   */
  public static boolean isDefault(Value value) {
    return value != null && value.isDefault();
  }

  /**
   * Validates that a PHP value is set to its default value.
   */
  public static boolean isDefault(Env env, Value value) {
    return PhpTypes.isDefault(value);
  }

  /**
   * Asserts that a PHP value is set to its default value.
   */
  public static void assertDefault(Env env, Value value) {
    if (!PhpTypes.isDefault(value)) {
      env.error(PhpTypes.buildErrorMessage(env, "Object is not default.", new Object[]{}));
    }
  }

  /**
   * Asserts that a PHP value is set to its default value.
   */
  public static void assertDefault(Env env, Value value, String message, Object... args) {
    if (!PhpTypes.isDefault(value)) {
      env.error(PhpTypes.buildErrorMessage(env, message, args));
    }
  }

  /**
   * Validates that a PHP value is not set to its default value.
   */
  public static boolean notDefault(Value value) {
    return value == null || !value.isDefault();
  }

  /**
   * Validates that a PHP value is not set to its default value.
   */
  public static boolean notDefault(Env env, Value value) {
    return PhpTypes.notDefault(value);
  }

  /**
   * Asserts that a PHP value is not set to its default value.
   */
  public static void assertNotDefault(Env env, Value value) {
    if (!PhpTypes.notDefault(value)) {
      env.error(PhpTypes.buildErrorMessage(env, "Object is default.", new Object[]{}));
    }
  }

  /**
   * Asserts that a PHP value is not set to its default value.
   */
  public static void assertNotDefault(Env env, Value value, String message, Object... args) {
    if (!PhpTypes.notDefault(value)) {
      env.error(PhpTypes.buildErrorMessage(env, message, args));
    }
  }

  /**
   * Validates that a PHP value is callable.
   */
  public static boolean isCallable(Env env, Value value) {
    return PhpTypes.notNull(value) && value.isCallable(env, false, null);
  }

  /**
   * Asserts that a PHP value is callable.
   */
  public static void assertCallable(Env env, Value value) {
    if (!PhpTypes.isCallable(env, value)) {
      env.error(PhpTypes.buildErrorMessage(env, "Object is not callable.", new Object[]{}));
    }
  }

  /**
   * Asserts that a PHP value is callable.
   */
  public static void assertCallable(Env env, Value value, String message, Object... args) {
    if (!PhpTypes.isCallable(env, value)) {
      env.error(PhpTypes.buildErrorMessage(env, message, args));
    }
  }

  /**
   * Converts a basic PHP value to a callable.
   */
  public static Callable toCallable(Value value) {
    return value.toCallable(Env.getCurrent(), false);
  }

  /**
   * Converts a basic PHP value to a callable.
   */
  public static Callable toCallable(Env env, Value value) {
    return value.toCallable(env, false);
  }

  /**
   * Converts a basic PHP value to a callable.
   */
  public static Callable toCallable(Value value, boolean isOptional) {
    return value.toCallable(Env.getCurrent(), isOptional);
  }

  /**
   * Converts a basic PHP value to a callable.
   */
  public static Callable toCallable(Env env, Value value, boolean isOptional) {
    return value.toCallable(env, isOptional);
  }

  /**
   * Asserts that a value is a boolean.
   */
  public static void assertBoolean(Env env, Value value) {
    if (!value.isBoolean()) {
      env.error(PhpTypes.buildErrorMessage(env, "Invalid boolean value.", new Object[]{}));
    }
  }

  /**
   * Asserts that a value is a boolean.
   */
  public static void assertBoolean(Env env, Value value, String message, Object... args) {
    if (!value.isBoolean()) {
      env.error(PhpTypes.buildErrorMessage(env, message, args));
    }
  }

  /**
   * Asserts that a value is an integer.
   */
  public static void assertInteger(Env env, Value value) {
    if (!value.isNumeric()) {
      env.error(PhpTypes.buildErrorMessage(env, "Invalid integer value.", new Object[]{}));
    }
  }

  /**
   * Assets that a value is an integer.
   */
  public static void assertInteger(Env env, Value value, String message, Object... args) {
    if (!value.isNumeric()) {
      env.error(PhpTypes.buildErrorMessage(env, message, args));
    }
  }

  /**
   * Builds a PHP error message.
   */
  public static String buildErrorMessage(Env env, String message, Object... args) {
    if (message.endsWith(".")) {
      message = message.substring(0, message.length() - 1);
    }

    Location location = env.getLocation();
    StringBuilder builder = new StringBuilder();
    builder.append(String.format(message, args));

    String fileName = location.getUserPath();
    if (fileName == null) {
      fileName = location.getFileName();
    }
    if (fileName != null) {
      builder.append(" in ").append(fileName);
    }

    builder.append(" on line ").append(location.getLineNumber()).append(".");
    return builder.toString();
  }

  /**
   * Converts a PHP array to JSON.
   *
   * @param env The Quercus environment.
   * @param array A PHP array value.
   * @return A populated JsonObject instance.
   */
  public static JsonObject arrayToJson(Env env, Value array) {
    return PhpTypes.arrayToJsonObject(env, array);
  }

  /**
   * Converts a PHP array to a JsonObject.
   *
   * @param env The Quercus environment.
   * @param array A PHP array value.
   * @return A populated JsonObject instance.
   */
  public static JsonObject arrayToJsonObject(Env env, Value array) {
    JsonObject json = new JsonObject();
    Iterator<Map.Entry<Value, Value>> iter = array.getIterator(env);

    while (iter.hasNext()) {
      Map.Entry<Value, Value> entry = iter.next();

      Value key = entry.getKey();
      Value value = entry.getValue();

//      System.out.println("++++0 " + key.toString());
      if (value.isArray()) {
        // Determine whether this is an associative array.
        if (PhpTypes.arrayIsAssoc(env, value)) {
          json.put(key.toString(), PhpTypes.arrayToJsonObject(env, value));
        }
        else {
          json.put(key.toString(), PhpTypes.arrayToJsonArray(env, value));
        }
      }
      else if (value.isBoolean()) {
        json.put(key.toString(), value.toBoolean());
      }
      else if (value.isDouble()) {
        json.put(key.toString(), value.toJavaDouble());
      }
      else if (value.isNumeric()) {
        json.put(key.toString(), value.toLong());
      }
      else if (value.isString()) {
        json.put(key.toString(), value.toString());
      }
      else if(value instanceof ObjectExtValue){
//        System.out.println("0------ " + value.getType() + "  ----  "+ value.getClassName() + "  ----  "+ value.getResourceType()+ "  ----  "+ value.getQuercusClass());
        json.put(key.toString(), PhpTypes.arrayToJsonObject(env, value));
      }
      else {
//        System.out.println("1------ " + value.getType() + "  ----  "+ value.getClassName() + "  ----  "+ value.getResourceType()+ "  ----  "+ value.getQuercusClass());
        if(value.getClassName() == null || value.getClassName().equals("NULL")){
          json.put(key.toString(), (Object)null);
        }
        else{
          json.put(key.toString(), value.toString());
        }
      }
    }
    return json;
  }

  /**
   * Converts a PHP array to a JSON array.
   *
   * @param env The Quercus environment.
   * @param array A PHP array value.
   * @return A populated JsonArray instance.
   */
  public static JsonArray arrayToJsonArray(Env env, Value array) {
    JsonArray json = new JsonArray();
    Iterator<Map.Entry<Value, Value>> iter = array.getIterator(env);

    while (iter.hasNext()) {
      Map.Entry<Value, Value> entry = iter.next();

      Value value = entry.getValue();
      Value key = entry.getKey();

      if (value.isArray()) {
        if (PhpTypes.arrayIsAssoc(env, value)) {
          json.add(PhpTypes.arrayToJsonObject(env, value));
        }
        else {
          json.add(PhpTypes.arrayToJsonArray(env, value));
        }
      }
      else if (value.isBoolean()) {
        json.add(value.toBoolean());
      }
      else if (value.isDouble()) {
        json.add(value.toJavaDouble());
      }
      else if (value.isNumeric()) {
          json.add(value.toLong());
      }
      else if (value.isString()) {
        json.add(value.toString());
      }
      else if(value instanceof ObjectExtValue){
//        System.out.println("3++++ " + key.toString() + "    ,    " + value.toString());
//        System.out.println("3------ " + value.getType() + "  ----  "+ value.getClassName() + "  ----  "+ value.getResourceType()+ "  ----  "+ value.getQuercusClass());
        json.add(PhpTypes.arrayToJsonObject(env, value));
      }
      else {
//        System.out.println("4------ " + value.getType() + "  ----  "+ value.getClassName() + "  ----  "+ value.getResourceType()+ "  ----  "+ value.getQuercusClass());
        if(value.getClassName() == null || value.getClassName().equals("NULL")){
          json.add((Object)null);
        }
        else{
          json.add(value.toString());
        }
      }
    }
    return json;
  }

  /**
   * Determines whether a PHP array is associative by looking at the keys.
   */
  private static boolean arrayIsAssoc(Env env, Value array) {
    Iterator<Value> iter = array.getKeyIterator(env);
    while (iter.hasNext()) {
      Value key = iter.next();
      if (key.isString() || key.isBoolean()) {
        return true;
      }
    }
    return false;
  }

  public static Value arrayFromJson(Env env, JsonObject json) {
    return arrayFromJson(env, json, false);
  }

    /**
     * Converts a JSON object to a PHP array.
     *
     * @param env The Quercus environment.
     * @param json A Vert.x json object.
     * @return A populated PHP array.
     */
  public static Value arrayFromJson(Env env, JsonObject json, boolean asStdClass) {
    Value result;
    if(asStdClass) {
      result = env.createObject();
    }
    else{
      result = new ArrayValueImpl();
    }

    Map<String, Object> map = json.getMap();
    Iterator<String> iter = map.keySet().iterator();
    while (iter.hasNext()) {
      String key = iter.next();
      Object value = json.getValue(key);
      if (value instanceof JsonObject) {
        if(asStdClass) {
          result.putField(env, env.createString(key), PhpTypes.arrayFromJson(env, (JsonObject) value, asStdClass));
        }
        else {
          result.put(env.createString(key), PhpTypes.arrayFromJson(env, (JsonObject) value, asStdClass));
        }
      }
      else if (value instanceof JsonArray) {
        if(asStdClass) {
          result.putField(env, env.createString(key), PhpTypes.arrayFromJson(env, (JsonArray) value, asStdClass));
        }
        else {
          result.put(env.createString(key), PhpTypes.arrayFromJson(env, (JsonArray) value, asStdClass));
        }
      }
      else {
        if(asStdClass) {
          result.putField(env, env.createString(key), env.wrapJava(value));
        }
        else {
          result.put(env.createString(key), env.wrapJava(value));
        }
      }
    }
    return result;
  }


  public static Value arrayFromJson(Env env, MultiMap map) {
    return arrayFromJson(env, map, false);
  }

  /**
   * Converts a MultiMap object to a PHP array.
   *
   * @param env The Quercus environment.
   * @param json A Vert.x json object.
   * @return A populated PHP array.
   */
  public static Value arrayFromJson(Env env, MultiMap map, boolean asStdClass) {
    Value result;
    if(asStdClass) {
      result = env.createObject();
    }
    else{
      result = new ArrayValueImpl();
    }

    List<Map.Entry<String, String>> entries = map.entries();
    for (int i=0; i < entries.size(); i++) {
      Map.Entry<String, String> entry = entries.get(i);
      String key = entry.getKey();
      Object value = entry.getValue();
      if(asStdClass){
        result.putField(env, env.createString(key), env.wrapJava(value));
      }
      else{
        result.put(env.createString(key), env.wrapJava(value));
      }
    }
    return result;
  }


  public static ArrayValue arrayFromJson(Env env, JsonArray json) {
    return arrayFromJson(env, json, false);
  }

  /**
   * Converts a JSON array to a PHP array.
   *
   * @param env The Quercus environment.
   * @param json A Vert.x json array.
   * @return A populated PHP array.
   */
  public static ArrayValue arrayFromJson(Env env, JsonArray json, boolean asStdClass) {
    ArrayValue result = new ArrayValueImpl();

    Iterator<Object> iter = json.iterator();

//    Iterator<Map.Entry<Value, Value>> iter = array.getIterator(env);

//      Map.Entry<Value, Value> entry = iter.next();
//
//      Value key = entry.getKey();
//      Value value = entry.getValue();

    while (iter.hasNext()) {
      Object value = iter.next();
      if (value instanceof JsonObject) {
        result.put(PhpTypes.arrayFromJson(env, (JsonObject) value, asStdClass));
      }
      else if (value instanceof JsonArray) {
        result.put(PhpTypes.arrayFromJson(env, (JsonArray) value, asStdClass));
      }
      else {
        result.put(env.wrapJava(value));
      }
    }
    return result;
  }

}
