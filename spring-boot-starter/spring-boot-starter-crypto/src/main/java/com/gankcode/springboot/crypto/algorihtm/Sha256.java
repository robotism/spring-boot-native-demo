package com.gankcode.springboot.crypto.algorihtm;


import com.gankcode.springboot.crypto.base.BaseDigest;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Sha256 extends BaseDigest {

    public Sha256() {
        super("SHA-256", 64);
    }
}
