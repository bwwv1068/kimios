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
package org.kimios.kernel.rules;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.kimios.kernel.rules.impl.RuleImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleManager
{
    private static Logger log = LoggerFactory.getLogger(RuleManager.class);

    private static String SLASH_SEPARATOR = "/";

    private RuleBeanFactory ruleBeanFactory;

    public RuleBeanFactory getRuleBeanFactory()
    {
        return ruleBeanFactory;
    }

    public void setRuleBeanFactory(RuleBeanFactory ruleBeanFactory)
    {
        this.ruleBeanFactory = ruleBeanFactory;
    }

    public List<RuleBean> processRulesBefore(MethodInvocation arg0) throws Throwable
    {
        DmsEvent evt = arg0.getMethod().getAnnotation(DmsEvent.class);
        EventContext ctx = null;
        String path = null;
        List<RuleBean> beans = null;
        if (evt != null) {
            ctx = EventContext.get();
            if (ctx != null && ctx.getEntity() != null) {
                path = ctx.getEntity().getPath();
                String[] allPath = path.split(SLASH_SEPARATOR);
                String pPath = "";
                List<String> fPaths = new ArrayList<String>();
                for (String aP : allPath) {
                    aP = pPath + aP;
                    pPath = aP + SLASH_SEPARATOR;
                    if (aP.equalsIgnoreCase("")) {
                        aP = "/";
                    }
                    fPaths.add(aP);
                }

                if (log.isTraceEnabled()) {
                    for (String finalPaths : fPaths) {
                        log.trace("Fpath: " + finalPaths);
                    }
                }
                path = path.substring(0, path.lastIndexOf("/"));
                beans = ruleBeanFactory.loadConditionByEventAndPath(ctx.getEvent(), path, fPaths);
                if (beans != null && beans.size() > 0) {
                    evalutate(beans, ctx, DmsEventOccur.BEFORE);
                }
            }
        }
        return beans;
    }

    public void processRulesAfter(List<RuleBean> beans, EventContext ctx) throws Throwable
    {
        if (ctx != null && beans != null && beans.size() > 0) {
            evalutate(beans, ctx, DmsEventOccur.AFTER);
        }
    }

    private void evalutate(List<RuleBean> beans, EventContext ctx, DmsEventOccur occur)
    {
        EventBean currentEvent = new EventBean(ctx.getEvent().ordinal(), occur.ordinal());
        log.debug(ctx.getEvent().name() + "/" + ctx.getEvent().ordinal() + ", Status: " + occur.name() + " / " +
                occur.ordinal());
        log.debug("Loaded rules count: " + beans.size());
        for (RuleBean each : beans) {
            log.debug(occur.name() + " " + ctx.getEvent().name() + ": " + each.getName() + " Match event: " +
                    each.getEvents().contains(currentEvent));
            if (each.getEvents().contains(currentEvent)) {
                try {
                    Class<?> cClass = Class.forName(each.getJavaClass());
                    cClass.asSubclass(RuleImpl.class);
                    RuleImpl liveInstance = (RuleImpl) cClass.newInstance();
                    /*
                    *  set true parameters (reflection)
                    */
                    for (String fName : each.getParameters().keySet()) {
                        log.debug(
                                "Set parameter " + fName + ": " + each.getParameters().get(fName).getClass().getName());
                        log.debug("Set parameter " + fName + ": " + each.getParameters().get(fName).toString());
                        Field f = cClass.getDeclaredField(fName);
                        f.setAccessible(true);
                        if (f.getType().equals(Class.class)) {
                            f.set(liveInstance, Class.forName(each.getParameters().get(fName).toString()));
                        } else {
                            f.set(liveInstance, each.getParameters().get(fName));
                        }
                    }
                    liveInstance.setConditionContext(ctx);
                    if (liveInstance.isTrue()) {
                        liveInstance.execute();
                    }
                } catch (Exception e) {
                    log.error("Exception while handling rule...", e);
                }
            }
        }
    }
}

