package org.wso2.spi.test.service.provider.impl;


import org.wso2.spi.test.service.api.Codec;

public class MP3V2 implements Codec {

    public void play(String data) {
        System.out.println("Provider 1 MP3V2 Out: " + data);
    }

}
