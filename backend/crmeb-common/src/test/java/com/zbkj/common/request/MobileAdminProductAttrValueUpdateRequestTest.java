package com.zbkj.common.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class MobileAdminProductAttrValueUpdateRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void supportsCamelCaseAttrValuePayload() throws Exception {
        String json = "{\"attrValue\":[{\"id\":12,\"cost\":\"2764.00\",\"price\":\"2764.00\",\"otPrice\":\"2764.00\",\"stock\":50}]}";

        MobileAdminProductAttrValueUpdateRequest request =
                objectMapper.readValue(json, MobileAdminProductAttrValueUpdateRequest.class);

        Assert.assertEquals(1, request.getAttrValue().size());
        MobileAdminProductAttrValueUpdateRequest.AttrValueItem item = request.getAttrValue().get(0);
        Assert.assertEquals(Integer.valueOf(12), item.getId());
        Assert.assertEquals(new BigDecimal("2764.00"), item.getPrice());
        Assert.assertEquals(new BigDecimal("2764.00"), item.getOtPrice());
        Assert.assertEquals(Integer.valueOf(50), item.getStock());
    }

    @Test
    public void supportsSnakeCaseAttrValuePayload() throws Exception {
        String json = "{\"attr_value\":[{\"unique\":\"sku-a\",\"cost\":\"10.00\",\"price\":\"12.00\",\"ot_price\":\"15.00\",\"stock\":3}]}";

        MobileAdminProductAttrValueUpdateRequest request =
                objectMapper.readValue(json, MobileAdminProductAttrValueUpdateRequest.class);

        Assert.assertEquals(1, request.getAttrValue().size());
        MobileAdminProductAttrValueUpdateRequest.AttrValueItem item = request.getAttrValue().get(0);
        Assert.assertEquals("sku-a", item.getUnique());
        Assert.assertEquals(new BigDecimal("10.00"), item.getCost());
        Assert.assertEquals(new BigDecimal("15.00"), item.getOtPrice());
        Assert.assertEquals(Integer.valueOf(3), item.getStock());
    }
}
