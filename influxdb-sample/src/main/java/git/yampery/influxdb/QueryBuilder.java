package git.yampery.influxdb;

import com.influxdb.exceptions.InfluxException;
import git.yampery.common.StringUtils;

/**
 * <p>InfluxDB查询语法构建</p>
 *
 * @author Yampery
 * @date 2021/12/31 18:02
 */
public class QueryBuilder {

    private String range = "|> range(start: %s, stop: %s)";
    private String rangeStart = "|> range(start: %s)";
    private String filter = "|> filter(fn: (r) => r[\"%s\"] == \"%s\")";
    private String limit = "  |> limit(n:%d, offset: %d)";
    private String sort = " |> sort(columns: [\"%s\"], desc: %b)";

    public static final String TIME = "_time";
    public static final String VALUE = "_value";

    private static final String SYMBOL = " |> ";

    private static StringBuilder queryBuilder;

    private String bucket;

    private String start;

    private String stop;

    private QueryBuilder() {
        queryBuilder = new StringBuilder();
    }

    public static QueryBuilder builder() {
        return new QueryBuilder();
    }

    public QueryBuilder bucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public QueryBuilder start(String start) {
        this.start = start;
        return this;
    }

    public QueryBuilder stop(String stop) {
        this.stop = stop;
        return this;
    }

    public QueryBuilder measurement(String measurement) {
        queryBuilder.append(String.format(filter, "_measurement", measurement));
        return this;
    }

    public QueryBuilder field(String field) {
        queryBuilder.append(String.format(filter, "_field", field));
        return this;
    }

    public QueryBuilder page(int count, int offset) {
        queryBuilder.append(String.format(limit, count, offset));
        return this;
    }

    public QueryBuilder sort(String field) {
        queryBuilder.append(String.format(sort, field, false));
        return this;
    }

    public QueryBuilder sort(String field, boolean desc) {
        queryBuilder.append(String.format(sort, field, desc));
        return this;
    }

    public String build() {
        queryBuilder = new StringBuilder("from(bucket:\"").append(bucket).append("\")")
                .append(range())
                .append(queryBuilder);
        return queryBuilder.toString();
    }


    /**
     * 构建日期
     */
    private String range() {
        if (StringUtils.isBlank(start)) {
            throw new InfluxException("missing required argument start");
        }
        if (StringUtils.isBlank(stop)) {
            return String.format(rangeStart, start);
        } else {
            return String.format(range, start, stop);
        }
    }
}
