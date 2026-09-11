package com.zbkj.common.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

public class UserCenterResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void serializesMobileAdminPermissionFields() {
        UserCenterResponse response = new UserCenterResponse()
                .setIsMobileAdmin(true)
                .setIsWriteOffStaff(true);

        JsonNode json = objectMapper.valueToTree(response);

        Assert.assertTrue(json.get("isMobileAdmin").asBoolean());
        Assert.assertTrue(json.get("isWriteOffStaff").asBoolean());
    }
}
