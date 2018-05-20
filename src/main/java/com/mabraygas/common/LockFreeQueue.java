package com.mabraygas.common;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LockFreeQueue<E> extends ConcurrentLinkedQueue<E> {
    public LockFreeQueue() { }

    public synchronized boolean add(E e) {
        boolean succ = super.add(e);
        notify();

        return succ;
    }

    public synchronized E poll(long maxWaitMs) throws InterruptedException {
        if(isEmpty()) {
            if(maxWaitMs == -1) {
                maxWaitMs = 1000;
            }
            wait(maxWaitMs);
        }

        return super.poll();
    }

    public synchronized ArrayList<E> pollAll(long maxWaitMs) throws InterruptedException {
        if(isEmpty()) {
            if(maxWaitMs == -1) {
                maxWaitMs = 1000;
            }
            wait(maxWaitMs);
        }

        ArrayList<E> ret = new ArrayList<E>();
        E singleEle;
        while(null != (singleEle = super.poll())) {
            ret.add(singleEle);
        }

        return ret;
    }
}
