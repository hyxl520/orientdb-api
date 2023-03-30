package com.example.finder.resource.framework;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.TransactionManager;
import com.example.finder.graph.framework.Vertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 资源图
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-23 9:35
 * @email 1158055613@qq.com
 */
public class ResourceGraph {
    private final ResourceRepository resourceRepository;

    public ResourceGraph(ResourceRepository resourceRepository) {
        if (resourceRepository == null) {
            throw new NullPointerException("存储库不得为空");
        }
        this.resourceRepository = resourceRepository;
    }

    /**
     * 放入一个新节点到图，该操作是立即完成的
     *
     * @param source 节点对象
     * @return com.example.finder.resource.framework.ResourceNode<T>
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/27 13:29
     */
    public <T extends Vertex> ResourceNode<T> putNode(T source) {
        ResourceNode<T> node = new GraphResourceNode<>(source);
        if (node.save()) {
            return node;
        }
        return null;
    }

    /**
     * 批量放入节点，该操作是事务性的，一个顶点插入失败将会全部回滚，该操作是立即完成的
     *
     * @param sources 节点对象
     * @return int
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/27 13:31
     */
    public <T extends Vertex> int putNodes(List<T> sources) {
        Map<Class<? extends Vertex>, T> classBooleanMap = new HashMap<>();
        sources.forEach(item -> classBooleanMap.put(item.getClass(), item));
        classBooleanMap
                .values()
                .forEach(Vertex::createSchema);
        AtomicInteger result = new AtomicInteger();
        TransactionManager.doTransaction(session -> {
            result.set((int) sources
                    .stream()
                    .map(item -> new GraphResourceNode<>(item, false))
                    .map(GraphResourceNode::save)
                    .filter(item -> item)
                    .count());
            return true;
        });
        return result.get();
    }


    /**
     * 从图中取出一个节点
     *
     * @param nodeId 节点ID
     * @return com.example.finder.resource.framework.ResourceNode<T>
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/27 13:31
     */
    @SuppressWarnings("unchecked")
    public <T extends Vertex> ResourceNode<T> extractNode(String nodeId) {
        return (ResourceNode<T>) resourceRepository.queryNode(nodeId);
    }

    /**
     * 按照参数从图中查询取出一个节点，如果有多个节点只返回第一个，这种情况应该使用{@link #extractNodes(Map)}
     *
     * @param params 参数
     * @return com.example.finder.resource.framework.ResourceNode<T>
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/27 13:36
     */
    @SuppressWarnings("unchecked")
    public <T extends Vertex> ResourceNode<T> extractNode(Map<String, Object> params) {
        return (ResourceNode<T>) resourceRepository.queryNode(params);
    }

    public List<ResourceNode<? extends Vertex>> extractNodes(Map<String, Object> params) {
        return resourceRepository.queryNodes(params);
    }

    public PagedResult<ResourceNode<? extends Vertex>> extractNodes(Map<String, Object> params, PageConfig pageConfig) {
        return resourceRepository.queryNodes(params, pageConfig);
    }

    public PagedResult<ResourceNode<? extends Vertex>> extractNodes(Class<? extends Vertex> type, PageConfig pageConfig) {
        return resourceRepository.queryNodes(type, pageConfig);
    }

    /**
     * 从图中取出一条关系
     *
     * @param edgeId 关系ID
     * @return com.example.finder.resource.framework.ResourceRelation<E>
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/27 13:38
     */
    @SuppressWarnings("unchecked")
    public <E extends Edge> ResourceRelation<E> extractRelation(String edgeId) {
        return (ResourceRelation<E>) resourceRepository.queryEdge(edgeId);
    }

    /**
     * 按照参数从图中查询取出一条关系，如果有多个关系匹配只返回第一个，这种情况应该使用{@link #extractRelations(Map)}
     *
     * @param params 参数
     * @return com.example.finder.resource.framework.ResourceRelation<E>
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/27 13:38
     */
    @SuppressWarnings("unchecked")
    public <E extends Edge> ResourceRelation<E> extractRelation(Map<String, Object> params) {
        return (ResourceRelation<E>) resourceRepository.queryEdge(params);
    }

    public List<ResourceRelation<? extends Edge>> extractRelations(Map<String, Object> params) {
        return resourceRepository.queryEdges(params);
    }

    public PagedResult<ResourceRelation<? extends Edge>> extractRelations(Map<String, Object> params, PageConfig pageConfig) {
        return resourceRepository.queryEdges(params, pageConfig);
    }

