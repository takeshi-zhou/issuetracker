package cn.edu.fudan.measureservice.domain;

import java.util.List;

public class Objects {

    private List<Object> objects;
    private ObjectAverage objectAverage;

    public List<Object> getObjects() {
        return objects;
    }

    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    public ObjectAverage getObjectAverage() {
        return objectAverage;
    }

    public void setObjectAverage(ObjectAverage objectAverage) {
        this.objectAverage = objectAverage;
    }
}
