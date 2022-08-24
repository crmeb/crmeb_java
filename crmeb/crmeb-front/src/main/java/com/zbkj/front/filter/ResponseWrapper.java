package com.zbkj.front.filter;


import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Response包装类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream buffer;

    private ServletOutputStream out;

    public ResponseWrapper(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
        buffer = new ByteArrayOutputStream();
        out = new WrapperOutputStream(buffer);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return out;
    }

    @Override
    public void flushBuffer()
            throws IOException {
        if (out != null) {
            out.flush();
        }
    }

    public byte[] getContent()
            throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    static class WrapperOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bos;

        public WrapperOutputStream(ByteArrayOutputStream bos) {
            this.bos = bos;
        }

        @Override
        public void write(int b) {
            bos.write(b);
        }

        @Override
        public boolean isReady() {
            return false;

        }

        @Override
        public void setWriteListener(WriteListener arg0) {

        }
    }
}
