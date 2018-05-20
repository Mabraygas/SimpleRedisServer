package com.mabraygas.thread;

public class GlobalWorkerThread extends IServerThread {
    @Override
    public void Init(int threadId) {
        m_threadId = threadId;
    }

    @Override
    public void run() {

    }

    private int m_threadId;
}
