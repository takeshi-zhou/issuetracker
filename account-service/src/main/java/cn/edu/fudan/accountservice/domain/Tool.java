package cn.edu.fudan.accountservice.domain;

/**
 * @author zyh
 * @date 2020/2/25
 */
public class Tool {
    private String uuid;
    private String toolType;
    private String toolName;
    private String description;
    /**
     * 0表示不使用工具，1表示使用工具
     */
    private int enabled;

    private String accountName;

    public String getAccountName() {
        return accountName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getToolType() {
        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }
}
