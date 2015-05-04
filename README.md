#### ActivityProperty
task:

    taskAffinity: task也有taskAffinity属性，它的值等于它的根Activity的taskAffinity的值
    
activity:

    taskAffinity: 每个activity都有taskAffinity属性，它是指activity希望进入的task（每个task也有taskAffinity），如果没有明确指明，则是application标签指明的taskAffinity，如果application也没有指明，则是应用包名





