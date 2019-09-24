/*
 * (c) Midland Software Limited 2019
 * Name     : EncryptionUtils.java
 * Author   : ferraciolliw
 * Date     : 24 Sep 2019
 */
package com.wiltech.todo.utils;

import org.springframework.stereotype.Component;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.stereotype.Component;

/**
 * The type Encryption utils.
 */
@Component
public class EncryptionUtils {

    //attribute from jasypt library
    private BasicTextEncryptor textEncryptor;

    /**
     * Instantiates a new Encryption utils.
     */
    /* constructor */
    public EncryptionUtils(){
        textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("mySecretEncriptionKey23456");
    }

    /**
     * Encrypt string.
     * @param data the data
     * @return the string
     */
    public String encrypt(String data){
        return textEncryptor.encrypt(data);
    }

    /**
     * Decrypt string.
     * @param encriptedData the encripted data
     * @return the string
     */
    public String decrypt(String encriptedData){
        return textEncryptor.decrypt(encriptedData);
    }

}
