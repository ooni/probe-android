package org.openobservatory.ooniprobe.common;

import com.google.gson.Gson;

import org.openobservatory.engine.OONIURLListResult;
import org.openobservatory.ooniprobe.model.jsonresult.EventResult;

public class MKException extends Exception {
	public MKException(EventResult event) {
		super(new Gson().toJson(event.value));
	}
	public MKException(OONIURLListResult result) {
		super(new Gson().toJson(result));
	}
}
