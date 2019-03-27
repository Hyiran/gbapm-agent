package com.gb.apm.profiler.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gb.apm.bootstrap.core.context.SpanEventRecorder;
import com.gb.apm.bootstrap.core.context.Trace;
import com.gb.apm.dapper.context.AsyncState;
import com.gb.apm.dapper.context.AsyncTraceId;
import com.gb.apm.dapper.context.SpanRecorder;
import com.gb.apm.dapper.context.TraceId;
import com.gb.apm.dapper.context.scope.TraceScope;
import com.gb.apm.profiler.context.id.StatefulAsyncTraceId;

public class AsyncTrace implements Trace {
    private static final int BEGIN_STACKID = 1;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final Trace trace;
    private final boolean entryPoint;

    private int asyncId;
    private short asyncSequence;
    private AsyncState asyncState;

    public AsyncTrace(final Trace trace, final AsyncState asyncState) {
        if (asyncState == null) {
            throw new IllegalArgumentException("asyncState must not be null.");
        }
        this.trace = trace;
        this.asyncState = asyncState;
        this.entryPoint = true;
    }

    public AsyncTrace(final Trace trace, final int asyncId, final short asyncSequence, final long startTime) {
        this.trace = trace;
        this.asyncId = asyncId;
        this.asyncSequence = asyncSequence;

        this.asyncState = null;
        this.entryPoint = false;

        this.trace.getSpanRecorder().recordStartTime(startTime);
        traceBlockBegin(BEGIN_STACKID);
    }

    public int getAsyncId() {
        return asyncId;
    }

    @Override
    public long getId() {
        if (this.entryPoint) {
            return this.trace.getId();
        }

        return -1;
    }

    @Override
    public long getStartTime() {
        if (this.entryPoint) {
            return this.trace.getStartTime();
        }

        return 0;
    }

    @Override
    public Thread getBindThread() {
        if (this.entryPoint) {
            return this.trace.getBindThread();
        }

        return null;
    }

    @Override
    public TraceId getTraceId() {
        return trace.getTraceId();
    }

    @Override
    public boolean canSampled() {
        return trace.canSampled();
    }

    @Override
    public boolean isRoot() {
        return trace.isRoot();
    }

    @Override
    public SpanEventRecorder traceBlockBegin() {
        final SpanEventRecorder recorder = trace.traceBlockBegin();
        if (this.entryPoint) {
            return recorder;
        }
        recorder.recordAsyncId(asyncId);
        recorder.recordAsyncSequence(asyncSequence);
        return recorder;
    }

    @Override
    public SpanEventRecorder traceBlockBegin(int stackId) {
        final SpanEventRecorder recorder = trace.traceBlockBegin(stackId);
        if (this.entryPoint) {
            return recorder;
        }
        recorder.recordAsyncId(asyncId);
        recorder.recordAsyncSequence(asyncSequence);
        return recorder;
    }

    @Override
    public void traceBlockEnd() {
        trace.traceBlockEnd();
    }

    @Override
    public void traceBlockEnd(int stackId) {
        trace.traceBlockEnd(stackId);
    }

    @Override
    public boolean isAsync() {
        if (this.entryPoint) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isRootStack() {
        if (this.entryPoint) {
            return this.trace.isRootStack();
        }
        return trace.getCallStackFrameId() == BEGIN_STACKID;
    }

    @Override
    public AsyncTraceId getAsyncTraceId() {
        return getAsyncTraceId(false);
    }

    @Override
    public AsyncTraceId getAsyncTraceId(boolean closeable) {
        final AsyncTraceId asyncTraceId = this.trace.getAsyncTraceId();
        final AsyncState asyncState = this.asyncState;
        if (closeable && this.entryPoint && asyncState != null) {
            asyncState.setup();
            return new StatefulAsyncTraceId(asyncTraceId, asyncState);
        }

        return asyncTraceId;
    }

    @Override
    public void close() {
        if (this.entryPoint) {
            closeOrFlush();
        } else {
            traceBlockEnd(BEGIN_STACKID);
            trace.close();
        }
    }

    private void closeOrFlush() {
        final AsyncState asyncState = this.asyncState;
        if (asyncState == null) {
            return;
        }

        if (asyncState.await()) {
            // flush.
            this.trace.flush();
            if (isDebug) {
                logger.debug("Flush trace={}, asyncState={}", this, this.asyncState);
            }
        } else {
            // close.
            this.trace.close();
            if (isDebug) {
                logger.debug("Close trace={}. asyncState={}", this, this.asyncState);
            }
        }
        this.asyncState = null;
    }

    @Override
    public void flush() {
        this.trace.flush();
    }

    @Override
    public SpanRecorder getSpanRecorder() {
        return trace.getSpanRecorder();
    }

    @Override
    public SpanEventRecorder currentSpanEventRecorder() {
        return trace.currentSpanEventRecorder();
    }

    @Override
    public int getCallStackFrameId() {
        return trace.getCallStackFrameId();
    }

    @Override
    public TraceScope getScope(String name) {
        return trace.getScope(name);
    }

    @Override
    public TraceScope addScope(String name) {
        return trace.addScope(name);
    }
}