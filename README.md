# ReSwipeCard

本项目是在 [CardSwipeLayout](https://github.com/yuqirong/CardSwipeLayout)的基础上进行的修改，感谢原作者。

项目新增：

- 优化触摸与点击处理事件
- 增加硬件加速选项
- 修改系统ItemTouchHelper的源码，优化动画
- 上下左右滑动控制
- 滑出时间控制
- 滑动最大距离
- 循环卡片
- 卡片堆叠方式
- 卡片数量
- 自定义滑动动画(not yet)
- 增加手动滑动



![Alt text](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/img/card_stack.jpg)

项目中加入了RecyclerView，如果自己的项目中用到了，需要向底下一样:

```java
   compile 'lin.jerrylin0322.reswipecard:0.0.1' {
        exclude  module: 'recyclerview-v7'
    }
```