    /**
     * 通过关系Id直接判断是否是有向边
     *
     * @param edgeId 关系ID
     * @return boolean
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/27 13:41
     */
    public boolean isDirectedRelation(String edgeId) {
        ResourceRelation<? extends Edge> relation = extractRelation(edgeId);
        if (relation == null) {
            throw new NullPointerException("关系不存在");
        }
        return relation.isDirected();
    }

    /**
     * 将某条关系重定向到另外一个节点
     *
     * @param relation 从图中取出的某条关系
     * @param to       从图中取出的某个目标节点
     * @return boolean
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/27 13:39
     */
    public boolean relink(ResourceRelation<? extends Edge> relation, ResourceNode<? extends Vertex> to) {
        if (relation == null) {
            throw new NullPointerException("关系不能为null");
        }
        Map<String, Object> inParams = new HashMap<>();
        inParams.put("in", to.getRecordId());
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("out", to.getRecordId());
        if (!relation.isDirected()) {
            return TransactionManager.doTransaction(session -> resourceRepository.update(inParams, relation.getInEdgeId(), Edge.class) >= 0 && resourceRepository.update(outParams, relation.getOutEdgeId(), Edge.class) >= 0);
        }
        return resourceRepository.update(inParams, relation.getRecordId(), Edge.class) >= 0;
    }

    /**
     * 查找图中两个节点之间的最短路径，路径默认查询OUT方向，最大深度不限，可包含任意的关系类型
     *
     * @param startNode 起始节点
     * @param endNode   终止节点
     * @return java.util.List<com.example.finder.resource.framework.ResourceNode < ? extends com.example.finder.graph.framework.Vertex>>
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/3/1 14:34
     */
    public List<ResourceNode<? extends Vertex>> shortestPath(ResourceNode<? extends Vertex> startNode, ResourceNode<? extends Vertex> endNode) {
        return shortestPath(startNode, endNode, RelationDirection.OUT, -1);
    }

    /**
     * 计算出两个节点的最短路径
     *
     * @param startNode 起始节点
     * @param endNode   终止节点
     * @param direction 路径关系方向
     * @param depth     最大深度
     * @param types     要求路径包含的关系类型
     * @return java.util.List<com.example.finder.resource.framework.ResourceNode < ? extends com.example.finder.graph.framework.Vertex>>
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/3/1 14:32
     */
    @SafeVarargs
    public final List<ResourceNode<? extends Vertex>> shortestPath(ResourceNode<? extends Vertex> startNode, ResourceNode<? extends Vertex> endNode, RelationDirection direction, int depth, Class<? extends Edge>... types) {
        return resourceRepository.shortestPath(startNode, endNode, direction, depth, types);
    }

    /**
     * 图的遍历
     *
     * @param startNode    起始遍历顶点
     * @param findType     要获取的目标节点类型
     * @param filterParams 过滤参数
     * @param depth        遍历深度过深会占用大量内存，过浅不能找到所有顶点
     * @param strategy     遍历策略，深度优先占用内存小但速度慢，广度优先占用内存大但速度快，一般情况下能较快的求出最优解
     * @param config       分页配置
     * @return com.example.finder.resource.framework.PagedResult<com.example.finder.resource.framework.ResourceNode < ? extends com.example.finder.graph.framework.Vertex>>
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/3/6 11:21
     */
    public final PagedResult<ResourceNode<? extends Vertex>> traverse(ResourceNode<? extends Vertex> startNode, Class<? extends Vertex> findType, Map<String, Object> filterParams, int depth, TraverseStrategy strategy, PageConfig config) {
        return resourceRepository.traverse(startNode, findType, filterParams, depth, strategy, config);
    }

    @SafeVarargs
    public final PagedResult<ResourceNode<? extends Vertex>> traverse(ResourceNode<? extends Vertex> startNode, Map<String, Object> filterParams, int depth, TraverseStrategy strategy, PageConfig config, Class<? extends Vertex>... findTypes) {
        return resourceRepository.traverse(startNode, filterParams, depth, strategy, config, findTypes);
    }


    /**
     * 获取一个图节点匹配器，用于关系查找
     *
     * @return com.example.finder.resource.framework.GraphMatcher
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/28 14:50
     */
    public GraphNodeMatcher<ResourceNode<? extends Vertex>> getGraphResourceNodeMatcher() {
        return new ResourceNodeGraphMatcher();
    }

    /**
     * 获取一个图关系匹配器，用于关系查找
     *
     * @return com.example.finder.resource.framework.GraphEdgeMatcher<com.example.finder.resource.framework.ResourceRelation < ? extends com.example.finder.graph.framework.Edge>>
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/3/1 15:38
     */
    public GraphEdgeMatcher<ResourceRelation<? extends Edge>> getGraphResourceRelationMatcher() {
        return new ResourceRelationGraphMatcher();
    }
}
