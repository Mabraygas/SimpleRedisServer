package com.mabraygas.common;

public enum Commands {
    // Put Write ops before Read ops.
    DEL(0, false),
    SET(1, false),
    EXPIRE(2, false),
    EXISTS(3, false),
    GET(4, false),
    TTL(5, false),
    OTHERS(-101, false);

    private final Boolean m_isGlobalRequest;
    private final int m_commandSort;

    private Commands(final int sort, final Boolean isGlobalRequest) {
        this.m_isGlobalRequest = isGlobalRequest;
        this.m_commandSort = sort;
    }

    public Boolean IsGlobalRequest() { return m_isGlobalRequest; }
    public int GetSort() { return m_commandSort; }
}
