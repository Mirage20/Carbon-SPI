package org.wso2.spi.test.service.provider.impl;


import org.wso2.spi.test.service.api.Codec;

public class MP4 implements Codec {

    public void play(String data) {
        System.out.println("Provider 4 MP4 Out: " + data);
    }

}
