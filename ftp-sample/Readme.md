自定义Jmeter的FTP测试
-------------------

# 使用方法

```mvn clean package``` 

- 安装

在target下生成ftp-sample-1.0.0.jar，将此jar包放在jmeter的lib/ext下面。

重启jmeter，可以自定义添加JavaSamper，也可以打开resources/ftp_test_plan.jmx文件。

- 配置

put操作不考虑remote参数，直接会将本地文件上传到远程test/目录下，并用随机字串命名，方便进行压测。

get操作正常。