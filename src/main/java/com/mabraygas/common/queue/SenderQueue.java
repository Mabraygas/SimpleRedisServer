package com.mabraygas.common.queue;

import java.security.InvalidParameterException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SenderQueue implements IQueue {

    public static void Init(Integer count) {
        m_count = count;

        m_queue = new ConcurrentLinkedQueue[m_count];
        for(int idx = 0; idx < m_count; idx++) {
            m_queue[idx] = new ConcurrentLinkedQueue<Integer>();
        }
    }

    public static void Push(String key, Integer val) {
        if(key == null) {
            Push(0, val);
        }

        int id = key.hashCode() % m_count;
        if(id < 0) {
            id += m_count;
        }

        Push(id, val);
    }

    public static void Push(Integer id, Integer val) {
        if (id < 0 || id > m_count) {
            throw new InvalidParameterException();
        }

        m_queue[id].add(val);
    }

    public static Integer Pop(Integer id) throws InterruptedException {
        if(id < 0 || id > m_count) {
            throw new InvalidParameterException();
        }

        Integer ret = m_queue[id].poll();
        while(ret == null) {
            Thread.sleep(100);
            ret = m_queue[id].poll();
        }

        return ret;
    }

    private static Integer m_count;
    private static ConcurrentLinkedQueue<Integer>[] m_queue;
}
