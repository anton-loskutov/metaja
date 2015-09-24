# Metaja

Metaja is library for java that allows to use **metaja templates** for java source code generation at runtime which gives ability to do java metaprogramming. 

## Metaja templates

**Metaja template** is just a program for generation some text, for a example java code.  
Metaja template consists of sequence of blocks of two types: **macro** and **output**. 

**Macro** block begins with _/\*\*\*_ and ends with _\*\*\*/_ and contains generation instructions written in java.

**Output** block is everything between two neighboring macro blocks and contains output text. 

Very first block should be a macro block and it should begin with template's signature - list of parameters in round brackets, just like java method signature without method name. Template's signature should be followed by opening a curly bracket. The last block should also be a macro block and it should ends with a closing curly bracket. This convention made metaja parsing engine very simple.    

Simple template:
```java
/*** (boolean world) { ***/   
public static Hello {
    public static void main(String[] args) {
    /*** if (world) { ***/
        System.out.println("Hello, World!");    
    /*** } else { ***/
        System.out.println("Hello!");   
    /*** } ***/
    }
}
/*** } ***/
```

Metaja template's syntax allows to inject output block into macro block using \# symbol. Everything between two sequent \# symbols considered an output block:
```java
/*** (boolean world) { ***/   
public static Hello {
    public static void main(String[] args) {
    /*** 
    	# System.out.println("Hello#
    	if (boolean world) {
        	#, World#	
        }
        #!"); #
     ***/
    }
}
/*** } ***/
```

Metaja template's syntax allows to use interpolation of macro expressions inside injected output block using $ symbols:
```java
/*** (boolean world) { ***/   
public static Hello {
    public static void main(String[] args) {
    /*** 
    	# System.out.println("Hello$ world ? ", World" : "" $!"); #
     ***/
    }
}
/*** } ***/
```

_(Metaja templates syntax was inspired by [StringTemplate](http://www.stringtemplate.org/) and follows its "documents with holes" paradigm. Also, idea of metaja templates in some ways is like [JSP](http://www.oracle.com/technetwork/java/javaee/jsp/index.html).)_

Interesting property of metaja templates is that you can always get them to follow java syntax, especially while using them for generation of java source code. This can be useful for integrating them to java projects in IDEs and even debugging generated code (Metaja's template engine tries to save line numbers when it is possible).

## Metaja usage

Lets make some simple interface and template implementation for it:

_NameHolder.java_
```java
public interface NameHolder {
    String getName();
}
```

_NameHolderTemplate.java_
```java
/*** (String name) { ***/
public class NameHolderTemplate implements NameHolder {
    @Override
    public String getName() {
        /*** if (name != null) {
         # return "$ name $"; #
        } else {
         ***/
        return "Anonymous";
        /*** } ***/
    }
}
/*** } ***/
```

Some features can be noted:
1. Template is a regular java file. Common scenario of metaja usage requires that you place this java file as is on classpath instead of compiled class file. That allows metaja to easily locate template just by the class name.
2. Templated class implements a non-templated interface. That allows programmer to link templated code (dynamically compiled) to non-templated (statically compiled) code.


Now let's see how to use this template at the runtime: 
```java
public class MetajaHello {
    public static void main(String[] args) {
        ClassLoader defaultClassLoader = ClassLoader.getSystemClassLoader();

        // path of template on classpath
        String templatePath = NameHolderTemplate.class.getCanonicalName().replace('.', '/') + ".java";

        Class<NameHolder> c = org.metaja.Metaja.load(
            // class loader
            defaultClassLoader,

            // class name
            NameHolderTemplate.class.getCanonicalName(),

            // template file name (needed by javac)
            templatePath,

            // template content
            org.metaja.utils.ResourceUtils.readResource(defaultClassLoader, templatePath, StandardCharsets.UTF_8),

            // template argument value
            new Object[] { "World" }
        );

        NameHolder instance = org.metaja.utils.ClassUtils.newInstance(c);

        System.out.println("Hello, " + instance.getName() + "!");
    }
}
```

So many parameters for a load can describe principles of working with metaja very well, but if you follow common scenario (using plain java for template and putting it on a classpath in place of a class file) many parameters can be left as default.
```java
public class MetajaHelloSimplified {
    public static void main(String[] args) {
        NameHolder name = org.metaja.utils.ClassUtils.newInstance(org.metaja.Metaja.load(
                // class name
                NameHolderTemplate.class.getCanonicalName(),

                // template argument value
                new Object[] { "World" }
        ));

        System.out.println("Hello, " + name.getName() + "!");
    }
}
```

Other more useful examples of templates can be found in sources in [_org.metaja.common_](https://github.com/anton-loskutov/metaja/tree/master/src/org/metaja/common) package.

## Metaja dependencies

Metaja was written for [Kabuki project](https://github.com/anton-loskutov/kabuki) in java 8 and requires only jdk 1.8 (metaja uses [java compiler api](http://docs.oracle.com/javase/8/docs/api/javax/tools/JavaCompiler.html) from **tools.jar** therefore you can not use it with jre)

## Metaja templates in depth

_TODO:_