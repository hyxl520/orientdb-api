package com.example.finder.demo.floor;

import com.example.finder.graph.framework.Vertex;
import lombok.*;

/**
 * 机房
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-28 13:15
 * @email 1158055613@qq.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineRoom implements Vertex {
    private Long id;

    private Integer onFloor;

    private Long floorCode;

    private String roomCOde;
}
