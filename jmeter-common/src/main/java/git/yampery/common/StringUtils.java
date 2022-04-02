package git.yampery.common;

/**
 * <p>字符串工具</p>
 *
 * @author Yampery
 * @date 2022/4/2 17:10
 */
public class StringUtils {

    public static boolean isBlank(String s) {
        return null == s || "".equals(s.trim());
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }
}
