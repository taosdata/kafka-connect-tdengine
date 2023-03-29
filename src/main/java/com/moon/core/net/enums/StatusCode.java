package com.moon.core.net.enums;

import com.moon.core.enums.EnumDescriptor;
import com.moon.core.lang.ref.WeakAccessor;
import com.moon.core.util.IteratorUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author moonsky
 */
public enum StatusCode implements EnumDescriptor {
    /*
     * ----------------------------------------
     * 1xx - 信息提示
     * 这些状态代码表示临时的响应。客户端在收到常规响应之前，应准备接收一个或多个 1xx 响应。
     * ----------------------------------------
     */

    /**
     * 初始的请求已经接受，客户应当继续发送请求的其余部分。（HTTP 1.1新）
     */
    Continue("100", "初始的请求已经接受"),
    /**
     * 服务器将遵从客户的请求转换到另外一种协议（HTTP 1.1新）
     */
    SwitchingProtocols("101", "服务器将遵从客户的请求转换到另外一种协议"),

    /*
     * ----------------------------------------
     * 2xx - 成功
     * 这类状态代码表明服务器成功地接受了客户端请求。
     * ----------------------------------------
     */

    /**
     * 对GET和POST请求的应答文档跟在后面。
     */
    OK("200", ""),
    /**
     * 服务器已经创建了文档，Location头给出了它的URL。
     */
    Created("201", "服务器已经创建了文档"),
    /**
     * 已经接受请求，但处理尚未完成。
     */
    Accepted("202", "已经接受请求"),
    /**
     * 文档已经正常地返回，但一些应答头可能不正确，因为使用的是文档的拷贝，非权威性信息（HTTP 1.1新）。
     */
    NonAuthoritativeInformation("203", "文档已返回"),
    /**
     * 没有新文档，浏览器应该继续显示原来的文档。如果用户定期地刷新页面，而Servlet可以确定用户文档足够新，这个状态代码是很有用的。
     */
    NoContent("204", "没有新文档"),
    /**
     * 没有新的内容，但浏览器应该重置它所显示的内容。用来强制浏览器清除表单输入内容（HTTP 1.1新）。
     */
    ResetContent("205", "没有新的内容"),
    /**
     * 客户发送了一个带有Range头的GET请求（分块请求），服务器完成了它（HTTP 1.1新）。
     */
    PartialContent("206", ""),

    /*
     * ----------------------------------------
     * 3xx - 重定向
     * 客户端浏览器必须采取更多操作来实现请求。例如，浏览器可能不得不请求服务器上的不同的页面，或通过代理服务器重复该请求。
     * ----------------------------------------
     */

    /**
     * 客户请求的文档可以在多个位置找到，这些位置已经在返回的文档内列出。如果服务器要提出优先选择，则应该在Location应答头指明。
     */
    MultipleChoices("300", ""),
    /**
     * 客户请求的文档在其他地方，新的URL在Location头中给出，浏览器应该自动地访问新的URL。
     */
    MovedPermanently("301", "客户请求的文档在其他地方"),
    /**
     * 类似于301，但新的URL应该被视为临时性的替代，而不是永久性的。
     * <p>
     * 注意，在HTTP1.0中对应的状态信息是“Moved Temporatily”。
     * <p>
     * 出现该状态代码时，浏览器能够自动访问新的URL，因此它是一个很有用的状态代码。
     * <p>
     * 注意这个状态代码有时候可以和301替换使 用。
     * <p>
     * 例如，如果浏览器错误地请求 http://host/~user （缺少了后面的斜杠），有的服务器返回301，有的则返回302。
     * 严格地说，我们只能假定只有当原来的请求是GET时浏览器才会自动重定向。请参见{@link #TemporaryRedirect}。
     */
    Found("302", ""),
    /**
     * 类似于301/302，不同之处在于，如果原来的请求是POST，Location头指定的重定向目标文档应该通过GET提取（HTTP 1.1新）。
     */
    SeeOther("303", ""),
    /**
     * 客户端有缓冲的文档并发出了一个条件性的请求（一般是提供If-Modified-Since头表示客户只想比指定日期更新的文档）。
     * <p>
     * 服务器告诉客户，原来缓冲的文档还可以继续使用。
     */
    NotModified("304", ""),
    /**
     * 客户请求的文档应该通过Location头所指明的代理服务器提取（HTTP 1.1新）。
     */
    UseProxy("305", ""),
    /**
     * 与302{@link #Found}相同。
     * <p>
     * 许多浏览器会错误地响应302应答进行重定向，即使原来的请求是POST，即使它实际上只能在POST请求的应答是303时才能重定向。
     * <p>
     * 由于这个原因，HTTP 1.1新增了307，以便更加清除地区分几个状态代码：
     * <p>
     * 当出现303应答时，浏览器可以跟随重定向的GET和POST请求；如果是307应答，则浏览器只能跟随对GET请求的重定向。（HTTP 1.1新）
     */
    TemporaryRedirect("307", ""),

