/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.mail.jira.plugins.mrimsender.mrim.MRIMPackageFactory;
import ru.mail.jira.plugins.mrimsender.mrim.MrimConsts;
import ru.mail.jira.plugins.mrimsender.mrim.MrimErrors;
import ru.mail.jira.plugins.mrimsender.mrim.MrimException;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.xsrf.XsrfTokenGenerator;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;

/**
 * This is servlet that works with MRIM plugin configuration.
 * 
 * @author Andrey Markelov
 */
public class MrimConfigurer
    extends HttpServlet
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.getLog(MrimConfigurer.class);

    /**
     * Relative path to "admin page template".
     */
    private static final String ADMIN_TEMPLATE = "/templates/MrimAdmin.vm";

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 3810279792418307180L;

    /**
     * Relative path to "user page template".
     */
    private static final String USER_TEMPLATE = "/templates/MrimUser.vm";

    /**
     * MRIM settings.
     */
    private final MrimSettings mrimSettings;

    /**
     * Velocity renderer.
     */
    private final TemplateRenderer renderer;

    /**
     * Utility for work with JIRA users.
     */
    private final UserUtil userUtil;

    /**
     * Constructor.
     */
    public MrimConfigurer(
        TemplateRenderer renderer,
        MrimSettings mrimSettings,
        UserUtil userUtil)
    {
        this.renderer = renderer;
        this.mrimSettings = mrimSettings;
        this.userUtil = userUtil;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException,
           IOException
    {
        //--> check action
        String action = req.getParameter("action");
        if (action == null)
        {
            resp.sendError(404);
            return;
        }

        //--> redirect to main page if user is not logged
        JiraAuthenticationContext jiraContext = ComponentManager.getInstance().getJiraAuthenticationContext();
        User user = jiraContext.getLoggedInUser();
        if (user == null)
        {
            resp.sendRedirect(getBaseUrl(req));
            return;
        }

        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "private,no-cache,no-store");
        resp.setDateHeader("Expires", 0);
        resp.setContentType("text/html;charset=utf-8");

        if (req.getParameter("action").equals("admin"))
        {
            if (userUtil.getJiraAdministrators().contains(user))
            {
                viewAdminConfiguration(req, resp);
            }
            else
            {
                resp.sendError(403, jiraContext.getI18nHelper().getText("mrim.admin.configuration.securityerror"));
            }
        }
        else if (action.equals("user"))
        {
            viewUserConfiguration(req, resp, user);
        }
        else
        {
            resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException,
           IOException
    {
        //--> check action
        String action = req.getParameter("action");
        if (action == null)
        {
            resp.sendError(404);
            return;
        }

        //--> redirect to main page if user is not logged
        JiraAuthenticationContext jiraContext = ComponentManager.getInstance().getJiraAuthenticationContext();
        User user = jiraContext.getLoggedInUser();
        if (user == null)
        {
            resp.sendRedirect(getBaseUrl(req));
            return;
        }

        resp.setContentType("text/html;charset=utf-8");

        if (req.getParameter("action").equals("saveadmin"))
        {
            if (userUtil.getJiraAdministrators().contains(user))
            {
                saveAdminCfg(req, resp);
            }
            else
            {
                resp.sendError(403, jiraContext.getI18nHelper().getText("mrim.admin.configuration.securityerror"));
            }
        }
        else if (req.getParameter("action").equals("saveuser"))
        {
            saveUserCfg(req, resp, user);
        }
        else
        {
            resp.sendError(404);
        }
    }

    /**
     * Return context path of JIRA.
     */
    private String getBaseUrl(HttpServletRequest req)
    {
        return (req.getScheme() + "://" + req.getServerName() + ":" +
            req.getServerPort() + req.getContextPath());
    }

    /**
     * Save admin cfg.
     */
    private void saveAdminCfg(HttpServletRequest req, HttpServletResponse resp)
    throws RenderingException,
           IOException,
           ServletException
    {
        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager.getComponentInstanceOfType(XsrfTokenGenerator.class);
        String token = xsrfTokenGenerator.getToken(req);
        if (!xsrfTokenGenerator.generatedByAuthenticatedUser(token))
        {
            resp.sendError(403);
            return;
        }
        else
        {
            String atl_token = req.getParameter("atl_token");
            if (!atl_token.equals(token))
            {
                resp.sendError(403);
                return;
            }
        }

        String login = req.getParameter("email");
        String pass = req.getParameter("pass");

        if (login.length() > 0)
        {
            try
            {
                MrinRunner.getInstance().init(login, pass);
            }
            catch (MrimException e)
            {
                if (e.getErrorCode() == MrimErrors.ERROR_AUTH_LOGIN)
                {
                    String url = resp.encodeRedirectURL(getBaseUrl(req) + "/plugins/servlet/mrimsender/view?action=admin&error=mrim.admin.configuration.auth.error");
                    resp.sendRedirect(url);
                    return;
                }
                else
                {
                    String url = resp.encodeRedirectURL(getBaseUrl(req) + "/plugins/servlet/mrimsender/view?action=admin&error=mrim.base.internal.error");
                    resp.sendRedirect(url);
                    return;
                }
            }
        }

        mrimSettings.setLogin(login);
        mrimSettings.setPassword(pass);
        mrimSettings.setContextPath(getBaseUrl(req));

        String url = resp.encodeRedirectURL(getBaseUrl(req) + "/plugins/servlet/mrimsender/view?action=admin&okey=true");
        resp.sendRedirect(url);
    }

    /**
     * Save user cfg.
     */
    private void saveUserCfg(
        HttpServletRequest req,
        HttpServletResponse resp,
        User user)
    throws RenderingException,
           IOException
    {
        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager.getComponentInstanceOfType(XsrfTokenGenerator.class);
        String token = xsrfTokenGenerator.getToken(req);
        if (!xsrfTokenGenerator.generatedByAuthenticatedUser(token))
        {
            resp.sendError(403);
            return;
        }
        else
        {
            String atl_token = req.getParameter("atl_token");
            if (!atl_token.equals(token))
            {
                resp.sendError(403);
                return;
            }
        }

        if (mrimSettings.getLogin() == null)
        {
            String url = resp.encodeRedirectURL(getBaseUrl(req) + "/plugins/servlet/mrimsender/view?action=user&error=mrim.user.configuration.settings.error");
            resp.sendRedirect(url);
            return;
        }

        String login = req.getParameter("email");

        if (login.length() == 0)
        {
            String url = resp.encodeRedirectURL(getBaseUrl(req) + "/plugins/servlet/mrimsender/view?action=user&error=mrim.user.configuration.validateerror");
            resp.sendRedirect(url);
            return;
        }

        String doenable = req.getParameter("active");

        if (doenable != null && !doenable.equals(""))
        {
            doenable = "true";

            int localHost = 0;
            int localPort = 0;
            QueueSingleton.getInstance().putPing();
            MrinRunner mr = MrinRunner.getInstance();
            if (mr.getStatus() != MrinRunner.RUN)
            {
                try
                {
                    mr.init(mrimSettings.getLogin(), mrimSettings.getPassword());
                    localHost = mr.getLocalIpAddress();
                    localPort = mr.getPort();
                }
                catch (MrimException mrimex)
                {
                    log.error("MrimConfigurer::saveUserCfg - ", mrimex);
                    String url = resp.encodeRedirectURL(getBaseUrl(req) + "/plugins/servlet/mrimsender/view?action=user&error=mrim.base.internal.error");
                    resp.sendRedirect(url);
                    return;
                }
            }
            else
            {
                localHost = mr.getLocalIpAddress();
                localPort = mr.getPort();
            }

            DataKeeper dk = new DataKeeper(
                MRIMPackageFactory.createAddContactPackage(mr.getSeq(), login, localHost, localPort),
                DataKeeper.ADD_CONTACT);
            QueueSingleton.getInstance().put(dk);
            String greeting = ComponentManager.getInstance().getJiraAuthenticationContext().getI18nHelper().getText("mrim.base.greeting");
            dk = new DataKeeper(
                MRIMPackageFactory.createMessagePackage(mr.getSeq(), MrimConsts.MESSAGE_FLAG_AUTHORIZE | MrimConsts.MESSAGE_FLAG_CP1251, login, greeting, localHost, localPort),
                DataKeeper.AUTHORIZE);
            QueueSingleton.getInstance().put(dk);
            dk = new DataKeeper(
                MRIMPackageFactory.createMessagePackage(mr.getSeq(), MrimConsts.MESSAGE_FLAG_CP1251, login, greeting, localHost, localPort),
                DataKeeper.SEND_MESSAGE);
            QueueSingleton.getInstance().put(dk);
        }

        UserPropertyUtils.setUserMrimLogin(userUtil, user.getName(), login);
        UserPropertyUtils.setUserMrimStatus(userUtil, user.getName(), doenable);

        String url = resp.encodeRedirectURL(getBaseUrl(req) + "/plugins/servlet/mrimsender/view?action=user&okey=true");
        resp.sendRedirect(url);
    }

    /**
     * View admin cfg.
     */
    private void viewAdminConfiguration(
        HttpServletRequest req,
        HttpServletResponse resp)
    throws RenderingException,
           IOException
    {
        String okey = req.getParameter("okey");
        String error = req.getParameter("error");
        if (okey == null)
        {
            okey = "false";
        }

        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager.getComponentInstanceOfType(XsrfTokenGenerator.class);
        String atl_token = xsrfTokenGenerator.generateToken(req);

        Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("login", mrimSettings.getLogin());
        parms.put("baseUrl", getBaseUrl(req));
        parms.put("okey", okey);
        parms.put("error", error);
        parms.put("atl_token", atl_token);

        renderer.render(ADMIN_TEMPLATE, parms, resp.getWriter());
    }

    /**
     * View user cfg.
     */
    private void viewUserConfiguration(
        HttpServletRequest req,
        HttpServletResponse resp,
        User user)
    throws RenderingException,
           IOException
    {
        String okey = req.getParameter("okey");
        String error = req.getParameter("error");
        if (okey == null)
        {
            okey = "false";
        }

        String email = UserPropertyUtils.getUserMrimLogin(userUtil, user.getName());
        if (email == null)
        {
            email = user.getEmailAddress();
        }

        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager.getComponentInstanceOfType(XsrfTokenGenerator.class);
        String atl_token = xsrfTokenGenerator.generateToken(req);

        Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("baseUrl", getBaseUrl(req));
        parms.put("login", email);
        parms.put("status", UserPropertyUtils.getUserMrimStatus(userUtil, user.getName()));
        parms.put("okey", okey);
        parms.put("error", error);
        parms.put("atl_token", atl_token);

        renderer.render(USER_TEMPLATE, parms, resp.getWriter());
    }
}
