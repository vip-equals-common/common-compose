package vip.equals.common.compose;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author H.J
 * @version 1.0.0
 */
public final class ComposeUtil {

    private ComposeUtil() {
    }

    /**
     * 组装fk（外键）对象
     * 1-1
     *
     * @param d             D对象，内有一个外键属性，即SD的主键
     * @param fkFunction    获取D的外键，即SD的主键
     * @param sdFunction    通过SD获取对应的对象
     * @param sdSetFunction 处理SD对象方法，如setSD
     * @param <D>           对象
     * @param <FK>          D的一个属性，D需要关联对象SD的主键
     * @param <SD>          需要关联的对象
     * @return D
     */
    public static <D, FK, SD> D up(D d,
                                   Function<D, FK> fkFunction,
                                   Function<FK, SD> sdFunction,
                                   BiConsumer<D, SD> sdSetFunction) {
        if (d == null || fkFunction == null || sdFunction == null || sdSetFunction == null) {
            return d;
        }
        FK fk = fkFunction.apply(d);
        if (fk == null) {
            return d;
        }
        SD apply = sdFunction.apply(fk);
        if (apply == null) {
            return d;
        }
        sdSetFunction.accept(d, apply);
        return d;
    }

    /**
     * 为d的子list组装数据
     *
     * <pre>
     * OrderVO对象中有List<OrderItemVO>
     * OrderItemVO中已有itemId，要设置itemName的值
     *
     * OrderVO
     *    String orderId
     *    List<OrderItemVO> orderItems
     * OrderItemVO
     *    orderId
     *    itemId
     *    itemName
     * </pre>
     *
     * @param d            D
     * @param subFunction  获取对象的子数组 (获取List<OrderItemVO>)
     * @param sidFunction  获取子对象外键 OrderItemVO::getItemId
     * @param dataFunction 批量获取数据，并转为Map形式 可以使用 baseManager#findByBidsAsMap
     * @param consumer     设置数据
     */
    public static <D, CD, SID, SD> void up4SubList(D d,
                                                   Function<D, List<CD>> subFunction,
                                                   Function<CD, SID> sidFunction,
                                                   Function<List<SID>, Map<SID, SD>> dataFunction,
                                                   BiConsumer<CD, SD> consumer) {
        up4SubList(d, subFunction, sidFunction, dataFunction, consumer, false);
    }

    /**
     * 为d的子list组装数据
     *
     * <pre>
     * OrderVO对象中有List<OrderItemVO>
     * OrderItemVO中已有itemId，要设置itemName的值
     *
     * OrderVO
     *    String orderId
     *    List<OrderItemVO> orderItems
     * OrderItemVO
     *    orderId
     *    itemId
     *    itemName
     * </pre>
     *
     * @param d                D
     * @param subFunction      获取对象的子数组 (获取List<OrderItemVO>)
     * @param sidFunction      获取子对象外键 OrderItemVO::getItemId
     * @param dataFunction     批量获取数据，并转为Map形式 可以使用 baseManager#findByBidsAsMap
     * @param consumer         设置数据
     * @param emptyDataConsume 数据为空时，是否设置
     */
    public static <D, CD, SID, SD> void up4SubList(D d,
                                                   Function<D, List<CD>> subFunction,
                                                   Function<CD, SID> sidFunction,
                                                   Function<List<SID>, Map<SID, SD>> dataFunction,
                                                   BiConsumer<CD, SD> consumer,
                                                   boolean emptyDataConsume) {
        if (d == null || subFunction == null || sidFunction == null || dataFunction == null || consumer == null) {
            return;
        }
        Set<SID> sidSet = new HashSet<>();
        List<CD> listCd = subFunction.apply(d);
        if (listCd == null || listCd.isEmpty()) {
            return;
        }
        for (CD cd : listCd) {
            SID sid = sidFunction.apply(cd);
            if (sid != null) {
                sidSet.add(sid);
            }
        }
        if (sidSet.isEmpty()) {
            return;
        }
        Map<SID, SD> data = dataFunction.apply(new ArrayList<>(sidSet));
        for (CD cd : listCd) {
            SID sid = sidFunction.apply(cd);
            if (sid == null) {
                continue;
            }
            SD sd = data.get(sid);
            if (sd != null || emptyDataConsume) {
                consumer.accept(cd, sd);
            }
        }
    }

