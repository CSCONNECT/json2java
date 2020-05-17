package com.hmg.json2java.helpers;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;


public class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	JavaFileObject o;
    ClassFileManager(StandardJavaFileManager m) {
        super(m);
    }
    @Override
    public JavaFileObject getJavaFileForOutput(
        JavaFileManager.Location location,
        String className,
        JavaFileObject.Kind kind,
        FileObject sibling
    ) {
        return o = new JavaFileObject(className, kind);
    }
}
