package com.mabraygas.common.queue;

import com.mabraygas.common.LockFreeQueue;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class WorkerQueue implements IQueue {

    public static void Init(Integer count) {
        m_count = count;

        m_queue = new LockFreeQueue[m_count];
        for(int idx = 0; idx < m_count; idx++) {
            m_queue[idx] = new LockFreeQueue<Integer>();
        }
    }

    public static void Push(String key, Integer val) {
        if (key == null) {
            Push(0, val);
            return;
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

        Integer ret = m_queue[id].poll(-1);
        return ret;
    }

    public static ArrayList<Integer> PopAll(Integer id) throws InterruptedException {
        if (id < 0 || id > m_count) {
            throw new InvalidParameterException();
        }

        ArrayList<Integer> ret = m_queue[id].pollAll(-1);
        return ret;
    }

    private static Integer m_count;
    private static LockFreeQueue<Integer>[] m_queue;
}