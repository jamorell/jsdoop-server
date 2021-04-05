package com.jsdoop.server.configuration;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import com.jsdoop.server.controllers.WeightsController;


@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@ControllerAdvice
public class RestExceptionHandler extends DefaultHandlerExceptionResolver {
	public String httpServletRequestToString(HttpServletRequest request) throws Exception {

	    ServletInputStream mServletInputStream = request.getInputStream();
	    byte[] httpInData = new byte[request.getContentLength()];
	    int retVal = -1;
	    StringBuilder stringBuilder = new StringBuilder();

	    while ((retVal = mServletInputStream.read(httpInData)) != -1) {
	        for (int i = 0; i < retVal; i++) {
	            stringBuilder.append(Character.toString((char) httpInData[i]));
	        }
	    }

	    return stringBuilder.toString();
	}
	
	@ExceptionHandler(value= HttpMessageNotWritableException.class)
	@Override
	protected ModelAndView handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
			HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {

//		sendServerError(ex, request, response);
        log.info("..........request = " + request);        
        log.info("..........response = " + response);
        log.info("..........request.type = " + request.getMethod());
        Enumeration<String> params = request.getParameterNames(); 
        while(params.hasMoreElements()){
         String paramName = params.nextElement();
         System.out.println("..........Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
        }
        String myString = org.apache.commons.io.IOUtils.toString(request.getInputStream());
        log.info(".........TOTAL REQUEST = " + myString);
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                	String headerName = headerNames.nextElement();
                	log.info("request Header -> " + headerName + ": " + request.getHeader(headerName));
                }
        }
        
//        Enumeration<String> attributeNames = request.getAttributeNames();
//        if (headerNames != null) {
//                while (attributeNames.hasMoreElements()) {
//                	String attName = attributeNames.nextElement();
//                	log.info("request Attribute -> " + attName + ": " + request.getAttribute(attName));
//                }
//        }
        
        
        Collection<String> responseHeaderNames = response.getHeaderNames();
        if (responseHeaderNames != null) {
        	Iterator<String> it = responseHeaderNames.iterator();
                while (it.hasNext()) {
                	String headerName = it.next();
                	log.info("response Header -> " + headerName + ": " + response.getHeader(headerName));
                }
        }      
        
        String url = ((HttpServletRequest)request).getRequestURL().toString();
        String queryString = ((HttpServletRequest)request).getQueryString();

        log.info(".........context url = " + url) ;
        log.info(".........context queryString = " + queryString) ;       
        try {
			log.info(".........TOTAL REQUEST = " + httpServletRequestToString(request));
			log.info(".........TOTAL RESPONSE = " + org.apache.commons.io.IOUtils.toString(request.getInputStream()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Set<MediaType> producibleMediaTypes =
				(Set<MediaType>) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        log.info("MOST IMPORTANT ATT producibleMediaTypes" + producibleMediaTypes);
        log.info("MOST IMPORTANT ATT contentype" + request.getContentType());       

        Set<MediaType> producibleMediaTypes2 =
				(Set<MediaType>) request
						.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);


        log.info("!CollectionUtils.isEmpty(producibleMediaTypes) = " + !CollectionUtils.isEmpty(producibleMediaTypes2));
//        log.info("((ServletServerHttpResponse)response).getHeaders().getContentType() " + ((ServletServerHttpResponse)response).getHeaders().getContentType());
        
        ex.printStackTrace();
		return null;
	}
	private static final Logger log = LoggerFactory.getLogger(WeightsController.class);

//    @ExceptionHandler(value= HttpMessageNotWritableException.class)
//    @Override
//    protected ModelAndView handleBindException(
//            BindException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler)
//            throws IOException {
//        System.out.println("In CustomExceptionHandlerResolver");
//        log.info("..........request = " + request);        
//        log.info("..........response = " + response);
//        response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
//        return new ModelAndView();
//    }
}