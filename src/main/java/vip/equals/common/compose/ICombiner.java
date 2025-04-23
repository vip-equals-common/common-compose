package vip.equals.common.compose;


import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 数据合并器
 *
 * @author H.J
 * @version 1.0.0
 */
public interface ICombiner<K, D, CTX> {

    /**
     * 合并组装数据
     *
     * @param vs          数据列表，此数列表数据不完整，需要进行组装
     * @param idFunction  获取id的函数
     * @param setFunction 设置数据的函数
     * @param <V>         数据对象
     */
    default <V> void combine(List<V> vs, Function<V, K> idFunction, BiConsumer<V, D> setFunction) {
        if (vs == null || vs.isEmpty()) {
            return;
        }
        combine(vs, idFunction, setFunction, null);
    }

    /**
     * 合并组装数据
     *
     * @param vs          数据列表，此数列表数据不完整，需要进行组装
     * @param idFunction  获取id的函数
     * @param setFunction 设置数据的函数
     * @param ctx         上下文
     * @param <V>         数据对象
     */
    <V> void combine(List<V> vs, Function<V, K> idFunction, BiConsumer<V, D> setFunction, CTX ctx);

    /**
     * 合并组装数据
     *
     * @param v           数据，此数列表数据不完整，需要进行组装
     * @param idFunction  获取id的函数
     * @param setFunction 设置数据的函数
     * @param <V>         数据对象
     */
    default <V> void combine(V v, Function<V, K> idFunction, BiConsumer<V, D> setFunction) {
        if (v == null) {
            return;
        }
        List<V> list = new ArrayList<>();
        list.add(v);
        combine(list, idFunction, setFunction, null);
    }

    /**
     * 合并组装数据
     *
     * @param v           数据，此数列表数据不完整，需要进行组装
     * @param idFunction  获取id的函数
     * @param setFunction 设置数据的函数
     * @param ctx         上下文
     * @param <V>         数据对象
     */
    default <V> void combine(V v, Function<V, K> idFunction, BiConsumer<V, D> setFunction, CTX ctx) {
        if (v == null) {
            return;
        }
        List<V> list = new ArrayList<>();
        list.add(v);
        combine(list, idFunction, setFunction, ctx);
    }

    /**
     * 合并组装数据
     *
     * @param iList       数据列表，此数列表数据不完整，需要进行组装
     * @param idFunction  获取id的函数
     * @param setFunction 设置数据的函数
     * @param <V>         数据对象
     */
    default <V> void combine(IList<V> iList, Function<V, K> idFunction, BiConsumer<V, D> setFunction) {
        if (iList == null || iList.getList() == null || iList.getList().isEmpty()) {
            return;
        }
        combine(iList.getList(), idFunction, setFunction, null);
    }

    /**
     * 合并组装数据
     *
     * @param iList       包含数据列表的对象，此数列表数据不完整，需要进行组装
     * @param idFunction  获取id的函数
     * @param setFunction 设置数据的函数
     * @param ctx         上下文
     * @param <V>         数据对象
     */
    default <V> void combine(IList<V> iList, Function<V, K> idFunction, BiConsumer<V, D> setFunction, CTX ctx) {
        if (iList == null || iList.getList() == null || iList.getList().isEmpty()) {
            return;
        }
        combine(iList.getList(), idFunction, setFunction, ctx);
    }
}
