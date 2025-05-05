package com.atix.demo.utils.mapper;

import java.util.List;

/**
 * Interface that defines the requirement to map a class into a Bus, promoting
 * Open-Closed principle
 */
public interface Mappeable<T> {
    /**
     * Indicates the kinds of operations for which the Mappeable class is compatible with
     *
     * @return a List of the supported class, for which the class must be mapped
     */
    List<T> getSupportedTypes();
}
