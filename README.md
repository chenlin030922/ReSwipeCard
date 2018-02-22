# ReSwipeCard

ReSwipeCard is based on [CardSwipeLayout](https://github.com/yuqirong/CardSwipeLayout)。

[中文说明](https://github.com/JerryChan123/ReSwipeCard/blob/master/README_zh.md)

#### Introduction：

- Solve the conflict between scroll and click


- Hardware acceleration support
- support for  defining  directions to slide
- support for  defining  directions to slide out
- support for defining durations to slide out
- support for looping the card or not
- support for defining the way to stack the cards
- support for defining the numbers of the stacked card
- support for sliding card automatically

below is the demo of this projec ，you could install the app-debug.apk to see on the mobile：

![gif](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/gif/normal.gif)

-----

### How to use:

```java
//don't use RecyclerView on your project
dependencies {
    compile ('lin.jerrylin0322.reswipecard:reswipecard:1.0.0')
}
//already use RecyclerView on your project
dependencies {
    compile ('lin.jerrylin0322.reswipecard:reswipecard:1.0.0') {
                exclude module:'recyclerview-v7' }
}

```

maven:

```java
<dependency>
  <groupId>lin.jerrylin0322.reswipecard</groupId>
  <artifactId>reswipecard</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

in Java：

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

You need to use SwipeTouchLayout as the root node in your adapter's xml item:

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

All parameters are defined on`CardSetting`.

The direction for sliding:

```java
public int getSwipeDirection() {
        return ReItemTouchHelper.LEFT | ReItemTouchHelper.RIGHT
                | ReItemTouchHelper.UP |ReItemTouchHelper.DOWN;
    }
```

If you don't need the left slide,just remove from the above,and the effect is like below：

![gif](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/gif/no_left_swipe.gif)

The direction for sliding out:

```java
    public int couldSwipeOutDirection() {
        return ReItemTouchHelper.LEFT | ReItemTouchHelper.RIGHT
                | ReItemTouchHelper.UP |ReItemTouchHelper.DOWN;
    }
```

If you don't need the top slide out,just remove from the above,and the effect is like below：

![gif](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/gif/no_swipe_out_vertical.gif)



The way to stack the cards:

```java
CardConfig.java
public int getStackDirection() {
        return  ReItemTouchHelper.DOWN;
    }
```

All ways to stack the cards：

![Alt text](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/img/card_stack.jpg)

Whether needs to loop thr card

```java
 public boolean isLoopCard() {
        return true;
    }
```

The example for not looping the card：

![gif](https://github.com/JerryChan123/ReSwipeCard/blob/dev/pic/gif/no_loop.gif)

Sliding out the card automatically:

```java
//direction=ReItemTouchHelper.LEFT or ReItemTouchHelper.RIGHT .etc
mReItemTouchHelper.swipeManually(direction);
```

The degressive scale value for every card(first card is 1f,second card is 0.9f ...)

```java
    public float getCardScale() {
        return DEFAULT_SCALE;
    }
```

The count for show cards:

```java
  public int getShowCount() {
        return DEFAULT_SHOW_ITEM;
    }
```

Max rotate value when sliding card:

```java
   public float getCardRotateDegree() {
        return DEFAULT_ROTATE_DEGREE;
    }
```

The offset value for every card(first card is 0,second card is DEFAULT_TRANSLATE_Y,third card is DEFAULT_TRANSLATE_Y*2 ...)

```java
 public int getCardTranslateDistance() {
        return DEFAULT_TRANSLATE_Y;
    }
```

Need to open the haveware acceleration:

```java
    public boolean enableHardWare() {
        return true;
    }
```

The duration for sliding out:

```java
    public int getSwipeOutAnimDuration() {
        return 400;
    }
```

Decide the Distance for notifing the card to slided out，default is the RecyclerView's width'*0.3f：

```java
    public float getSwipeThreshold() {
        return 0.3f;
    }
```

----

If you have any problem ,talk to me through [Issues](https://github.com/JerryChan123/ReSwipeCard/issues)
