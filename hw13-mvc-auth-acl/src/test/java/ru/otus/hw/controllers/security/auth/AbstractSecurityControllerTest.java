package ru.otus.hw.controllers.security.auth;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static io.micrometer.common.util.StringUtils.isEmpty;
import static io.micrometer.common.util.StringUtils.isNotEmpty;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static ru.otus.hw.DataTest.getDbUsers;

public class AbstractSecurityControllerTest {

    protected void verifyMvcSecurity(MockMvc mvc, MockHttpServletRequestBuilder requestBuilder, String userName,
                                     int status, String redirectUrl, String expectedView) throws Exception {
        if (isNotEmpty(userName) && getDbUsers().stream().anyMatch(it -> it.getUsername().equals(userName))) {
            requestBuilder.with(user(userName));
        }
        ResultActions result = mvc.perform(requestBuilder)
            .andExpect(status().is(status));

        if (isNotEmpty(redirectUrl)) {
            result.andExpect(redirectedUrl(redirectUrl));
        }
        if (isEmpty(redirectUrl) && isNotEmpty(expectedView)) {
            result.andExpect(view().name(expectedView));
        }
    }
}
