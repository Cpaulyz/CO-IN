package com.cosin.util;

/**
 * 用于计算分页的公共类
 *
 * @author chenyz
 */
public class PageHelper {
    /**
     * 每页大小
     */
    public static final int PAGE_SIZE = 10;

    /**
     * 根据当前页获取index
     * @param currPage 当前页
     * @return currIndex
     */
    public static int getIndexByCurrPage(int currPage) {
        return (currPage - 1) * PAGE_SIZE;
    }
}
