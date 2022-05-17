package lhy.library.utils;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class WebUtil {
    public final static String AJAX_HEADER = "x-requested-with";
    public final static String XMLHTTPREQUEST = "XMLHttpRequest";
    private static String CHARACTER_CODING = "UTF-8";
    private static String CONTENT_TYPE_XML = "application/xml";
    private static String CONTENT_TYPE_JSON = "application/json";

    /**
     * 将Map转化为排序后的组字符串
     *
     * @param params
     * @return
     */
    public static String convertToSortStr(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }

        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        StringBuilder query = new StringBuilder();

        for (String key : keys) {
            String value = params.get(key);
            query.append(key).append("=").append(value).append("&");
        }
        return query.toString();
    }

    public static boolean isNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if ((obj instanceof List)) {
            return ((List) obj).size() == 0;
        }
        if ((obj instanceof String)) {
            return ((String) obj).trim().equals("");
        }
        return false;
    }

    public static String getQueryString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder query = new StringBuilder();
        boolean hasParam = false;
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (String key : keys) {
            String value = params.get(key);
            if (isNotEmpty(key, value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }
                query.append(key).append("=").append(URLEncoder.encode(value, CHARACTER_CODING));
            }
        }

        return query.toString();
    }

    /**
     * 不能用BufferedReader的ReadLine和read来读，因为不方便处理换行和编码处理。只能一个一个读，才能保证正确性。
     *
     * @param is
     * @return
     */
    public static String stream2Str(InputStream is) throws Exception {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i = -1;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            is.close();
            return baos.toString();
        } catch (Exception e) {
            throw e;
        }
    }
//
//	public static ContentType getContentType(String format){
//		ContentType contentType = ContentType.create(CONTENT_TYPE_XML,CHARACTER_CODING);
//		if ("JSON".equalsIgnoreCase(format)) {
//			contentType = ContentType.create(CONTENT_TYPE_JSON, CHARACTER_CODING);
//		}
//		return contentType;
//	}
}
