package io.bitchat.server.http.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.lang.Singleton;
import io.bitchat.core.init.InitAble;
import io.bitchat.core.init.InitOrder;
import io.bitchat.lang.config.BaseConfig;
import io.bitchat.lang.config.ConfigFactory;
import io.bitchat.server.http.RenderType;
import io.bitchat.server.http.RequestMethod;
import io.bitchat.server.http.router.RouteResult;
import io.bitchat.server.http.router.Router;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author houyi
 */
@Slf4j
@InitOrder(3)
public class ControllerContext implements InitAble {

    private static AtomicBoolean init = new AtomicBoolean();

    /**
     * 所有的路由信息
     */
    private static Router<RenderType> router = new Router<>();

    /**
     * 保存所有的RouterController的代理类
     */
    private static Map<String, ControllerProxy> proxyMap = new ConcurrentHashMap<>();

    private ControllerContext() {

    }

    public static ControllerContext getInstance() {
        return Singleton.get(ControllerContext.class);
    }

    @Override
    public void init() {
        initRouter();
    }

    public ControllerProxy getProxy(HttpMethod method, String uri) {
        RouteResult<RenderType> routeResult = this.getRouteResult(method, uri);
        if (routeResult == null) {
            return null;
        }
        // 获取代理
        ControllerProxy controllerProxy = proxyMap.get(routeResult.decodedPath());
        log.debug("\n=========================  getControllerProxy =========================" +
                        "\nmethod={}, uri={}" +
                        "\ncontrollerProxy={}" +
                        "\n=========================  getControllerProxy =========================",
                method, uri, controllerProxy);
        return controllerProxy;
    }

    /**
     * 获取路由结果
     */
    private RouteResult<RenderType> getRouteResult(HttpMethod method, String uri) {
        RouteResult<RenderType> routeResult = router.route(method, uri);
        log.debug("getRouteResult with method={}, uri={}, routeResult={}", method, uri, routeResult);
        return routeResult;
    }

    /**
     * 执行初始化工作
     */
    private void initRouter() {
        if (!init.compareAndSet(false, true)) {
            return;
        }
        try {
            BaseConfig baseConfig = ConfigFactory.getConfig(BaseConfig.class);
            // 获取所有RouterController
            Set<Class<?>> classSet = ClassScaner.scanPackageByAnnotation(baseConfig.basePackage(), Controller.class);
            if (CollectionUtil.isNotEmpty(classSet)) {
                for (Class<?> cls : classSet) {
                    Controller controller = cls.getAnnotation(Controller.class);
                    // 获取Controller中所有的方法
                    Method[] methods = cls.getMethods();
                    for (Method method : methods) {
                        Mapping mapping = method.getAnnotation(Mapping.class);
                        if (mapping != null) {
                            this.addRoute(controller, mapping);
                            // 添加控制器
                            this.addProxy(cls, method, controller, mapping);
                        }
                    }
                }
                router.notFound(RenderType.HTML);
                log.info("[ControllerContext] initRouter success! routers are listed blow:" +
                                "\n*************************************" +
                                "\n{}" +
                                "*************************************\n",
                        router);
            } else {
                log.warn("[ControllerContext] No Controller Scanned!");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("[ControllerContext] Init controller error,cause:", e);
        }
    }

    private void addRoute(Controller controller, Mapping mapping) {
        // Controller+Mapping 唯一确定一个控制器的方法
        String path = controller.path() + mapping.path();
        HttpMethod method = RequestMethod.getHttpMethod(mapping.requestMethod());
        // 添加路由
        router.addRoute(method, path, mapping.renderType());
    }

    private void addProxy(Class<?> cls, Method method, Controller controller, Mapping mapping) {
        try {
            // Controller+Mapping 唯一确定一个控制器的方法
            String path = controller.path() + mapping.path();
            ControllerProxy proxy = new ControllerProxy();
            proxy.setRenderType(mapping.renderType());
            proxy.setRequestMethod(mapping.requestMethod());
            Object object = Singleton.get(cls);
            proxy.setController(object);
            proxy.setMethod(method);
            proxy.setMethodName(method.getName());

            proxyMap.putIfAbsent(path, proxy);
            log.info("[ControllerContext] addProxy path={} to proxy={}", path, proxy);
        } catch (Exception e) {
            log.error("[ControllerContext] addProxy error,cause:", e.getMessage(), e);
        }
    }


}