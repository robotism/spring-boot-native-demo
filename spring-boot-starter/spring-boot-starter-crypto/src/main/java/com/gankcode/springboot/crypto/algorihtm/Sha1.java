package com.gankcode.springboot.crypto.algorihtm;


import com.gankcode.springboot.crypto.base.BaseDigest;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Sha1 extends BaseDigest {

    public Sha1() {
        super("SHA1", 40);
    }
}
