package com.gankcode.springboot.crypto.algorihtm;


import com.gankcode.springboot.crypto.base.BaseDigest;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Sha512 extends BaseDigest {

    public Sha512() {
        super("SHA-512", 128);
    }
}
