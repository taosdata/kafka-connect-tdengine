package com.moon.core.lang.reflect;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

/**
 * @author moonsky
 */
enum Asserts implements Predicate<Executable> {
    isStatic {
        @Override
        public boolean test(Executable method) {
            return ModifierUtil.isStatic(method);
        }
    },
    isMember {
        @Override
        public boolean test(Executable method) {
            return !ModifierUtil.isStatic(method);
        }
    },
    noParams {
        @Override
        public boolean test(Executable method) {
            return method.getParameterCount() == 0;
        }
    },
    lowestDefault {
        @Override
        public boolean test(Executable executable) {
            return !ModifierUtil.isPrivate(executable);
        }
    },
    lowestProtected {
        @Override
        public boolean test(Executable executable) {
            int m = executable.getModifiers();
            return Modifier.isPublic(m) || Modifier.isProtected(m);
        }
    }
}
