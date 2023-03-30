package com.example.finder.demo.floor;

import com.example.finder.graph.framework.Edge;
import lombok.*;

import java.util.Date;

/**
 * 拥有关系 a have b
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-28 13:28
 * @email 1158055613@qq.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Have implements Edge {
    private Date date;
}
