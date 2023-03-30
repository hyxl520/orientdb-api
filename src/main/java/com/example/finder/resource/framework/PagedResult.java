package com.example.finder.resource.framework;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页结果
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-03-02 14:24
 * @email 1158055613@qq.com
 */
@Getter
public class PagedResult<R> {
    private final List<R> sources = new ArrayList<>();

    private final PageConfig pageConfig;

    private final Long total;

    public PagedResult(List<R> sources, PageConfig pageConfig, Long total) {
        this.sources.addAll(sources);
        this.total = total;
        this.pageConfig = pageConfig;
    }
}
