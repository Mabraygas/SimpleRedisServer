package com.mabraygas.common;

public class MemData {
    public MemData(String key, String value, long expire) {
        m_key = key;
        m_value = value;
        m_expire = expire;
    }

    public String m_key;
    public String m_value;
    public long m_expire;
}
