/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2012  DevLib'
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
package org.kimios.utils.configuration;

import java.util.List;

import org.kimios.exceptions.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class ConfigurationManager
{
    private static Logger log = LoggerFactory.getLogger(ConfigurationManager.class);

    private ConfigurationHolder holder;

    public ConfigurationHolder getHolder()
    {
        return holder;
    }

    public void setHolder(ConfigurationHolder holder)
    {
        this.holder = holder;
    }

    private static ConfigurationManager instance;

    public ConfigurationManager()
    {
        instance = this;
    }

    public static void init(ApplicationContext springContext) throws ConfigException
    {
        /*
            Look for spring instantiated ConfigHolder
         */
        if (instance.holder == null) {
            log.info("Configuration not defined. Looking for default implementation");
            instance.holder = springContext.getBean(ConfigurationHolder.class);
            if (instance.holder == null) {
                log.error("No config holder found");
                throw new RuntimeException("No config holder found");
            }
        }
    }

    public static String getValue(String key) throws ConfigException
    {
        if (instance.holder.exists(key)) {
            return instance.holder.getStringValue(key);
        } else {
            log.warn("[Kimios Kernel] Key " + key + " cannot be found in global configuration");
            return null;
        }
    }

    public static List<String> getListValue(String key) throws ConfigException
    {
        if (instance.holder.exists(key)) {
            return instance.holder.getValues(key);
        } else {
            log.warn("[Kimios Kernel] Key " + key + " cannot be found in global configuration");
            return null;
        }
    }
}

