package vip.equals.common.compose;


import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 数据合并器
 * <pre>
 * 用于处理一个对象中有多个属性都是同一个类型对象数据
 * 如对象中有:creator，updater 都要获取他们的对应的对象数据，同时creator，updater都是同一类型数据对象
 * 如按ICombiner来组装，则需要调用二次（数据库/缓存调用二次）
 * IMultiCombiner则可以一次解决获creator，updater 同一类型数据对象的获取和设值
 * </pre>
 *
 * @author H.J
 * @version 1.0.0
 */
public interface IMultiCombiner<K, D, CTX> extends ICombiner<K, D, CTX> {

    /**
     * 合并组装数据
     *
     * @param vs          数据列表，此数列表数据不完整，需要进行组装
     * @param idFunction  获取id的函数
     * @param setFunction 设置数据的函数
     * @param ctx         上下文
     * @param <V>         数据对象
     */
    default <V> void combine(List<V> vs, Function<V, K> idFunction, BiConsumer<V, D> setFunction, CTX ctx) {
        List<IdGetDataSet<V, K, D>> getSets = new ArrayList<>();
        getSets.add(new IdGetDataSet<>(idFunction, setFunction));
        combine(vs, getSets, ctx);
    }

    /**
     * 合并组装数据
     *
     * @param vs      数据列表，此数列表数据不完整，需要进行组装
     * @param getSets 获取id的函数,设置数据的函数
     * @param <V>     数据对象
     */
    default <V> void combine(List<V> vs, List<IdGetDataSet<V, K, D>> getSets) {
        if (vs == null || vs.isEmpty()) {
            return;
        }
        combine(vs, getSets, null);
    }

    /**
     * 合并组装数据
     *
     * @param vs      数据列表，此数列表数据不完整，需要进行组装
     * @param getSets 获取id的函数,设置数据的函数
     * @param ctx     上下文
     * @param <V>     数据对象
     */
    <V> void combine(List<V> vs, List<IdGetDataSet<V, K, D>> getSets, CTX ctx);

    /**
     * 合并组装数据
     *
     * @param v       数据，此数列表数据不完整，需要进行组装
     * @param getSets 获取id的函数,设置数据的函数
     * @param <V>     数据对象
     */
    default <V> void combine(V v, List<IdGetDataSet<V, K, D>> getSets) {
        if (v == null) {
            return;
        }
        List<V> list = new ArrayList<>();
        list.add(v);
        combine(list, getSets, null);
    }

    /**
     * 合并组装数据
     *
     * @param v       数据，此数列表数据不完整，需要进行组装
     * @param getSets 获取id的函数,设置数据的函数
     * @param ctx     上下文
     * @param <V>     数据对象
     */
    default <V> void combine(V v, List<IdGetDataSet<V, K, D>> getSets, CTX ctx) {
        if (v == null) {
            return;
        }
        List<V> list = new ArrayList<>();
        list.add(v);
        combine(list, getSets, ctx);
    }

    /**
     * 合并组装数据
     *
     * @param iList   包含数据列表的对象，通过getList获取数据列表，此数据列表数据不完整，需要进行组装
     * @param getSets 获取id的函数,设置数据的函数
     * @param <V>     数据对象
     */
    default <V> void combine(IList<V> iList, List<IdGetDataSet<V, K, D>> getSets) {
        if (iList == null || iList.getList() == null || iList.getList().isEmpty()) {
            return;
        }
        combine(iList.getList(), getSets, null);
    }

    /**
     * 合并组装数据
     *
     * @param iList   包含数据列表的对象，通过getList获取数据列表，此数据列表数据不完整，需要进行组装
     * @param getSets 获取id的函数,设置数据的函数
     * @param ctx     上下文
     * @param <V>     数据对象
     */
    default <V> void combine(IList<V> iList, List<IdGetDataSet<V, K, D>> getSets, CTX ctx) {
        if (iList == null || iList.getList() == null || iList.getList().isEmpty()) {
            return;
        }
        combine(iList.getList(), getSets, ctx);
    }
}
