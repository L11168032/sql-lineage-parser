package com.lan.lineage.common;

import lombok.Data;

/**
 * @author lanxueri
 * @ClassName LineageColumn
 * @Description TODO
 * @createTime 2020-07-31
 */
@Data
public class LineageColumn implements Comparable<LineageColumn>  {
    private String targetColumnName;

    private String sourceDbName;

    private String sourceTableName;

    private String sourceColumnName;

    private String expression;

    private Boolean isEnd = false;


    public void setSourceTableName(String sourceTableName) {
        sourceTableName = EmptyUtils.isNotEmpty(sourceTableName) ? sourceTableName.replace("`","") : sourceTableName;
        if (sourceTableName.contains(".")){
            this.sourceDbName = sourceTableName.substring(0,sourceTableName.indexOf("."));
            this.sourceTableName = sourceTableName.substring(sourceTableName.indexOf(".")+1);
        }else {
            this.sourceTableName = sourceTableName;

        }
    }


    public int compareTo(LineageColumn o) {
        if (this.getTargetColumnName().equals(o.getTargetColumnName()))
            return 0;
        return -1;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineageColumn myColumn = (LineageColumn) o;

        if (!this.getTargetColumnName().equals(myColumn.getTargetColumnName())) return false;
        if (EmptyUtils.isNotEmpty(sourceTableName) && !sourceTableName.equals(myColumn.sourceTableName)) return false;
        if (EmptyUtils.isNotEmpty(sourceColumnName))
            return sourceColumnName.equals(myColumn.sourceColumnName);
        return true;
    }

    @Override
    public int hashCode() {
        int result = getTargetColumnName().hashCode();
        if (EmptyUtils.isNotEmpty(sourceTableName)){
            result = 31 * result + sourceTableName.hashCode();
        }
        if (EmptyUtils.isNotEmpty(sourceColumnName)){
            result = 31 * result + sourceColumnName.hashCode();
        }
        return result;
    }
}
