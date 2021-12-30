package git.yampery.ftp;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

import static git.yampery.ftp.UploadStatus.Upload_New_File_Success;

/**
 * @decription FtpSample
 * <p>Ftp测试取样器
 *      1. 初始化参数
 *      2. 根据参数初始化安装应用
 *      3. 执行测试
 *      4. 测试结束，释放资源
 * </p>
 * @author Yampery
 * @date 2019/3/22 19:44
 */
public class FtpSample extends AbstractJavaSamplerClient {

    protected FtpClient ftpClient = null;
    Logger logger = LoggerFactory.getLogger(FtpSample.class);

    /**
     * 初始化参数
     * @return
     */
    @Override
    public Arguments getDefaultParameters() {
        logger.info("Get default parameters");
        Arguments arguments = new Arguments();
        arguments.addArgument("host", "localhost");
        arguments.addArgument("port", "2121");
        arguments.addArgument("username", "ftpuser");
        arguments.addArgument("password", "password");
        // ftp操作: put / get
        arguments.addArgument("operation", "put");
        arguments.addArgument("local", "week80.csv");
        arguments.addArgument("remote", "week80.csv");
        return arguments;
    }

    /**
     * 初始化安装
     * @param context
     */
    @Override
    public void setupTest(JavaSamplerContext context) {
        ftpClient = new FtpClient();
        try {
            ftpClient.connect(context.getParameter("host"),
                    Integer.parseInt(context.getParameter("port")),
                    context.getParameter("username"), context.getParameter("password"));
        } catch (IOException e) {
            logger.error("连接FTP出错：" + e.getMessage());
        }
    }

    /**
     * 测试主体
     * @param context
     * @return
     */
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult results = new SampleResult();
        // 事务开始
        results.sampleStart();
        String local = context.getParameter("local");
        String remote = context.getParameter("remote");
        String opt = context.getParameter("operation");
        String randStr = UUID.randomUUID().toString();
        try {
            if (opt.equalsIgnoreCase("put")) {
                // 如果是上传，则忽略remote，生成随机字串作为目标文件名称，方便压测
                // 上传使用/test目录下，所以必须存在该目录
                results.setSuccessful(ftpClient.upload(
                        local, "/test/" + randStr + local.substring(local.lastIndexOf(".")))
                        .equals(Upload_New_File_Success));
            } else { // 下载，忽略local
                results.setSuccessful(
                        ftpClient.download(
                                remote, randStr + remote.substring(remote.lastIndexOf("."))));
            }
            results.setSuccessful(true);
        } catch (IOException e) {
            results.setSuccessful(false);
            logger.error(opt + " exception: ", e);
        }
        // 事务结束
        results.sampleEnd();
        return results;
    }

    /**
     * 测试体结束后操作
     * @param context
     */
    @Override
    public void teardownTest(JavaSamplerContext context) {
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
