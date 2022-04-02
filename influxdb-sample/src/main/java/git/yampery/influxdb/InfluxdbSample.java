package git.yampery.influxdb;

import com.alibaba.fastjson.JSON;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.List;

/**
 * <p>Influxdb2.0 测试取样器
 * 1. 初始化参数
 * 2. 根据参数初始化安装应用
 * 3. 执行测试
 * 4. 测试结束，释放资源
 * </p>
 *
 * @author Yampery
 * @date 2019/3/22 19:44
 */
@Slf4j
public class InfluxdbSample extends AbstractJavaSamplerClient {

    private static final String WRITE_OPT = "write";
    private static final String QUERY_OPT = "query";

    public static InfluxDBClient client = null;

    /**
     * 初始化参数
     *
     * @return Arguments
     */
    @Override
    public Arguments getDefaultParameters() {
        log.info("Get default parameters");
        Arguments arguments = new Arguments();
        arguments.addArgument("protocol", "http");
        arguments.addArgument("host", "localhost");
        arguments.addArgument("port", "8086");
        arguments.addArgument("token", "Please input the generator token.");
        arguments.addArgument("bucket", "jmeter");
        arguments.addArgument("org", "org");
        arguments.addArgument("opt", "write or query");
        arguments.addArgument("data", "write or query data");
        return arguments;
    }

    /**
     * 初始化安装
     */
    @Override
    public void setupTest(JavaSamplerContext context) {
        String protocol = context.getParameter("protocol");
        String host = context.getParameter("host");
        String port = context.getParameter("port");
        String token = context.getParameter("token");

        String url = String.format("%s://%s:%s", protocol, host, port);
        client = InfluxDBClientFactory.create(url, token.toCharArray());
        log.info("Connection：{}", url);
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult results = new SampleResult();
        // 事务开始
        results.sampleStart();
        String bucket = context.getParameter("bucket");
        String org = context.getParameter("org");
        String opt = context.getParameter("opt");
        String data = context.getParameter("data");
        try {
            if (opt.equals(WRITE_OPT)) {
                WriteApi writeApi = client.makeWriteApi();
                writeApi.writeRecord(bucket, org, WritePrecision.NS, data);
            } else if (opt.equals(QUERY_OPT)) {
                List<FluxTable> tables = client.getQueryApi().query(data, org);
                results.setResponseData(JSON.toJSONString(tables), "UTF-8");
                results.setResponseCodeOK();
            } else {
                throw new UnsupportedOperationException(String.format("Unsupported operation: %s", opt));
            }
            results.setSuccessful(true);
        } catch (Exception e) {
            log.error("Error", e);
            results.setSuccessful(false);
        }
        results.sampleEnd();
        return results;
    }

    /**
     * 测试体结束后操作
     */
    @Override
    public void teardownTest(JavaSamplerContext context) {
        client.close();
        log.info("关闭连接");
    }

    public static void main(String[] args) {
        String token = "qzga4o81OGuT1R-626zwba_mrm4ZYMkPXOuCMm82Ap2A96kT5hXjb0XaQaTW8nndLv8vHvu8WF0L1hOvmF4qUg==";
        client = InfluxDBClientFactory.create("http://192.168.0.128:48086", token.toCharArray());
        String query = QueryBuilder
                .builder()
                .bucket("ds_cloud")
                .sort(QueryBuilder.TIME)
                .start("-30d")

                .stop("1d")
                .measurement("computer")
                .field("Ramp10")
                .page(10, 0)
                .build();
        System.out.println(query);
        List<FluxTable> tables = client.getQueryApi().query(query, "dongsenyun");
        System.out.println(JSON.toJSONString(tables));
    }
}