    /**
     * 组装fk（外键）对象并转换
     * 1-1
     * <pre>
     * 场景：如果有Order订单，其中有一个属性是userId，代表是属于哪个User的订单
     *
     * Order:
     *     oid: 订单ID
     *     userId: 订单属于哪个用户
     * User:
     *     userId：用户ID
     *     userName：用户名字
     *
     * 页面展示VO为：
     * OrderVO
     *     oid: 订单ID
     *     userId: 订单属于哪个用户
     *     userName: 用户名字
     * fkFunction:   Order::getUserId
     * sdFunction: 根据userId查询User
     * dConvertFunction: Order -> OrderVo
     * sdFunction: OrderVO设置User
     * </pre>
     *
     * @param d                D对象，内有一个外键属性，即SD的主键
     * @param fkFunction       获取D的外键，即SD的主键
     * @param sdFunction       通过SD获取对应的对象
     * @param dConvertFunction d的转换器
     * @param sdSetFunction    D转换后对象处理SD对象方法，如setSD
     * @param <D>              对象
     * @param <FK>             D的一个属性，D需要关联对象SD的主键
     * @param <SD>             需要关联的对象
     * @param <V>              转换后的对象
     * @return V D转换后的对象，且设置了SD对象
     */
    public static <D, FK, SD, V> V up(D d,
                                      Function<D, FK> fkFunction,
                                      Function<FK, SD> sdFunction,
                                      Function<D, V> dConvertFunction,
                                      BiConsumer<V, SD> sdSetFunction) {
        if (d == null || fkFunction == null || sdFunction == null || dConvertFunction == null || sdSetFunction == null) {
            return null;
        }
        FK fk = fkFunction.apply(d);
        V v = dConvertFunction.apply(d);
        if (fk == null) {
            return v;
        }
        SD apply = sdFunction.apply(fk);
        if (apply == null) {
            return v;
        }
        sdSetFunction.accept(v, apply);
        return v;
    }

    /**
     * 组装fk（外键）对象
     * 1-N
     *
     * @param d                 D对象，内有一个外键属性，即SD的主键
     * @param pkFunction        获取D的外键，即SD的主键
     * @param sdListFunction    通过SD获取对应的对象list
     * @param sdListSetFunction SD对象list方法，如setSD
     * @param <D>               对象
     * @param <PK>              D的一个属性，D需要关联对象SD的主键
     * @param <SD>              需要关联的对象
     * @return D
     */
    public static <D, PK, SD> D upList(D d,
                                       Function<D, PK> pkFunction,
                                       Function<PK, List<SD>> sdListFunction,
                                       BiConsumer<D, List<SD>> sdListSetFunction) {
        if (d == null || pkFunction == null || sdListFunction == null || sdListSetFunction == null) {
            return d;
        }
        PK pk = pkFunction.apply(d);
        if (pk == null) {
            return d;
        }
        List<SD> apply = sdListFunction.apply(pk);
        if (apply == null) {
            return d;
        }
        sdListSetFunction.accept(d, apply);
        return d;
    }


