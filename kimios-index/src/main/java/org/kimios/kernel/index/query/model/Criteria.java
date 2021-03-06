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

package org.kimios.kernel.index.query.model;

import java.util.List;

/**
 * @author Fabien Alin <fabien.alin@gmail.com>
 * @version 1.0
 */
public class Criteria
{
    private String query;

    private String fieldName;

    private int position = 0;

    private boolean isFaceted;

    private int level = 0;

    private List<String> filtersValues;

    private String facetField;

    private boolean isFacetRange;

    private String facetRangeMin;

    private String facetRangeMax;

    private String facetRangeGap;

    private Long metaId;

    private Long metaType;

    private String rangeMin;

    private String rangeMax;


    public String getQuery()
    {
        return query;
    }

    public void setQuery( String query )
    {
        this.query = query;
    }

    public boolean isFaceted()
    {
        return isFaceted;
    }

    public void setFaceted( boolean faceted )
    {
        isFaceted = faceted;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel( int level )
    {
        this.level = level;
    }

    public List<String> getFiltersValues()
    {
        return filtersValues;
    }

    public void setFiltersValues( List<String> filtersValues )
    {
        this.filtersValues = filtersValues;
    }

    public String getFacetField()
    {
        return facetField;
    }

    public void setFacetField( String facetField )
    {
        this.facetField = facetField;
    }

    public boolean isFacetRange()
    {
        return isFacetRange;
    }

    public void setFacetRange( boolean facetRange )
    {
        isFacetRange = facetRange;
    }

    public String getFacetRangeMin()
    {
        return facetRangeMin;
    }

    public void setFacetRangeMin( String facetRangeMin )
    {
        this.facetRangeMin = facetRangeMin;
    }

    public String getFacetRangeMax()
    {
        return facetRangeMax;
    }

    public void setFacetRangeMax( String facetRangeMax )
    {
        this.facetRangeMax = facetRangeMax;
    }

    public String getFacetRangeGap()
    {
        return facetRangeGap;
    }

    public void setFacetRangeGap( String facetRangeGap )
    {
        this.facetRangeGap = facetRangeGap;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName( String fieldName )
    {
        this.fieldName = fieldName;
    }

    public Long getMetaId()
    {
        return metaId;
    }

    public void setMetaId( Long metaId )
    {
        this.metaId = metaId;
    }

    public Long getMetaType()
    {
        return metaType;
    }

    public void setMetaType( Long metaType )
    {
        this.metaType = metaType;
    }

    public String getRangeMin()
    {
        return rangeMin;
    }

    public void setRangeMin( String rangeMin )
    {
        this.rangeMin = rangeMin;
    }

    public String getRangeMax()
    {
        return rangeMax;
    }

    public void setRangeMax( String rangeMax )
    {
        this.rangeMax = rangeMax;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }

    @Override
    public String toString()
    {
        return "Criteria{" +
            "query='" + query + '\'' +
            ", fieldName='" + fieldName + '\'' +
            ", position=" + position +
            ", isFaceted=" + isFaceted +
            ", level=" + level +
            ", filtersValues=" + filtersValues +
            ", facetField='" + facetField + '\'' +
            ", isFacetRange=" + isFacetRange +
            ", facetRangeMin='" + facetRangeMin + '\'' +
            ", facetRangeMax='" + facetRangeMax + '\'' +
            ", facetRangeGap='" + facetRangeGap + '\'' +
            ", metaId=" + metaId +
            ", metaType=" + metaType +
            ", rangeMin='" + rangeMin + '\'' +
            ", rangeMax='" + rangeMax + '\'' +
            '}';
    }
}
