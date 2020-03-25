
package com.wiltech.edge.filter;


import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import lombok.extern.slf4j.Slf4j;

/**
 * This class can be used to allow sensitive headers
 * Omitted by zuul.
 * 
 * @see ZuulFilter
 */
@Component
@Slf4j
public class GatewayFilter extends ZuulFilter {

  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return 1;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();

//      RequestContext ctx = RequestContext.getCurrentContext();
//      HttpServletRequest request = ctx.getRequest();
      log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
      log.info("The auth headers is " + request.getHeader("Authorization"));

    /*
     * Adding authorization header to zuul request header as 
     * zuul omits sensitive headers
     */
    if(request.getHeader("Authorization") != null){
    	ctx.addZuulRequestHeader("Authorization", request.getHeader("Authorization"));
    }
    return null;
  }

}
