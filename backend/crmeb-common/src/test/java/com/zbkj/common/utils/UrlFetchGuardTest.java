package com.zbkj.common.utils;

import com.zbkj.common.exception.CrmebException;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;

public class UrlFetchGuardTest {

    @Test
    public void sanitizeSpreadSortKey_allowsFrontKeys() {
        Assert.assertEquals("u.spread_count", UrlFetchGuard.sanitizeSpreadSortKey("childCount"));
        Assert.assertEquals("orderCount", UrlFetchGuard.sanitizeSpreadSortKey("orderCount"));
        Assert.assertEquals("numberCount", UrlFetchGuard.sanitizeSpreadSortKey("numberCount"));
    }

    @Test
    public void sanitizeSpreadSortKey_rejectsInjection() {
        Assert.assertEquals("u.create_time",
                UrlFetchGuard.sanitizeSpreadSortKey("updatexml(1,concat(0x7e,user()),1)"));
        Assert.assertEquals("u.create_time", UrlFetchGuard.sanitizeSpreadSortKey(""));
    }

    @Test
    public void sanitizeSortDirection_onlyAscOrDesc() {
        Assert.assertEquals("asc", UrlFetchGuard.sanitizeSortDirection("ASC"));
        Assert.assertEquals("desc", UrlFetchGuard.sanitizeSortDirection("DESC"));
        Assert.assertEquals("desc", UrlFetchGuard.sanitizeSortDirection("desc; drop table"));
    }

    @Test
    public void validate_rejectsLoopback() {
        try {
            UrlFetchGuard.validate("http://127.0.0.1/a.png", Collections.<String>emptyList());
            Assert.fail("expected CrmebException");
        } catch (CrmebException expected) {
            Assert.assertTrue(expected.getMessage().contains("内网") || expected.getMessage().contains("url"));
        }
    }

    @Test
    public void validate_rejectsNonHttp() {
        try {
            UrlFetchGuard.validate("file:///etc/passwd", Collections.<String>emptyList());
            Assert.fail("expected CrmebException");
        } catch (CrmebException expected) {
            // ok
        }
    }

    @Test
    public void validate_rejectsHostOutsideAllowlist() {
        try {
            UrlFetchGuard.validate("https://evil.example/x.png", Arrays.asList("hddimg.szhentai.com"));
            Assert.fail("expected CrmebException");
        } catch (CrmebException expected) {
            Assert.assertTrue(expected.getMessage().contains("允许"));
        }
    }

    @Test
    public void normalizeHost_fromUrl() {
        Assert.assertEquals("hddimg.szhentai.com",
                UrlFetchGuard.normalizeHost("https://hddimg.szhentai.com/crmebimage/a.png"));
    }

    @Test
    public void isBlockedAddress_siteLocal() throws Exception {
        Assert.assertTrue(UrlFetchGuard.isBlockedAddress(InetAddress.getByName("10.0.0.1")));
        Assert.assertTrue(UrlFetchGuard.isBlockedAddress(InetAddress.getByName("192.168.1.1")));
    }

    @Test
    public void validateResponseSize_rejectsOverLimit() {
        byte[] big = new byte[UrlFetchGuard.MAX_BYTES + 1];
        try {
            UrlFetchGuard.validateResponseSize(big);
            Assert.fail("expected CrmebException");
        } catch (CrmebException expected) {
            // ok
        }
    }
}
