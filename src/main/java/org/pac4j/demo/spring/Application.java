package org.pac4j.demo.spring;

import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.jee.context.JEEContext;
import org.pac4j.jee.context.session.JEESessionStore;
import org.pac4j.jee.http.adapter.JEEHttpActionAdapter;
import org.pac4j.springframework.web.LogoutController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Map;

@Controller
public class Application {

    @Value("${salt}")
    private String salt;

    @Value("${pac4j.centralLogout.defaultUrl:#{null}}")
    private String defaultUrl;

    @Value("${pac4j.centralLogout.logoutUrlPattern:#{null}}")
    private String logoutUrlPattern;

    @Autowired
    private Config config;

    @Autowired
    private JEEContext webContext;

    @Autowired
    private ProfileManager profileManager;

    private LogoutController logoutController;

    @PostConstruct
    protected void afterPropertiesSet() {
        logoutController = new LogoutController();
        logoutController.setDefaultUrl(defaultUrl);
        logoutController.setLogoutUrlPattern(logoutUrlPattern);
        logoutController.setLocalLogout(false);
        logoutController.setCentralLogout(true);
        logoutController.setConfig(config);
        logoutController.setDestroySession(false);
    }

    @RequestMapping("/")
    public String root(final Map<String, Object> map) throws HttpAction {
        return index(map);
    }

    @RequestMapping("/index.html")
    public String index(final Map<String, Object> map) throws HttpAction {
        map.put("profiles", profileManager.getProfiles());
        map.put("sessionId", JEESessionStore.INSTANCE.getSessionId(webContext, false).orElse("nosession"));
        return "index";
    }

    @RequestMapping("/oidc/index.html")
    public String oidc(final Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/protected/index.html")
    public String protect(final Map<String, Object> map) {
        return protectedIndex(map);
    }

    protected String protectedIndex(final Map<String, Object> map) {
        map.put("profiles", profileManager.getProfiles());
        return "protectedIndex";
    }

    @RequestMapping("/centralLogout")
    @ResponseBody
    public void centralLogout() {
        logoutController.logout(webContext.getNativeRequest(), webContext.getNativeResponse());
    }

    @ExceptionHandler(HttpAction.class)
    public void httpAction() {
        // do nothing
    }
}
