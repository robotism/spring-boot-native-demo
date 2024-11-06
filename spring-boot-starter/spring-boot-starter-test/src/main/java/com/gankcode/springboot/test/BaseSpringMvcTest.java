package com.gankcode.springboot.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Type;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class BaseSpringMvcTest extends BaseSpringTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String AUTHORIZATION = "Authorization";

    private static final SharedHttpSessionConfigurer SHARED_HTTP_SESSION_CONFIGURER = new SharedHttpSessionConfigurer();

    private static final MockMvcConfigurer SHARED_TOKEN_CONFIGURER = new MockMvcConfigurer() {

        private String token;

        @Override
        public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
            builder.alwaysDo((result) -> {
                final String token = result.getResponse().getHeader(AUTHORIZATION);
                if (StringUtils.hasText(token)) {
                    this.token = token;
                }
            });
        }

        @Override
        public RequestPostProcessor beforeMockMvcCreated(@NotNull ConfigurableMockMvcBuilder<?> builder,
                                                         @NotNull WebApplicationContext context) {
            return (request) -> {
                if (StringUtils.hasText(this.token)) {
                    request.addHeader(AUTHORIZATION, this.token);
                }
                return request;
            };
        }
    };

    private static MockMvc sMockMvc;


    @Resource
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        if (sMockMvc == null) {
            sMockMvc = MockMvcBuilders.webAppContextSetup(context)
                    .apply(SHARED_HTTP_SESSION_CONFIGURER)
                    .apply(SHARED_TOKEN_CONFIGURER)
                    .build();  //初始化MockMvc对象
        }
    }

    public MockMvc getMockMvc() {
        return sMockMvc;
    }

    public <T> T request(RequestBuilder requestBuilder, Class<T> responseClass) throws Exception {
        if (requestBuilder == null) {
            return null;
        }
        return request(requestBuilder, responseClass);
    }

    public <T> T request(RequestBuilder requestBuilder, Type responseType) throws Exception {
        if (requestBuilder == null) {
            return null;
        }
        final MockMvc mockMvc = getMockMvc();
        final MockHttpServletResponse httpServletResponse = mockMvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        final T responseBean = mapper.readValue(
                httpServletResponse.getContentAsString(),
                mapper.getTypeFactory().constructType(responseType)
        );

        assert responseBean != null;
        return responseBean;
    }
}
