package com.atix.demo.utils.mapper;


import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class with utilities to handle {@link Mappeable}
 */
public class MappeableUtils {
    private static final Logger logger = LoggerFactory.getLogger(MappeableUtils.class);

    /**
     * Fills a {@link Map} from a list of {@link Mappeable}
     *
     * @param <T>  the type of the Mappeable
     * @param map  the target map
     * @param objs the list of Mappeables
     */
    public static <T> void doMap(Map<T, ? super Mappeable<T>> map, List<? extends Mappeable<T>> objs) {
        for (Mappeable<T> obj : objs) {
            if (obj.getSupportedTypes() == null)
                continue;
            for (T id : obj.getSupportedTypes()) {
                logger.info("Bean of type {} registered for {}", obj.getClass(), id);
                map.put(id, obj);
            }
        }
    }

    /**
     * Gets the matching {@link Mappeable} from a map. This is used
     * only for String Mappeables, and will return an exact match, if it
     * exists, of a partial match if no perfect match is found. If you need
     * to find a match in other kinds of Mappeables then consider using
     * {@link MappeableUtils#getExactMatch(Map, Object)}
     *
     * @param map  the map with the {@link Mappeable}
     * @param type the type assigned to the Mappeable that is being searched
     * @return the matching {@link Mappeable}. If no match is found then
     *         {@code null} is returned
     */
    public static Mappeable<String> getExactOrPartialMatch(
            Map<String, Mappeable<String>> map, String type) {
        String currKey = "";
        for (String key : map.keySet()) {
            if (type.startsWith(key) && key.length() > currKey.length()) {
                currKey = key;
            }
        }
        return map.get(currKey);
    }

    /**
     * Gets the matching {@link Mappeable} from a map
     *
     * @param map  the map with the {@link Mappeable}
     * @param type the type assigned to the Mappeable that is being searched
     * @return the matching {@link Mappeable}. If no match is found then
     *         {@code null} is returned
     */
    public static <T> Mappeable<T> getExactMatch(
            Map<T, Mappeable<T>> map, T type) {
        return map.get(type);
    }
}