    /*
     * ----------------------------------------
     * 4xx - 客户端错误
     * 发生错误，客户端似乎有问题。例如，客户端请求不存在的页面，客户端未提供有效的身份验证信息。
     * ----------------------------------------
     */

    /**
     * 请求出现语法错误
     */
    BadRequest("400", "请求出现语法错误"),
    /**
     * 访问被拒绝，客户试图未经授权访问受密码保护的页面。
     * <p>
     * 应答中会包含一个WWW-Authenticate头，浏览器据此显示用户名字/密码对话框，然后在填写合适的Authorization头后再次发出请求。
     * <p>
     * IIS 定义了许多不同的 401 错误，它们指明更为具体的错误原因。这些具体的错误代码在浏览器中显示，但不在 IIS 日志中显示：
     */
    Unauthorized("401", "访问被拒绝"),
    /**
     * 登录失败
     */
    Unauthorized401_1("401.1", "登录失败"),
    /**
     * 服务器配置导致登录失败
     */
    Unauthorized401_2("401.2", "服务器配置导致登录失败"),
    /**
     * 由于 ACL 对资源的限制而未获得授权
     */
    Unauthorized401_3("401.3", "由于 ACL 对资源的限制而未获得授权"),
    /**
     * 筛选器授权失败
     */
    Unauthorized401_4("401.4", " 筛选器授权失败"),
    /**
     * ISAPI/CGI 应用程序授权失败
     */
    Unauthorized401_5("401.5", "ISAPI/CGI 应用程序授权失败"),
    /**
     * 访问被 Web 服务器上的 URL 授权策略拒绝。这个错误代码为 IIS 6.0 所专用。
     */
    Unauthorized401_7("401.7", "URL 授权策略拒绝"),

    /**
     * 资源不可用。服务器理解客户的请求，但拒绝处理它。通常由于服务器上文件或目录的权限设置导致。禁止访问：IIS 定义了许多不同的 403 错误，
     * <p>
     * 它们指明更为具体的错误原因：
     */
    Forbidden("403", "资源不可用"),
    Forbidden403_01("403.1", "执行访问被禁止"),
    Forbidden403_02("403.2", "读访问被禁止"),
    Forbidden403_03("403.3", "写访问被禁止"),
    Forbidden403_04("403.4", "要求 SSL"),
    Forbidden403_05("403.5", "要求 SSL 128"),
    Forbidden403_06("403.6", "IP 地址被拒绝"),
    Forbidden403_07("403.7", "要求客户端证书"),
    Forbidden403_08("403.8", "站点访问被拒绝"),
    Forbidden403_09("403.9", "用户数过多"),
    Forbidden403_10("403.10", "配置无效"),
    Forbidden403_11("403.11", "密码更改"),
    Forbidden403_12("403.12", "拒绝访问映射表"),
    Forbidden403_13("403.13", "客户端证书被吊销"),
    Forbidden403_14("403.14", "拒绝目录列表"),
    Forbidden403_15("403.15", "超出客户端访问许可"),
    Forbidden403_16("403.16", "客户端证书不受信任或无效"),
    Forbidden403_17("403.17", "客户端证书已过期或尚未生效"),
    /**
     * 在当前的应用程序池中不能执行所请求的 URL。这个错误代码为 IIS 6.0 所专用。
     */
    Forbidden403_18("403.18", "在当前的应用程序池中不能执行所请求的 URL"),
    /**
     * 不能为这个应用程序池中的客户端执行 CGI。这个错误代码为 IIS 6.0 所专用。
     */
    Forbidden403_19("403.19", "不能为这个应用程序池中的客户端执行 CGI"),
    /**
     * Passport 登录失败。这个错误代码为 IIS 6.0 所专用。
     */
    Forbidden403_20("403.20", "Passport 登录失败"),

    NotFound("404", "无法找到指定位置的资源"),
    NotFound404_0("404.0", "没有找到文件或目录"),
    NotFound404_1("404.1", "无法在所请求的端口上访问 Web 站点"),
    NotFound404_2("404.2", "Web 服务扩展锁定策略阻止本请求"),
    NotFound404_3("404.3", "MIME 映射策略阻止本请求"),

