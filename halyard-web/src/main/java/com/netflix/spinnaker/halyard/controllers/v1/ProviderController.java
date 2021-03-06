/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.halyard.controllers.v1;

import com.netflix.spinnaker.halyard.config.config.v1.HalconfigParser;
import com.netflix.spinnaker.halyard.config.model.v1.node.NodeFilter;
import com.netflix.spinnaker.halyard.config.model.v1.node.Provider;
import com.netflix.spinnaker.halyard.config.model.v1.problem.Problem.Severity;
import com.netflix.spinnaker.halyard.config.model.v1.problem.ProblemSet;
import com.netflix.spinnaker.halyard.config.services.v1.ProviderService;
import com.netflix.spinnaker.halyard.core.DaemonResponse;
import com.netflix.spinnaker.halyard.core.DaemonResponse.StaticRequestBuilder;
import com.netflix.spinnaker.halyard.core.DaemonResponse.UpdateRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/v1/config/deployments/{deploymentName:.+}/providers")
public class ProviderController {
  @Autowired
  HalconfigParser halconfigParser;

  @Autowired
  ProviderService providerService;

  @RequestMapping(value = "/{providerName:.+}", method = RequestMethod.GET)
  DaemonResponse<Provider> provider(
      @PathVariable String deploymentName,
      @PathVariable String providerName,
      @RequestParam(required = false, defaultValue = DefaultControllerValues.validate) boolean validate,
      @RequestParam(required = false, defaultValue = DefaultControllerValues.severity) Severity severity) {
    StaticRequestBuilder<Provider> builder = new StaticRequestBuilder<>();

    builder.setBuildResponse(() -> providerService.getProvider(deploymentName, providerName));

    if (validate) {
      builder.setValidateResponse(() -> providerService.validateProvider(deploymentName, providerName, severity));
    }

    return builder.build();
  }

  @RequestMapping(value = "/{providerName:.+}/enabled", method = RequestMethod.PUT)
  DaemonResponse<Void> setEnabled(
      @PathVariable String deploymentName,
      @PathVariable String providerName,
      @RequestParam(required = false, defaultValue = DefaultControllerValues.validate) boolean validate,
      @RequestParam(required = false, defaultValue = DefaultControllerValues.severity) Severity severity,
      @RequestBody boolean enabled) {
    UpdateRequestBuilder builder = new UpdateRequestBuilder();

    builder.setUpdate(() -> providerService.setEnabled(deploymentName, providerName, enabled));

    Supplier<ProblemSet> doValidate = ProblemSet::new;
    if (validate) {
      doValidate = () -> providerService.validateProvider(deploymentName, providerName, severity);
    }

    builder.setValidate(doValidate);
    builder.setHalconfigParser(halconfigParser);

    return builder.build();
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  DaemonResponse<List<Provider>> providers(@PathVariable String deploymentName,
      @RequestParam(required = false, defaultValue = DefaultControllerValues.validate) boolean validate,
      @RequestParam(required = false, defaultValue = DefaultControllerValues.severity) Severity severity) {
    StaticRequestBuilder<List<Provider>> builder = new StaticRequestBuilder<>();

    builder.setBuildResponse(() -> providerService.getAllProviders(deploymentName));

    if (validate) {
      builder.setValidateResponse(() -> providerService.validateAllProviders(deploymentName, severity));
    }

    return builder.build();
  }
}
