package com.example.finder.demo.people;

import com.example.finder.graph.framework.Vertex;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-23 10:56
 * @email 1158055613@qq.com
 */
@Getter
@Setter
@ToString
public class People implements Vertex {

    private String name;

    private String sex;

    private Integer age;

    public People(String name, String sex, Integer age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }

    public People() {
    }
}
