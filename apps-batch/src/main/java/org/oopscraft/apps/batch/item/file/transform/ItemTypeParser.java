package org.oopscraft.apps.batch.item.file.transform;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.item.file.annotation.Length;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * ItemTypeParser
 */
@Slf4j
public class ItemTypeParser {

    private final Class<?> itemClass;

    @Getter
    private List<ItemField> itemFields = new ArrayList<>();

    /**
     * ItemTypeParser
     * @param itemClass
     */
    public ItemTypeParser(Class<?> itemClass) {
        this.itemClass = itemClass;

        // parse fields
        for(Field field : this.itemClass.getDeclaredFields()){
            if(Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())){
                continue;
            }
            itemFields.add(parseItemField(field));
        }
    }

    /**
     * createItemField
     * @param field field
     * @return ItemField
     */
    private ItemField parseItemField(Field field) {
        ItemField itemField = new ItemField();
        itemField.setName(field.getName());
        Length length = field.getDeclaredAnnotation(Length.class);
        itemField.setLength(length);
        return itemField;
    }


}
