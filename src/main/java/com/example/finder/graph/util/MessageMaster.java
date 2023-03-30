package com.example.finder.graph.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息对象管理类
 *
 * @Auther Huang Yongxiang
 * @Date 2021/09/29 16:12
 */
public class MessageMaster {
    /**
     * 序列化消息时是否忽略没使用Expose注解的字段
     */
    private boolean isExcludeFieldsWithoutExposeAnnotation;
    /**
     * 是否对data进行格式化，主要是对Long型和BigDecimal型转化成字符串型，保证前端显示正确
     */
    private boolean isFormatData;
    /**
     * 基础信息字段
     */
    private StringBuilder message;

    /**
     * 基础回执码字段
     */
    private Integer code;

    /**
     * 基础数据字段
     */
    private Object data;

    /**
     * 其他消息字段
     */
    private static Map<String, Object> others;

    public MessageMaster() {
        others = new ConcurrentHashMap<>();
    }

    /**
     * 给定基本参数直接返回一个消息的JSON字符串
     *
     * @param code    状态值
     * @param message 消息
     * @return java.lang.String
     * @author Huang Yongxiang
     * @date 2021/9/30 9:24
     */
    public static String getMessage(int code, String message) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("code", code);
        msg.put("message", message);
        return JsonUtil.getMapJson(msg);
    }

    public static String getMessage(Code code, String message) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("code", code.getFlag());
        msg.put("message", message);
        return JsonUtil.getMapJson(msg);
    }

    /**
     * 判断该消息字符串是否为成功回执
     *
     * @param message 由该类产生的消息字符串
     * @return boolean
     * @author Huang Yongxiang
     * @date 2022/3/15 10:30
     */
    public static boolean isMessageOk(String message) {
        if (StringUtils.isEmpty(message)) {
            return false;
        }
        try {
            JsonObject jsonObject = JsonUtil.stringToJsonObj(message);
            return jsonObject.get("code").getAsInt() == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static <T> T getDataAsClassType(String message, Class<T> clazz) {
        if (StringUtils.isEmpty(message) || clazz == null) {
            return null;
        }
        try {
            JsonObject jsonObject = JsonUtil.stringToJsonObj(message);
            return JsonUtil.jsonStringToPojo(jsonObject.get("data").getAsString(), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<JsonObject> getListData(String message) {
        if (StringUtils.isEmpty(message)) {
            return new LinkedList<>();
        }
        try {
            JsonObject jsonObject = JsonUtil.stringToJsonObj(message);
            JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
            List<JsonObject> jsonObjects = new LinkedList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObjects.add(jsonArray.get(i).getAsJsonObject());
            }
            return jsonObjects;
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    private static Map<String, Object> convertData(Object data) {
        return ObjectUtil.convertFieldsToStringByClass(data, Long.class, BigDecimal.class, Class.class);
    }

    private static List<Map<String, Object>> convertListData(Object data) {
        if (data == null) {
            return new LinkedList<>();
        }
        if (data instanceof Collection) {
            Collection<?> listData = (Collection<?>) data;
            List<Map<String, Object>> list = new LinkedList<>();
            listData.forEach(d -> {
                if (d == null) {
                    return;
                }
                if (d instanceof Map) {
                    list.add(convertMapData(d));
                } else {
                    list.add(ObjectUtil.convertFieldsToStringByClass(d, Long.class, BigDecimal.class, Class.class));
                }
            });
            return list;
        }
        return new LinkedList<>();
    }

    private static Map<String, Object> convertMapData(Object data) {
        if (data == null) {
            return new HashMap<>();
        }
        if (data instanceof Map) {
            Map<?, ?> d = (Map<?, ?>) data;
            Map<String, Object> result = new HashMap<>();
            ;
            for (Map.Entry<?, ?> entry : d.entrySet()) {
                if (entry == null) {
                    continue;
                }
                String key = entry.getKey() instanceof String ? (String) entry.getKey() : String.valueOf(entry.getKey());
                Object value = null;
                if (entry.getValue() instanceof BigDecimal || entry.getValue() instanceof Long || entry.getValue() instanceof Class) {
                    value = String.valueOf(entry.getValue());
                } else {
                    value = entry.getValue();
                }
                result.put(key, value);
            }
            return result;
        }
        return new HashMap<>();

    }

    public static String getMessageEntry(String message) {
        if (StringUtils.isEmpty(message)) {
            return null;
        }
        try {
            return JsonUtil.stringToJsonObj(message).get("message").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonObject getData(String message) {
        if (StringUtils.isEmpty(message)) {
            return null;
        }
        JsonObject jsonObject = JsonUtil.stringToJsonObj(message);
        return jsonObject.get("data").getAsJsonObject();
    }


    /**
     * 给定基本参数直接返回一个消息的JSON字符串
     *
     * @param code    状态值
     * @param message 消息
     * @param data    数据
     * @return java.lang.String
     * @author Huang Yongxiang
     * @date 2021/9/30 9:24
     */
    public static String getMessage(int code, String message, Object data) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("code", code);
        msg.put("message", message);
        msg.put("data", data);
        return JsonUtil.getMapJson(msg);
    }

    public static String getMessage(Code code, String message, Object data) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("code", code.getFlag());
        msg.put("message", message);
        msg.put("data", data);
        return JsonUtil.getMapJson(msg);
    }

    public static String getMessage(Code code, String message, Object data, boolean isFormatData) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("code", code.getFlag());
        msg.put("message", message);
        if (isFormatData) {
            if (data instanceof String || data instanceof Number) {
                msg.put("data", data);
            } else if (data instanceof Collection) {
                msg.put("data", convertListData(data));
            } else if (data instanceof Map) {
                msg.put("data", convertMapData(data));
            } else {
                msg.put("data", convertData(data));
            }
        } else {
            msg.put("data", data);
        }
        return JsonUtil.getMapJson(msg);
    }


    public String getMessage() {
        return message.toString();
    }

    public void setMessage(String message) {
        this.message = new StringBuilder(message);
        others.put("message", message);
    }

    /**
     * 插入一条新的消息字段，除基本字段：code,message,data之外的消息字段，如果原字段存在则会覆盖
     *
     * @param key   消息名
     * @param value 消息内容
     * @return void
     * @author Huang Yongxiang
     * @date 2021/9/29 17:01
     */
    public void insertNewMessage(String key, Object value) {
        others.put(key, value);
    }

    /**
     * 从消息对象中移除一个消息字段
     *
     * @param key 消息名
     * @return java.lang.Object 不存在返回null
     * @author Huang Yongxiang
     * @date 2021/9/29 17:01
     */
    public Object removeMessage(String key) {
        return others.remove(key);
    }

    /**
     * 重写toString方法，返回值是一个消息的格式化JSON字符串
     *
     * @param
     * @return java.lang.String
     * @author Huang Yongxiang
     * @date 2021/9/29 17:12
     */
    @Override
    public String toString() {
        return JsonUtil.getMapJson(others);
    }

    public Integer getCode() {
        return code;
    }

    public MessageMaster setCode(Integer code) {
        this.code = code;
        others.put("code", code);
        return this;
    }

    public MessageMaster setCode(Code code) {
        this.code = code.flag;
        others.put("code", code.flag);
        return this;
    }

    public Object getData() {
        return data;
    }

    public MessageMaster setData(Object data) {
        if (isFormatData) {
            if (data instanceof String || data instanceof Number) {
                this.data = data;
            } else if (data instanceof Collection) {
                this.data = convertListData(data);
            } else if (data instanceof Map) {
                this.data = convertMapData(data);
            } else {
                this.data = convertData(data);
            }
            others.put("data", this.data);
        } else {
            this.data = data;
            others.put("data", data);
        }
        return this;
    }

    public boolean isExcludeFieldsWithoutExposeAnnotation() {
        return isExcludeFieldsWithoutExposeAnnotation;
    }

    public MessageMaster setExcludeFieldsWithoutExposeAnnotation(boolean excludeFieldsWithoutExposeAnnotation) {
        isExcludeFieldsWithoutExposeAnnotation = excludeFieldsWithoutExposeAnnotation;
        return this;
    }

    public MessageMaster setFormatData(boolean formatData) {
        isFormatData = formatData;
        return this;
    }

    public enum DefaultMessage {
        CONNECT_TIME_OUT(Code.ERROR, "服务连接超时，请稍后再试"), GET_DATA_TIME_OUT(Code.ERROR, "数据拉取连接超时，请稍后再试"), EMPTY_PARAMS(Code.BAD_REQUEST, "请指定参数"), LOGIN_EXPIRED(Code.FORBIDDEN, "没有足够权限访问资源"), SYSTEM_ERROR(Code.ERROR, "系统内部错误，程序猿抢修中...");
        private final Code code;
        private final String message;

        DefaultMessage(Code code, String message) {
            this.code = code;
            this.message = message;
        }

        public Code getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return MessageMaster.getMessage(code, message);
        }
    }

    /**
     * 状态枚举
     *
     * @author Huang Yongxiang
     * @date 2021/9/30 9:25
     */
    public enum Code {
        /**
         * 请求成功
         */
        OK("success", 200),
        /**
         * 服务器内部错误
         */
        ERROR("error", 500),
        /**
         * 客户端请求的语法错误，服务器无法理解
         */
        BAD_REQUEST("bad request", 400),
        /**
         * 服务器理解请求客户端的请求，但是拒绝执行此请求
         */
        FORBIDDEN("Forbidden", 403),
        /**
         * 超时
         */
        TIME_OUT("time out", 408),
        /**
         * 服务器无法根据客户端请求的内容特性完成请求
         */
        NOT_ACCEPTABLE("Not Acceptable", 406),
        /**
         * 服务器不支持请求的功能，无法完成请求
         */
        NOT_SUPPORT("Not Implemented", 501),
        /**
         * 已接受。已经接受请求，但未处理完成
         */
        ACCEPTED("Accepted", 202);
        private String message;
        private Integer flag;

        Code(String message, int flag) {
            this.message = message;
            this.flag = flag;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getFlag() {
            return flag;
        }

        public void setFlag(Integer flag) {
            this.flag = flag;
        }


    }


}