    /**
     * 请求方法（GET、POST、HEAD、DELETE、PUT、TRACE等）对指定的资源不适用
     * <p>
     * 用来访问本页面的 HTTP 谓词不被允许（方法不被允许）（HTTP 1.1新）
     */
    MethodNotAllowed("405", "请求方法对指定的资源不适用"),
    /**
     * 指定的资源已经找到，但它的MIME类型和客户在Accpet头中所指定的不兼容，客户端浏览器不接受所请求页面的 MIME 类型（HTTP 1.1新）。
     */
    NotAcceptable("406", "指定的资源已经找到"),
    /**
     * 要求进行代理身份验证，类似于401，表示客户必须先经过代理服务器的授权。（HTTP 1.1新）
     */
    ProxyAuthenticationRequired("407", "要求进行代理身份验证"),
    /**
     * 在服务器许可的等待时间内，客户一直没有发出任何请求。
     * <p>
     * 客户可以在以后重复同一请求。（HTTP 1.1新）
     */
    RequestTimeout("408", "请求超时"),
    /**
     * 通常和PUT请求有关。
     * <p>
     * 由于请求和资源的当前状态相冲突，因此请求不能成功。（HTTP 1.1新）
     */
    Conflict("409", "请求和资源状态冲突"),
    /**
     * 所请求的文档已经不再可用，而且服务器不知道应该重定向到哪一个地址。
     * <p>
     * 它和404的不同在于，返回410表示文档永久地离开了指定的位置，而404表示由于未知的原因文档不可用。（HTTP 1.1新）
     */
    Gone("410", "文档不可用"),
    /**
     * 服务器不能处理请求，除非客户发送一个Content-Length头。（HTTP 1.1新）
     */
    LengthRequired("411", "服务器不能处理请求"),
    /**
     * 请求头中指定的一些前提条件失败（HTTP 1.1新）。
     */
    PreconditionFailed("412", ""),
    /**
     * 目标文档的大小超过服务器当前愿意处理的大小。
     * <p>
     * 如果服务器认为自己能够稍后再处理该请求，则应该提供一个Retry-After头（HTTP 1.1新）。
     */
    RequestEntityTooLarge("413", "目标文档过大"),
    /**
     * URI太长（HTTP 1.1新）。
     */
    RequestURITooLong("414", "URI太长"),
    /**
     * 不支持的媒体类型
     */
    UnsupportedMediaType("415", "不支持的媒体类型"),
    /**
     * 服务器不能满足客户在请求中指定的Range头。（HTTP 1.1新）
     */
    RequestedRangeNotSatisfiable("416", "客户端请求的范围无效"),
    /**
     * 执行失败。
     */
    ExpectationFailed("417", "服务器无法满足Expect的请求头信息"),
    /**
     * 锁定的错误
     */
    LockedError("423", "锁定的错误"),
    TooManyRequest("429", "请求过多"),

    /*
     * ----------------------------------------
     * 信息提示
     * ----------------------------------------
     */

    /**
     * 服务器遇到了意料不到的情况，不能完成客户的请求。
     */
    InternalServerError("500", ""),
    InternalServerError500_12("500.12", "应用程序正忙于在 Web 服务器上重新启动"),
    InternalServerError500_13("500.13", "Web 服务器太忙"),
    InternalServerError500_15("500.15", "不允许直接请求 Global.asa"),
    /**
     * UNC 授权凭据不正确。这个错误代码为 IIS 6.0 所专用。
     */
    InternalServerError500_16("500.16", "UNC 授权凭据不正确"),
    /**
     * URL 授权存储不能打开。这个错误代码为 IIS 6.0 所专用。
     */
    InternalServerError500_18("500.18", "URL 授权存储不能打开"),
    /**
     * 内部 ASP 错误
     */
    InternalServerError500_100("500.100", "内部 ASP 错误"),
    /**
     * 服务器不支持实现请求所需要的功能，页眉值指定了未实现的配置。
     * <p>
     * 例如，客户发出了一个服务器不支持的PUT请求。
     */
    NotImplemented("501", "服务器不支持实现请求所需要的功能"),
    /**
     * 服务器作为网关或者代理时，为了完成请求访问下一个服务器，但该服务器返回了非法的应答。
     * <p>
     * 亦说Web 服务器用作网关或代理服务器时收到了无效响应
     */
    BadGateway("502", ""),
    BadGateway502_1("502.1", "CGI 应用程序超时"),
    BadGateway502_2("502.2", "CGI 应用程序出错"),
    /**
     * 服务不可用，服务器由于维护或者负载过重未能应答。例如，Servlet可能在数据库连接池已满的情况下返回503。
     * <p>
     * 服务器返回503时可以提供一个Retry-After头。
     * <p>
     * 这个错误代码为 IIS 6.0 所专用。
     */
    ServerUnavailable("503", "服务不可用"),
    /**
     * 网关超时，由作为代理或网关的服务器使用，表示不能及时地从远程服务器获得应答。（HTTP 1.1新） 。
     */
    GatewayTimeout("504", "网关超时"),
    /**
     * 服务器不支持请求中所指明的HTTP版本。（HTTP 1.1新）
     */
    HTTPVersionNotSupported("505", "服务器不支持请求中所指明的HTTP版本"),
    ;

    private final String code;

    private final String message;

    private static class This {
        private final static WeakAccessor<Map<String, StatusCode>> ACCESSOR = WeakAccessor.of(() -> {
            Map<String, StatusCode> status = new HashMap<>();
            IteratorUtil.forEach(StatusCode.values(), code -> status.put(code.code, code));
            return status;
        });
    }

    StatusCode(String code, String msg) {
        this.message = msg;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public StatusCode codeOf(String code) {
        return This.ACCESSOR.get().get(code);
    }

    public StatusCode withCode(String code) {
        return codeOf(code);
    }

    @Override
    public String getText() {
        return name().replaceAll("\\d.*", "");
    }

    @Override
    public String getName() {
        return String.valueOf(code);
    }
}
