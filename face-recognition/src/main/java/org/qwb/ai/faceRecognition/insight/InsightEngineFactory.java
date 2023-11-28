package org.qwb.ai.faceRecognition.insight;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class InsightEngineFactory extends BasePooledObjectFactory<InsightCompareEngine> {

    public InsightEngineFactory() {
    }


    @Override
    public InsightCompareEngine create() {
        InsightCompareEngine engine = new InsightCompareEngine();
        return engine;
    }

    @Override
    public PooledObject<InsightCompareEngine> wrap(InsightCompareEngine engine) {
        return new DefaultPooledObject<>(engine);
    }


    @Override
    public void destroyObject(PooledObject<InsightCompareEngine> p) throws Exception {
        InsightCompareEngine faceEngine = p.getObject();
        super.destroyObject(p);
    }
}