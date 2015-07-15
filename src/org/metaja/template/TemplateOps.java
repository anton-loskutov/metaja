package org.metaja.template;

import org.metaja.utils.Iteration;
import org.metaja.utils.ReflectionUtils;

public interface TemplateOps {

    void OUT(Object s);

    // Utils

    Iteration ITERATION = Iteration.INSTANCE;

    // Reflection Utils

    ReflectionUtils REFLECTION = ReflectionUtils.INSTANCE;

    ReflectionUtils.Modifier PUBLIC = ReflectionUtils.Modifier.PUBLIC;
    ReflectionUtils.Modifier PRIVATE = ReflectionUtils.Modifier.PRIVATE;
    ReflectionUtils.Modifier STATIC = ReflectionUtils.Modifier.STATIC;
}
