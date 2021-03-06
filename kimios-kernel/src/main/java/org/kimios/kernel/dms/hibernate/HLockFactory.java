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
package org.kimios.kernel.dms.hibernate;

import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.Lock;
import org.kimios.kernel.dms.LockFactory;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.CheckoutViolationException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HLockFactory extends HFactory implements LockFactory
{
    private static Logger log = LoggerFactory.getLogger(HLockFactory.class);

    public Lock getDocumentLock(Document d) throws ConfigException, DataSourceException
    {
        try {
            Lock l = (Lock) getSession().get(Lock.class, d.getUid());
            return l;
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public boolean checkin(Document d, User u) throws AccessDeniedException, ConfigException, DataSourceException
    {
        try {
            try {
                log.debug("Checkin doc " + d.getUid() + " " + d.getPath() + " by " + u.getUid() + "@" +
                        u.getAuthenticationSourceName());
                Lock existingLock = (Lock) getSession().get(Lock.class, d.getUid());
                if (existingLock == null) {
                    return false;
                }
                if (!(existingLock.getUser().equals(u.getUid())) &&
                        existingLock.getUserSource().equals(u.getAuthenticationSourceName()))
                {
                    throw new AccessDeniedException();
                }
                getSession().delete(existingLock);
                getSession().flush();

                return true;
            } catch (ObjectNotFoundException cve) {
                log.debug("No lock found");
                return false;
            }
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public boolean checkout(Document d, User user)
            throws ConfigException, DataSourceException, CheckoutViolationException
    {
        try {
            try {
                Lock l = new Lock();
                l.setUser(user.getID());
                l.setDate(new Date());
                l.setDocument(d);
                l.setUserSource(user.getAuthenticationSourceName());
                getSession().save(l);
                getSession().flush();

                return true;
            } catch (ConstraintViolationException ce) {
                Lock existingLock = getDocumentLock(d);
                if (!existingLock.getUser().equals(user.getUid()) ||
                        !existingLock.getUserSource().equals(user.getAuthenticationSourceName()))
                {
                    throw new CheckoutViolationException();
                }

                return false;
            } catch (org.hibernate.NonUniqueObjectException ce) {
                Lock existingLock = getDocumentLock(d);
                if (!existingLock.getUser().equals(user.getUid()) ||
                        !existingLock.getUserSource().equals(user.getAuthenticationSourceName()))
                {
                    throw new CheckoutViolationException();
                }

                return false;
            }
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

