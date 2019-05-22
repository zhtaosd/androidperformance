package com.zht.launchstarter.task;

public abstract class MainTask extends Task {
    @Override
    public boolean runOnMainThread() {
        return true;
    }
}
