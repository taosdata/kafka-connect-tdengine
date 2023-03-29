package com.moon.runner;

import com.moon.runner.core.ParseUtil;

import java.util.Date;

/**
 * 介绍，运行计算表达式工具类，如：
 * <p>employee.name<p>employee.name.length()
 * <p>group.employee.age.doubleValue() + employee.name.length() + 20 * 5 等。
 * <p>
 * <strong>【 一 】</strong>、支持的运算：
 * <p>
 * 1. 基本运算：+、-、*、/、%；
 * <p>
 * 2. 位运算：&、|、^、<<、>>、>>>；
 * <p>
 * 3. 比较运算：==、>、>=、==、<、<=；
 * <p>
 * 4. 逻辑运算：&&、||、!;
 * <p>
 * 5. 括号提升优先级：( )
 * <p>
 * <strong>【 二 】</strong>、预定义关键字：null、true、false
 * <p>
 * <strong>【 三 】</strong>、int 数字：12、25 等
 * <p>
 * <strong>【 四 】</strong>、double 数字：20.0、36.5 等
 * <p>
 * <strong>注意：</strong>
 * 参与运算的数字只有 int 或 double，
 * 如果一个方法调用的返回值是其他类型数字且没有继续参与运算，
 * 则可以返回其他类似数字，包括 char
 * <p>
 * RunnerUtil 中的数字间可以有一个或多个下划线作为方便阅读用的区分，如：
 * <p>
 * 123_456 == 123456、3_456.789_123 == 3456.789123
 * <p>
 * <strong>【 五 】</strong>、字符串：'string'、"string"
 * <p>
 * <strong>【 六 】</strong>、Map：解析成 HashMap，
 * <p>
 * Map 的键：null、true、false、int 型数字、double 型数字、字符串；
 * 其他所有形式的键均认为是字符串，且不能是表达式；
 * <p>
 * 类似 JavaScript 中的对象，不同的是 Map 的键支持数字、true 等；
 * <p>
 * Map 的值支持任意对象；
 * <p>
 * <strong>声明方式：</strong><p>
 * 1. 空 Map 必须包含一个冒号，如：{:}
 * <p>
 * 2. 带有值的 Map：{key: 'value', null: 1, true: true, false: 2, 'null': null, "true": "string",}
 * <p>
 * 一个键值对需用<strong> 冒号 </strong>分割，键值对之间需用<strong> 逗号 </strong>分割；
 * 最后一个键值对后的逗号可有可无。
 * <p>
 * <strong>【 七 】</strong>、List：解析成 ArrayList，
 * <p>
 * List 的每一项可以是任意值，两个连续逗号之间若没有任何字符会被解析成一个 null 值，如：
 * <p>
 * 空 List：{}
 * <p>
 * 带有值的 List：{null, true, false,, 20, 23.5, 'abc', "xyz",  ,};
 * <p>
 * 最后一个逗号可有可无
 * <p>
 * <strong>【 八 】</strong>、静态方法调用符：@ ；
 * <p>
 * 如：@System.currentTimeMillis() ==== 无参公共静态方法调用；
 * <p>
 * <strong>说明：</strong>
 * 静态方法调用只能调用此工具库所包含的类和 JDK 中以下包的静态方法：
 * <p>
 * {@link java.lang}、 {@link java.util}、 {@link java.lang.reflect}
 * <p>
 * 目前只能调用最多一个参数的方法，变长参数不完全支持
 * <p>
 * <strong>【 九 】</strong>、实例方法调用，如：
 * <p>
 * 无参公共实例方法调用：'string'.length()；
 * <p>
 * {0}.getSheet(0) ==== 带有一个参数的公共实例方法调用；
 * <p>
 * <strong>【 十 】</strong>、链式取值和方法调用：employee.name.length()
 * <p>
 * <strong>【 十一 】</strong>、内置函数：@；
 * 此工具类内置了一些内置函数，提供一些常用的操作，对于一些基本的操作，
 * <p>
 * 建议使用函数，而不是静态或实例方法调用；
 * <p>
 * 函数调用与静态方法调用相同，都是用 “@” 符号标识，如：@map(...)，
 * <p>
 * 不同的是函数命名空间全小写，并且函数优先级高于方法执行；
 * <p>
 * 内置函数有：
 * <p>
 * - @map(...)：将一组数据按键值列表返回一个 Map，<p>
 * - @map.getSheet(Map, keyName)：获取 Map 指定值<p>
 * - @map.size(Map)：返回 Map 的大小；<p>
 * - @map.isEmpty(Map)<p>
 * - @map.hasKey(Map)<p>
 * - @map.hasValue(Map)<p>
 * <p>
 * - @list(...)：将一组数据列表封装成 List<p>
 * - @list.getSheet(List, int)：获取指定索引值<p>
 * - @list.size(List)：返回 List 的大小；<p>
 * - @list.isEmpty(List)：<p>
 * - @list.hasIndex(List)：<p>
 * - @list.hasValue(List)：<p>
 * <p>
 * - @time()：返回当前时间毫秒数{@link System#currentTimeMillis()}；<p>
 * - @time(Object)：将对象转换成{@link java.time.LocalDateTime}<p>
 * - @time.year()：当前年份<p>
 * - @time.month()：当前月份<p>
 * - @time.day()：当前月份第几天<p>
 * - @time.hour()：当前小时数<p>
 * - @time.minute()：当前分钟数<p>
 * - @time.second()：当前秒数<p>
 * <p>
 * - @str(Object)：将对象转换成 String {@link Object#toString()}<p>
 * - @str.substring(String,index[,index])：{@link String#substring(int, int)}}<p>
 * - @str.indexOf(String,String)：{@link String#indexOf(int)}<p>
 * - @str.contains(String,String)：{@link String#contains(CharSequence)}<p>
 * - @str.startsWith(String,String[, int])：{@link String#startsWith(String)}<p>
 * - @str.endsWith(String,String[, int])：{@link String#endsWith(String)}<p>
 * - @str.length(String)：{@link String#length()} 返回字符串长度<p>
 * - @str.random()：{@link com.moon.core.util.RandomStringUtil#next()} 随机字符串<p>
 * - @str.random(int)：{@link com.moon.core.util.RandomStringUtil#next(int)} 随机字符串<p>
 * - @str.random(int, int)：{@link com.moon.core.util.RandomStringUtil#next(int, int)} 随机字符串<p>
 * <p>
 * - @date()：返回当前日期{@link java.util.Date}}<p>
 * - @date(Object)：将对象转换成{@link java.util.Date}}<p>
 * - @date.now()：返回当前时间毫秒数{@link System#currentTimeMillis()}；<p>
 * - @date.format(Date, String)：格式化日期{@link java.text.DateFormat#format(Date)}}<p>
 * <p>
 * - @math.double(Object)：<p>
 * - @math.int(Object)：<p>
 * - @math.ceil(Number)：<p>
 * - @math.floor(Number)：<p>
 * - @math.sin(Number)：正弦函数<p>
 * - @math.cos(Number)：余弦函数<p>
 * - @math.tan(Number)：正切函数<p>
 * - @math.abs(Number)：绝对值<p>
 * - @math.round(Number)：四舍五入<p>
 * - @math.pow(Number,Number)：幂次方<p>
 * - @math.cbrt(Number,Number)：<p>
 * - @math.sqrt(Number,Number)：<p>
 * - @math.log(Number)：<p>
 * - @math.log10(Number)：<p>
 * - @math.random()：随机数<p>
 * <p>
 * <strong>注意：</strong>
 * * 静态方法调用只支持部分包下的类，具体见【 八 】
 * <p>
 * * 方法调用只支持无参方法和只有一个参数的方法，变长参数的方法不完全支持（慎用）
 * <p>
 * * 基本数据类型只支持 boolean、int、double，没有 char 类型数据，被征用做字符串了
 * <p>
 * * 没有 char 数据类型，双引号和单引号包裹的都是字符串，
 * <p>
 * * 默认字符串表达式分隔符：DELIMITERS = {"{{", "}}"}
 *
 * @author moonsky
 */
