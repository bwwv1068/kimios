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
package org.kimios.kernel.dms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kimios.kernel.exception.MetaValueTypeException;

@Entity
@Table(name = "meta_number_value")
public class MetaNumberValue extends MetaValueBean
{
    @Column(name = "meta_number_value", nullable = false)
    private double value;

    public MetaNumberValue()
    {
    }

    public MetaNumberValue(DocumentVersion version, long metaUid, double value)
    {
        super(version, metaUid);
        this.value = value;
    }

    public MetaNumberValue(DocumentVersion version, Meta meta, double value)
    {
        super(version, meta);
        this.value = value;
    }

    public Double getValue()
    {
        return this.value;
    }

    public void setValue(Object value) throws MetaValueTypeException
    {
        if (value.getClass().equals(Double.class)) {
            this.value = (Double) value;
        } else {
            throw new MetaValueTypeException(
                    "Meta value type \"" + Double.class.getName() + "\" was expected instead of \"" +
                            value.getClass().getName() + "\"");
        }
    }

    public void setValue(double value)
    {
        this.value = (Double) value;
    }
}

