#### ActivityProperty
lauchMode：

    standard： 每次激活Activity时(startActivity)，都创建Activity实例，并放入任务栈；
    singleTop：每次激活Activity时(startActivity)，如果任务栈栈顶就是该Activity，则不需要创建，其余情况都要创建Activity实例；
    singleTask：如果要激活的那个Activity在任务栈中存在该实例，则不需要创建，只需要把此Activity放入栈顶，并把该Activity以上的Activity实例都pop；
    singleInstance：这个跟singleTask基本上是一样，只有一个区别：在这个模式下的Activity实例所处的task中，只能有这个activity实例，不能有其他的实例

taskAffinity:

    activity: 每个activity都有taskAffinity属性，它是指activity希望进入的task（每个task也有taskAffinity），如果没有明确指明，则是application标签指明的taskAffinity，如果application也没有指明，则是应用包名
    task: task也有taskAffinity属性，它的值等于它的根Activity的taskAffinity的值(注意：taskAffinity不是task的唯一标示，即singleInstance模式的task可以和其它lauchMode的task的taskAffinity的值相等)

疑问：假如有四个activity，分别是A(standard, taskAffinityA)、B(singleTop, taskAffinityB)、C(singleTask, taskAffinityC)、D(singleInstance, taskAffinityD)，括号里是设置的启动模式和对应的taskAffinity，如果A-B-C-D启动，有几个task？
如果是C-D-A-B启动，又有多少个task？如果D-C-A-B呢？如果D-A-D-B-D-C-A呢？

总结：

    此task是否允许放其他activity实例
        允许：在检查此activity是否需要new task
            需要：则根据activity的taskAffinity属性，重新新建一个task，并将activity放入其中
            不需要：则直接将activity实例放入此task中
        不允许：根据activity的taskAffinity属性，重新新建一个task，并将activity放入其中

Intent flag:

    FLAG_ACTIVITY_NEW_TASK:当使用这个标记的时候，如果没有taskAffinity指定的task运行此activity，则会新建一个task，并新建此activity实例放入此task中；如果已经有一个task在运行你要启动的activity,这是将不会启动新的activity,而是把这个拥有你要启动activity的task切换到前台，保持它最后操作是的状态
    FLAG_ACTIVITY_CLEAR_TOP:这个FLAG就相当于加载模式中的SingleTask（如果Activity的启动模式是默认的standard，则会先finish掉此activity，然后重新实例化一个新的activity）
    FLAG_ACTIVITY_SINGLE_TOP:这个FLAG就相当于加载模式中的singleTop
    FLAG_ACTIVITY_BROUGHT_TO_FRONT:暂不知道用途，这个标志一般不是由程序代码设置的
    FLAG_ACTIVITY_REORDER_TO_FRONT:如果已经启动了四个Activity：A，B，C和D，在D Activity里，想再启动一个Actvity B，但不变成A,B,C,D,B，而是希望是A,C,D,B(只对standard和singleTop有用)
    FLAG_ACTIVITY_NO_HISTORY:意思就是说用这个FLAG启动的Activity，一旦推出，他就不会存在于栈中，原来是A,B,C 这个时候再C中以这个FLAG启动D的 ， D再启动E，这个时候栈中情况为A,B,C,E。
    FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS: 如果设置，新的Activity不会在最近启动的Activity的列表中保存。
    FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY: 这个标志一般不由应用程序代码设置，如果这个Activity是从历史记录里启动的（常按HOME键），那么，系统会帮你设定。
    FLAG_ACTIVITY_MULTIPLE_TASK: 不建议使用此标志，除非你自己实现了应用程序启动器。
    FLAG_ACTIVITY_NO_ANIMATION:  这个标志将阻止系统进入下一个Activity时应用Acitivity迁移动画。
    FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET: 如果设置，这将在Task的Activity Stack中设置一个还原点，当Task恢复时，需要清理Activity。例如下一次Task带着FLAG_ACTIVITY_RESET_TASK_IF_NEEDED标记进入前台时，这个Activity和它之上的都将关闭，以至于用户不能再返回到它们，但是可以回到之前的Activity。
    FLAG_ACTIVITY_RESET_TASK_IF_NEEDED：配合FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET使用（目测点击launcher图片启动应用，系统都设置了此参数）



