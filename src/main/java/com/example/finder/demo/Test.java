package com.example.finder.demo;

import com.example.finder.demo.floor.Floor;
import com.example.finder.demo.floor.Have;
import com.example.finder.demo.floor.Host;
import com.example.finder.demo.floor.MachineRoom;
import com.example.finder.graph.framework.Vertex;
import com.example.finder.resource.framework.*;

import java.util.Date;
import java.util.List;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-03-03 11:23
 * @email 1158055613@qq.com
 */
public class Test {
    public static void main(String[] args) {
        Floor floor1 = new Floor(1L, "四川省 成都市", 23);

        MachineRoom room1 = new MachineRoom(1L, 10, 1L, "10-1");
        MachineRoom room2 = new MachineRoom(2L, 6, 1L, "6-1");

        Host host1 = new Host(1L, new Date(), "127.0.0.1", 1L);
        Host host2 = new Host(2L, new Date(), "126.0.0.1", 1L);
        Host host3 = new Host(3L, new Date(), "125.0.0.1", 2L);
        Host host4 = new Host(4L, new Date(), "124.0.0.1", 3L);

        ResourceNode<Floor> floorNode1 = new GraphResourceNode<>(floor1);

        ResourceNode<MachineRoom> roomNode1 = new GraphResourceNode<>(room1);
        ResourceNode<MachineRoom> roomNode2 = new GraphResourceNode<>(room2);

        ResourceNode<Host> hostNode1 = new GraphResourceNode<>(host1);
        ResourceNode<Host> hostNode2 = new GraphResourceNode<>(host2);
        ResourceNode<Host> hostNode3 = new GraphResourceNode<>(host3);
        ResourceNode<Host> hostNode4 = new GraphResourceNode<>(host4);

        ResourceRelation<Have> haveResourceRelation = new GraphResourceRelation<>(new Have(new Date()));

        floorNode1.link(roomNode1, new GraphResourceRelation<>(new Have(new Date())));

        floorNode1.link(roomNode2, new GraphResourceRelation<>(new Have(new Date())));

        roomNode1.link(hostNode1, haveResourceRelation);

        roomNode2.link(hostNode2, new GraphResourceRelation<>(new Have(new Date())));
        roomNode2.link(hostNode3, new GraphResourceRelation<>(new Have(new Date())));
        roomNode2.link(hostNode4, new GraphResourceRelation<>(new Have(new Date())));


        ResourceGraph graph = new ResourceGraph(new OrientDBRepository());

        PagedResult<ResourceNode<? extends Vertex>> hostPagedResult = graph
                .getGraphResourceNodeMatcher()
                .asStart(floorNode1)
                .findDirected(Have.class, RelationDirection.OUT)
                .findDirected(Have.class, RelationDirection.OUT)
                .collect(Host.class, new PageConfig(1, 5, true));

        hostPagedResult
                .getSources()
                .forEach(item -> System.out.println(item.getSource()));

        List<ResourceNode<? extends Vertex>> result = graph.shortestPath(floorNode1, hostNode2, RelationDirection.OUT, 3, Have.class);
        result.forEach(item -> System.out.println(item.getSource()));

        graph.relink(haveResourceRelation, roomNode2);





    }
}
