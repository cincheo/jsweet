
module org.jsweet.prosncons {

    enum DemoEnum {
        VALUE,
        VALUE2,
        FORGOTTEN_VALUE
    }

    var a: number = 1;
        
    // no warning
    if (a == a) {
        alert("please help me, I won't print!");
    }
        
    // no warning
    a = a;

    var nullInstance: DemoEnum = null;
    alert(DemoEnum[nullInstance]); // no warning
        
    var e: DemoEnum = DemoEnum.FORGOTTEN_VALUE;
    switch (e) { // no warning
        case DemoEnum.VALUE:
            alert("I am good, I am handled.");
            break;
        case DemoEnum.VALUE2:
            alert("I am good, I am handled.");
            break;
    }
}