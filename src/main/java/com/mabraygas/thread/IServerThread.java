package com.mabraygas.thread;

public abstract class IServerThread implements Runnable  {
    ///
    public abstract void Init(int threadId);
    ///
    public abstract void run();
}
