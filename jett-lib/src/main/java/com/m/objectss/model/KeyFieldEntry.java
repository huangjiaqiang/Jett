package com.m.objectss.model;

/**
 * Project Name: Paper
 * File Name:    KeyFieldEntry.java
 * ClassName:    KeyFieldEntry
 *
 * Description: keyField模型实体.
 *
 * @author jiaqianghuang
 * @date 2020年09月04日 2:14 PM
 *
 *
 */
public class KeyFieldEntry
{
    int fieldId;
    String fieldKey;

    public KeyFieldEntry(int fieldId, String fieldKey)
    {
        this.fieldId = fieldId;
        this.fieldKey = fieldKey;
    }

    public int getFieldId()
    {
        return fieldId;
    }

    public String getFieldKey()
    {
        return fieldKey;
    }
}
