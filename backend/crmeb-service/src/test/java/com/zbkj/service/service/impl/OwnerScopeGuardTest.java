package com.zbkj.service.service.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * 归属校验判定：调用方 uid 必须等于资源 uid。
 */
public class OwnerScopeGuardTest {

    static boolean isOwner(Integer callerUid, Integer resourceUid) {
        return callerUid != null && callerUid.equals(resourceUid);
    }

    @Test
    public void ownerMatch_allows() {
        Assert.assertTrue(isOwner(100, 100));
    }

    @Test
    public void ownerMismatch_denies() {
        Assert.assertFalse(isOwner(100, 200));
        Assert.assertFalse(isOwner(null, 200));
    }
}
