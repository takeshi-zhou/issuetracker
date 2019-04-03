package cn.edu.fudan.measureservice.domain;

import java.util.List;

public class Objects {

    private List<OObject> objects;
    private ObjectAverage objectAverage;

    public List<OObject> getObjects() {
        return objects;
    }

    public void setObjects(List<OObject> objects) {
        this.objects = objects;
    }

    public ObjectAverage getObjectAverage() {
        return objectAverage;
    }

    public void setObjectAverage(ObjectAverage objectAverage) {
        this.objectAverage = objectAverage;
    }
}
