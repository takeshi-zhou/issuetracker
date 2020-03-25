package cn.edu.fudan.tagservice.domain;

public class ModifyTaggedItem {

    private String itemId;
    private String preTagId;
    private String newTagId;

    public ModifyTaggedItem(String itemId, String preTagId, String newTagId) {
        this.itemId = itemId;
        this.preTagId = preTagId;
        this.newTagId = newTagId;
    }

    public ModifyTaggedItem() {
        super();
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getPreTagId() {
        return preTagId;
    }

    public void setPreTagId(String preTagId) {
        this.preTagId = preTagId;
    }

    public String getNewTagId() {
        return newTagId;
    }

    public void setNewTagId(String newTagId) {
        this.newTagId = newTagId;
    }
}
