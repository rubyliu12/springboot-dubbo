package com.dubbo.common.util;

import com.dubbo.common.util.annotation.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 必填参数校验
 *
 * @author lukew
 * @eamil 13507615840@163.com
 * @create 2018-10-25 22:31
 **/

public class RequiredParameterValidator {

    private RequiredParameterValidator() {
    }

    /**
     * 获取object对象属性中注解NotNull.class的并校验必填
     *
     * @param object
     * @return
     */
    public static Result validate(Object object) {

        List<Field> fields = ReflectionUtils.getFields(object);
        List<String> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fields)) {
            fields.forEach(field -> {
                if (field.isAnnotationPresent(NotNull.class)) {
                    list.add(field.getName());
                }
            });
        }
        return validator(object, list);
    }

    /**
     * 指定属性校验必填
     *
     * @param object
     * @param list
     * @return
     */
    public static Result validate(Object object, String... list) {

        return validator(object, Arrays.asList(list));
    }

    public static Result validator(Object object, List<String> list) {

        if (CollectionUtils.isEmpty(list)) {
            return Result.result(null);
        }
        StringBuilder stringBuffer = new StringBuilder();
        List<Field> fields = ReflectionUtils.getFields(object);
        Map<String, Field> fieldMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(fields)) {
            fields.forEach(field -> fieldMap.put(field.getName(), field));
            list.forEach(key -> {
                if (fieldMap.containsKey(key)) {
                    Object value = ReflectionUtils.getFieldValue(object, key);
                    if (value == null || StringUtils.isEmpty(value.toString())) {
                        NotNull notNull = fieldMap.get(key).getAnnotation(NotNull.class);
                        String property = StringUtils.isBlank(notNull.value()) ? key : notNull.value();
                        stringBuffer.append(property).append(" 不可为空 ! ");
                    }
                }
            });
        }
        return Result.result(stringBuffer.toString());
    }

    public static class Result {

        private boolean success;

        private String msg;

        static Result result(String msg) {

            Result result = new Result();
            result.setMsg(msg);
            result.setSuccess(StringUtils.isBlank(msg));
            return result;
        }

        public boolean isSuccess() {
            return success;
        }

        void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMsg() {
            return msg;
        }

        void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
