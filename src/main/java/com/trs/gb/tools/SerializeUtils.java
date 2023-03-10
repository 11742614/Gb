package com.trs.gb.tools;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by 苏亚青 on 2019/1/15.
 */
public class SerializeUtils{

    public static byte[] fstserialize(Object object){
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // 反序列化
    public static Object fstdeserialize(byte[] bytes) {
        ByteArrayInputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayOutputStream);
            return objectInputStream.readObject();
        } catch (Exception e) {
            System.out.println("deserialize exception");

        }
        return null;
    }




}
