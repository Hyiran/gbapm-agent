package com.gb.apm.dapper.context;

/**
 * @author emeroad
 */
public interface TraceId {

	TraceId getNextTraceId();

	long getSpanId();

	String getTransactionId();

	String getAgentId();

	long getAgentStartTime();

	long getTransactionSequence();

	long getParentSpanId();

	short getFlags();

	boolean isRoot();
}
