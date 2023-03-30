package com.example.finder.demo.floor;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.Vertex;
import com.example.finder.graph.util.DateUtils;
import com.example.finder.resource.framework.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 测试，最好参考图数据库工作台的图显示界面
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-28 13:19
 * @email 1158055613@qq.com
 */
public class FloorTest {
    public static void main(String[] args) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(3, 3, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        /*=================创建实体类=================>*/
        //有两个楼宇
        Floor floor1 = new Floor(1L, "四川省 成都市", 23);
        Floor floor2 = new Floor(2L, "四川省 乐山市", 10);

        //有四个机房
        MachineRoom room1 = new MachineRoom(1L, 10, 1L, "10-1");
        MachineRoom room2 = new MachineRoom(2L, 6, 1L, "6-1");
        MachineRoom room3 = new MachineRoom(4L, 2, 2L, "2-1");
        MachineRoom room4 = new MachineRoom(5L, 6, 2L, "6-1");

        //有五个主机
        Host host1 = new Host(1L, new Date(), "127.0.0.1", 1L);
        Host host2 = new Host(2L, new Date(), "126.0.0.1", 1L);
        Host host3 = new Host(3L, new Date(), "125.0.0.1", 2L);
        Host host4 = new Host(4L, new Date(), "124.0.0.1", 3L);
        Host host5 = new Host(5L, new Date(), "123.0.0.1", 4L);
        /*=======================Finished======================<*/

        /*=================封装成顶点=================>*/
        ResourceNode<Floor> floorNode1 = new GraphResourceNode<>(floor1);
        ResourceNode<Floor> floorNode2 = new GraphResourceNode<>(floor2);

        ResourceNode<MachineRoom> roomNode1 = new GraphResourceNode<>(room1);
        ResourceNode<MachineRoom> roomNode2 = new GraphResourceNode<>(room2);
        ResourceNode<MachineRoom> roomNode3 = new GraphResourceNode<>(room3);
        ResourceNode<MachineRoom> roomNode4 = new GraphResourceNode<>(room4);

        ResourceNode<Host> hostNode1 = new GraphResourceNode<>(host1);
        ResourceNode<Host> hostNode2 = new GraphResourceNode<>(host2);
        ResourceNode<Host> hostNode3 = new GraphResourceNode<>(host3);
        ResourceNode<Host> hostNode4 = new GraphResourceNode<>(host4);
        ResourceNode<Host> hostNode5 = new GraphResourceNode<>(host5);
        /*=======================Finished======================<*/

        /*=================串联关系=================>*/
        //楼宇1拥有机房1和2
        ResourceRelation<Have> have1 = new GraphResourceRelation<>(new Have(null));
        floorNode1.link(roomNode1, have1);
        floorNode1.link(roomNode2, new GraphResourceRelation<>(new Have(new Date())));

        //楼宇2拥有机房3和4
        floorNode2.link(roomNode3, new GraphResourceRelation<>(new Have(new Date())));
        floorNode2.link(roomNode4, new GraphResourceRelation<>(new Have(new Date())));

        //机房1拥有主机1和2
        roomNode1.link(hostNode1, new GraphResourceRelation<>(new Have(new Date())));
        roomNode1.link(hostNode2, new GraphResourceRelation<>(new Have(new Date())));
        //roomNode2.link(hostNode2, new GraphResourceRelation<>(new Have(new Date())));

        roomNode2.link(hostNode3, new GraphResourceRelation<>(new Have(new Date())));
        roomNode3.link(hostNode4, new GraphResourceRelation<>(new Have(new Date())));
        roomNode4.link(hostNode5, new GraphResourceRelation<>(new Have(new Date())));
        /*=======================Finished======================<*/

        //创建资源图对象，其提供了对图的各种操作
        ResourceGraph graph = new ResourceGraph(new OrientDBRepository());
        /*=================实体查询=================>*/
        poolExecutor.submit(() -> {

            //查询ip为126.0.0.1的主机
            ResourceNode<Host> find1 = graph.extractNode(QueryParamsBuilder
                    .newInstance()
                    .addParams("ip", "126.0.0.1")
                    .getParams());
            System.out.println(find1.getSource());
        });
        /*=======================Finished======================<*/

        /*=================关系查询=================>*/
        //查询楼宇1的所有主机
        List<ResourceNode<? extends Vertex>> find2 = graph.getGraphResourceNodeMatcher()
                                                          //楼宇1作为起始查找节点
                                                          .asStart(floorNode1)
                                                          //查找楼宇1拥有的机房
                                                          .findDirected(Have.class, RelationDirection.OUT)
                                                          //查询楼宇1拥有的主机
                                                          .findDirected(Have.class, RelationDirection.OUT)
                                                          //收集查询结果为Host对象
                                                          .collect(Host.class);
        find2.forEach(item -> System.out.println(item.getSource()));

        //查询楼宇2的第二层Have关系
        List<ResourceRelation<? extends Edge>> find3 = graph
                .getGraphResourceRelationMatcher()
                .asStart(floorNode2)
                .findDirected(Have.class, RelationDirection.OUT)
                .findDirected(Have.class, RelationDirection.OUT)
                .collect(Have.class);
        find3.forEach(item -> System.out.println(item.getSource()));
        /*=======================Finished======================<*/

        /*=================路径查询=================>*/
        //查询楼宇1到主机2的最短路径
        List<ResourceNode<? extends Vertex>> find4 = graph.shortestPath(floorNode1, hostNode2, RelationDirection.OUT, 3, Have.class);
        find4.forEach(item -> System.out.println(item.getSource()));
        /*=======================Finished======================<*/

        /*=================属性修改=================>*/
        //修改楼宇1的地址
        System.out.println(floorNode1.update(QueryParamsBuilder
                .newInstance()
                .addParams("address", "四川省绵阳市")
                .getParams()));
        System.out.println(graph
                .extractNode(floorNode1.getRecordId())
                .getSource());

        //修改关系have1的日期
        System.out.println(have1.update(QueryParamsBuilder
                .newInstance()
                .addParams("date", DateUtils.addDays(new Date(), -1))
                .getParams()));
        System.out.println(graph
                .extractRelation(have1.getRecordId())
                .getSource());
        /*=======================Finished======================<*/
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
