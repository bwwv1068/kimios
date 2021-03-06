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
package org.kimios.kernel.index;

import java.io.File;
import java.util.List;

import org.apache.lucene.search.Query;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.security.DMEntityACL;

/**
 * @author Fabien Alin (Farf) <fabien.alin@gmail.com>
 *
 *         Abstract Class handling main index features.
 * @see IndexManager
 */
public interface AbstractIndexManager
{
    void reindex(String path) throws DataSourceException, ConfigException, IndexException;

    int getReindexProgression();

    boolean deleteDirectory(File path);

    void deleteDocument(Document document) throws IndexException;

    void indexDocument(DMEntity document) throws IndexException, DataSourceException, ConfigException;

    void updateAcls(long docUid, List<DMEntityACL> acls) throws IndexException;

    void deletePath(String path) throws IndexException;

    void updatePath(String oldPath, String newPath) throws IndexException;
}
