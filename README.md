动态的实现任意多个数量的方形EditText的容器，限制一个字符输入，并且焦点自动向后移动，在有字符时，删除时焦点会自动向前移动，单独点击EditText时，会清空内容获取焦点。

## 一、控件的使用 ##
在工程的build.gradle文件中添加

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2.在项目build.gradle中添加依赖

```
dependencies {
	        compile 'com.github.MingYueChunQiu:BlockEditTextViewGroup:1.0'
	}
```
如果报Failed to resolve:com.android.support:appcompat-v7:这样的错，将依赖改写成这样

```
compile ("com.github.MingYueChunQiu:BlockEditTextViewGroup:1.0"){
        exclude group:'com.android.support'
    }
```

## 二、控件的使用 ##
在代码中使用时，可以使用builder进行创建，也可以直接使用构造函数

```
        BlockEditTextViewGroup blockEditTextViewGroup = new BlockEditTextViewGroup.Builder(this)
                .setCount(3)
                .setTextSize(50)
                .setMargin(20)
                .setItemWidth(200)
                .setOnCompleteAllInputListener(new BlockEditTextView.OnCompleteAllInputListener() {
                    @Override
                    public void onCompleteAllInput(List<String> list) {
                        LogUtil.d("完成", list.size() + "");
                    }
                }).build();
        ((LinearLayoutCompat)view).addView(blockEditTextViewGroup);
```
可以给控件直接设置EditText个数，外边距，文本大小，每个EditText所占的item宽度居中显示，可以监听所有EditText都完成输入的回调事件。
如果有什么建议或意见，欢迎大家提出改善。