public final class RunnerUtil extends ParseUtil {

    /**
     * @see AssertionError 不可实例化
     */
    private RunnerUtil() { super(); }

    /**
     * 运行简单表达式，形如： 1 + 2 或 'a' + 'b' 等不含有参数的表达式。
     * <p>
     * 如果 expression 是一个包含参数的表达式，将抛出异常
     * <p>
     * 实际实现方式:
     * {@link RunnerUtil#run(String)}会缓存所有不包含参数表达式的结果，
     * 只解析执行一次，并缓存，以后的运行返回第一次缓存的结果
     *
     * @param expression 字符串表达式
     *
     * @return
     *
     * @see Throwable 如果字符串表达式中包含变量
     */
    public final static Object run(String expression) { return parse(expression).run(); }

    /**
     * 计算带有变量复杂表达式，可接受一个参数，形如：
     * <p>
     * 1 + 2 + key1[0].name      === key1 可以是 Map 的 key 或一个实体对象的字段
     * <p>
     * 或
     * <p>
     * 'a' + '2' + [0].key.name  === 0 是数组或 List 的索引
     * <p>
     * 等带有参数的表达式
     *
     * @param expression 字符串表达式
     * @param data       表达式运行时变量所引用的数据
     *
     * @return
     *
     * @see NullPointerException 如果字符串表达式中包含 data 中没有的变量
     */
    public final static Object run(String expression, Object data) { return parse(expression).run(data); }

