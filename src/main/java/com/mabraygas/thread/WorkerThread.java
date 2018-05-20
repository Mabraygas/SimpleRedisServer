package com.mabraygas.thread;

import com.mabraygas.common.CommonResources;
import com.mabraygas.common.MemoryBuffer;
import com.mabraygas.common.Resource;
import com.mabraygas.common.Tools;
import com.mabraygas.common.queue.SenderQueue;
import com.mabraygas.common.queue.WorkerQueue;

import java.util.ArrayList;
import java.util.Collections;

public class WorkerThread extends IServerThread {
    @Override
    public void Init(int threadId) {
        m_threadId = threadId;
        m_buffer = new MemoryBuffer();
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

            ArrayList<Resource> resources = new ArrayList<Resource>();
            for(Integer id : resourceIds) {
                Resource resource = CommonResources.GetResource(id);
                resources.add(resource);
            }

            // Put all write ops before read ops.
            Collections.sort(resources, (o1, o2) -> o1.m_commands.GetSort() - o2.m_commands.GetSort());

            String lastKey = "";
            int delIdx = -1, setIdx = -1, expireIdx = -1;
            for(int idx = 0; idx < resources.size(); idx++) {
                Resource resource = resources.get(idx);
                if(!resource.m_key.equals(lastKey)) {
                    lastKey = resource.m_key;
                    delIdx = setIdx = expireIdx = -1;
                }

                switch(resource.m_commands) {
                    case DEL: {
                        if(delIdx != -1) {
                            resource.m_responseStr = "Del Succ.";
                        } else {
                            delIdx = idx;
                            resource.m_responseStr = m_buffer.Del(resource.m_key) ? "Del Succ." : "Del Fail.";
                        }
                        break;
                    }
                    case SET: {
                        if(setIdx != -1) {
                            resource.m_responseStr = "Set Fail.";
                        } else {
                            setIdx = idx;
                            resource.m_responseStr = m_buffer.Set(resource.m_key, resource.m_value, resource.m_expire) ? "Set Succ." : "Set Fail.";
                        }
                        break;
                    }
                    case EXPIRE: {
                        if(expireIdx != -1) {
                            resource.m_responseStr = "Expire Set Fail.";
                        } else {
                            expireIdx = idx;
                            resource.m_responseStr = m_buffer.SetExpire(resource.m_key, resource.m_expire) ? "Expire Set Succ." : "Expire Set Fail.";
                        }
                        break;
                    }
                    case EXISTS: {
                        if(delIdx != -1) {
                            resource.m_responseStr = "Not Exists.";
                        } else {
                            resource.m_responseStr = m_buffer.Exists(resource.m_key) ? "Exists." : "Not Exists.";
                        }
                        break;
                    }
                    case GET: {
                        if(setIdx != -1) {
                            resource.m_responseStr = resources.get(setIdx).m_value;
                        } else {
                            resource.m_responseStr = m_buffer.Get(resource.m_key);
                        }
                        break;
                    }
                    case TTL: {
                        if(expireIdx != -1) {
                            long expire = resources.get(expireIdx).m_expire;
                            long timestamp = Tools.UnixTimestamp();
                            expire -= timestamp;
                            if(expire < 0) {
                                expire = 0;
                            }
                            resource.m_responseStr = Long.toString(expire);
                        } else {
                            resource.m_responseStr = Long.toString(m_buffer.TTL(resource.m_key));
                        }
                        break;
                    }
                    default:
                        resource.m_responseStr = "Commond Error!";
                }
            }

            for(Resource resource : resources) {
                SenderQueue.Push(resource.GetKey(), resource.m_resourceId);
            }
        }
    }

    private int m_threadId;
    private MemoryBuffer m_buffer;
}
