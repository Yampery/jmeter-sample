package git.yampery.ftp;

/**
 * <p>下载状态</p>
 *
 * @author Yampery
 * @date 2019/3/22 18:20
 */
public enum DownloadStatus {
    /**
     * 远程文件不存在
     */
    Remote_File_Not_Exists,
    /**
     * 本地文件大于远程文件
     */
    Local_Bigger_Remote,
    /**
     * 断点下载文件成功
     */
    Download_From_Break_Success,
    /**
     * 断点下载文件失败
     */
    Download_From_Break_Failed,
    /**
     * 全新下载文件成功
     */
    Download_New_Success,
    /**
     * 全新下载文件失败
     */
    Download_New_Failed
}
