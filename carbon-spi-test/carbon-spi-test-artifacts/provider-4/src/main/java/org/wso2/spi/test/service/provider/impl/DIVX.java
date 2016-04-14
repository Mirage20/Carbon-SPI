package org.wso2.spi.test.service.provider.impl;

import org.wso2.spi.test.service.api.Codec;

public class DIVX implements Codec {

    public void play(String data) {
        System.out.println("Provider 4 DIVX Out: " + data);
    }

}
