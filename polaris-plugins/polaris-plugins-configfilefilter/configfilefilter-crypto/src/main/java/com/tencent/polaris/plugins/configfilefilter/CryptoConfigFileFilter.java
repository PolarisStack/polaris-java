/*
 * Tencent is pleased to support the open source community by making Polaris available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.tencent.polaris.plugins.configfilefilter;

import com.tencent.polaris.api.exception.PolarisException;
import com.tencent.polaris.api.plugin.PluginType;
import com.tencent.polaris.api.plugin.common.InitContext;
import com.tencent.polaris.api.plugin.common.PluginTypes;
import com.tencent.polaris.api.plugin.compose.Extensions;
import com.tencent.polaris.api.plugin.configuration.ConfigFile;
import com.tencent.polaris.api.plugin.configuration.ConfigFileResponse;
import com.tencent.polaris.api.plugin.filter.ConfigFileFilter;
import com.tencent.polaris.plugins.configfilefilter.service.RSAService;
import com.tencent.polaris.plugins.configfilefilter.util.AESUtil;

/**
 * @author fabian4
 * @date 2023/6/14
 */
public class CryptoConfigFileFilter implements ConfigFileFilter {

    private RSAService rsaService;

    public RSAService getRsaService() {
        return rsaService;
    }

    @Override
    public String getName() {
        return "AES";
    }

    @Override
    public PluginType getType() {
        return PluginTypes.CONFIG_FILTER.getBaseType();
    }

    @Override
    public void init(InitContext ctx) throws PolarisException {
        rsaService = new RSAService();
    }

    @Override
    public void doBefore(ConfigFile configFile) {
        configFile.setEncrypted(Boolean.TRUE);
        configFile.setPublicKey(rsaService.getPKCS1PublicKey());
    }

    @Override
    public void doAfter(ConfigFileResponse configFileResponse) {
        ConfigFile configFile = configFileResponse.getConfigFile();
        if (configFile.isEncrypted()) {
            byte[] password = rsaService.decrypt(configFile.getDataKey());
            String result = AESUtil.decrypt(configFile.getContent(), password);
            configFile.setContent(result);
        }
    }

    @Override
    public void postContextInit(Extensions ctx) throws PolarisException {

    }

    @Override
    public void destroy() {

    }
}
