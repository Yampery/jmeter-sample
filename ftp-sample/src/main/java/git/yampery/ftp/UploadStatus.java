package git.yampery.ftp;

/**
 * <p>上传状态</p>
 *
 * @author Yampery
 * @date 2019/3/22 18:25
 */
public enum UploadStatus {
    /**
     * 远程服务器相应目录创建失败
     */
    Create_Directory_Fail,
    /**
     * 远程服务器闯将目录成功
     */
    Create_Directory_Success,
    /**
     * 上传新文件成功
     */
    Upload_New_File_Success,
    /**
     * 上传新文件失败
     */
    Upload_New_File_Failed,
    /**
     * 文件已经存在
     */
    File_Exits,
    /**
     * 远程文件大于本地文件
     */
    Remote_Bigger_Local,
    /**
     * 断点续传成功
     */
    Upload_From_Break_Success,
    /**
     * 断点续传失败
     */
    Upload_From_Break_Failed,
    /**
     * 删除远程文件失败
     */
    Delete_Remote_Failed
}
