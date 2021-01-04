package com.vertx.commons.codec;

import com.vertx.commons.annotations.EventBusCodec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.ext.healthchecks.Status;

/**
 * @author Luiz Schmidt
 */
@EventBusCodec
public class StatusHealthCodec implements MessageCodec<Status, Status> {

	@Override
	public void encodeToWire(Buffer buffer, Status s) {
		//Nothing to do
	}

	@Override
	public Status decodeFromWire(int pos, Buffer buffer) {
		return null;
	}

	@Override
	public Status transform(Status s) {
		return s;
	}

	@Override
	public String name() {
		return StatusHealthCodec.class.getName();
	}

	@Override
	public byte systemCodecID() {
		return -1;
	}

}
