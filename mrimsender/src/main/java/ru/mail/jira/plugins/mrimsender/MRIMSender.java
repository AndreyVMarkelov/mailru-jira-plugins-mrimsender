/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import ru.mail.jira.plugins.mrimsender.mrim.MRIMPackageFactory;
import ru.mail.jira.plugins.mrimsender.mrim.MrimException;
import com.atlassian.core.util.DateUtils;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.user.UserPropertyManager;
import com.atlassian.jira.user.util.UserUtil;

/**
 * This class handles all issue events and sends them to MRIM.
 * 
 * @author Andrey Markelov
 */
public class MRIMSender
    implements InitializingBean,
               DisposableBean
{
    /*
     * Logger.
     */
    private static Log log = LogFactory.getLog(MRIMSender.class);

    /**
     * MRIM settings.
     */
    private final MrimSettings mrimSettings;

    /**
     * Utility for work with JIRA users.
     */
    private final UserUtil userUtil;

    /**
     * Event publisher.
     */
    private EventPublisher eventPublisher;

    /**
     * Date format.
     */
    private SimpleDateFormat format;

    /**
     * User property manager.
     */
    private final UserPropertyManager userProps;

    /**
     * Constructor.
     */
    public MRIMSender(
        EventPublisher eventPublisher,
        MrimSettings mrimSettings,
        UserUtil userUtil,
        UserPropertyManager userProps)
    {
        this.eventPublisher = eventPublisher;
        this.mrimSettings = mrimSettings;
        this.userUtil = userUtil;
        this.userProps = userProps;
        this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
    }

    @Override
    public void afterPropertiesSet()
    throws Exception
    {
        //--> register ourselves with the EventPublisher
        eventPublisher.register(this);
    }
 
    /**
     * Called when the plugin is being disabled or removed.
     */
    @Override
    public void destroy()
    throws Exception
    {
        //--> unregister ourselves with the EventPublisher
        eventPublisher.unregister(this);
    }

    /**
     * Format update value.
     */
    public String formatUpdValue(String key, String value)
    {
        if (key.equals("timeestimate") ||
            key.equals("timespent") ||
            key.equals("timeoriginalestimate"))
        {
            try
            {
                return DateUtils.getDurationString(Long.parseLong(value));
            }
            catch (NumberFormatException nex)
            {
                return value;
            }
        }
        else
        {
            return value;
        }
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent)
    {
        if (mrimSettings.getLogin() == null || mrimSettings.getLogin().length() == 0)
        {
            return;
        }

        Issue issue = issueEvent.getIssue();

        Comment comment = issueEvent.getComment();
        String issueLink = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL).concat("/browse/").concat(issue.getKey());
        User user = issueEvent.getUser();

        Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("n","\n");
        parms.put("i18n", ComponentAccessor.getI18nHelperFactory().getInstance(user));
        parms.put("issueKey", issue.getKey());
        parms.put("description", issue.getDescription());
        parms.put("environment", issue.getEnvironment());
        parms.put("summary", issue.getSummary());
        if (issue.getPriorityObject() != null)
        {
            parms.put("priority", issue.getPriorityObject().getName());
        }
        else
        {
            parms.put("priority", "");
        }
        parms.put("eventType", issueEvent.getEventTypeId());
        parms.put("time", format.format(issueEvent.getTime()));
        parms.put("user", user.getDisplayName());
        parms.put("issueLink", issueLink);

        Map<String, String> changes = new HashMap<String, String>();
        try
        {
            if (issueEvent.getChangeLog() != null)
            {
                List<GenericValue> changeItems = issueEvent.getChangeLog().getRelated("ChildChangeItem");
                Iterator<GenericValue> iter = changeItems.iterator();
                while (iter.hasNext())
                {
                    GenericValue changeItem = iter.next();
                    String field = (changeItem.get("field") != null) ? changeItem.get("field").toString() : "";
                    String newStr = (changeItem.get("newstring") != null) ? changeItem.get("newstring").toString() : "";
                    changes.put(field, formatUpdValue(field, newStr));
                }
            }
        }
        catch (GenericEntityException e)
        {
            log.error("Error obtaining old and new issue data", e);
            return;
        }
        parms.put("changes", changes);

        long issueType = issueEvent.getEventTypeId();
        if (issueType == EventType.ISSUE_REOPENED_ID)
        {
            parms.put("isReopened", issueLink);
        }
        else if (issueType == EventType.ISSUE_CREATED_ID)
        {
            parms.put("isCreated", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_ASSIGNED_ID || issueType == EventType.ISSUE_MOVED_ID)
        {
            parms.put("isAssigned", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_COMMENT_EDITED_ID || issueType == EventType.ISSUE_COMMENTED_ID)
        {
            parms.put("isCommented", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_CLOSED_ID)
        {
            parms.put("isClosed", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_UPDATED_ID)
        {
            parms.put("isUpdated", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_RESOLVED_ID)
        {
            parms.put("isResolved", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_DELETED_ID)
        {
            parms.put("isDeleted", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_WORKSTARTED_ID)
        {
            parms.put("isStarted", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_WORKSTOPPED_ID)
        {
            parms.put("isStopped", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_WORKLOGGED_ID)
        {
            parms.put("isWorklogget", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_WORKLOG_UPDATED_ID)
        {
            parms.put("isWorklogUpdated", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_WORKLOG_DELETED_ID)
        {
            parms.put("isWorklogDeleted", Boolean.TRUE);
        }
        else if (issueType == EventType.ISSUE_GENERICEVENT_ID)
        {
            parms.put("isGenericEvent", Boolean.TRUE);
        }

        if (comment != null)
        {
            parms.put("comment", comment.getBody());
        }

        PermissionManager pm = ComponentManager.getInstance().getPermissionManager();
        //--> set users
        Set<String> mailers = new HashSet<String>();
        User assignee = issue.getAssigneeUser();
        if (assignee == null)
        {
            parms.put("assignee", " ");
        }
        else
        {
            parms.put("assignee", assignee.getDisplayName());
            if (UserPropertyUtils.getUserMrimStatus(userUtil, userProps, assignee.getName()).equals("true") &&
                pm.hasPermission(Permissions.BROWSE, issue, assignee) &&
                !assignee.equals(user))
            {
                mailers.add(UserPropertyUtils.getUserMrimLogin(userUtil, userProps, assignee.getName()));
            }
        }

        User reporter = issue.getReporterUser();
        if (reporter == null)
        {
            parms.put("reporter", " ");
        }
        else
        {
            parms.put("reporter", reporter.getDisplayName());
            if (UserPropertyUtils.getUserMrimStatus(userUtil, userProps, reporter.getName()).equals("true") &&
                pm.hasPermission(Permissions.BROWSE, issue, reporter) &&
                !reporter.equals(user))
            {
                mailers.add(UserPropertyUtils.getUserMrimLogin(userUtil, userProps, reporter.getName()));
            }
        }

        List<String> watchers = ComponentManager.getInstance().getWatcherManager().getCurrentWatcherUsernames(issue);
        if (watchers != null)
        {
            for (String watcher : watchers)
            {
                if (UserPropertyUtils.getUserMrimStatus(userUtil, userProps, watcher).equals("true") &&
                    pm.hasPermission(Permissions.BROWSE, issue, userUtil.getUserObject(watcher)) &&
                    !userUtil.getUserObject(watcher).equals(user))
                {
                    mailers.add(UserPropertyUtils.getUserMrimLogin(userUtil, userProps, watcher));
                }
            }
        }
        //<-- end set users

        try
        {
            String message = ComponentAccessor.getVelocityManager().getEncodedBody(
                "templates/",
                "EventTemplate.vm",
                "UTF-8",
                parms);

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
                    log.error("Error occurred during initializing MRIM", mrimex);
                }
            }
            else
            {
                localHost = mr.getLocalIpAddress();
                localPort = mr.getPort();
            }

            for (String to : mailers)
            {
                DataKeeper dk = new DataKeeper(
                    MRIMPackageFactory.createSimpleMessagePackage(mr.getSeq(), to, message, localHost, localPort),
                    DataKeeper.SEND_MESSAGE);
                QueueSingleton.getInstance().put(dk);
            }
        }
        catch (Exception ex)
        {
            log.error("Error occurred during sending MRIM message", ex);
        }
    }
}
