package com.im;

import lombok.Data;

@Data
public class MessageVo {
    public MessageVo(Integer fromId, Integer toId, String message) {
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;
    }

    public Integer fromId;
    public Integer toId;
    public String message;
}
