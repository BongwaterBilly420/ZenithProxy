package com.zenith.via.platform;

import com.viaversion.viaversion.api.platform.PlatformTask;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.Future;

public class MCProxyPlatformTask implements PlatformTask<Future<?>> {
    private final Future<?> future;

    public MCProxyPlatformTask(Future<?> future) {
        this.future = future;
    }

    @Override
    public @Nullable Future<?> getObject() {
        return future;
    }

    @Override
    public void cancel() {
        future.cancel(true);
    }
}
