package core.encryption;
import org.apache.commons.codec.digest.DigestUtils;
public class BaseEncryption {
    public static String encrypt(String plainText) {
        if (plainText == null) {
            throw new IllegalArgumentException("输入的文本不能为 null");
        }
        try {
            // 使用 SHA-256 算法加密，比 MD5 更安全
            return DigestUtils.sha256Hex(plainText.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("加密过程中发生错误", e);
        }
    }
}
