package com.marklynch.flickrsearch.logging;

import timber.log.Timber;

public class TimberDebugTree extends Timber.DebugTree {
    @Override
    public String createStackElementTag(StackTraceElement element) {
        return String.format(
                "[L:%s] [M:%s] [C:%s]",
                element.getLineNumber(),
                element.getMethodName(),
                super.createStackElementTag(element)
        );
    }
}