# 实体关系

## Project 项目

- projectId
- name
- description
- layout

## Layout 排版

- projectId
- type = 'force' | 'grid' | 'free'
- nodeId
- xAxis
- yAxis

## Node 实体

- projectId
- nodeId
- name
- group
- properties
- textSize
- radius
- figure
- color

```
layout {
    force: {
        xAxis
        yAxis
    },
    grid: {
        xAxis
        yAxis
    },
    free: {
        xAxis
        yAxis
    },
}
```

## Relation 关系

- projectId
- name
- source(nodeId)
- target(nodeId)
- width
