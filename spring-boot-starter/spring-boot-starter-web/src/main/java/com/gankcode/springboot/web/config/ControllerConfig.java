package com.gankcode.springboot.web.config;

import com.gankcode.springboot.i18n.I18nUtils;
import com.gankcode.springboot.utils.JsonTemplate;
import com.gankcode.springboot.utils.ScanPackagesUtils;
import com.gankcode.springboot.web.http.ErrorCode;
import com.gankcode.springboot.web.http.RequestException;
import com.gankcode.springboot.web.http.ResponseBean;
import com.gankcode.springboot.web.utils.HttpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;


@Slf4j
@AllArgsConstructor
@RestControllerAdvice(annotations = Controller.class)
class ControllerConfig {


    @Slf4j
    @RestControllerAdvice(annotations = RestController.class)
    public static class RestControllerResponseBodyAdvice implements ResponseBodyAdvice {

        @Resource
        private JsonTemplate jsonTemplate;

        @Resource
        private ScanPackagesUtils scanPackagesUtils;

        @Override
        public boolean supports(MethodParameter returnType, @NotNull Class converterType) {
            return scanPackagesUtils.isComponentScanPackage(returnType.getDeclaringClass());
        }

        @Override
        public Object beforeBodyWrite(Object body,
                                      @NotNull MethodParameter returnType,
                                      @NotNull MediaType selectedContentType,
                                      @NotNull Class selectedConverterType,
                                      @NotNull ServerHttpRequest request,
                                      @NotNull ServerHttpResponse response) {
            if (body instanceof ResponseBean || body instanceof ResponseEntity) {
                return body;
            }

            if (MediaType.APPLICATION_JSON.getType().equals(selectedContentType.getType())
                    && MediaType.APPLICATION_JSON.getSubtype().equals(selectedContentType.getSubtype())) {
                final ResponseBean<?> result = ResponseBean.builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message(ErrorCode.SUCCESS.getI18nMessage())
                        .data(body)
                        .build();
                if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
                    return jsonTemplate.toJson(result);
                } else {
                    return result;
                }
            }
            return body;
        }

    }


    @ExceptionHandler({
            Exception.class
    })
    @ResponseBody
    public ResponseBean<?> unknownExceptions(final Exception e) throws IOException {
        log.error("URI = " + HttpUtils.getRequestURI(), e);
        final HttpServletResponse response = HttpUtils.getResponse();
        if (response == null || response.isCommitted()) {
            return null;
        }
        final ResponseBean<?> bean = new ResponseBean<>();
        bean.setCode(ErrorCode.ERROR.getCode());
        bean.setMessage(ErrorCode.ERROR.getI18nMessage());
        return bean;
    }


    @ExceptionHandler({
            HttpMessageNotReadableException.class
    })
    @ResponseBody
    public ResponseBean<?> httpException(final Exception e) {
        if (log.isDebugEnabled()) {
            log.error("URI = " + HttpUtils.getRequestURI(), e);
        }
        final ResponseBean<?> bean = new ResponseBean<>();
        final Throwable cause = e.getCause() != null ? e.getCause() : e;
        bean.setCode(ErrorCode.INVALID_DATA.getCode());
        bean.setMessage(ErrorCode.INVALID_DATA.getI18nMessage(String.format("%s -> %s",
                cause.getClass().getSimpleName(),
                cause.getMessage()
        )));
        return bean;
    }

//    @ExceptionHandler({
//            DeadlockLoserDataAccessException.class,
//            CannotAcquireLockException.class
//    })
//    @ResponseBody
//    public ResponseBean<?> txLockExceptions(final Exception e) {
//        log.error("URI = " + HttpUtils.getRequestURI(), e);
//        final ResponseBean<?> bean = new ResponseBean<>();
//        bean.setCode(ErrorCode.BUSY.getCode());
//        bean.setMessage(ErrorCode.BUSY.getI18nMessage());
//        return bean;
//    }


    @ExceptionHandler({
            RequestException.class
    })
    @ResponseBody
    public ResponseBean<?> requestExceptions(final RequestException e) throws Exception {
        if (log.isDebugEnabled()) {
            log.error("URI = " + HttpUtils.getRequestURI(), e);
        }
        final HttpServletResponse response = HttpUtils.getResponse();
        if (response == null || response.isCommitted()) {
            return null;
        }
        if (e.getCode() == ErrorCode.PERMISSION_DENIED.getCode()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getLocalizedMessage());
            return null;
        } else {
            final ResponseBean<?> bean = new ResponseBean<>();
            bean.setCode(e.getCode());
            bean.setMessage(e.getLocalizedMessage());
            return bean;
        }
    }


    @ExceptionHandler({
            ValidationException.class
    })
    @ResponseBody
    public ResponseBean<?> validationException(ValidationException e) throws Exception {
        final Throwable cause = e.getCause();
        if (cause instanceof RequestException) {
            return requestExceptions((RequestException) cause);
        } else {
            return validateException(e);
        }
    }

    /**
     * 处理 Get 请求中 使用 @Valid 验证路径中请求实体校验失败后抛出的异常是 BindException
     * 处理请求参数格式错误 @RequestParam 上 validate 失败后抛出的异常是 ConstraintViolationException
     * 处理请求参数格式错误 @RequestBody 上 validate 失败后抛出的异常是 MethodArgumentNotValidException
     *
     * @param e 异常
     * @return ResponseBean
     */
    @ResponseBody
    @ExceptionHandler({
            BindException.class,
            ConstraintViolationException.class,
            ServletRequestBindingException.class,
            MissingPathVariableException.class,
            MissingRequestHeaderException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class
    })
    public ResponseBean<?> validateException(Exception e) {
        if (log.isDebugEnabled()) {
            log.error("URI = " + HttpUtils.getRequestURI(), e);
        }
        final List<String> details = new ArrayList<>();
        switch (e) {
            case ConstraintViolationException ce -> {
                for (ConstraintViolation<?> constraintViolation : ce.getConstraintViolations()) {
                    final String filed = StreamSupport.stream(constraintViolation.getPropertyPath().spliterator(), false)
                            .map(Path.Node::getName)
                            .reduce((f, s) -> s).orElse("");
                    details.add(filed + " = " + constraintViolation.getInvalidValue());
                }
            }
            case MissingPathVariableException m ->
                    details.add("path variable: " + m.getVariableName() + " = <missing>");
            case MissingRequestHeaderException m -> details.add("header: " + m.getHeaderName() + " = <missing>");
            case MissingServletRequestParameterException mrpe ->
                    details.add("parameter: " + mrpe.getParameterName() + " = <missing>");
            case ServletRequestBindingException m -> details.add(I18nUtils.translate(m.getMessage()));
            case TypeMismatchException ignored -> {
                final MethodArgumentTypeMismatchException m = (MethodArgumentTypeMismatchException) e;
                details.add(m.getMessage());
            }
            case BindException be -> {
                final BindingResult result = be.getBindingResult();
                for (FieldError fieldError : result.getFieldErrors()) {
                    details.add(fieldError.getField() + " = " + fieldError.getRejectedValue());
                }
            }
            case null, default -> details.add(e != null ? e.getMessage() : "");
        }

        final ResponseBean<?> bean = new ResponseBean<>();
        bean.setCode(ErrorCode.INVALID.getCode());
        bean.setMessage(ErrorCode.INVALID.getI18nMessage(String.join("; ", details)));
        return bean;

    }

}
