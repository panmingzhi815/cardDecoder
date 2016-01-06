package sample;

/**
 * Created by xiaopan on 2016-01-06.
 */
public class ByteUtils {

    public static byte[] hexStringToByteArray(String s) {
        s = s.replaceAll(" ","");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexStringNoFormat(byte[] bytes) {
        StringBuilder format = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            format.append(byteToHexString(bytes[i]));
        }
        return format.toString();
    }

    public static String byteToHexString(byte bytes) {
        char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'
        };
        char hexChars1 = 0;
        char hexChars2 = 0;
        int v;

        v = bytes & 0xFF;
        hexChars1 = hexArray[v / 16];
        hexChars2 = hexArray[v % 16];

        return hexChars1 + "" + hexChars2;
    }
}
