package org.qwb.ai.common.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LongListFormatSerializer extends JsonSerializer<List<Long>> {


    @Override
    public void serialize(List<Long> value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String text = "";
        //是否为空
        if (!CollectionUtils.isEmpty(value)) {
            try {
                List<String> strList = new ArrayList<>(value.size());
                for (Long aLong : value) {
                    strList.add(String.valueOf(aLong));
                }
                //格式化是否为空
                if (!CollectionUtils.isEmpty(strList)) {
                    jsonGenerator.writeObject(strList);
                    return;
                }
            } catch (Exception ignored) {

            }
        }
        jsonGenerator.writeString(text);
    }
}
