package com.example.finder.resource.framework;

import lombok.Getter;

/**
 * 关系方向
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-28 13:10
 * @email 1158055613@qq.com
 */
@Getter
public enum RelationDirection {
    OUT("out"), IN("in"), BOTH("both");

    private final String name;

    RelationDirection(String name) {
        this.name = name;
    }
}
