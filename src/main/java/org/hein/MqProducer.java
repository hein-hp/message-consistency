package org.hein;

public interface MqProducer<T> {

    /**
     * send msg
     */
    void sendMsg(String topic, String tag, T msg);
}
