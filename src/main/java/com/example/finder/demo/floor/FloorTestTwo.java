package com.example.finder.demo.floor;

import com.example.finder.graph.framework.Vertex;
import com.example.finder.resource.framework.*;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-03-06 10:23
 * @email 1158055613@qq.com
 */
public class FloorTestTwo {
    public static void main(String[] args) {
        ////有两个楼宇
        //Floor floor1 = new Floor(1L, "四川省 成都市", 23);
        //Floor floor2 = new Floor(2L, "四川省 乐山市", 10);
        //
        ////一个局域网
        //Net net = new Net(1L, new Date());
        //
        ////有四个机房
        //MachineRoom room1 = new MachineRoom(1L, 10, 1L, "10-1");
        //MachineRoom room2 = new MachineRoom(2L, 6, 1L, "6-1");
        //MachineRoom room3 = new MachineRoom(4L, 2, 2L, "2-1");
        //MachineRoom room4 = new MachineRoom(5L, 6, 2L, "6-1");
        //
        ////有五个主机
        //Host host1 = new Host(1L, new Date(), "127.0.0.1", 1L);
        //Host host2 = new Host(2L, new Date(), "126.0.0.1", 1L);
        //Host host3 = new Host(3L, new Date(), "125.0.0.1", 2L);
        //Host host4 = new Host(4L, new Date(), "124.0.0.1", 3L);
        //Host host5 = new Host(5L, new Date(), "123.0.0.1", 4L);
        ///*=======================Finished======================<*/
        //
        ///*=================封装成顶点=================>*/
        //ResourceNode<Floor> floorNode1 = new GraphResourceNode<>(floor1);
        //ResourceNode<Floor> floorNode2 = new GraphResourceNode<>(floor2);
        //
        //ResourceNode<Net> netNode1 = new GraphResourceNode<>(net);
        //
        //ResourceNode<MachineRoom> roomNode1 = new GraphResourceNode<>(room1);
        //ResourceNode<MachineRoom> roomNode2 = new GraphResourceNode<>(room2);
        //ResourceNode<MachineRoom> roomNode3 = new GraphResourceNode<>(room3);
        //ResourceNode<MachineRoom> roomNode4 = new GraphResourceNode<>(room4);
        //
        //ResourceNode<Host> hostNode1 = new GraphResourceNode<>(host1);
        //ResourceNode<Host> hostNode2 = new GraphResourceNode<>(host2);
        //ResourceNode<Host> hostNode3 = new GraphResourceNode<>(host3);
        //ResourceNode<Host> hostNode4 = new GraphResourceNode<>(host4);
        //ResourceNode<Host> hostNode5 = new GraphResourceNode<>(host5);
        ///*=======================Finished======================<*/
        //
        ///*=================串联关系=================>*/
        ////楼宇1通过局域网连接机房1
        //floorNode1.link(netNode1, new GraphResourceRelation<>(new With()));
        //netNode1.link(roomNode1, new GraphResourceRelation<>(new With()));
        //floorNode1.link(roomNode2, new GraphResourceRelation<>(new Have(new Date())));
        //
        ////楼宇2拥有机房3和4
        //floorNode2.link(roomNode3, new GraphResourceRelation<>(new Have(new Date())));
        //floorNode2.link(roomNode4, new GraphResourceRelation<>(new Have(new Date())));
        //
        ////机房1拥有主机1和2
        //roomNode1.link(hostNode1, new GraphResourceRelation<>(new Have(new Date())));
        //roomNode1.link(hostNode2, new GraphResourceRelation<>(new Have(new Date())));
        ////roomNode2.link(hostNode2, new GraphResourceRelation<>(new Have(new Date())));
        //
        //roomNode2.link(hostNode3, new GraphResourceRelation<>(new Have(new Date())));
        //roomNode3.link(hostNode4, new GraphResourceRelation<>(new Have(new Date())));
        //roomNode4.link(hostNode5, new GraphResourceRelation<>(new Have(new Date())));
        ///*=======================Finished======================<*/
        ResourceGraph graph = new ResourceGraph(new OrientDBRepository());
        /*=================查询=================>*/
        //遍历查找楼宇1的所有主机
        PagedResult<ResourceNode<? extends Vertex>> result = graph.traverse(graph.extractNode("#172:10"), QueryParamsBuilder
                .newInstance()
                .getParams(), 7, TraverseStrategy.DEPTH_FIRST, new PageConfig(1, 10, true), Host.class,
                MachineRoom.class, Floor.class);
        result
                .getSources()
                .forEach(item -> System.out.println(item.getSource()));
        /*=======================Finished======================<*/
    }
}
