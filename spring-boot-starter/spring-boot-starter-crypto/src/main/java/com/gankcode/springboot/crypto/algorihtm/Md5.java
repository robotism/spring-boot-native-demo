package com.gankcode.springboot.crypto.algorihtm;


import com.gankcode.springboot.crypto.base.BaseDigest;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Md5 extends BaseDigest {

    public Md5() {
        super("MD5", 32);
    }
}