    /**
     * 计算带有变量复杂表达式，可接受多个参数，主要与{@link #run(String, Object)}的区别；
     * <p>
     * 这儿带有的参数可以是 map、collection、数组、Java bean 对象；
     * <p>
     * 如果存在相同键名，后出现的将会覆盖之前出现的值
     * <p>
     * 如果数据是 Iterator 对象，可作为字段传入，不可作为直接量传入
     *
     * @param expression
     * @param data
     *
     * @return
     */
    public final static Object runMulti(String expression, Object... data) { return parse(expression).runMulti(data); }

    /**
     * 运行字符串中的表达式，如：
     * <p>
     * RunnerUtil.parseRun("1 + 2 = {{1+2}}");        // ====== "1 + 2 = 3"
     * <p>
     * RunnerUtil.parseRun("中华人民共和国{{'棒棒的'}}"); // ====== "中华人民共和国棒棒的"
     * <p>
     * 默认分隔符为：${@link #DELIMITERS} === {"{{", "}}"}；
     * 可包含多个表达式，但不能嵌套包含，也不能交叉嵌套，如下：
     * <p>
     * 错误示例："1 + 2 = {{ 1 + {{ 3 + 4 }} + 2 }}"
     * <p>
     * 正确示例："1 + 2 = {{ 1 + 2 }}  {{ 3 + 4 }}"
     * <p>
     * <strong>说明：</strong>如果字符串中只有一个表达式，并且始末位置分别就是始末分割符，
     * 那么这个表达式返回值可以是任意对象，否则只能返回字符串，如：
     * RunnerUtil.parseRun("中华人民共和国{{'棒棒的'}}"); // ====== "中华人民共和国棒棒的"
     * <p>
     * RunnerUtil.parseRun("{{'棒棒的'}}");             // ====== "棒棒的"
     * <p>
     * RunnerUtil.parseRun("{{1}}");                  // ====== 1
     * <p><strong>【注意：】</strong>
     * 由于花括号 “{}、{:}” 在此工具中可表示 Map 或 List，
     * 所以在可能引起边界混淆的地方最好自定义分隔符，如：
     * <p>
     * 错误示例： {{{'name'}}}
     * <p>
     * 应该写成： {{ {'name'}[0] }} === 边界处留有空格；
     * <p>
     * 或： $[{'name'}] === 自定义分隔符；
     *
     * @param expression 包含插值语法的字符串表达式
     *
     * @return
     *
     * @see Throwable 如果字符串表达式中包含变量
     */
    public final static Object parseRun(String expression) { return parse(expression, DELIMITERS).run(); }

