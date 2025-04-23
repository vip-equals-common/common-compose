package vip.equals.common.compose;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *
 * 合并组装数据
 * 当已有一组数据(list)时，需要为list中每一个元素设置一些属性，这些属性值都需要通过查询数据库或feignApi来获取
 * 禁止for循环来单个查询数据库或feignApi来获取数据后设置
 * 当从数据库中查询了一组数据后，需要组装数据，实现此接口，批量获取额外的需要组装的数据，然后组装
 * 一般来讲使用者只需要提供获取id方法（要组装数据的id），和设置数据的方法即可
 * 最大限度的复用代码
 * OrderVO{
 * ...
 * userId
 * userEntity
 * ...
 * }
 * OrderVO中有userId，页面展示需要使用UserEntity的name和profile
 * 可以写一个UserCompose类来完成从原数据中批量获取userId，批量查询数据，然后设置回原数据在中
 * 使用者只需要提供从原数据中获取userId方法，设置UserEntity的方法即可
 * OrderVO场景中
 * 获取userId方法: OrderVO::getUserId
 * 设置UserEntity方法: OrderVO::setUserEntity
 * 以后需要用到批量获取设置UserEntity的地方，都可以复用UserCompose
 * </pre>
 *
 * @author H.J
 * @version 1.0.0
 */
public interface ICompose<V> {

    /**
     * 执行合并
     *
     * @param vs 数据列表
     */
    void up(List<V> vs);

    /**
     * 执行合并
     *
     * @param v 数据
     */
    default void up(V v) {
        if (v != null) {
            List<V> list = new ArrayList<>();
            list.add(v);
            up(list);
        }
    }

    /**
     * 执行合并
     *
     * @param ds 数据
     */
    default void up(IList<V> ds) {
        List<V> list = ds.getList();
        if (list != null && !list.isEmpty()) {
            up(list);
        }
    }

}
