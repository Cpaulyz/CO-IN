package com.cosin.model.enums;

/**
 * 布局类型
 */
public enum  LayoutTypeEnum {
    FORCE("FORCE"), // 力导图模式
    GRID("GRID"), // 排版模式
    FIXED("FIXED"); // 自由模式

    private String type;

    LayoutTypeEnum(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
