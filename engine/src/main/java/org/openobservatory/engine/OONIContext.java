package org.openobservatory.engine;

import oonimkall.Context;

/** OONIContext is the context for long running tasks. */
public final class OONIContext {
    Context ctx;

    OONIContext(Context ctx) {
        this.ctx = ctx;
    }

    void cancel() {
        ctx.cancel();
    }
}
