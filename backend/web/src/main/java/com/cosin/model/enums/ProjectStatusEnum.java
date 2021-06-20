package com.cosin.model.enums;

public enum  ProjectStatusEnum {
    PRIVATE("PRIVATE"), // 私有
    PUBLIC("PUBLIC");   // 共有

    private String type;

    ProjectStatusEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
