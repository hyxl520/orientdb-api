package com.example.finder.demo.floor;

import com.example.finder.graph.framework.Vertex;
import lombok.*;

/**
 * 楼宇
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-28 13:07
 * @email 1158055613@qq.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Floor implements Vertex {
    private Long id;

    private String address;

    private Integer numberOfFloors;
}
