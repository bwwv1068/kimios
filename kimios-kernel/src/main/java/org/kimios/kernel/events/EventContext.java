/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.events;

import java.util.HashMap;
import java.util.Map;

import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventContext
{
    private static Logger logger = LoggerFactory.getLogger(EventContext.class);

    private DmsEventName event;

    /*
    *
    *   controller passed arguments
    *
    */
    private Object[] contextParameters;

    private DMEntity entity;

    private DMEntity parentEntity;

    private Session session;

    private DmsEventOccur currentOccur;

    public DmsEventOccur getCurrentOccur()
    {
        return currentOccur;
    }

    public void setCurrentOccur(DmsEventOccur currentOccur)
    {
        this.currentOccur = currentOccur;
    }

    //initialized at the application start
    //used by controllers to set created, updated instances ...
    private static ThreadLocal<Map<String, Object>> liveParameters;

    private static ThreadLocal<EventContext> contexts;

    public static synchronized void init()
    {
        liveParameters = new ThreadLocal<Map<String, Object>>();
        contexts = new ThreadLocal<EventContext>()
        {
            @Override
            protected EventContext initialValue()
            {
                return null;
            }
        };
        logger.info("Event context successfully initialized");
    }

    private EventContext()
    {
        logger.trace("Creating event context instance");
        liveParameters.set(new HashMap<String, Object>());
        contexts.set(this);
    }

    public static void clear()
    {
        logger.trace("Removing event context instance " + contexts.get());
        contexts.remove();
    }

    public static EventContext get()
    {
        EventContext ctx = contexts.get();
        logger.trace("Getting context " + ctx + " for thread " + Thread.currentThread().getName());
        if (ctx == null) {
            ctx = new EventContext();
        }
        return ctx;
    }

    public DmsEventName getEvent()
    {
        return event;
    }

    public void setEvent(DmsEventName event)
    {
        this.event = event;
    }

    public Object[] getContextParameters()
    {
        return contextParameters;
    }

    public void setContextParameters(Object[] contextParameters)
    {
        this.contextParameters = contextParameters;
    }

    public static void addParameter(String name, Object value)
    {
        liveParameters.get().put(name, value);
    }

    public static Map<String, Object> getParameters()
    {
        return liveParameters.get();
    }

    public static void setParameters(Map<String, Object> _parameters)
    {
        liveParameters.set(_parameters);
    }

    public DMEntity getEntity()
    {
        return entity;
    }

    public void setEntity(DMEntity entity)
    {
        this.entity = entity;
    }

    public DMEntity getParentEntity()
    {
        return parentEntity;
    }

    public void setParentEntity(DMEntity parentEntity)
    {
        this.parentEntity = parentEntity;
    }

    public Session getSession()
    {
        return session;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }
}

