package com.example.finder.demo.floor;

import com.example.finder.resource.framework.OrientDBRepository;
import com.example.finder.resource.framework.QueryParamsBuilder;
import com.example.finder.resource.framework.ResourceGraph;
import com.example.finder.resource.framework.ResourceNode;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-03-09 14:17
 * @email 1158055613@qq.com
 */
public class FloorTestThree {
    public static void main(String[] args){
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(3, 3, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

        poolExecutor.submit(() -> {
            ResourceGraph graph = new ResourceGraph(new OrientDBRepository());

            //查询ip为126.0.0.1的主机
            ResourceNode<Host> find1 = graph.extractNode(QueryParamsBuilder
                    .newInstance()
                    .addParams("ip", "121.0.0.1")
                    .getParams());
            System.out.println(find1.getSource());
        });
        poolExecutor.submit(() -> {
            ResourceGraph graph = new ResourceGraph(new OrientDBRepository());

            //查询ip为126.0.0.1的主机
            ResourceNode<Host> find1 = graph.extractNode(QueryParamsBuilder
                    .newInstance()
                    .addParams("ip", "127.0.0.1")
                    .getParams());
            System.out.println(find1.getSource());
        });

    }
}
