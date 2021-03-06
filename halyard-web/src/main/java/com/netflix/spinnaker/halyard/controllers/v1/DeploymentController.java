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

import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.config.model.v1.node.NodeFilter;
import com.netflix.spinnaker.halyard.config.model.v1.problem.Problem.Severity;
import com.netflix.spinnaker.halyard.config.services.v1.DeploymentService;
import com.netflix.spinnaker.halyard.core.DaemonResponse;
import com.netflix.spinnaker.halyard.core.DaemonResponse.StaticRequestBuilder;
import com.netflix.spinnaker.halyard.core.tasks.v1.DaemonTask;
import com.netflix.spinnaker.halyard.core.tasks.v1.TaskRepository;
import com.netflix.spinnaker.halyard.deploy.services.v1.DeployService;
import com.netflix.spinnaker.halyard.deploy.services.v1.GenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/config/deployments")
public class DeploymentController {
  @Autowired
  DeploymentService deploymentService;

  @Autowired
  GenerateService generateService;

  @Autowired
  DeployService deployService;

  @RequestMapping(value = "/{deploymentName:.+}", method = RequestMethod.GET)
  DaemonResponse<DeploymentConfiguration> deploymentConfiguration(@PathVariable String deploymentName,
      @RequestParam(required = false, defaultValue = DefaultControllerValues.validate) boolean validate,
      @RequestParam(required = false, defaultValue = DefaultControllerValues.severity) Severity severity) {
    StaticRequestBuilder<DeploymentConfiguration> builder = new StaticRequestBuilder<>();

    builder.setBuildResponse(() -> deploymentService.getDeploymentConfiguration(deploymentName));

    if (validate) {
      builder.setValidateResponse(() -> deploymentService.validateDeployment(deploymentName, severity));
    }

    return builder.build();
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  DaemonResponse<List<DeploymentConfiguration>> deploymentConfigurations(
      @RequestParam(required = false, defaultValue = DefaultControllerValues.validate) boolean validate,
      @RequestParam(required = false, defaultValue = DefaultControllerValues.severity) Severity severity) {
    StaticRequestBuilder<List<DeploymentConfiguration>> builder = new StaticRequestBuilder<>();

    builder.setBuildResponse(() -> deploymentService.getAllDeploymentConfigurations());

    if (validate) {
      builder.setValidateResponse(() -> deploymentService.validateAllDeployments(severity));
    }

    return builder.build();
  }

  @RequestMapping(value = "/{deploymentName:.+}/generate/", method = RequestMethod.POST)
  DaemonTask<Void> generateConfig(@PathVariable String deploymentName) {
    StaticRequestBuilder<Void> builder = new StaticRequestBuilder<>();

    builder.setBuildResponse(() -> {
      generateService.generateConfig(deploymentName);
      return null;
    });

    return TaskRepository.submitTask(builder::build);
  }

  @RequestMapping(value = "/{deploymentName:.+}/deploy/", method = RequestMethod.POST)
  DaemonTask<Void> deploy(@PathVariable String deploymentName) {
    StaticRequestBuilder<Void> builder = new StaticRequestBuilder<>();

    builder.setBuildResponse(() -> {
      deployService.deploySpinnaker(deploymentName);
      return null;
    });

    return TaskRepository.submitTask(builder::build);
  }
}
