package com.lhy.jelly.ui.home;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

import lhy.library.utils.Md5Utils;
import lhy.library.utils.WebUtil;

public class HomeFragmentTest extends TestCase {
    public static void main(String[] args) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("accessToken", "c0V6dGNlRmM2bXJFbkVDcFpRYUNLQTVleThBaUVNbkI=");
        params.put("appType", "tm_mini_pos");
        params.put("brandColor", "1C93FF");
        String data = WebUtil.convertToSortStr(params);
        System.out.println(Md5Utils.encode(data));
    }

}