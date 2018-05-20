package com.mabraygas.thread;

import com.mabraygas.common.CommonResources;
import com.mabraygas.common.Resource;
import com.mabraygas.common.queue.SenderQueue;
import com.mabraygas.common.queue.WorkerQueue;

import java.util.ArrayList;

public class SenderThread extends IServerThread {
    @Override
    public void Init(int threadId) {
        m_threadId = threadId;
    }

    @Override
    public void run() {
        ArrayList<Integer> resourceIds = null;
        while (true) {
            try {
                resourceIds = WorkerQueue.PopAll(m_threadId);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            if(resourceIds == null || resourceIds.isEmpty()) {
                continue;
            }

            for(Integer id : resourceIds) {
                CommonResources.ReturnResource(id);
            }
        }
    }

    private int m_threadId;
}
