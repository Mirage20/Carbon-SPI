package org.wso2.spi.test.service.provider.impl;


import org.wso2.spi.test.service.api.Codec;

public class OGG implements Codec {

    public void play(String data) {
        System.out.println("Provider 3 OGG Out: " + data);
    }

}
