package org.smartdeveloperhub.harvesters.it.backend.exhibitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.backend.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

@Produces("application/psr.sdh.itcollector.entity+json")
public class ITHarvesterEntityProvider<T> extends AbstractMessageReaderWriterProvider<T> {

	private static final String MEDIA_TYPE = "application/psr.sdh.itcollector.entity+json";

	@Override
	public boolean isReadable(Class<?> type	, Type genericType,
								Annotation[] annotations, MediaType mediaType) {

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
						MediaType mediaType, MultivaluedMap<String,
						String> httpHeaders, InputStream entityStream)
								throws IOException, WebApplicationException {


			String serialized = IOUtils.toString(entityStream, Charsets.UTF_8);
			return (T) Entities.unmarshallEntity(serialized, (Class<Entity>) type);
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
								Annotation[] annotations, MediaType mediaType) {

		return true;
	}

	@Override
	public void writeTo(T object, Class<?> type, Type genericType,
						Annotation[] annotations, MediaType mediaType,
						MultivaluedMap<String, Object> httpHeaders,
						OutputStream entityStream)
								throws IOException, WebApplicationException {

		if (Entity.class.isAssignableFrom(object.getClass())) {

			String serialized = Entities.marshallEntity((Entity) object);
			IOUtils.write(serialized, entityStream, Charsets.UTF_8);

		} else {

			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(entityStream, object);
		}
	}

}
