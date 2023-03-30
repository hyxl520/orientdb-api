package com.example.finder.demo.people;

import com.example.finder.graph.framework.Vertex;
import com.example.finder.resource.framework.*;

import java.util.Date;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-23 10:58
 * @email 1158055613@qq.com
 */
public class PeopleTest {
    public static void main(String[] args) {
        ResourceGraph graph = new ResourceGraph(new OrientDBRepository());

        People people1 = new People("张三", "男", 18);
        People people2 = new People("李四", "男", 21);
        People people3 = new People("王五", "男", 25);
        People people4 = new People("黄六", "男", 23);
        People people5 = new People("胡七", "男", 22);
        People people6 = new People("赵八", "男", 27);
        People people7 = new People("孙九", "男", 21);

        ResourceNode<People> zhang = new GraphResourceNode<>(people1);
        ResourceNode<People> li = new GraphResourceNode<>(people2);
        ResourceNode<People> wang = new GraphResourceNode<>(people3);
        ResourceNode<People> huang = new GraphResourceNode<>(people4);
        ResourceNode<People> hu = new GraphResourceNode<>(people5);
        ResourceNode<People> zhao = new GraphResourceNode<>(people6);
        ResourceNode<People> sun = new GraphResourceNode<>(people7);

        ///*=================批量创建关系=================>*/
        //Map<FriendOf, ResourceNode<People>> toMap = new HashMap<>();
        //toMap.put(new FriendOf(), li);
        //toMap.put(new FriendOf(), wang);
        //toMap.put(new FriendOf(), huang);
        //toMap.put(new FriendOf(), hu);
        //List<ResourceRelation<? extends Edge>> relations = zhang.linksUndirected(toMap);
        //relations.forEach(item -> System.out.println(item.getInEdgeId() + "," + item.getOutEdgeId()));
        ///*=======================Finished======================<*/

        /*=================分页查询=================>*/
        PagedResult<ResourceNode<? extends Vertex>> pagedResult = graph.extractNodes(QueryParamsBuilder
                .newInstance()
                .addParams("sex", "男")
                .getParams(), new PageConfig(1, 5, true));
        System.out.println("分页结果：" + pagedResult.getSources());
        System.out.println("总条数：" + pagedResult.getTotal());
        /*=======================Finished======================<*/


        //张三和所有人都是同学
        zhang.linkUndirected(li, new GraphResourceRelation<>(new ClassMates(new Date())));
        zhang.linkUndirected(wang, new GraphResourceRelation<>(new ClassMates(new Date())));
        zhang.linkUndirected(huang, new GraphResourceRelation<>(new ClassMates(new Date())));
        zhang.linkUndirected(hu, new GraphResourceRelation<>(new ClassMates(new Date())));
        zhang.linkUndirected(zhao, new GraphResourceRelation<>(new ClassMates(new Date())));
        zhang.linkUndirected(sun, new GraphResourceRelation<>(new ClassMates(new Date())));

        //黄六和胡、赵、孙是朋友
        huang.linkUndirected(hu, new GraphResourceRelation<>(new FriendOf()));
        huang.linkUndirected(zhao, new GraphResourceRelation<>(new FriendOf()));
        huang.linkUndirected(sun, new GraphResourceRelation<>(new FriendOf()));

        //胡和张、李、王是朋友
        hu.linkUndirected(zhang, new GraphResourceRelation<>(new FriendOf()));
        hu.linkUndirected(li, new GraphResourceRelation<>(new FriendOf()));
        hu.linkUndirected(wang, new GraphResourceRelation<>(new FriendOf()));

        /*=================关系查找=================>*/

        //查找张三的朋友的朋友
        graph
                .getGraphResourceNodeMatcher()
                .asStart(zhang)
                .findUndirected(FriendOf.class)
                .findUndirected(FriendOf.class)
                .collect(People.class)
                .forEach(item -> System.out.println(item.getSource()));
        /*=======================Finished======================<*/


    }

}
