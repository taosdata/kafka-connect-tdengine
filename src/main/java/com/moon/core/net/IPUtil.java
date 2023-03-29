package com.moon.core.net;

import com.moon.core.lang.IntUtil;
import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.ListUtil;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.ThrowUtil.runtime;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public final class IPUtil {

    private IPUtil() { noInstanceError(); }

    public static InetAddress getLocalhost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return runtime(e);
        }
    }

    public static String getLocalIPV4() { return getLocalhost().getHostAddress(); }

    public static String getLocalIPV6Address() {
        try {
            return Inet6Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static String ipv4ToIpv6(String ipv4) {
        return "::" + JoinerUtil.join(ListUtil.mapAsList(StringUtil.split(ipv4, '.'), fragment -> {
            return IntUtil.toString(Integer.valueOf(fragment), 16);
        }), ":");
    }
}