    /**
     * 1-N 组装
     *
     * <pre>
     * 场景：如果有Order订单，有ItemId的list
     *
     * Order:
     *     oid: 订单ID
     *     List<itemId>: 订单包含的商品
     * Item:
     *     itemId：商品ID
     *     itemName：商品名字
     *     oid: 订单ID
     *
     * 页面展示VO为：
     * OrderVO
     *     oid: 订单ID
     *     userId: 订单属于哪个用户
     *     userName: 用户名字
     *     List<ItemVO>: 商品列表
     *
     * pkFunction:   Order::getOid
     * sdListFunction: oid -> itemIdList -> ItemList -> ItemVOList
     * dConvertFunction: Order -> OrderVo
     * sdListSetFunction: OrderVO设置ItemVOList
     * </pre>
     *
     * @param d                 D对象，内有一个主键属性，SD中关联D的属性
     * @param pkFunction        获取D的主键，SD中关联D的属性
     * @param sdListFunction    通过PK获取对应的对象List
     * @param dConvertFunction  d的转换器
     * @param sdListSetFunction D转换后对象处理SD对象方法，如setSD
     * @param <D>               对象
     * @param <PK>              D的一个属性（一般为主键），SD中关联D的属性
     * @param <SD>              需要关联的对象
     * @param <V>               转换后的对象
     * @return V D转换后的对象，且设置了SD对象
     */
    public static <D, PK, SD, V> V upList(D d,
                                          Function<D, PK> pkFunction,
                                          Function<PK, List<SD>> sdListFunction,
                                          Function<D, V> dConvertFunction,
                                          BiConsumer<V, List<SD>> sdListSetFunction) {
        if (d == null || pkFunction == null || sdListFunction == null || dConvertFunction == null || sdListSetFunction == null) {
            return null;
        }
        PK pk = pkFunction.apply(d);
        V v = dConvertFunction.apply(d);
        if (pk == null) {
            return v;
        }
        List<SD> apply = sdListFunction.apply(pk);
        if (apply == null) {
            return v;
        }
        sdListSetFunction.accept(v, apply);
        return v;
    }

