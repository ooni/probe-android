package org.openobservatory.ooniprobe.common;

import com.google.gson.Gson;

import org.openobservatory.engine.OONICheckInResults;
import org.openobservatory.ooniprobe.model.jsonresult.EventResult;

public class MKException extends Exception {
	public MKException(String failure) {
		super(failure);
	}

	public MKException(EventResult event) {
		super(new Gson().toJson(event.value));
	}
	public MKException(OONICheckInResults result) {
		super(new Gson().toJson(result));
	}
}
