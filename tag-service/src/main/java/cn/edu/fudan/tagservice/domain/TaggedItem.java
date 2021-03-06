package cn.edu.fudan.tagservice.domain;

import java.io.Serializable;

/**
 * @author WZY
 * @version 1.0
 **/
public class TaggedItem implements Serializable {

    private String item_id;
    private String tag_id;

    public TaggedItem(String item_id, String tag_id) {
        this.item_id = item_id;
        this.tag_id = tag_id;
    }

    public TaggedItem() {
        super();
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    @Override
    public String toString(){
        return "{" +
                "item_id=" + item_id + "," +
                "tag_id=" + tag_id +
                "}";
    }
}
