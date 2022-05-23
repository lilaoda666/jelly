package com.lhy.jelly;

import org.junit.Test;

import static org.junit.Assert.*;

import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String barcode = "&id=124fdsf3&smInfoId=45654&";
        //还不确定扫出来的是个啥球艺，按H5的来 扫来的包含（id=xxx&smInfoId==XXX）
        int i = barcode.indexOf("id=");
        String idString = barcode.substring(i + 3);
        String id = "";
        String smInfoId = "";
        if (idString.contains("&")) {
            int index = idString.indexOf("&");
            id = idString.substring(0, index);
            if (idString.contains("smInfoId=")) {
                int mInfoIdIndex = idString.indexOf("smInfoId=");
                String infoIdString = idString.substring(mInfoIdIndex + 9);
                if (infoIdString.contains("&")) {
                    int i1 = infoIdString.indexOf("&");
                    smInfoId = infoIdString.substring(0, i1);
                } else {
                    smInfoId = infoIdString;
                }
            }
        } else {
            id = idString;
        }
        System.out.println("id=" + id + " smInfoId=" + smInfoId);
    }
}