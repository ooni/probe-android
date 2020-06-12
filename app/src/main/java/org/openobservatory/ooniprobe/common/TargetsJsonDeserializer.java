package org.openobservatory.ooniprobe.common;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys.TorTarget.TorTargetObj;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

class TargetsJsonDeserializer implements JsonDeserializer<ArrayList<TestKeys.TorTarget>> {
    @Override public ArrayList<TestKeys.TorTarget> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ArrayList<TestKeys.TorTarget> targets = new ArrayList<>();
        Type type = new TypeToken<Map<String, TorTargetObj>>(){}.getType();
        Map<String, TorTargetObj> curTargets = new Gson().fromJson(json, type);
        for (Map.Entry<String, TorTargetObj> map : curTargets.entrySet()) {
            TorTargetObj curTarget = map.getValue();
            TestKeys.TorTarget target = curTarget.createTorTarget();
            targets.add(target);
        }
        return targets;
    }
}

