package com.uoko.rpc.serialize;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ClassSerializer implements JsonSerializer<Class<?>>,  
JsonDeserializer<Class<?>> {

	@Override
	public Class<?> deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		Class<?> result = null;
		try {
			result = Class.forName(json.getAsString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public JsonElement serialize(Class<?> c, Type arg1, JsonSerializationContext arg2) {
		 return new JsonPrimitive(c.getName());
	}

}
