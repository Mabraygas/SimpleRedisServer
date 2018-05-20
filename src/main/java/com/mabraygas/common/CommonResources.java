package com.mabraygas.common;

import com.mabraygas.common.queue.GlobalWorkerQueue;
import com.mabraygas.common.queue.ResourceIdQueue;
import com.mabraygas.common.queue.SenderQueue;
import com.mabraygas.common.queue.WorkerQueue;
import com.mabraygas.thread.GlobalWorkerThread;
import com.mabraygas.thread.SenderThread;
import com.mabraygas.thread.WorkerThread;

import java.security.InvalidParameterException;

public class CommonResources {
    public static void Init() {
        ResourceIdQueue.Init(m_resourceQueueCount);
        WorkerQueue.Init(m_workerQueueCount);
        SenderQueue.Init(m_senderQueueCount);
        GlobalWorkerQueue.Init(m_globalWorkerQueueCount);

        for(int i = 0; i < m_resourceCount; i++) {
            ResourceIdQueue.Push(0, i);
        }

        m_resourceArr = new Resource[m_resourceCount];
        for(int i = 0; i < m_resourceCount; i++) {
            m_resourceArr[i] = new Resource(i);
        }

        StartServiceThreads();
    }

    public static void StartServiceThreads() {
        // Start GlobalWorkerThread.
        for(int i = 0; i < m_globalWorkerThreadCount; i++) {
            GlobalWorkerThread globalWorkerThread = new GlobalWorkerThread();
            globalWorkerThread.Init(i);
            new Thread(globalWorkerThread).start();
        }

        // Start WorkerThreads.
        for(int i = 0; i < m_workerThreadCount; i++) {
            WorkerThread workerThread = new WorkerThread();
            workerThread.Init(i);
            new Thread(workerThread).start();
        }

        // Start SenderThreads.
        for(int i = 0; i < m_senderThreadCount; i++) {
            SenderThread senderThread = new SenderThread();
            senderThread.Init(i);
            new Thread(senderThread).start();
        }
    }

    public static Integer GetResourceId() throws InterruptedException {
        return ResourceIdQueue.Pop(0);
    }

    public static Resource GetResource(Integer id) {
        if(id < 0 || id > m_resourceCount) {
            throw new InvalidParameterException();
        }

        return m_resourceArr[id];
    }

    public static void ReturnResource(Integer id) {
        if(id < 0 || id > m_resourceCount) {
            throw new InvalidParameterException();
        }

        m_resourceArr[id].InvokeCB();
        m_resourceArr[id].Clear();
        ResourceIdQueue.Push(0, id);
    }

    public static Integer m_resourceCount = 17;
    public static Integer m_resourceQueueCount = 1;
    public static Integer m_globalWorkerQueueCount = 1;
    public static Integer m_workerQueueCount = 17;
    public static Integer m_senderQueueCount = 17;

    public static Integer m_globalWorkerThreadCount = m_globalWorkerQueueCount;
    public static Integer m_workerThreadCount = m_workerQueueCount;
    public static Integer m_senderThreadCount = m_senderQueueCount;

    private static Resource[] m_resourceArr;
}
