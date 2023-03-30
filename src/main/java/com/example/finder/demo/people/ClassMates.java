package com.example.finder.demo.people;

import com.example.finder.graph.framework.Edge;
import lombok.Data;

import java.util.Date;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-26 13:48
 * @email 1158055613@qq.com
 */
@Data
public class ClassMates implements Edge {
    private final Date knowDate;

    public ClassMates(Date knowDate) {
        this.knowDate = knowDate;
    }
}
