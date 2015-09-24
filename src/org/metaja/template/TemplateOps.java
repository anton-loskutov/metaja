package org.metaja.template;

import org.metaja.utils.IterationUtils;
import org.metaja.utils.ReflectionUtils;

public interface TemplateOps {

    void OUT(Object s);

    // Utils
    IterationUtils ITERATION = IterationUtils.INSTANCE;
    ReflectionUtils REFLECTION = ReflectionUtils.INSTANCE;

    ReflectionUtils.Modifier PUBLIC = ReflectionUtils.Modifier.PUBLIC;
    ReflectionUtils.Modifier PROTECTED = ReflectionUtils.Modifier.PROTECTED;
    ReflectionUtils.Modifier PRIVATE = ReflectionUtils.Modifier.PRIVATE;
    ReflectionUtils.Modifier ABSTRACT = ReflectionUtils.Modifier.ABSTRACT;
    ReflectionUtils.Modifier STATIC = ReflectionUtils.Modifier.STATIC;
}
