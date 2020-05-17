package com.hmg.json2java.helpers;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class CharSequenceJavaFileObject extends SimpleJavaFileObject {

    final CharSequence content;
    public CharSequenceJavaFileObject(
        String className, 
        CharSequence content
    ) {
        super(URI.create(
            "string:///" 
          + className.replace('.', '/') 
          + JavaFileObject.Kind.SOURCE.extension), 
            JavaFileObject.Kind.SOURCE);
        this.content = content;
    }
    @Override
    public CharSequence getCharContent(
        boolean ignoreEncodingErrors
    ) {
        return content;
    }
}
