package com.example.finder.demo.floor;

import com.example.finder.graph.framework.Vertex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-03-06 10:23
 * @email 1158055613@qq.com
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Net implements Vertex {
    private Long id;

    private Date date;
}
