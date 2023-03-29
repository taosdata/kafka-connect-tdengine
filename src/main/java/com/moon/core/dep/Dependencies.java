package com.moon.core.dep;

import com.moon.core.enums.Arrays2;
import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;

import java.util.function.Function;

/**
 * 依赖包管理
 * <p>
 * 别的模块不同功能可能会有不同的依赖，这里集中管理所有依赖信息，别的地方都调这里的信息
 * <p>
 * <p>
 * 相对而言，这是一种比较"重"的办法，随着相关依赖的增加，可修改为从配置文件读取的方式
 *
 * @author moonsky
 */
@SuppressWarnings("all")
public enum Dependencies {
    /**
     * hibernate-validator 表单验证相关依赖
     */
    HIBERNATE_VALIDATOR(//
        strs("org.hibernate.validator:hibernate-validator",//
            "org.glassfish.web:javax.el",//
            "javax.el:javax.el-api"),//
        strs("org.springframework.boot:spring-boot-starter-validation:2.x+")),
    /**
     * poi 基本依赖，office 2003 以上版本
     */
    XLS(strs("org.apache.poi:poi:4.x+")),
    /**
     * poi 基本依赖，office 2007 以上版本
     */
    XLSX(strs("org.apache.poi:poi:4.x+", "org.apache.poi:poi-ooxml:4.x+")),
    /**
     * uuid 生成器
     */
    UUID_GENERATOR(strs("com.fasterxml.uuid:java-uuid-generator:4.x+")),
    ;
    /**
     * 基本依赖包信息
     */
    private final String[] dependencies;
    /**
     * spring-boot 依赖
     * 由于 spring-boot 集中管理了一些依赖包，多个依赖包在 spring-boot 中可能被统一成一个包管理了
     */
    private final String[] forSpringBoot;

    Dependencies(String[] dependencies) { this(dependencies, null); }

    Dependencies(String[] dependencies, String[] forSpringBoot) {
        this.forSpringBoot = defaultArr(forSpringBoot);
        this.dependencies = defaultArr(dependencies);
    }

    /**
     * 获取所有相关的基础依赖
     *
     * @return 依赖包信息
     */
    public String getDependenciesStr() { return JoinerUtil.join(dependencies, "; "); }

    /**
     * 自定义运行是异常，由于相关依赖包只需要在需要时才用引入，有些可能用不到，所以统一用运行时异常返回
     *
     * @param transfer 异常构建器，接收异常消息作为参数
     * @param <EX>     实际运行是异常类型
     *
     * @return 异常对象
     */
    public <EX extends RuntimeException> EX newException(Function<String, ? extends EX> transfer) {
        StringBuilder sb = new StringBuilder("请确保存在相关依赖包：");
        sb.append(getDependenciesStr()).append("; ");
        sb.append(getMavenDep()).append(getSpringBootDep());
        return transfer.apply(sb.toString());
    }

    /**
     * 获取默认异常
     *
     * @return 默认异常
     */
    public IllegalStateException getException() { return newException(IllegalStateException::new); }

    /**
     * 直接抛出异常
     *
     * @param <T> 兼容返回值
     *
     * @return 兼容返回值
     */
    public <T> T throwException() { throw getException(); }

    private String getMavenDep() {
        String template = "\n\t如果你使用 Maven 管理，请检查：\n%s\n";
        String deps = toMvnDeps(this.dependencies);
        return StringUtil.isEmpty(deps) ? "" : String.format(template, deps);
    }

    private String getSpringBootDep() {
        String template = "\n\t如果你使用 Maven 管理的 SpringBoot 项目，请检查：\n%s\n";
        String deps = toMvnDeps(this.forSpringBoot);
        return StringUtil.isEmpty(deps) ? "" : String.format(template, deps);
    }

    private static String toMvnDeps(String[] deps) {
        String[] strs = new String[deps.length];
        for (int i = 0; i < deps.length; i++) {
            strs[i] = ManageType.MAVEN.dependencyNode(deps[i]);
        }
        return JoinerUtil.join(strs, "\n");
    }

    private static String[] strs(String... deps) { return deps; }

    private static String[] defaultArr(String[] arr) { return arr == null ? Arrays2.STRINGS.empty() : arr; }
}
