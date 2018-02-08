# ReSwipeCard

本项目是在 [CardSwipeLayout](https://github.com/yuqirong/CardSwipeLayout)的基础上进行的修改，感谢原作者。

项目新增：

- 优化触摸与点击处理事件
- 增加硬件加速选项
- 修改系统ItemTouchHelper的源码，优化动画
- 上下左右滑动控制
- 上下左右滑出控制
- 滑出时间控制
- 滑出阈值
- 循环卡片
- 卡片堆叠方式
- 卡片数量
- 自定义滑动动画(not yet)
- 增加手动滑动

效果图如下所示：

![gif](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/gif/normal.gif)



功能一：上下左右滑动控制，默认四个方向都可以滑动：

```java
public int getSwipeDirection() {
        return ReItemTouchHelper.LEFT | ReItemTouchHelper.RIGHT
                | ReItemTouchHelper.UP |ReItemTouchHelper.DOWN;
    }
```

去掉左边滑动的效果图如图所示：

![gif](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/gif/no_left_swipe.gif)

上下左右滑出控制，默认四个方向都可以滑出:

```java
    public int couldSwipeOutDirection() {
        return ReItemTouchHelper.LEFT | ReItemTouchHelper.RIGHT
                | ReItemTouchHelper.UP |ReItemTouchHelper.DOWN;
    }
```

去掉垂直方向的滑出效果图如图所示：

![gif](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/gif/no_swipe_out_vertical.gif)

功能二：修改卡片堆叠方式，默认为从下往上：

```java
CardConfig.java
public int getStackDirection() {
        return  ReItemTouchHelper.DOWN;
    }
```

效果图：

![Alt text](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/img/card_stack.jpg)



项目中加入了RecyclerView，如果自己的项目中用到了，需要向底下一样:

```java
   compile 'lin.jerrylin0322.reswipecard:0.0.1' {
        exclude  module: 'recyclerview-v7'
    }
```



