package com.example.finder.resource.framework;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 分页配置
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-03-02 14:20
 * @email 1158055613@qq.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageConfig {
    private int pageNum = 1;

    private int pageSize = 10;

    private boolean getTotalCount = true;
}
