package vip.equals.common.compose;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * idGet函数和根据获取到的id获得的对象的dataSet函数
 *
 * @author H.J
 * @version 1.0.0
 */
public class IdGetDataSet<D, ID, SD> {

    private Function<D, ID> idGet;
    private BiConsumer<D, SD> dataSet;

    public IdGetDataSet() {
    }

    public IdGetDataSet(Function<D, ID> idGet, BiConsumer<D, SD> dataSet) {
        this.idGet = idGet;
        this.dataSet = dataSet;
    }

    public Function<D, ID> getIdGet() {
        return idGet;
    }

    public void setIdGet(Function<D, ID> idGet) {
        this.idGet = idGet;
    }

    public BiConsumer<D, SD> getDataSet() {
        return dataSet;
    }

    public void setDataSet(BiConsumer<D, SD> dataSet) {
        this.dataSet = dataSet;
    }

    public static <D, ID, SD> IdGetDataSet<D, ID, SD> of(Function<D, ID> idGet, BiConsumer<D, SD> dataSet) {
        return new IdGetDataSet<>(idGet, dataSet);
    }
}
