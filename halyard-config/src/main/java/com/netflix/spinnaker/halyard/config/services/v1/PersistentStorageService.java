/*
 * Copyright 2017 Google, Inc.
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

package com.netflix.spinnaker.halyard.config.services.v1;

import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.config.model.v1.node.NodeFilter;
import com.netflix.spinnaker.halyard.config.model.v1.node.PersistentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersistentStorageService {
  @Autowired
  LookupService lookupService;

  @Autowired
  DeploymentService deploymentService;

  public PersistentStorage getPersistentStorage(String deploymentName) {
    NodeFilter filter = new NodeFilter().setDeployment(deploymentName).setPersistentStorage();

    List<PersistentStorage> matching = lookupService.getMatchingNodesOfType(filter, PersistentStorage.class);

    switch (matching.size()) {
      case 0:
        PersistentStorage persistentStorage = new PersistentStorage();
        setPersistentStorage(deploymentName, persistentStorage);
        return persistentStorage;
      case 1:
        return matching.get(0);
      default:
        throw new RuntimeException("It shouldn't be possible to have multiple persistent storage nodes. This is a bug.");
    }
  }

  public void setPersistentStorage(String deploymentName, PersistentStorage newPersistentStorage) {
    DeploymentConfiguration deploymentConfiguration = deploymentService.getDeploymentConfiguration(deploymentName);
    deploymentConfiguration.setPersistentStorage(newPersistentStorage);
  }
}
