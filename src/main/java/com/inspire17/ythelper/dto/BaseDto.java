package com.inspire17.ythelper.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspire17.ythelper.helper.annotations.ToJsonString;

public class BaseDto {
    @Override
    public String toString() {
        if (this.getClass().isAnnotationPresent(ToJsonString.class)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                return super.toString();
            }
        }
        return super.toString();
    }
}
