#### ActivityProperty
lauchMode：

> standard： 每次激活Activity时(startActivity)，都创建Activity实例，并放入任务栈；

> singleTop：每次激活Activity时(startActivity)，如果任务栈栈顶就是该Activity，则不需要创建，其余情况都要创建Activity实例；

> singleTask：如果要激活的那个Activity在任务栈中存在该实例，则不需要创建，只需要把此Activity放入栈顶，并把该Activity以上的Activity实例都pop；（如果该activity位于栈低（会导致task的realActivity是singleTask模式），并且在MainActivity指定的task中，当按home键或其它操作进入桌面界面，并点击icon打开本应用时，会导致此task中除此activity实例外，其它的activity实例都出栈，并会执行此activity的onNewIntent方法）

> singleInstance：这个跟singleTask基本上是一样，只有一个区别：在这个模式下的Activity实例所处的task中，只能有这个activity实例，不能有其他的实例


taskAffinity:

> activity: 每个activity都有taskAffinity属性，它是指activity希望进入的task（每个task也有taskAffinity），如果没有明确指明，则是application标签指明的taskAffinity，如果application也没有指明，则是应用包名

> task: task也有taskAffinity属性，它的值等于它的根Activity的taskAffinity的值(注意：taskAffinity不是task的唯一标示，即singleInstance模式的task可以和其它lauchMode的task的taskAffinity的值相等)
    如果MainActivity指定的task的realActivity是singleTask模式，当按home键或其它操作进入桌面界面，并点击icon打开本应用时，会导致此task中除此activity实例外，其它的activity实例都出栈，并会执行此activity的onNewIntent方法，如果此activity不存在，这重新new一个此activity实例

疑问：假如有四个activity，分别是A(standard, taskAffinityA)、B(singleTop, taskAffinityB)、C(singleTask, taskAffinityC)、D(singleInstance, taskAffinityD)，括号里是设置的启动模式和对应的taskAffinity，如果A-B-C-D启动，有几个task？
如果是C-D-A-B启动，又有多少个task？如果D-C-A-B呢？如果D-A-D-B-D-C-A呢？

activity放入哪个task的个人经验总结：

    当前task是否允许放其他activity实例
        允许：在检查此activity是否需要new task
            需要：则根据activity的taskAffinity属性，查找是否存在该task，没有则重新新建一个task，并将activity放入其中
            不需要：则直接将activity实例放入此task中
        不允许：根据activity的taskAffinity属性，查找是否存在该task，重新新建一个task，并将activity放入其中

Intent flag:

> FLAG_ACTIVITY_NEW_TASK:当使用这个标记的时候，如果没有taskAffinity指定的task运行此activity，则会新建一个task，并新建此activity实例放入此task中；如果已经有一个task在运行你要启动的activity,这是将不会启动新的activity,而是把这个拥有你要启动activity的task切换到前台，保持它最后操作是的状态

> FLAG_ACTIVITY_CLEAR_TOP:这个FLAG就相当于加载模式中的SingleTask（如果Activity的启动模式是默认的standard，则会先finish掉此activity，然后重新实例化一个新的activity）

> FLAG_ACTIVITY_SINGLE_TOP:这个FLAG就相当于加载模式中的singleTop

> FLAG_ACTIVITY_BROUGHT_TO_FRONT:暂不知道用途，这个标志一般不是由程序代码设置的

> FLAG_ACTIVITY_REORDER_TO_FRONT:如果已经启动了四个Activity：A，B，C和D，在D Activity里，想再启动一个Actvity B，但不变成A,B,C,D,B，而是希望是A,C,D,B(只对standard和singleTop有用)

> FLAG_ACTIVITY_NO_HISTORY:意思就是说用这个FLAG启动的Activity，一旦推出，他就不会存在于栈中，原来是A,B,C 这个时候再C中以这个FLAG启动D的 ， D再启动E，这个时候栈中情况为A,B,C,E。

> FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS: 如果设置，新的Activity不会在最近启动的Activity的列表中保存。

> FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY: 这个标志一般不由应用程序代码设置，如果这个Activity是从历史记录里启动的（常按HOME键），那么，系统会帮你设定。

> FLAG_ACTIVITY_MULTIPLE_TASK: 不建议使用此标志，除非你自己实现了应用程序启动器。

> FLAG_ACTIVITY_NO_ANIMATION:  这个标志将阻止系统进入下一个Activity时应用Acitivity迁移动画。

> FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET: 如果设置，这将在Task的Activity Stack中设置一个还原点，当Task恢复时，需要清理Activity。例如下一次Task带着FLAG_ACTIVITY_RESET_TASK_IF_NEEDED标记进入前台时，这个Activity和它之上的都将关闭，以至于用户不能再返回到它们，但是可以回到之前的Activity。

> FLAG_ACTIVITY_RESET_TASK_IF_NEEDED：配合FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET使用（目测点击launcher图片启动应用，系统都设置了此参数）

Activity 常用属性：

> allowTaskReparenting(默认false)：是否允许activity更换从属的任务,用来标记Activity能否从启动的Task移动到有着affinity的Task（当这个Task进入到前台时）——“true”，表示能移动，“false”，表示它必须呆在启动时呆在的那个Task里(重新宿主只能限于“standard”和“singleTop”模式)

>- 例如：appA的ActivityA 点击一个按钮启动appB的activityB，这时activityB在appA的taskA中，然后按home键退到桌面，（然后点击appB icon启动appB，这是activityB实例会从taskA移到taskB中，并放入栈顶）或者（点击appA icon启动appA时，也会将activityB移动到activityB taskAffinity指定的taskB中去（如果没有，则新建taskB），然后显示taskA中的activityA）

> alwaysRetainTaskState(默认false)：用来标记Activity所在的Task的状态是否总是由系统来保持——“true”，表示总是；“false”，表示在某种情形下允许系统恢复Task 到它的初始化状态。默认值是“false”。这个特性只针对Task的根Activity有意义；对其它Activity来说，忽略之。（暂时不是很清楚这到低是什么意思？希望高手帮忙解答）

> clearTaskOnLaunch(默认false)：用来标记是否从Task中清除所有的Activity，除了根Activity外（每当从主画面重新启动时）——“true”，表示总是清除其它的 Activity，“false”表示不。默认值是“false”。这个特性只对启动一个新的Task的Activity（根Activity）有意义； 对Task中其它的Activity忽略。

>- 例如：启动ActivityA（clearTaskOnLaunch=true）、ActivityB、ActivityC，假设这三个Activity在同一个task中，当按home键退回到主界面重新启动应用时，ActivityB、ActivityC会被销毁，会显示ActivityA的页面

> finishOnTaskLaunch(默认false)：这个属性和android:allowReparenting属性相似，不同之处在于allowReparenting属性是重新宿主到taskaffinity的task中，而finishOnTaskLaunch属性是销毁实例。如果这个属性和android:allowReparenting都设定为“true”，则这个属性好些。

>- 例如：appA的ActivityA 点击一个按钮启动appB的activityB(finishOnTaskLaunch=true)，这时activityB在appA的taskA中，然后按home键退到桌面，不管是启动appA还是appB，activityB实例都将被销毁

> exported：是否允许activity被其它程序调用

> excludeFromRecents：是否可被显示在最近打开的activity列表里

查看android task栈信息adb命令：

    adb shell dumpsys activity activities
