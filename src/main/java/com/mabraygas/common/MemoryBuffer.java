package com.mabraygas.common;

import java.util.HashMap;

public class MemoryBuffer {
    public MemoryBuffer() {
        m_buffer = new HashMap<String, MemData>();
    }

    public Boolean Del(String key) {
        if(!m_buffer.containsKey(key)) {
            return false;
        }

        long expire = m_buffer.get(key).m_expire;
        long timestamp = Tools.UnixTimestamp();
        m_buffer.remove(key);
        return expire > timestamp;
    }

    public Boolean Set(String key, String value, long expire) {
        long timestamp = Tools.UnixTimestamp();
        if(expire <= timestamp) {
            return false;
        }

        m_buffer.put(key, new MemData(key, value, expire));
        return true;
    }

    public Boolean SetExpire(String key, long expire) {
        if(!m_buffer.containsKey(key)) {
            return false;
        }

        long timestamp = Tools.UnixTimestamp();
        if(expire <= timestamp) {
            return false;
        }

        MemData data = m_buffer.get(key);
        data.m_expire = expire;
        m_buffer.put(key, data);
        return true;
    }

    public Boolean Exists(String key) {
        if(!m_buffer.containsKey(key)) {
            return false;
        }

        long timestamp = Tools.UnixTimestamp();
        MemData data = m_buffer.get(key);
        return data.m_expire > timestamp;
    }

    public String Get(String key) {
        if(!Exists(key)) {
            return null;
        }

        return m_buffer.get(key).m_value;
    }

    public long TTL(String key) {
        if(!Exists(key)) {
            return 0;
        }

        long expire = m_buffer.get(key).m_expire;
        long timestamp = Tools.UnixTimestamp();
        return expire - timestamp;
    }

    private HashMap<String, MemData> m_buffer;
}
