package git.yampery.influxdb;

import com.alibaba.fastjson.JSON;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxTable;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * <p>Influxdb2.0 测试取样器
 *      1. 初始化参数
 *      2. 根据参数初始化安装应用
 *      3. 执行测试
 *      4. 测试结束，释放资源
 * </p>
 * @author Yampery
 * @date 2019/3/22 19:44
 */
public class InfluxdbSample extends AbstractJavaSamplerClient {

    Logger logger = LoggerFactory.getLogger(InfluxdbSample.class);

    private static final String WRITE_OPT = "write";
    private static final String QUERY_OPT = "query";

    public static InfluxDBClient client = null;

    /**
     * 初始化参数
     * @return Arguments
     */
    @Override
    public Arguments getDefaultParameters() {
        logger.info("Get default parameters");
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
        logger.info("Connection：{}", url);
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
                WriteApi writeApi = client.getWriteApi();
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
            logger.error("Error", e);
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
        logger.info("关闭连接");
    }

    public static void main(String[] args) {
        String query = String.format("from(bucket: \"%s\") |> range(start: -1h)", "bucket");
        List<FluxTable> tables = client.getQueryApi().query(query, "org");
        System.out.println(JSON.toJSONString(tables));
    }
}
