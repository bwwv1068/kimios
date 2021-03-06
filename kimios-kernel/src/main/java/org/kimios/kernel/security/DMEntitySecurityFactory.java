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
package org.kimios.kernel.security;

import java.util.List;
import java.util.Vector;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.exception.DataSourceException;

public interface DMEntitySecurityFactory
{
    public DMEntitySecurity getDMEntitySecurity(DMEntity e, String name, String source, int type)
            throws ConfigException,
            DataSourceException;

    public Vector<DMEntitySecurity> getDMEntitySecurities(DMEntity e) throws ConfigException, DataSourceException;

    public List<DMEntityACL> getDMEntityACL(DMEntity e) throws ConfigException, DataSourceException;

    public List<DMEntityACL> saveDMEntitySecurity(DMEntitySecurity des) throws ConfigException, DataSourceException;

    public void updateDMEntitySecurity(DMEntitySecurity des) throws ConfigException, DataSourceException;

    public void deleteDMEntitySecurity(DMEntitySecurity des) throws ConfigException, DataSourceException;

    public boolean ruleExists(DMEntity e, String userName, String userSource, Vector<String> hashs,
            Vector<String> noAccessHash) throws ConfigException, DataSourceException;

    public void cleanACL(DMEntity e) throws ConfigException, DataSourceException;

    public void cleanACLRecursive(DMEntity d) throws ConfigException, DataSourceException;

    public <T extends DMEntityImpl> List<T> authorizedEntities(List<T> e, String userName, String userSource,
            Vector<String> hashs, Vector<String> noAccessHash) throws ConfigException, DataSourceException;

    public boolean hasAnyChildNotWritable(DMEntity e, String userName, String userSource, Vector<String> writeHash,
            String noAccessHash) throws ConfigException, DataSourceException;

    public boolean hasAnyChildCheckedOut(DMEntity e, String userName, String userSource)
            throws ConfigException, DataSourceException;
}