    /**
     * 运行字符串中的带变量的表达式，如：
     * <p>
     * Map data = {"desc": "棒棒的"} // 这是一个 Map
     * <p>
     * RunnerUtil.parseRun("中华人民共和国{{desc}}", data); // ====== "中华人民共和国棒棒的"
     * <p>
     * 默认分隔符为：${@link #DELIMITERS} === {"{{", "}}"}
     * <p>
     * <strong>【注意：】</strong>
     * 由于花括号 “{}、{:}” 在此工具中可表示 Map 或 List，
     * 所以在可能引起边界混淆的地方最好自定义分隔符，如：
     * <p>
     * 错误示例： {{{'name'}}}
     * <p>
     * 应该写成： {{ {'name'}[0] }} === 边界处留有空格；
     * <p>
     * 或： $[{'name'}] === 自定义分隔符；
     *
     * @param expression 包含插值语法的字符串表达式
     * @param data       表达式运行时变量所引用的数据
     *
     * @return
     *
     * @see NullPointerException 如果字符串表达式中包含 data 中没有的变量
     * @see #parseRun(String)
     */
    public final static Object parseRun(String expression, Object data) {
        return parse(expression, DELIMITERS).run(data);
    }

    public final static Object parseRunMulti(String expression, Object... data) {
        return parse(expression, DELIMITERS).runMulti(data);
    }

    /**
     * 可自定义分隔符，如：
     * <p>
     * Map data = {"desc": "棒棒的"} // 这是一个 Map
     * <p>
     * String[] delimiters = {"${", "}"};
     * <p>
     * RunnerUtil.parseRun("本草纲目{desc}", delimiters, data); // ====== "本草纲目棒棒的"
     * <p>
     * <strong>【注意：】</strong>同一个字符串中不可包含多种不同的分隔符而运行多次；
     * <p>
     * 由于花括号 “{}、{:}” 在此工具中可表示 Map 或 List，
     * 所以在可能引起边界混淆的地方最好自定义分隔符，如：
     * <p>
     * 错误示例： {{{'name'}}}
     * <p>
     * 应该写成： {{ {'name'}[0] }} === 边界处留有空格；
     * <p>
     * 或： $[{'name'}] === 自定义分隔符；
     *
     * @param expression 包含插值语法的字符串表达式
     * @param delimiters 必须是一个长度不小于 2 包含始末标记的非空字符串，长度大于 2 后面的内容会被忽略
     *
     * @return
     *
     * @see Throwable 如果字符串表达式中包含变量
     * @see #parseRun(String, String[], Object)
     */
    public final static Object parseRun(String expression, String[] delimiters) {
        return parse(expression, delimiters).run();
    }

    /**
     * 可自定义分隔符，如：
     * <p>
     * Map data = {"desc": "棒棒的"} // 这是一个 Map
     * <p>
     * String[] delimiters = {"${", "}"};
     * <p>
     * RunnerUtil.parseRun("本草纲目${desc}", delimiters, data); // ====== "本草纲目棒棒的"
     * <p>
     * <strong>【注意：】</strong>同一个字符串中不可包含多种不同的分隔符而运行多次；
     * <p>
     * 由于花括号 “{}、{:}” 在此工具中可表示 Map 或 List，
     * 所以在可能引起边界混淆的地方最好自定义分隔符，如：
     * <p>
     * 错误示例： {{{'name'}}}
     * <p>
     * 应该写成： {{ {'name'}[0] }} === 边界处留有空格；
     * <p>
     * 或： $[{'name'}] === 自定义分隔符；
     *
     * @param expression 包含插值语法的字符串表达式
     * @param delimiters 必须是一个长度不小于 2 包含始末标记的非空字符串，长度大于 2 后面的内容会被忽略
     * @param data       表达式运行时变量所引用的数据
     *
     * @return
     *
     * @see NullPointerException 如果字符串表达式中包含 data 中没有的变量
     * @see #parseRun(String)
     */
    public final static Object parseRun(String expression, String[] delimiters, Object data) {
        return parse(expression, delimiters).run(data);
    }

    public final static Object parseRunMulti(String expression, String[] delimiters, Object... data) {
        return parse(expression, delimiters).runMulti(data);
    }
}
