/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.tracer.boot.listener;

import com.alipay.sofa.tracer.boot.compatible.SpringBootV1Config;
import com.alipay.sofa.tracer.boot.compatible.SpringBootV2Config;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

/**Ø
 * Parse SOFATracer Configuration in early stage.
 *
 * @author qilong.zql
 * @since 2.2.2
 */
public class SofaTracerConfigurationListener
                                            implements
                                            ApplicationListener<ApplicationEnvironmentPreparedEvent>,
                                            Ordered {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();

        if (isSpringCloudBootstrapEnvironment(environment)) {
            return;
        }

        if (isSpringBoot2()) {
            SpringBootV2Config.configSpringBootOnV2(environment);
        }

        if (isSpringBoot1()) {
            SpringBootV1Config.configSpringBootOnV1(environment);
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 30;
    }

    private boolean isSpringCloudBootstrapEnvironment(Environment environment) {
        if (!(environment instanceof ConfigurableEnvironment)) {
            return false;
        } else {
            return !((ConfigurableEnvironment) environment).getPropertySources().contains(
                "sofaBootstrap")
                   && isSpringCloud();
        }
    }

    private boolean isSpringCloud() {
        return ClassUtils.isPresent("org.springframework.cloud.bootstrap.BootstrapConfiguration",
            null);
    }

    public boolean isSpringBoot1() {
        return SpringBootVersion.getVersion().startsWith("1");
    }

    public boolean isSpringBoot2() {
        return SpringBootVersion.getVersion().startsWith("2");
    }

}