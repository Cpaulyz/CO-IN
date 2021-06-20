package com.cosin.model.enums;

public enum UserIdentity {
	/**
	 * 普通用户
	 */
	USER("用户"),

	/**
	 * 管理员角色
	 */
	ADMIN("管理员");

	private final String name;

	UserIdentity(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
