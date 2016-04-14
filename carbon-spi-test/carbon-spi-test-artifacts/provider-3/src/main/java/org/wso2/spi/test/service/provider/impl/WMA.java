package org.wso2.spi.test.service.provider.impl;

import org.wso2.spi.test.service.api.Codec;

public class WMA implements Codec {

    public void play(String data) {
        System.out.println("Provider 3 WMA Out: " + data);
    }

}
