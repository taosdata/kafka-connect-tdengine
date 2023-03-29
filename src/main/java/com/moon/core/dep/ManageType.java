package com.moon.core.dep;

import com.moon.core.lang.StringUtil;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public enum ManageType {
    MAVEN("" +//
        "{indentAll}{indent}<dependency>\n" +//
        "{indentAll}{indent}{indent}<groupId>%s</groupId>\n" +//
        "{indentAll}{indent}{indent}<artifactId>%s</artifactId>\n" +//
        "{indentAll}{indent}{indent}<version>%s</version>\n" +//
        "{indentAll}{indent}</dependency>"),
    GRADLE("{indentAll}{indent}%s:%s:%s");

    public final String TEMPLATE;

    public final static int DEFAULT_INDENT = 4;
    public final static int DEFAULT_INDENT_ALL = 4;

    ManageType(String template) { TEMPLATE = template; }

    /**
     * 依赖格式：groupId:artifactId:version?
     *
     * @param dependency groupId:artifactId:version?
     *
     * @return 经过格式化后的 Maven 依赖节点
     */
    public String dependencyNode(String dependency) { return dependencyNode(dependency, DEFAULT_INDENT); }

    /**
     * 依赖格式：groupId:artifactId:version?
     *
     * @param dependency groupId:artifactId:version?
     * @param indent     缩进
     *
     * @return 经过格式化后的 Maven 依赖节点
     */
    public String dependencyNode(String dependency, int indent) {
        return dependencyNode(dependency, indent, DEFAULT_INDENT_ALL);
    }

    /**
     * 依赖格式：groupId:artifactId:version?
     *
     * @param dependency groupId:artifactId:version?
     * @param indent     缩进
     * @param indentAll  整体缩进
     *
     * @return 经过格式化后的 Maven 依赖节点
     */
    public String dependencyNode(String dependency, int indent, int indentAll) {
        String[] parts = dependency.split(":");
        int length = parts.length;
        String groupId, artifactId, version = parts.length > 2 ? parts[2] : null;
        if (length > 0) {
            groupId = parts[0];
        } else {
            throw new IllegalArgumentException("Unknown 'groupId' from: " + dependency);
        }
        if (length > 1) {
            artifactId = parts[1];
        } else {
            throw new IllegalArgumentException("Unknown 'artifactId' from: " + dependency);
        }
        return dependencyNode(groupId, artifactId, version, indent, indentAll);
    }

    /**
     * Maven 依赖坐标
     *
     * @param groupId    groupId
     * @param artifactId artifactId
     * @param indent     缩进
     *
     * @return 格式化后的 Maven 依赖节点
     */
    public String dependencyNode(
        String groupId, String artifactId, int indent
    ) { return dependencyNode(groupId, artifactId, null, indent, indent); }

    /**
     * Maven 依赖坐标
     *
     * @param groupId    groupId
     * @param artifactId artifactId
     * @param version    版本号
     * @param indent     缩进
     *
     * @return 格式化后的 Maven 依赖节点
     */
    public String dependencyNode(
        String groupId, String artifactId, String version, int indent
    ) { return dependencyNode(groupId, artifactId, version, indent, indent); }

    /**
     * Maven 依赖坐标
     *
     * @param groupId    groupId
     * @param artifactId artifactId
     * @param version    版本号
     * @param indent     缩进
     * @param indentAll  整体缩进
     *
     * @return 格式化后的 Maven 依赖节点
     */
    public String dependencyNode(
        String groupId, String artifactId, String version, int indent, int indentAll
    ) {
        version = StringUtil.defaultIfEmpty(version, " version? ");
        return String.format(TEMPLATE, groupId, artifactId, version)
            .replaceAll("\\{indentAll\\}", StringUtil.repeat(" ", indentAll))
            .replaceAll("\\{indent\\}", StringUtil.repeat(" ", indent));
    }
}
