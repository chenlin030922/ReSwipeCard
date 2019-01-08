# ReSwipeCard

> 觉得还行给个小星星不

本项目是在 [CardSwipeLayout](https://github.com/yuqirong/CardSwipeLayout)的基础上进行的修改，感谢原作者。

#### 版本日志

> 1.0.1：修复bug

项目功能：
- 解决滑动与点击事件冲突
- 滑动方向控制
- 滑出方向控制
- 滑出时间控制
- 滑出阈值
- 循环卡片
- 卡片堆叠方式
- 卡片数量
- 增加手动滑动

效果图如下所示，也可以直接下载跟根目录下的app-debug.apk查看：

![gif](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/gif/normal.gif)

-----

### 使用方式

```java
//项目中未使用RecyclerView  
dependencies {
    compile ('lin.jerrylin0322.reswipecard:reswipecard:1.0.1')
}
//如果项目中使用了RecyclerView:
dependencies {
    compile ('lin.jerrylin0322.reswipecard:reswipecard:1.0.1') {
                exclude module:'recyclerview-v7' }
}

```

maven:

```java
<dependency>
  <groupId>lin.jerrylin0322.reswipecard</groupId>
  <artifactId>reswipecard</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

代码中：

```java
 CardSetting setting=new CardSetting();
        setting.setSwipeListener(new OnSwipeCardListener<CardBean>() {
            @Override
            public void onSwiping(RecyclerView.ViewHolder viewHolder, float dx, float dy, int direction) {
                switch (direction) {
                    case ReItemTouchHelper.DOWN:
                        Log.e("aaa", "swiping direction=down");
                        break;
                    case ReItemTouchHelper.UP:
                        Log.e("aaa", "swiping direction=up");
                        break;
                    case ReItemTouchHelper.LEFT:
                        Log.e("aaa", "swiping direction=left");
                        break;
                    case ReItemTouchHelper.RIGHT:
                        Log.e("aaa", "swiping direction=right");
                        break;
                }
            }

            @Override
            public void onSwipedOut(RecyclerView.ViewHolder viewHolder, CardBean o, int direction) {
                switch (direction) {
                    case ReItemTouchHelper.DOWN:
                        Toast.makeText(NormalActivity.this, "swipe down out", Toast.LENGTH_SHORT).show();
                        break;
                    case ReItemTouchHelper.UP:
                        Toast.makeText(NormalActivity.this, "swipe up out ", Toast.LENGTH_SHORT).show();
                        break;
                    case ReItemTouchHelper.LEFT:
                        Toast.makeText(NormalActivity.this, "swipe left out", Toast.LENGTH_SHORT).show();
                        break;
                    case ReItemTouchHelper.RIGHT:
                        Toast.makeText(NormalActivity.this, "swipe right out", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onSwipedClear() {
                Toast.makeText(NormalActivity.this, "cards are consumed", Toast.LENGTH_SHORT).show();
            }
        });
        CardTouchHelperCallback helperCallback = new CardTouchHelperCallback(mRecyclerView, list,setting);
        mReItemTouchHelper = new ReItemTouchHelper(helperCallback);
        CardLayoutManager layoutManager = new CardLayoutManager(mReItemTouchHelper, setting);
        mRecyclerView.setLayoutManager(layoutManager);
        CardAdapter cardAdapter = new CardAdapter(list);
 		mRecyclerView.setAdapter(cardAdapter);
```

在Adapter的item的xml中使用SwipeTouchLayout当做根布局:

```java
<?xml version="1.0" encoding="utf-8"?>
<com.lin.cardlib.SwipeTouchLayout xmlns:android="http://schemas.android.com/apk/res/android"
                                  xmlns:app="http://schemas.android.com/apk/res-auto"
                                  android:layout_width="wrap_content"
                                  android:layout_height="wrap_content"
                                  android:paddingBottom="30dp">

  ...

</com.lin.cardlib.SwipeTouchLayout>
```



----

### `CardSetting`

所有的参数变量都在`CardSetting`当中实现。

上下左右滑动控制，默认四个方向都可以滑动：

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



修改卡片堆叠方式，默认为从下往上：

```java
CardConfig.java
public int getStackDirection() {
        return  ReItemTouchHelper.DOWN;
    }
```

效果图：

![Alt text](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/img/card_stack.jpg)

是否循环，默认为循环：

```java
 public boolean isLoopCard() {
        return true;
    }
```

去掉循环的效果如下图所示：

![gif](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/gif/no_loop.gif)

自动滑动效果:

```java
//direction=ReItemTouchHelper.LEFT or ReItemTouchHelper.RIGHT .etc
mReItemTouchHelper.swipeManually(direction);
```

卡片缩放递减的值，默认为0.1f：

```java
    public float getCardScale() {
        return DEFAULT_SCALE;
    }
```

卡片展示数量：

```java
  public int getShowCount() {
        return DEFAULT_SHOW_ITEM;
    }
```

移动过程中最大卡片旋转值：

```java
   public float getCardRotateDegree() {
        return DEFAULT_ROTATE_DEGREE;
    }
```

布局时卡片偏移量，第一张不偏移，随后递增，默认偏移量为14：

```java
 public int getCardTranslateDistance() {
        return DEFAULT_TRANSLATE_Y;
    }
```

是否开启硬件加速：

```java
    public boolean enableHardWare() {
        return true;
    }
```

控制滑出时间：

```java
    public int getSwipeOutAnimDuration() {
        return 400;
    }
```

控制滑动可以滑出的阈值，默认是RecyclerView的宽度*0.3f：

```java
    public float getSwipeThreshold() {
        return 0.3f;
    }
```

----

如果有任何的问题，可以在 [Issues](https://github.com/JerryChan123/ReSwipeCard/issues)当中告诉我~

License
-------

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.

