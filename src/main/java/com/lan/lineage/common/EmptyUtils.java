package com.lan.lineage.common;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author lanxueri
 * @ClassName EmptyUtils
 * @Description TODO
 * @createTime 2020-07-31
 */
public class EmptyUtils {

    private EmptyUtils() {

    }

    /**
     * 判断集合是否为空 coll->null->true coll-> coll.size() == 0 -> true
     */
    public static <T> boolean isEmpty(Collection<T> coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * 判断集合是否不为空
     */
    public static <T> boolean isNotEmpty(Collection<T> coll) {
        return !isEmpty(coll);
    }

    /**
     * 判断map是否为空
     */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 判断map是否不为空
     */
    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    /**
     * 判断一个对象是否为空
     */
    public static <T> boolean isEmpty(T t) {
        if (t == null) {
            return true;
        }
        return StringUtils.isEmpty(t.toString());
    }

    /**
     * 判断数组是否不为空
     */
    public static <T> boolean isNotEmpty(T[] datas) {
        return !isEmpty(datas);
    }

    /**
     * 判断数组是否不为空
     */
    public static <T> boolean isEmpty(T[] datas) {
        return ObjectUtils.isEmpty(datas);
    }


    /**
     * 判断一个对象是否不为空
     */
    public static <T> boolean isNotEmpty(T t) {
        return !isEmpty(t);
    }


}