    /**
     * 1-1 以list形式
     *
     * @param list         需要转换的list
     * @param idFunction   list中每个元素外键id
     * @param dataFunction id list 转换成 id -> data的map，通过ids去查相关数据库获取dataList，并转为map
     * @param consumer     d的设置data方法
     */
    public static <D, ID, SD> List<D> up(List<D> list,
                                         Function<D, ID> idFunction,
                                         Function<List<ID>, Map<ID, SD>> dataFunction,
                                         BiConsumer<D, SD> consumer) {
        if (list == null || list.isEmpty() || idFunction == null || dataFunction == null || consumer == null) {
            return list;
        }
        Set<ID> ids = list.stream().map(idFunction).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return list;
        }
        Map<ID, SD> allData = dataFunction.apply(new ArrayList<>(ids));
        for (D d : list) {
            ID id = idFunction.apply(d);
            if (id != null) {
                SD data = allData.get(id);
                if (data != null) {
                    consumer.accept(d, data);
                }
            }
        }
        return list;
    }

    /**
     * 1-1 以list形式
     * <p>
     * 同一个对象中，有多个属性，都是另一个对象的外键
     * 如：创建者，更新者
     * </p>
     *
     * @param list         需要转换的list
     * @param dataFunction id list 转换成 id -> data的map，通过ids去查相关数据库获取dataList，并转为map
     * @param getSets      d外键id的获取和设置data方法
     */
    public static <D, ID, SD> List<D> up(List<D> list,
                                         Function<List<ID>, Map<ID, SD>> dataFunction,
                                         List<IdGetDataSet<D, ID, SD>> getSets) {
        if (list == null || list.isEmpty() || getSets == null || getSets.isEmpty() || dataFunction == null) {
            return list;
        }
        Set<ID> all = new HashSet<>();
        for (IdGetDataSet<D, ID, SD> getSet : getSets) {
            List<ID> ids = list.stream().map(getSet.getIdGet()).filter(Objects::nonNull).collect(Collectors.toList());
            all.addAll(ids);
        }
        if (all.isEmpty()) {
            return list;
        }
        Map<ID, SD> allData = dataFunction.apply(new ArrayList<>(all));
        for (D d : list) {
            for (IdGetDataSet<D, ID, SD> getSet : getSets) {
                ID id = getSet.getIdGet().apply(d);
                if (id != null) {
                    SD data = allData.get(id);
                    if (data != null) {
                        getSet.getDataSet().accept(d, data);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 1-1 以list形式
     *
     * @param list            需要转换的list
     * @param idFunction      list中每个元素外键id
     * @param dataFunction    id list 转换成 id -> data的map，通过ids去查相关数据库获取dataList，并转为map
     * @param convertFunction t转换为v t的数据转换
     * @param consumer        v的设置data方法
     */
    public static <D, ID, SD, V> List<V> up(List<D> list,
                                            Function<D, ID> idFunction,
                                            Function<List<ID>, Map<ID, SD>> dataFunction,
                                            Function<D, V> convertFunction,
                                            BiConsumer<V, SD> consumer) {
        if (list == null || list.isEmpty() || idFunction == null || dataFunction == null || convertFunction == null || consumer == null) {
            return new ArrayList<>();
        }
        Set<ID> ids = list.stream().map(idFunction).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return list.stream().map(convertFunction).collect(Collectors.toList());
        }
        Map<ID, SD> allData = dataFunction.apply(new ArrayList<>(ids));
        ArrayList<V> vList = new ArrayList<>();
        for (D d : list) {
            V v = convertFunction.apply(d);
            ID id = idFunction.apply(d);
            if (id != null) {
                SD data = allData.get(id);
                if (data != null) {
                    consumer.accept(v, data);
                }
            }
            vList.add(v);
        }
        return vList;
    }

    /**
     * 组装子对象list 1-N
     *
     * <pre>
     *     场景：如果有Order订单的list，需要把
     * Order:
     *     oid: 订单ID
     * OrderItem:
     *     itemId：商品ID
     *     oid: 订单ID
     *     num: 数量
     *
     * 页面展示DTO为：
     * OrderDTO
     *     oid: 订单ID
     *     List<OrderItemDTO>: 商品订单关联关系
     * OrderItemDTO
     *     itemId：商品ID
     *     oid: 订单ID
     *     num: 数量
     * </pre>
     * <p>
     * List<D>为查出D的list
     * sidFunction可以使用 baseManager#findAllAndGroup 来获取中间关系表数据，groupBy D的id，同时可以转换
     *
     * @param list        需要转换的list
     * @param idFunction  list中每个元素id Order oid
     * @param sidFunction baseManager#findAllAndGroup 来获取中间关系表数据，groupBy D的id，同时可以转换, 对通过批量 oid查出来的 OrderItem 按oid进行分组，同时可以转换
     * @param consumer    设置List<SD>
     */
    public static <D, ID, SD> void upList(List<D> list,
                                          Function<D, ID> idFunction,
                                          Function<List<ID>, Map<ID, List<SD>>> sidFunction,
                                          BiConsumer<D, List<SD>> consumer) {
        if (list == null || list.isEmpty() || idFunction == null || sidFunction == null || consumer == null) {
            return;
        }
        Set<ID> ids = list.stream().map(idFunction).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return;
        }
        Map<ID, List<SD>> id2sidMap = sidFunction.apply(new ArrayList<>(ids));
        if (id2sidMap == null || id2sidMap.isEmpty()) {
            return;
        }
        for (D d : list) {
            List<SD> sdList = id2sidMap.get(idFunction.apply(d));
            if (sdList != null) {
                consumer.accept(d, sdList);
            }
        }
    }

    /**
     * 组装子对象list 1-N
     *
     * <pre>
     *     场景：如果有Order订单的list，需要把
     * Order:
     *     oid: 订单ID
     * OrderItem:
     *     itemId：商品ID
     *     oid: 订单ID
     *     num: 数量
     *
     * 页面展示DTO为：
     * OrderDTO
     *     oid: 订单ID
     *     List<OrderItemDTO>: 商品订单关联关系
     * OrderItemDTO
     *     itemId：商品ID
     *     oid: 订单ID
     *     num: 数量
     * </pre>
     * <p>
     * List<D>为查出D的list
     * sidFunction可以使用 baseManager#findAllAndGroup 来获取中间关系表数据，groupBy D的id，同时可以转换
     *
     * @param list        需要转换的list
     * @param idFunction  list中每个元素id Order oid
     * @param sidFunction baseManager#findAllAndGroup 来获取中间关系表数据，groupBy D的id，同时可以转换, 对通过批量 oid查出来的 OrderItem 按oid进行分组，同时可以转换
     * @param dFunction   d的转换方法
     * @param consumer    设置List<SD>
     */
    public static <D, ID, SD, V> List<V> upList(List<D> list,
                                                Function<D, ID> idFunction,
                                                Function<List<ID>, Map<ID, List<SD>>> sidFunction,
                                                Function<D, V> dFunction,
                                                BiConsumer<V, List<SD>> consumer) {
        if (list == null || list.isEmpty() || idFunction == null || sidFunction == null || dFunction == null || consumer == null) {
            return new ArrayList<>();
        }
        Set<ID> ids = list.stream().map(idFunction).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return list.stream().map(dFunction).collect(Collectors.toList());
        }
        Map<ID, List<SD>> id2sidMap = sidFunction.apply(new ArrayList<>(ids));
        if (id2sidMap == null || id2sidMap.isEmpty()) {
            return list.stream().map(dFunction).collect(Collectors.toList());
        }
        List<V> result = new ArrayList<>();
        for (D d : list) {
            V v = dFunction.apply(d);
            List<SD> sdList = id2sidMap.get(idFunction.apply(d));
            if (sdList != null) {
                consumer.accept(v, sdList);
            }
            result.add(v);
        }
        return result;
    }

    /**
     * 组装子对象list
     * <pre>
     * 用于多(D)对多(S)，中间关系表SD
     * 查出了D的list，需要为每个D设置对应的多个S
     * List<D>为查出D的list
     * sidFunction可以使用 baseManager#findAllAndGroup 来获取中间关系表数据，groupBy D的id, 转换方法为S的id
     * dataFunction可以使用 baseManager#findByBidsAsMap
     * </pre>
     *
     * @param list         需要转换的list
     * @param idFunction   list中每个元素id
     * @param sidFunction  List<ID> 所有ID， 返回ID List<SID>
     * @param dataFunction List<SID> 所有SID， 返回所有 DATA 以MAP形式
     * @param consumer     v的设置data方法
     */
    public static <D, ID, SD, SID> List<D> upDataList(List<D> list,
                                                      Function<D, ID> idFunction,
                                                      Function<List<ID>, Map<ID, List<SID>>> sidFunction,
                                                      Function<List<SID>, Map<SID, SD>> dataFunction,
                                                      BiConsumer<D, List<SD>> consumer) {
        if (list == null || list.isEmpty() || idFunction == null || dataFunction == null || consumer == null) {
            return list;
        }
        Set<ID> ids = list.stream().map(idFunction).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return list;
        }
        Map<ID, List<SID>> id2sidMap = sidFunction.apply(new ArrayList<>(ids));
        if (id2sidMap == null || id2sidMap.isEmpty()) {
            return list;
        }
        Collection<List<SID>> values = id2sidMap.values();
        Set<SID> sidSet = new HashSet<>();
        values.forEach(sidSet::addAll);
        Map<SID, SD> allData = dataFunction.apply(new ArrayList<>(sidSet));
        if (allData == null || allData.isEmpty()) {
            return list;
        }
        for (D d : list) {
            List<SID> sidList = id2sidMap.get(idFunction.apply(d));
            if (sidList != null) {
                List<SD> dataList = new ArrayList<>();
                for (SID sid : sidList) {
                    SD data = allData.get(sid);
                    if (data != null) {
                        dataList.add(data);
                    }
                }
                consumer.accept(d, dataList);
            }
        }
        return list;
    }


    /**
     * 组装子对象list
     * <pre>
     * 用于多(D)对多(S)，中间关系表SD
     * 查出了D的list，需要为每个D设置对应的多个S
     * List<D>为查出D的list
     * sidFunction可以使用 baseManager#findAllByExampleAndGroup 来获取中间关系表数据，groupBy D的id, 转换方法为S的id
     * dataFunction可以使用 baseManager#findByBidsAsMap
     * </pre>
     *
     * @param list            需要转换的list
     * @param idFunction      list中每个元素id
     * @param sidFunction     List<ID> 所有ID， 返回ID List<SID>
     * @param dataFunction    List<SID> 所有SID， 返回所有 DATA 以MAP形式
     * @param convertFunction t转换为v t的数据转换
     * @param consumer        v的设置data方法
     */
    public static <D, ID, SD, SID, V> List<V> upDataList(List<D> list,
                                                         Function<D, ID> idFunction,
                                                         Function<List<ID>, Map<ID, List<SID>>> sidFunction,
                                                         Function<List<SID>, Map<SID, SD>> dataFunction,
                                                         Function<D, V> convertFunction,
                                                         BiConsumer<V, List<SD>> consumer) {
        if (list == null || list.isEmpty() || idFunction == null || dataFunction == null || convertFunction == null || consumer == null) {
            return new ArrayList<>();
        }
        Set<ID> ids = list.stream().map(idFunction).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return list.stream().map(convertFunction).collect(Collectors.toList());
        }
        Map<ID, List<SID>> id2sidMap = sidFunction.apply(new ArrayList<>(ids));
        if (id2sidMap == null || id2sidMap.isEmpty()) {
            return list.stream().map(convertFunction).collect(Collectors.toList());
        }
        Collection<List<SID>> values = id2sidMap.values();
        Set<SID> sidSet = new HashSet<>();
        values.forEach(sidSet::addAll);
        Map<SID, SD> allData = dataFunction.apply(new ArrayList<>(sidSet));
        if (allData == null || allData.isEmpty()) {
            return list.stream().map(convertFunction).collect(Collectors.toList());
        }
        ArrayList<V> vList = new ArrayList<>();
        for (D d : list) {
            V v = convertFunction.apply(d);
            List<SID> sidList = id2sidMap.get(idFunction.apply(d));
            if (sidList != null) {
                List<SD> dataList = new ArrayList<>();
                for (SID sid : sidList) {
                    SD data = allData.get(sid);
                    if (data != null) {
                        dataList.add(data);
                    }
                }
                consumer.accept(v, dataList);
            }
            vList.add(v);
        }
        return vList;
    }

    /**
     * 为list数组中每个元素的子list组装数据
     *
     * <pre>
     * List<OrderVO>
     * OrderVO对象中有List<OrderItemVO>
     * OrderItemVO中已有itemId，要设置itemName的值
     *
     * OrderVO
     *    String orderId
     *    List<OrderItemVO> orderItems
     * OrderItemVO
     *    orderId
     *    itemId
     *    itemName
     * </pre>
     *
     * @param list         List<D>
     * @param subFunction  获取对象的子数组 (获取List<OrderItemVO>)
     * @param sidFunction  获取子对象外键 OrderItemVO::getItemId
     * @param dataFunction 批量获取数据，并转为Map形式 可以使用 baseManager#findByBidsAsMap
     * @param consumer     设置数据
     */
    public static <D, CD, SID, SD> void up4SubList(List<D> list,
                                                   Function<D, List<CD>> subFunction,
                                                   Function<CD, SID> sidFunction,
                                                   Function<List<SID>, Map<SID, SD>> dataFunction,
                                                   BiConsumer<CD, SD> consumer) {
        up4SubList(list, subFunction, sidFunction, dataFunction, consumer, false);
    }

    /**
     * 为list数组中每个元素的子list组装数据
     *
     * <pre>
     * List<OrderVO>
     * OrderVO对象中有List<OrderItemVO>
     * OrderItemVO中已有itemId，要设置itemName的值
     *
     * OrderVO
     *    String orderId
     *    List<OrderItemVO> orderItems
     * OrderItemVO
     *    orderId
     *    itemId
     *    itemName
     * </pre>
     *
     * @param list             List<D>
     * @param subFunction      获取对象的子数组 (获取List<OrderItemVO>)
     * @param sidFunction      获取子对象外键 OrderItemVO::getItemId
     * @param dataFunction     批量获取数据，并转为Map形式 可以使用 baseManager#findByBidsAsMap
     * @param consumer         设置数据
     * @param emptyDataConsume 数据为空时，是否设置
     */
    public static <D, CD, SID, SD> void up4SubList(List<D> list,
                                                   Function<D, List<CD>> subFunction,
                                                   Function<CD, SID> sidFunction,
                                                   Function<List<SID>, Map<SID, SD>> dataFunction,
                                                   BiConsumer<CD, SD> consumer,
                                                   boolean emptyDataConsume) {
        if (list == null || list.isEmpty() || subFunction == null || sidFunction == null || dataFunction == null || consumer == null) {
            return;
        }
        Set<SID> sidSet = new HashSet<>();
        for (D d : list) {
            List<CD> listCd = subFunction.apply(d);
            if (listCd == null || listCd.isEmpty()) {
                continue;
            }
            for (CD cd : listCd) {
                SID sid = sidFunction.apply(cd);
                if (sid != null) {
                    sidSet.add(sid);
                }
            }
        }
        if (sidSet.isEmpty()) {
            return;
        }
        Map<SID, SD> data = dataFunction.apply(new ArrayList<>(sidSet));
        for (D d : list) {
            List<CD> listCd = subFunction.apply(d);
            if (listCd == null || listCd.isEmpty()) {
                continue;
            }
            for (CD cd : listCd) {
                SID sid = sidFunction.apply(cd);
                if (sid == null) {
                    continue;
                }
                SD sd = data.get(sid);
                if (sd != null || emptyDataConsume) {
                    consumer.accept(cd, sd);
                }
            }
        }
    }


    /**
     * 组装list中数据
     * <pre>
     * 如:Order List中有uid，又查出了UserInfo list，需要把UserInfo 设置到Order的属性userInfo
     * Order{
     * oid : 1
     * uid : 1
     * userInfo : null
     * }
     * <p>
     * UserInfo{
     * uid : 1,
     * name : zhangsan
     * }
     * </pre>
     *
     * @param list          需要组装的列表
     * @param idFunction    list中子对象id
     * @param subList       子对象列表
     * @param subIdFunction 子对象id
     * @param consumer      设置子对象方法
     * @param <D>           D Order
     * @param <ID>          uid
     * @param <SD>          UserInfo
     * @return List 组装好的数据
     */
    public static <D, ID, SD> List<D> up(List<D> list,
                                         Function<D, ID> idFunction,
                                         List<SD> subList,
                                         Function<SD, ID> subIdFunction,
                                         BiConsumer<D, SD> consumer) {
        return up(list, idFunction, subList, subIdFunction, consumer, false);
    }


    /**
     * 组装list中数据
     * <pre>
     * 如:Order List中有uid，又查出了UserInfo list，需要把UserInfo 设置到Order的属性userInfo
     * Order{
     * oid : 1
     * uid : 1
     * userInfo : null
     * }
     * <p>
     * UserInfo{
     * uid : 1,
     * name : zhangsan
     * }
     * </pre>
     *
     * @param list             需要组装的列表
     * @param idFunction       list中子对象id
     * @param subList          子对象列表
     * @param subIdFunction    子对象id
     * @param consumer         设置子对象方法
     * @param emptySubConsumer 如果子对象没有查找到，是否需要调用consumer方法
     * @param <D>              D Order
     * @param <ID>             uid
     * @param <SD>             UserInfo
     * @return List 组装好的数据
     */
    public static <D, ID, SD> List<D> up(List<D> list,
                                         Function<D, ID> idFunction,
                                         List<SD> subList,
                                         Function<SD, ID> subIdFunction,
                                         BiConsumer<D, SD> consumer,
                                         boolean emptySubConsumer) {
        if (list == null || list.isEmpty() || idFunction == null || subList == null || subList.isEmpty() || consumer == null) {
            return list;
        }
        for (D d : list) {
            ID id = idFunction.apply(d);
            if (id == null) {
                continue;
            }
            SD subData = null;
            for (SD sd : subList) {
                ID sid = subIdFunction.apply(sd);
                if (id.equals(sid)) {
                    subData = sd;
                    break;
                }
            }
            if (subData != null) {
                consumer.accept(d, subData);
            } else {
                if (emptySubConsumer) {
                    consumer.accept(d, null);
                }
            }
        }
        return list;
    }

    /**
     * 转换list数据同时组装list中数据
     * <pre>
     * 如:Order List中有uid，又查出了UserInfo list，需要把UserInfo 设置到OrderVO的属性userInfo
     * Order{
     * oid : 1
     * uid : 1
     * }
     * <p>
     * UserInfo{
     * uid : 1,
     * name : zhangsan
     * }
     * <p>
     * 最终
     * OrderVO{
     * oid : 1
     * UserInfo : userInfo
     * }
     * </pre>
     *
     * @param list          需要组装的列表
     * @param idFunction    list中子对象id
     * @param subList       子对象列表
     * @param subIdFunction 子对象id
     * @param consumer      设置子对象方法
     * @param <D>           D Order
     * @param <ID>          uid
     * @param <SD>          UserInfo
     * @return List 组装好的数据
     */
    public static <D, V, ID, SD> List<V> up(List<D> list,
                                            Function<D, V> convertFunction,
                                            Function<D, ID> idFunction,
                                            List<SD> subList,
                                            Function<SD, ID> subIdFunction,
                                            BiConsumer<V, SD> consumer) {
        return up(list, convertFunction, idFunction, subList, subIdFunction, consumer, false);
    }

    /**
     * 转换list数据同时组装list中数据
     * <pre>
     * 如:Order List中有uid，又查出了UserInfo list，需要把UserInfo 设置到OrderVO的属性userInfo
     * Order{
     * oid : 1
     * uid : 1
     * }
     * <p>
     * UserInfo{
     * uid : 1,
     * name : zhangsan
     * }
     * <p>
     * 最终
     * OrderVO{
     * oid : 1
     * UserInfo : userInfo
     * }
     * </pre>
     *
     * @param list             需要组装的列表
     * @param convertFunction  转换方法
     * @param idFunction       list中子对象id
     * @param subList          子对象列表
     * @param subIdFunction    子对象id
     * @param consumer         设置子对象方法
     * @param emptyDataConsume 如果子对象没有查找到，是否需要调用consumer方法
     * @param <D>              D Order
     * @param <ID>             uid
     * @param <SD>             UserInfo
     * @return List 组装好的数据
     */
    public static <D, V, ID, SD> List<V> up(List<D> list,
                                            Function<D, V> convertFunction,
                                            Function<D, ID> idFunction,
                                            List<SD> subList,
                                            Function<SD, ID> subIdFunction,
                                            BiConsumer<V, SD> consumer,
                                            boolean emptyDataConsume) {
        if (list == null || list.isEmpty() || idFunction == null || subList == null || subList.isEmpty() || consumer == null) {
            return new ArrayList<>();
        }
        List<V> vList = new ArrayList<>(list.size());
        for (D d : list) {
            V v = convertFunction.apply(d);
            vList.add(v);
            ID id = idFunction.apply(d);
            if (id == null) {
                continue;
            }
            SD subData = null;
            for (SD sd : subList) {
                ID sid = subIdFunction.apply(sd);
                if (id.equals(sid)) {
                    subData = sd;
                    break;
                }
            }
            if (subData != null) {
                consumer.accept(v, subData);
            } else {
                if (emptyDataConsume) {
                    consumer.accept(v, null);
                }
            }
        }
        return vList;
    }
}
