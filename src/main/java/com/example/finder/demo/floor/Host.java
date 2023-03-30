package com.example.finder.demo.floor;

import com.example.finder.graph.framework.Vertex;
import lombok.*;

import java.util.Date;

/**
 * 主机
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-28 13:17
 * @email 1158055613@qq.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Host  implements Vertex {
    private Long id;

    private Date inDate;

    private String ip;

    private Long machineRoom;
}
