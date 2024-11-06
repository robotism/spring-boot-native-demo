package com.gankcode.springboot.web.http.pageable;

import io.undertow.servlet.spec.HttpServletRequestImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import java.util.Comparator;
import java.util.List;

/**
 * 保持url query参数顺序
 *
 */
public class PageRequestArgumentResolver extends ServletModelAttributeMethodProcessor {

    public PageRequestArgumentResolver(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return BasePageRequestBean.class.equals(parameter.getParameterType());
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        // 修改 request 的 queryParameters  为 Linked Map

        final HttpServletRequestImpl requestImpl = (HttpServletRequestImpl) request.getNativeRequest();

        final List<String> names = PageRequestUtils.getLinkedQueryParameterNames(requestImpl);

        final WebDataBinder proxy = new ExtendedServletRequestDataBinder(binder.getTarget()) {
            @Override
            protected void doBind(MutablePropertyValues mpvs) {
                mpvs.getPropertyValueList().sort(Comparator.comparingInt(o -> names.indexOf(o.getName())));
                super.doBind(mpvs);
            }
        };
        super.bindRequestParameters(proxy, request);
    }

}